package com.niikelion.ic10_language.logic.state

class NetworkState(val channels: Map<Int, Double> = (0..7).associateWith { 0.0 })

class NetworkStateChange(val channels: Map<Int, SimpleChange<Double>>) : CompositeChange<NetworkState> {
    override fun compose(source: NetworkState, action: CompositeChangeAction) = action.compose {
        NetworkState(compose(source.channels, channels))
    }
}

class NetworkStateChangeBuilder(private val previousState: NetworkState) {
    private val channels = mutableMapOf<Int, SimpleChange<Double>>()

    fun readChannel(index: Int): Double = channels[index]?.nextValue ?: previousState.channels[index] ?: 0.0

    fun writeChannel(index: Int, value: Double) {
        channels[index] = SimpleChange(previousState.channels[index] ?: 0.0, value)
    }

    val result get() = NetworkStateChange(channels)
}
