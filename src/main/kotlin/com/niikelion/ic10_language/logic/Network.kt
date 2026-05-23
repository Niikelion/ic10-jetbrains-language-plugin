package com.niikelion.ic10_language.logic

data class Network(
    val dataConnected: Set<Long>,
    val softConnected: Set<Long>       // power/cable-only; channel access via NetworkState only
) {
    companion object {
        fun single(deviceIds: Collection<Long>): Network =
            Network(dataConnected = deviceIds.toSet(), softConnected = emptySet())
    }

    fun canAccess(observerId: Long, targetId: Long): Boolean =
        observerId == targetId || (targetId in dataConnected && observerId in dataConnected)

    /** All device IDs visible to [observerId] for batch discovery (data network only). */
    fun networkDevicesVisibleTo(observerId: Long): Set<Long> {
        if (observerId !in dataConnected) return emptySet()
        return dataConnected
    }
}
