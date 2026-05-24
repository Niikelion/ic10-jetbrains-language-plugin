package com.niikelion.ic10_language.simulation.environment

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * Top-level structure of a `.ic10env` environment config file.
 *
 * Example:
 * ```json
 * {
 *   "devices": [
 *     { "id": 1, "name": "Controller", "source": "controller.ic10" },
 *     { "id": 2, "name": "Sensor",     "source": "sensor.ic10" }
 *   ],
 *   "networks": [
 *     { "id": 0, "dataConnected": [1, 2] }
 *   ]
 * }
 * ```
 *
 * `networks` is optional — if omitted, all devices are placed on a single data network.
 * Device `id` is optional — if omitted, IDs are assigned sequentially starting from 1.
 * `name` is optional and used only for display in the debugger.
 */
data class EnvironmentConfig(
    @SerializedName("devices")  val devices:  List<DeviceConfig>  = emptyList(),
    @SerializedName("networks") val networks: List<NetworkConfig> = emptyList()
) {
    companion object {
        private val gson = Gson()

        @Throws(JsonSyntaxException::class, IllegalArgumentException::class)
        fun parse(json: String): EnvironmentConfig {
            val raw = gson.fromJson(json, EnvironmentConfig::class.java)
                ?: throw IllegalArgumentException("Empty or null environment config")
            return raw.validate()
        }

        @Throws(JsonSyntaxException::class, IllegalArgumentException::class)
        fun load(file: File): EnvironmentConfig = parse(file.readText())
    }

    private fun validate(): EnvironmentConfig {
        require(devices.isNotEmpty()) { "Environment config must declare at least one device" }
        devices.forEachIndexed { i, d -> d.validate(i) }
        return this
    }

    /**
     * Assign sequential IDs (1, 2, …) to devices that have no explicit `id`,
     * skipping IDs already claimed by explicit entries.
     */
    fun resolveIds(): EnvironmentConfig {
        val taken = devices.mapNotNull { it.id }.toMutableSet()
        var next = 1L
        fun nextFree(): Long {
            while (next in taken) next++
            return next.also { taken.add(it); next++ }
        }
        val resolved = devices.map { d -> if (d.id != null) d else d.copy(id = nextFree()) }
        return copy(devices = resolved)
    }
}

data class DeviceConfig(
    @SerializedName("id")         val id:          Long?               = null,
    @SerializedName("name")       val name:        String?             = null,
    /** Path to an .ic10 source file, relative to the .ic10env file. Mutually exclusive with [prefab]. */
    @SerializedName("source")     val source:      String?             = null,
    /** Stationpedia prefab name (e.g. "StructureMemory"). Mutually exclusive with [source]. */
    @SerializedName("prefab")     val prefab:      String?             = null,
    /**
     * Device class override. Supported values:
     *  - `"crafter"` — fabricator device (Autolathe, PipeBender, …) using [StructureCrafter]
     *  - omitted / anything else — passive device with no active logic
     * Ignored when [source] is present (IC10 devices always use [StructureCircuitHousing]).
     */
    @SerializedName("class")      val deviceClass: String?             = null,
    /** Initial property values by logic-type name (e.g. `"Setting": 5.0`). */
    @SerializedName("properties") val properties:  Map<String, Double> = emptyMap()
) {
    fun validate(index: Int) {
        require(source != null || prefab != null) {
            "Device at index $index must specify either \"source\" or \"prefab\""
        }
        require(source == null || prefab == null) {
            "Device at index $index cannot specify both \"source\" and \"prefab\""
        }
    }
}

data class NetworkConfig(
    @SerializedName("id")            val id:            Long        = 0,
    @SerializedName("dataConnected") val dataConnected: List<Long>  = emptyList(),
    @SerializedName("softConnected") val softConnected: List<Long>  = emptyList()
)
