package com.niikelion.ic10_language.logic

object Reagents {
    val nameByHash: Map<Long, String> by lazy {
        StationeersRegistryData.data.reagents.entries.associate { (name, reagent) -> reagent.Hash to name }
    }

    private val prefabHashByName: Map<String, Long> by lazy {
        StationeersRegistryData.data.pages
            .mapNotNull { page -> page.PrefabName?.let { it to (page.PrefabHash ?: 0L) } }
            .toMap()
    }

    private val sourcesByHash: Map<Long, Set<String>> by lazy {
        StationeersRegistryData.data.reagents
            .values
            .flatMap { reagent -> reagent.Sources?.keys?.map { reagent.Hash to it } ?: emptyList() }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.toSet() }
    }

    private val consumedByDevicePrefabHash: Map<Long, List<String>> by lazy {
        StationeersRegistryData.data.pages
            .mapNotNull { page ->
                page.PrefabHash?.let { hash ->
                    page.ResourceConsumer?.let { rc -> hash to rc.ConsumedResources }
                }
            }
            .toMap()
    }

    // devicePrefabHash → itemPrefabHash → reagentHash → amount
    private val recipesByDevicePrefabHash: Map<Long, Map<Long, Map<Long, Double>>> by lazy {
        val reagentHashByName = StationeersRegistryData.data.reagents.mapValues { it.value.Hash }
        val knownReagentNames = reagentHashByName.keys

        StationeersRegistryData.data.pages
            .mapNotNull { page ->
                val deviceHash = page.PrefabHash ?: return@mapNotNull null
                val recipes = page.Device?.Fabricator?.Recipes ?: return@mapNotNull null

                val byItem = recipes.mapNotNull { (itemPrefabName, recipeJson) ->
                    val itemHash = prefabHashByName[itemPrefabName] ?: return@mapNotNull null
                    val byReagent = recipeJson.entrySet()
                        .mapNotNull { (fieldName, element) ->
                            if (fieldName !in knownReagentNames) return@mapNotNull null
                            if (!element.isJsonPrimitive || !element.asJsonPrimitive.isNumber) return@mapNotNull null
                            val amount = element.asDouble
                            if (amount == 0.0) return@mapNotNull null
                            (reagentHashByName[fieldName] ?: return@mapNotNull null) to amount
                        }.toMap()
                    if (byReagent.isEmpty()) return@mapNotNull null
                    itemHash to byReagent
                }.toMap()

                if (byItem.isEmpty()) return@mapNotNull null
                deviceHash to byItem
            }
            .toMap()
    }

    fun findSourcePrefabHash(devicePrefabHash: Long, targetReagentHash: Long): Long? {
        val consumed = consumedByDevicePrefabHash[devicePrefabHash] ?: return null
        val sources = sourcesByHash[targetReagentHash] ?: return null
        val match = consumed.firstOrNull { it in sources } ?: return null
        return prefabHashByName[match]
    }

    fun recipesFor(devicePrefabHash: Long): Map<Long, Map<Long, Double>> =
        recipesByDevicePrefabHash[devicePrefabHash] ?: emptyMap()

    fun recipeFor(devicePrefabHash: Long, itemPrefabHash: Long): Map<Long, Double>? =
        recipesByDevicePrefabHash[devicePrefabHash]?.get(itemPrefabHash)
}
