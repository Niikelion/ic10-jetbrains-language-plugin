package com.niikelion.ic10_language.logic

data class Network(
    val dataConnected: Set<Long>,
    val softConnected: Set<Long>       // power/cable-only; Channel0-7 access only
) {
    companion object {
        private val CHANNEL_PROPERTY_IDS = (165..172).toSet()   // Channel0-Channel7

        fun isChannelProperty(logicTypeId: Int) = logicTypeId in CHANNEL_PROPERTY_IDS

        fun single(deviceIds: Collection<Long>): Network =
            Network(dataConnected = deviceIds.toSet(), softConnected = emptySet())
    }

    /** Used by future batch-op instructions to check per-property access. */
    fun canAccessProperty(observerId: Long, targetId: Long, propertyId: Int): Boolean {
        if (observerId == targetId) return true
        return when {
            targetId in dataConnected && observerId in dataConnected -> true
            targetId in softConnected && observerId in dataConnected -> isChannelProperty(propertyId)
            else -> false
        }
    }

    /** All device IDs visible to [observerId] for batch discovery. */
    fun networkDevicesVisibleTo(observerId: Long): Set<Long> {
        if (observerId !in dataConnected) return emptySet()
        return dataConnected + softConnected
    }
}
