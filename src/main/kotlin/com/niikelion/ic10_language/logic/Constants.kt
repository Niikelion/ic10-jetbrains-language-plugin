package com.niikelion.ic10_language.logic

import kotlin.collections.map

data class Constant(val name: String, val description: String, val value: Double)

object Constants {
    private val builtinConstants = arrayOf(
        Constant("nan", "A constant representing 'not a number'. This constant technically provides a 'quiet' NaN that does not halt execution.", Double.NaN),
        Constant("pinf", "Positive infinity value", Double.POSITIVE_INFINITY),
        Constant("ninf", "Negative infinity value", Double.NEGATIVE_INFINITY),
        Constant("pi", "A constant representing the PI value provided in double precision", 3.14159265358979),
        Constant("deg2rad", "Degrees to radians conversion constant", 0.0174532923847437),
        Constant("rad2deg", "Radians to degrees conversion constant", 57.2957801818848),
        Constant("epsilon", "A constant representing the smallest value representable in double precision", Math.ulp(1.0)),

        Constant("Contents", "reagentMode that can be used to get amount currently in the machine", 0.0),
        Constant("Required", "reagentMode that can be used to get amount missing", 1.0),
        Constant("Recipe", "reagentMode that can be used to get amount required by the recipe", 2.0),
        Constant("Average", "batchMode that is used to calculate average", 0.0),
        Constant("Sum", "batchMode that is used to calculate sum", 1.0),
        Constant("Minimum", "batchMode that is used to calculate minimum", 2.0),
        Constant("Maximum", "batchMode that is used to calculate maximum", 3.0),
    )
    private val loadedConstants = StationeersEnumData.data.scriptEnums.values
        .flatMap { it.values.entries }
        .map { Constant(it.key, it.value.description, it.value.value) }
        .toTypedArray()
    val all = arrayOf(*builtinConstants, *loadedConstants).associateBy { it.name }

    fun get(name: String) = all[name]
}