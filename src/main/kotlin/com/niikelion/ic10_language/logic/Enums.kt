package com.niikelion.ic10_language.logic

class Enum(
    val name: String,
    val values: Map<String, Value>
) {
    class Value(val value: Double, val description: String, val deprecated: Boolean): IValueLike {
        override fun getValue(): IUnresolvedValue = NumberValue(value)
    }
}

class Enums {
    companion object {
        val allEnums = StationeersEnumData.data.basicEnums.map {
            Enum(it.value.enumName, it.value.values.mapValues {
                v -> Enum.Value(v.value.value, v.value.description, v.value.deprecated)
            })
        }

        fun get(name: String) = allEnums.find { it.name == name }
    }
}