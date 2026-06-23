package com.niikelion.ic10_language.logic

interface IProgramState {
    fun get(register: Register): Double
}

sealed interface IUnresolvedValue {
    fun resolve(state: IProgramState): IValue?
}
sealed interface IValue: IUnresolvedValue {
    override fun resolve(state: IProgramState) = this
}

class NumberValue(val value: Double): IValue
class RegisterValue(val value: Register): IValue
class DeviceValue(val value: DeviceSlot): IValue
class NameValue(val name: String): IValue
class NetworkRefValue(val slot: DeviceSlot, val portIndex: Int): IValue

class RegisterReferenceValue(val startingValue: Register, val hoops: Int): IUnresolvedValue {
    companion object {
        const val MAX_INDIRECTION = 10

        fun resolve(startingValue: Register, hoops: Int, state: IProgramState): RegisterValue? {
            if (hoops > MAX_INDIRECTION)
                throw Exception("Register reference indirection depth $hoops exceeds maximum of $MAX_INDIRECTION")
            if (hoops <= 0) return RegisterValue(startingValue)
            val id = state.get(startingValue)
            return Registers.get(id.toInt())?.let { resolve(it, hoops-1, state) }
        }
        fun fromString(str: String) = Registers.parseReference(str)?.let { RegisterReferenceValue(it.first, it.second) }
    }

    override fun resolve(state: IProgramState) = resolve(startingValue, hoops, state)
}
class DeviceReferenceValue(val startingValue: Register, val hoops: Int): IUnresolvedValue {
    companion object {
        fun fromString(str: String) = DeviceSlots.parseReference(str)?.let { DeviceReferenceValue(it.first, it.second) }
    }

    override fun resolve(state: IProgramState): IValue? {
        val targetRegister = RegisterReferenceValue.resolve(startingValue, hoops, state)
        return targetRegister?.let { state.get(it.value).toInt() }?.let { DeviceSlots.get(it) }?.let { DeviceValue(it) }
    }
}