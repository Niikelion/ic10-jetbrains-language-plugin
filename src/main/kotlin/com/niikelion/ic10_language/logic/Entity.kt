package com.niikelion.ic10_language.logic

import com.niikelion.ic10_language.psi.Ic10Operation
import com.niikelion.ic10_language.psi.Ic10Value
import com.niikelion.ic10_language.psi.elements.Ic10LabelElement
import com.niikelion.ic10_language.psi.elements.Ic10NamedElement

class Entity(
    val name: String,
    val resolved: Boolean,
    val targetValue: IUnresolvedValue?,
    val builtIn: Boolean,
    val source: Ic10NamedElement?,
    val references: Ic10Value? = null
): IValueLike {
    companion object {
        private val registerEntities = Registers.all.map { from(it) } +
                Registers.aliases.map { (name, target) -> builtin(name, RegisterValue(target)) }
        private val deviceEntities = DeviceSlots.all.map { from(it) }
        private val constantEntities = Constants.all.values.map { from(it) }
        val builtinEntities = (registerEntities + deviceEntities + constantEntities)

        fun builtin(name: String, value: IUnresolvedValue) =
            Entity(name, true, value, true, source = null)

        fun from(register: Register) = builtin(register.name, RegisterValue(register))
        fun from(deviceSlot: DeviceSlot) = builtin(deviceSlot.name, DeviceValue(deviceSlot))
        fun from(constant: Constant) = builtin(constant.name, NumberValue(constant.value))
        fun from(label: Ic10LabelElement) = Entity(label.name, true, label.getValue(), false, source = label)
        fun from(name: String, value: Ic10Value): Entity {
            val reference = value.referenceName
            return Entity(name, reference == null, if (reference == null) value.value else null, false, reference, value)
        }
        fun from(operation: Ic10Operation): Entity? {
            if (operation.valueList.size != 2) return null
            val instruction = operation.instruction ?: return null

            val definition = operation.valueList[0]?.referenceName ?: return null
            val value = operation.valueList[1] ?: return null

            val name = definition.name

            return when(instruction.name) {
                "define" -> from(name, value)
                "alias" -> Entity(name, false, null, false, definition, value)
                else -> null
            }
        }
    }

    override fun getValue() = targetValue

    fun resolve(value: IUnresolvedValue?) = Entity(name, true, value, builtIn, source, references)
}