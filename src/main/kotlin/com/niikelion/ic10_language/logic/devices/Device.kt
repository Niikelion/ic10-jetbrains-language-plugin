package com.niikelion.ic10_language.logic.devices

import com.niikelion.ic10_language.logic.AspectNotFoundError
import com.niikelion.ic10_language.logic.AspectTypeMismatchError
import com.niikelion.ic10_language.logic.DeviceDataRegistry
import com.niikelion.ic10_language.logic.Macros
import com.niikelion.ic10_language.logic.PropertyNotFoundError
import com.niikelion.ic10_language.logic.SlotNotFoundError
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.StationeersEnumData
import com.niikelion.ic10_language.logic.aspects.DeviceAspect
import com.niikelion.ic10_language.logic.state.*
import kotlin.reflect.KClass

class PropertyDefinition(
    val name: String,
    val enableRead: Boolean = true,
    val enableWrite: Boolean = false,
) {
    val defaultValue: Double = 0.0
}

data class DeviceInfo(
    val prefabHash: Long,
    val properties: Map<Int, PropertyDefinition>
)

/**
 * Describes a single slot of a device: its [index], display [name], item [type] and the set of
 * slot logic properties it exposes (keyed by their `LogicSlotType` enum value).
 */
class SlotDefinition(
    val index: Int,
    val name: String,
    val type: String,
    val properties: Map<Int, PropertyDefinition>
)

class DeviceAspectsBuilder {
    private var aspects: MutableList<Device.AspectEntry> = mutableListOf()

    companion object {
        fun build(builder: DeviceAspectsBuilder.() -> Unit) =
            DeviceAspectsBuilder().also(builder).aspects
    }

    fun define(definition: DeviceAspect) {
        aspects.add(Device.AspectEntry.from(definition))
    }
    fun implement(type: KClass<out DeviceAspect>, definition: DeviceAspect) {
        aspects.add(Device.AspectEntry(type, definition))
    }
}

open class Device(
    val id: Long,
    val properties: Map<Int, PropertyDefinition>,
    val slots: Map<Int, SlotDefinition>,
    val aspects: List<AspectEntry>,
    val prefabId: Long,
    val customName: String? = null,
    val networkId: Long = 0L
) {
    companion object {
        val properties by lazy {
            val propDefs = StationeersEnumData.data.scriptEnums["LogicType"]?.values ?: emptyMap()
            propDefs.mapValues { (_, propDef) -> propDef.value.toInt() }
        }
        val slotProperties by lazy {
            val propDefs = StationeersEnumData.data.scriptEnums["LogicSlotType"]?.values ?: emptyMap()
            propDefs.mapValues { (_, propDef) -> propDef.value.toInt() }
        }
    }

    constructor(id: Long, data: DeviceDataRegistry.Entry, aspects: List<AspectEntry>, customName: String? = null, networkId: Long = 0L) : this(
        id,
        data.logicInfo.properties,
        data.logicInfo.slots,
        aspects,
        data.hash,
        customName,
        networkId
    )

    constructor(id: Long, cls: KClass<*>, aspects: List<AspectEntry>, customName: String? = null, networkId: Long = 0L) : this(
        id,
        DeviceDataRegistry.deviceFor(cls) ?: DeviceDataRegistry.Entry(
            cls.simpleName!!,
            Macros.hash.parse(cls.simpleName!!).uncheckedValue ?: 0,
            DeviceDataRegistry.LogicInfo(emptyMap())
        ),
        aspects,
        customName,
        networkId
    )

    inline fun <reified T : DeviceAspect> aspect() = aspects.find { a -> a.type == T::class }?.value as? T

    open fun tick(
        current: SimulationState,
        deviceNetworks: Map<Long, Pair<Long, Network>>,
        deviceDefinitions: Map<Long, DeviceInfo> = emptyMap()
    ): Sequence<SimulationState.StateChange> = sequence {
        yield(current.change(deviceNetworks, deviceDefinitions) { aspects.forEach { entry -> entry.value.tick(this, id) } })
    }

    open fun tickEnd(state: SimulationStateChangeBuilder) = aspects.forEach { it.value.tickEnd(state, id) }
    open val name get() = customName ?: this::class.simpleName ?: "UnknownDevice"

    open fun initialize(): DeviceState = DeviceState(
        properties.mapValues {
            when (it.value.name) {
                "ReferenceId" -> id.toDouble()
                "PrefabHash" -> prefabId.toDouble()
                "NameHash" -> customName
                    ?.let { n -> Macros.hash.parse(n) }
                    ?.uncheckedValue?.toDouble()
                else -> null
            } ?: it.value.defaultValue
        },
        slots.mapValues { (_, slot) -> slot.properties.mapValues { (_, prop) -> prop.defaultValue } },
        aspects.associate { Pair(it.value.stateClass, it.value.initialize()) }
    )

    class AspectEntry(val type: KClass<out DeviceAspect>, val value: DeviceAspect) {
        companion object {
            fun from(value: DeviceAspect) = AspectEntry(value::class, value)
            fun from(type: KClass<out DeviceAspect>, value: DeviceAspect) = AspectEntry(type, value)
        }
    }
}

class DeviceState(
    val properties: Map<Int, Double>,
    /** slotIndex → (slot logic type id → value). */
    val slots: Map<Int, Map<Int, Double>> = emptyMap(),
    val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State> = emptyMap()
) {
    inline fun <reified T: DeviceAspect.State> aspect() = aspects[T::class] as? T

    class StateChange(
        val properties: Map<Int, SimpleChange<Double>>,
        val slots: Map<Int, Map<Int, SimpleChange<Double>>>,
        val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State.Change>
    ): CompositeChange<DeviceState> {
        override fun compose(source: DeviceState, action: CompositeChangeAction) = action.compose {
            DeviceState(
                compose(source.properties, properties),
                source.slots.mapValues { (index, props) -> slots[index]?.let { compose(props, it) } ?: props },
                compose(source.aspects, aspects)
            )
        }

        operator fun plus(other: StateChange): StateChange {
            val mergedAspects = buildMap {
                for (key in aspects.keys + other.aspects.keys) {
                    val a = aspects[key]
                    val b = other.aspects[key]
                    put(key, when {
                        a == null -> b!!
                        b == null -> a
                        else -> a + b
                    })
                }
            }
            val mergedSlots = buildMap {
                for (index in slots.keys + other.slots.keys) {
                    val a = slots[index] ?: emptyMap()
                    val b = other.slots[index] ?: emptyMap()
                    put(index, a.composeWith(b))
                }
            }
            return StateChange(properties.composeWith(other.properties), mergedSlots, mergedAspects)
        }
    }
}

class DeviceStateChangeBuilder(
    private val previousState: DeviceState
) {
    private val properties = mutableMapOf<Int, SimpleChange<Double>>()
    private val slots = mutableMapOf<Int, MutableMap<Int, SimpleChange<Double>>>()
    private val aspects = mutableMapOf<KClass<out DeviceAspect.State>, DeviceAspect.State.Change.Builder>()

    fun aspect(stateClass: KClass<out DeviceAspect.State>): DeviceAspect.State.Change.Builder {
        val state = previousState.aspects[stateClass] ?: throw AspectNotFoundError()
        return aspects.computeIfAbsent(stateClass) { state.change() }
    }

    fun aspectOrNull(stateClass: KClass<out DeviceAspect.State>): DeviceAspect.State.Change.Builder? =
        previousState.aspects[stateClass]?.let { state -> aspects.computeIfAbsent(stateClass) { state.change() } }

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder> aspect() =
        aspect(S::class) as? B ?: throw AspectTypeMismatchError()

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder> aspectOrNull() =
        aspectOrNull(S::class) as? B

    fun <T> aspect(stateClass: KClass<out DeviceAspect.State>, builder: DeviceAspect.State.Change.Builder.() -> T): T {
        return aspect(stateClass).let(builder)
    }

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder, T>aspect(crossinline builder: B.() -> T): T {
        return aspect(S::class) {
            val target = this
            if (target !is B) throw AspectTypeMismatchError()
            target.builder()
        }
    }

    fun property(id: Int): Double = properties[id]?.nextValue ?: previousState.properties[id] ?: 0.0
    fun setProperty(id: Int, value: Double) {
        val previousValue = previousState.properties[id] ?: throw PropertyNotFoundError()
        properties[id] = SimpleChange(previousValue, value)
    }

    fun slotProperty(slotIndex: Int, propId: Int): Double =
        slots[slotIndex]?.get(propId)?.nextValue
            ?: previousState.slots[slotIndex]?.get(propId)
            ?: 0.0

    /**
     * Current values of every property held by [slotIndex] (item contents), with pending changes
     * applied. Returns `null` when the device has no such slot. Intended for aspect ticks that
     * inspect or move whole items between slots.
     */
    fun slot(slotIndex: Int): Map<Int, Double>? {
        val base = previousState.slots[slotIndex] ?: return null
        val pending = slots[slotIndex] ?: return base
        return base + pending.mapValues { it.value.nextValue }
    }

    /**
     * Writes a single slot property. The slot must exist on the device; any property may be set so
     * aspects can populate item attributes that are not exposed as readable slot logic types.
     */
    fun setSlotProperty(slotIndex: Int, propId: Int, value: Double) {
        if (previousState.slots[slotIndex] == null) throw SlotNotFoundError(slotIndex)
        val previousValue = slotProperty(slotIndex, propId)
        slots.getOrPut(slotIndex) { mutableMapOf() }[propId] = SimpleChange(previousValue, value)
    }

    /** Resets every property of [slotIndex] to 0 — i.e. removes whatever item it holds. */
    fun clearSlot(slotIndex: Int) {
        val slot = previousState.slots[slotIndex] ?: throw SlotNotFoundError(slotIndex)
        for (propId in slot.keys) setSlotProperty(slotIndex, propId, 0.0)
    }

    /** Moves the item in slot [from] into slot [to], clearing [from] afterwards. */
    fun moveSlot(from: Int, to: Int) {
        val source = slot(from) ?: throw SlotNotFoundError(from)
        if (previousState.slots[to] == null) throw SlotNotFoundError(to)
        for ((propId, value) in source) setSlotProperty(to, propId, value)
        clearSlot(from)
    }

    val stateChange get() = DeviceState.StateChange(
        properties,
        slots.mapValues { it.value.toMap() },
        aspects.mapValues { it.value.result }
    )
}