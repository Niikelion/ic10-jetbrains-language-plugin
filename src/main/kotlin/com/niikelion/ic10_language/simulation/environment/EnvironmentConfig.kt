package com.niikelion.ic10_language.simulation.environment

import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Top-level structure of a `.ic10env` environment config file (YAML or JSON — JSON is a valid subset of YAML).
 *
 * YAML example:
 * ```yaml
 * devices:
 *   - name: Controller
 *     source: controller.ic10
 *   - name: Memory
 *     prefab: StructureMemory
 *   - name: Autolathe
 *     prefab: StructureAutolathe
 *     class: crafter
 *     properties:
 *       Setting: 5.0
 * networks:
 *   - id: 0
 *     dataConnected: [1, 2, 3]
 * ```
 *
 * JSON example (also valid):
 * ```json
 * {
 *   "devices": [
 *     { "id": 1, "name": "Controller", "source": "controller.ic10" },
 *     { "id": 2, "name": "Memory",     "prefab": "StructureMemory" }
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
    val devices:  List<DeviceConfig>  = emptyList(),
    val networks: List<NetworkConfig> = emptyList()
) {
    companion object {
        @Throws(IllegalArgumentException::class)
        fun parse(text: String): EnvironmentConfig {
            val raw = try {
                Yaml().load<Map<String, Any>>(text)
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to parse environment config: ${e.message}", e)
            } ?: throw IllegalArgumentException("Empty or null environment config")
            return fromMap(raw).validate()
        }

        @Throws(IllegalArgumentException::class)
        fun load(file: File): EnvironmentConfig = parse(file.readText())

        private fun fromMap(map: Map<String, Any>): EnvironmentConfig {
            val devices = (map["devices"] as? List<*>)?.mapNotNull {
                (it as? Map<*, *>)?.let { m -> DeviceConfig.fromMap(m) }
            } ?: emptyList()
            val networks = (map["networks"] as? List<*>)?.mapNotNull {
                (it as? Map<*, *>)?.let { m -> NetworkConfig.fromMap(m) }
            } ?: emptyList()
            return EnvironmentConfig(devices, networks)
        }
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
    val id:          Long?               = null,
    val name:        String?             = null,
    /** Path to an .ic10 source file, relative to the .ic10env file. Mutually exclusive with [prefab]. */
    val source:      String?             = null,
    /** Stationpedia prefab name (e.g. "StructureMemory"). Mutually exclusive with [source]. */
    val prefab:      String?             = null,
    /**
     * Device class override. Supported values:
     *  - `"crafter"` — fabricator device (Autolathe, PipeBender, …) using [StructureCircuitHousing]
     *  - omitted / anything else — passive device with no active logic
     * Ignored when [source] is present (IC10 devices always use [StructureCircuitHousing]).
     */
    val deviceClass: String?             = null,
    /** Initial property values by logic-type name (e.g. `Setting: 5.0`). */
    val properties:  Map<String, Double> = emptyMap()
) {
    companion object {
        fun fromMap(map: Map<*, *>): DeviceConfig = DeviceConfig(
            id          = (map["id"] as? Number)?.toLong(),
            name        = map["name"] as? String,
            source      = map["source"] as? String,
            prefab      = map["prefab"] as? String,
            deviceClass = map["class"] as? String,
            properties  = (map["properties"] as? Map<*, *>)
                ?.entries
                ?.mapNotNull { (k, v) ->
                    val key   = k as? String ?: return@mapNotNull null
                    val value = (v as? Number)?.toDouble() ?: return@mapNotNull null
                    key to value
                }
                ?.toMap()
                ?: emptyMap()
        )
    }

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
    val id:            Long       = 0,
    val dataConnected: List<Long> = emptyList(),
    val softConnected: List<Long> = emptyList()
) {
    companion object {
        fun fromMap(map: Map<*, *>): NetworkConfig {
            fun longList(key: String) = (map[key] as? List<*>)
                ?.mapNotNull { (it as? Number)?.toLong() }
                ?: emptyList()
            return NetworkConfig(
                id            = (map["id"] as? Number)?.toLong() ?: 0L,
                dataConnected = longList("dataConnected"),
                softConnected = longList("softConnected")
            )
        }
    }
}
