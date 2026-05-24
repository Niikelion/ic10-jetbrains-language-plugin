package com.niikelion.ic10_language.logic.devices

import com.niikelion.ic10_language.logic.DeviceDataRegistry
import com.niikelion.ic10_language.logic.Macros
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

//class SlotDefinition //TODO: do

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
    }

    constructor(id: Long, data: DeviceDataRegistry.Entry, aspects: List<AspectEntry>, customName: String? = null, networkId: Long = 0L) : this(
        id,
        data.logicInfo.properties,
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
    val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State>
) {
    inline fun <reified T: DeviceAspect.State> aspect() = aspects[T::class] as? T

    class StateChange(
        val properties: Map<Int, SimpleChange<Double>>,
        val aspects: Map<KClass<out DeviceAspect.State>, DeviceAspect.State.Change>
    ): CompositeChange<DeviceState> {
        override fun compose(source: DeviceState, action: CompositeChangeAction) = action.compose {
            DeviceState(
                compose(source.properties, properties),
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
            return StateChange(properties.composeWith(other.properties), mergedAspects)
        }
    }
}

class DeviceStateChangeBuilder(
    private val previousState: DeviceState
) {
    private val properties = mutableMapOf<Int, SimpleChange<Double>>()
    private val aspects = mutableMapOf<KClass<out DeviceAspect.State>, DeviceAspect.State.Change.Builder>()

    fun aspect(stateClass: KClass<out DeviceAspect.State>): DeviceAspect.State.Change.Builder {
        val state = previousState.aspects[stateClass] ?: throw IllegalArgumentException("Cannot access device aspect that does not exist")
        return aspects.computeIfAbsent(stateClass) { state.change() }
    }

    fun aspectOrNull(stateClass: KClass<out DeviceAspect.State>): DeviceAspect.State.Change.Builder? =
        previousState.aspects[stateClass]?.let { state -> aspects.computeIfAbsent(stateClass) { state.change() } }

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder> aspect() =
        aspect(S::class) as? B ?: throw IllegalArgumentException("Aspect state change builder type missmatch")

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder> aspectOrNull() =
        aspectOrNull(S::class) as? B

    fun <T> aspect(stateClass: KClass<out DeviceAspect.State>, builder: DeviceAspect.State.Change.Builder.() -> T): T {
        return aspect(stateClass).let(builder)
    }

    inline fun <reified S: DeviceAspect.State, reified B: DeviceAspect.State.Change.Builder, T>aspect(crossinline builder: B.() -> T): T {
        return aspect(S::class) {
            val target = this
            if (target !is B) throw IllegalArgumentException("Aspect state change builder type missmatch")
            target.builder()
        }
    }

    fun property(id: Int): Double = properties[id]?.nextValue ?: previousState.properties[id] ?: 0.0
    fun setProperty(id: Int, value: Double) {
        val previousValue = previousState.properties[id] ?: throw IllegalArgumentException("Cannot access device property that does not exist")
        properties[id] = SimpleChange(previousValue, value)
    }

    val stateChange get() = DeviceState.StateChange(properties, aspects.mapValues { it.value.result })
}