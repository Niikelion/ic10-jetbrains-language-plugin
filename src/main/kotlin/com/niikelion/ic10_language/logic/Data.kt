package com.niikelion.ic10_language.logic

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.devices.PropertyDefinition
import com.niikelion.ic10_language.logic.devices.SlotDefinition
import kotlin.reflect.KClass

data class StationeersEnumData(
    val scriptEnums: Map<String, StationeersEnum>,
    val basicEnums: Map<String, StationeersEnum>
) {
    data class StationeersEnum(
        val enumName: String,
        val values: Map<String, Value>
    ) {
        data class Value(
            val value: Double,
            val deprecated: Boolean,
            val description: String
        )
    }

    companion object {
        val data: StationeersEnumData by lazy {
            val jsonText = StationeersEnumData::class.java.classLoader
                .getResource("data/enums.json")?.readText() ?: error("enums.json not found")
            Gson().fromJson(jsonText, StationeersEnumData::class.java)
        }
    }
}

data class StationeersRegistryData(
    val scriptCommands: Map<String, StationeersScriptCommand>,
    val pages: List<StationpediaPage>,
    val reagents: Map<String, StationeersReagent> = emptyMap()
) {
    data class StationeersScriptCommand(
        val desc: String,
        val example: String
    )

    data class StationeersReagent(
        val Hash: Long,
        val Unit: String,
        val Sources: Map<String, Double>?
    )

    data class StationpediaPage(
        val Key: String,
        val PrefabName: String?,
        val PrefabHash: Long?,
        val LogicInfo: StationeersLogicInfo?,
        val ResourceConsumer: StationeersResourceConsumer?,
        val Device: StationeersDeviceInfo?,
        val SlotInserts: List<StationeersSlotInsert>?
    ) {
        data class StationeersLogicInfo(
            val LogicTypes: Map<String, String>,
            val LogicSlotTypes: Map<String, Map<String, String>> = emptyMap()
        )
        data class StationeersSlotInsert(
            val SlotName: String?,
            val SlotType: String?,
            val SlotIndex: String?
        )
        data class StationeersResourceConsumer(
            val ConsumedResources: List<String>,
            val ProcessedReagents: List<Long>
        )
        data class StationeersDeviceInfo(
            val Fabricator: StationneersFabricator?
        )
        data class StationneersFabricator(
            val Recipes: Map<String, JsonObject>?
        )
    }

    companion object {
        val data: StationeersRegistryData by lazy {
            val jsonText = StationeersRegistryData::class.java.classLoader
                .getResource("data/stationpedia.json")?.readText() ?: error("stationpedia.json not found")
            Gson().fromJson(jsonText, StationeersRegistryData::class.java)
        }
    }
}

object DeviceDataRegistry {
    class Entry(
        val name: String,
        val hash: Long,
        val logicInfo: LogicInfo
    )

    class LogicInfo(
        val properties: Map<Int, PropertyDefinition>,
        val slots: Map<Int, SlotDefinition> = emptyMap()
    )

    val devices: Map<String, Entry> by lazy {
        val pages = StationeersRegistryData.data.pages.mapNotNull { page ->
            if (page.PrefabName == null || page.PrefabHash == null) return@mapNotNull null
            return@mapNotNull Entry(
                page.PrefabName,
                page.PrefabHash,
                LogicInfo(
                    page.LogicInfo?.LogicTypes?.mapNotNull { (name, mode) ->
                        Device.properties[name]?.let {
                            Pair(it, PropertyDefinition(name, mode.contains("Read"), mode.contains("Write")))
                        }
                    }?.associate { it } ?: emptyMap(),
                    buildSlots(page)
                )
            )
        }

        pages.associateBy { it.name }
    }

    /**
     * Builds the slot table for [page] by combining the per-slot logic types
     * (`LogicInfo.LogicSlotTypes`, keyed by slot index) with the slot names and item types
     * declared in `SlotInserts`.
     */
    private fun buildSlots(page: StationeersRegistryData.StationpediaPage): Map<Int, SlotDefinition> {
        val logicSlots = page.LogicInfo?.LogicSlotTypes ?: emptyMap()
        val inserts = page.SlotInserts ?: emptyList()
        val insertsByIndex = inserts.mapNotNull { insert ->
            insert.SlotIndex?.toIntOrNull()?.let { it to insert }
        }.toMap()

        val indices = logicSlots.keys.mapNotNull { it.toIntOrNull() } + insertsByIndex.keys
        if (indices.isEmpty()) return emptyMap()

        return indices.toSortedSet().associateWith { index ->
            val properties = logicSlots[index.toString()]?.mapNotNull { (name, mode) ->
                Device.slotProperties[name]?.let { id ->
                    id to PropertyDefinition(name, mode.contains("Read"), mode.contains("Write"))
                }
            }?.toMap() ?: emptyMap()
            val insert = insertsByIndex[index]
            SlotDefinition(index, insert?.SlotName ?: "", insert?.SlotType ?: "", properties)
        }
    }

    fun deviceFor(cls: KClass<*>) = cls.simpleName?.let { devices[it] }
}