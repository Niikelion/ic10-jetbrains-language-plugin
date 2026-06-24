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
    val properties: Map<Int, PropertyDefinition>,
    val slots: Map<Int, SlotDefinition> = emptyMap()
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

/**
 * An item occupying a slot. It carries its full set of properties (keyed by `LogicSlotType` value),
 * independent of which subset the holding slot exposes — so moving an item between slots preserves
 * properties that one slot cannot read but another can.
 */
data class Item(val properties: Map<Int, Double> = emptyMap())

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
        slots.mapValues { _ -> null as Item? },
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
    /** slotIndex → the item it holds, or null when empty. The key set is the device's fixed slots. */
    val slots: Map<Int, Item?> = emptyMap(),
    val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State> = emptyMap()
) {
    inline fun <reified T: DeviceAspect.State> aspect() = aspects[T::class] as? T

    class StateChange(
        val properties: Map<Int, SimpleChange<Double>>,
        /** Whole-item replacement per slot: previous item ↔ next item (either may be null). */
        val slots: Map<Int, SimpleChange<Item?>>,
        val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State.Change>
    ): CompositeChange<DeviceState> {
        override fun compose(source: DeviceState, action: CompositeChangeAction) = action.compose {
            DeviceState(
                compose(source.properties, properties),
                // Per-slot single-value compose: emptying a slot produces a null item, which the
                // map-level perform helper would mistake for "no change", so apply changes by key.
                source.slots.mapValues { (index, item) -> compose(item, slots[index]) },
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
            return StateChange(
                properties.composeWith(other.properties),
                slots.composeWith(other.slots),
                mergedAspects
            )
        }
    }
}

class DeviceStateChangeBuilder(
    private val previousState: DeviceState
) {
    private val properties = mutableMapOf<Int, SimpleChange<Double>>()
    private val slots = mutableMapOf<Int, SimpleChange<Item?>>()
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

    private fun hasSlot(slotIndex: Int) =
        slots.containsKey(slotIndex) || previousState.slots.containsKey(slotIndex)

    private fun putSlot(slotIndex: Int, item: Item?) {
        slots[slotIndex] = SimpleChange(previousState.slots[slotIndex], item)
    }

    /**
     * The item currently in [slotIndex] (pending changes applied), or null when the slot is empty or
     * the device has no such slot. Intended for aspect ticks that inspect or move whole items.
     */
    fun slot(slotIndex: Int): Item? =
        if (slots.containsKey(slotIndex)) slots[slotIndex]!!.nextValue else previousState.slots[slotIndex]

    /** Reads a single property of the item held by [slotIndex]; 0 when the slot is empty. */
    fun slotProperty(slotIndex: Int, propId: Int): Double =
        slot(slotIndex)?.properties?.get(propId) ?: 0.0

    /**
     * Writes a single property of the item in [slotIndex], materialising an item if the slot was
     * empty. The slot must exist on the device. Any property may be set so aspects can populate item
     * attributes that are not exposed as readable slot logic types.
     */
    fun setSlotProperty(slotIndex: Int, propId: Int, value: Double) {
        if (!hasSlot(slotIndex)) throw SlotNotFoundError(slotIndex)
        val item = slot(slotIndex) ?: Item()
        putSlot(slotIndex, Item(item.properties + (propId to value)))
    }

    /** Empties [slotIndex], removing whatever item it holds. */
    fun clearSlot(slotIndex: Int) {
        if (!hasSlot(slotIndex)) throw SlotNotFoundError(slotIndex)
        putSlot(slotIndex, null)
    }

    /** Moves the item in slot [from] into slot [to], leaving [from] empty. */
    fun moveSlot(from: Int, to: Int) {
        if (!hasSlot(from)) throw SlotNotFoundError(from)
        if (!hasSlot(to)) throw SlotNotFoundError(to)
        val item = slot(from)
        putSlot(to, item)
        putSlot(from, null)
    }

    val stateChange get() = DeviceState.StateChange(
        properties,
        slots.toMap(),
        aspects.mapValues { it.value.result }
    )
}