package com.niikelion.ic10_language

data class Constant(val name: String, val description: String, val value: Number? = null) {
    private val htmlForName = "<b>$name</b>${if (value != null) " = <span style=\"color: cyan;\">$value</span>" else ""}"
    private val htmlForDescription = "<i>$description</i>"
    private fun wrapInHtml(content: String) = "<html><body>$content</body></html>"

    fun getTooltipText(): String = wrapInHtml("$htmlForName<br/>$htmlForDescription")
}

object Constants {
    private val constants = arrayOf(
        Constant("nan", "A constant representing 'not a number'. This constant technically provides a 'quiet' NaN that does not halt execution."),
        Constant("pinf", "Positive infinity value"),
        Constant("ninf", "Negative infinity value"),
        Constant("pi", "A constant representing the PI value provided in double precision", 3.14159265358979),
        Constant("deg2rad", "Degrees to radians conversion constant", 0.0174532923847437),
        Constant("rad2deg", "Radians to degrees conversion constant", 57.2957801818848),
        Constant("epsilon", "A constant representing the smallest value representable in double precision"),
        Constant("Contents", "reagentMode that can be used to get amount currently in the machine", 0),
        Constant("Required", "reagentMode that can be used to get amount missing", 1),
        Constant("Recipe", "reagentMode that can be used to get amount required by the recipe", 2),
        Constant("Average", "batchMode that is used to calculate average", 0),
        Constant("Sum", "batchMode that is used to calculate sum", 1),
        Constant("Minimum", "batchMode that is used to calculate minimum", 2),
        Constant("Maximum", "batchMode that is used to calculate maximum", 3),
    ).associateBy { it.name }

    fun get(name: String) = constants[name]
    val all get() = constants.values
}