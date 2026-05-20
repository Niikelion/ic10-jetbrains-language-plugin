package com.niikelion.ic10_language.logic

import com.google.gson.Gson
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.devices.PropertyDefinition
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
    val pages: List<StationpediaPage>
) {
    data class StationeersScriptCommand(
        val desc: String,
        val example: String
    )

    data class StationpediaPage(
        val Key: String,
        val PrefabName: String?,
        val PrefabHash: Long?,
        val LogicInfo: StationeersLogicInfo?
    ) {
        data class StationeersLogicInfo(
            val LogicTypes: Map<String, String>
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
        val properties: Map<Int, PropertyDefinition>
    )

    val devices: Map<String, Entry> by lazy {
        val pages = StationeersRegistryData.data.pages.mapNotNull { page ->
            if (page.PrefabName == null || page.PrefabHash == null) return@mapNotNull null
            return@mapNotNull Entry(
                page.PrefabName,
                page.PrefabHash,
                page.LogicInfo?.let { info ->
                    LogicInfo(
                        info.LogicTypes.mapNotNull { (name, mode) ->
                            Device.properties[name]?.let {
                                Pair(it, PropertyDefinition(name, mode.contains("Read"), mode.contains("Write")))
                            }
                        }.associate { it },
                    )
                } ?: LogicInfo(emptyMap())
            )
        }

        pages.associateBy { it.name }
    }

    fun deviceFor(cls: KClass<*>) = cls.simpleName?.let { devices[it] }
}