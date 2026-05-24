package com.niikelion.ic10_language.simulation

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.logic.CompilationError
import com.niikelion.ic10_language.logic.Context
import com.niikelion.ic10_language.logic.Network
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.devices.StructureCircuitHousing
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.psi.Ic10File
import com.niikelion.ic10_language.simulation.environment.DeviceFactory
import com.niikelion.ic10_language.simulation.environment.EnvironmentConfig
import com.niikelion.ic10_language.simulation.environment.applyPropertyOverrides
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.OutputStream

private class Terminated: ProcessHandler() {
    init {
        startNotify()
        notifyProcessTerminated(1)
    }

    override fun destroyProcessImpl() = notifyProcessTerminated(1)
    override fun detachProcessImpl() = notifyProcessTerminated(1)
    override fun detachIsDefault(): Boolean = false
    override fun getProcessInput(): OutputStream? = null

}

@Service(Service.Level.PROJECT)
class SimulationService(private val project: Project) {
    private val process = MutableStateFlow<SimulationProcess?>(null)

    private fun startNewProcess(context: Context, initialState: SimulationState? = null): SimulationProcess {
        process.value?.run { stop() }
        return SimulationProcess(context, initialState).also {
            process.value = it
            it.addProcessListener(object: ProcessListener {
                override fun processTerminated(event: ProcessEvent) {
                    process.value = null
                }
            })
        }
    }

    fun start(console: ConsoleView, filePaths: List<String>): ProcessHandler {
        if (filePaths.isEmpty()) {
            console.print("ERROR: No source files configured.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val devices = mutableListOf<StructureCircuitHousing>()
        var nextId = 1L

        for (filePath in filePaths) {
            try {
                val file = LocalFileSystem.getInstance().findFileByPath(filePath)?.findPsiFile(project)
                    ?: ReadAction.compute<PsiFile, Throwable> {
                        val text = File(filePath).readText()
                        PsiFileFactory.getInstance(project).createFileFromText("Program.ic10", Ic10FileType.Instance, text)
                    }

                if (file !is Ic10File) {
                    console.print("ERROR: $filePath is not an IC10 file\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                    continue
                }

                console.print("Compiling $filePath\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                val code = ProgramCode.compile(file)
                devices.add(StructureCircuitHousing(nextId++, code))
            } catch (t: Throwable) {
                if (t is CompilationError) {
                    val line = t.target?.let { Ic10PsiUtils.getLineNumber(it) }?.let { "($it)" } ?: ""
                    console.print("Compilation error$line in $filePath: ${t.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                } else {
                    console.print("ERROR: Could not load $filePath: ${t.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
                }
            }
        }

        if (devices.isEmpty()) {
            console.print("ERROR: No devices could be compiled.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        console.print("Starting simulation with ${devices.size} device(s)\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        val network = Network.single(devices.map { it.id })
        return startNewProcess(Context(devices, mapOf(0L to network)))
    }

    fun startFromEnvironment(console: ConsoleView, configFilePath: String): ProcessHandler {
        if (configFilePath.isBlank()) {
            console.print("ERROR: No environment config file configured.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val configFile = File(configFilePath)
        if (!configFile.exists()) {
            console.print("ERROR: Environment config not found: $configFilePath\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val config = try {
            EnvironmentConfig.load(configFile).resolveIds()
        } catch (e: Exception) {
            console.print("ERROR: Failed to parse environment config: ${e.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val baseDir = configFile.parentFile
        val deviceById = mutableMapOf<Long, Device>()
        val propertyOverrides = mutableMapOf<Long, Map<String, Double>>()

        for (deviceConfig in config.devices) {
            val id = deviceConfig.id!! // guaranteed by resolveIds()
            val (device, overrides) = DeviceFactory.create(deviceConfig, id, baseDir, project, console) ?: continue
            deviceById[id] = device
            if (overrides.isNotEmpty()) propertyOverrides[id] = overrides
        }

        if (deviceById.isEmpty()) {
            console.print("ERROR: No devices could be created.\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
            return Terminated()
        }

        val networks: Map<Long, Network> = if (config.networks.isEmpty()) {
            mapOf(0L to Network.single(deviceById.keys))
        } else {
            config.networks.associate { nc ->
                nc.id to Network(
                    dataConnected = nc.dataConnected.filter { it in deviceById }.toSet(),
                    softConnected = nc.softConnected.filter { it in deviceById }.toSet()
                )
            }
        }

        console.print("Starting environment simulation with ${deviceById.size} device(s)\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        val context = Context(deviceById.values.toList(), networks)
        var initialState = context.initialize()
        for ((id, overrides) in propertyOverrides) {
            initialState = initialState.applyPropertyOverrides(id, overrides, console)
        }
        return startNewProcess(context, initialState)
    }
}

class SimulationProcess(val context: Context, initialState: SimulationState? = null): ProcessHandler() {
    private var simulationState: SimulationState = initialState ?: context.initialize()
    private val _state = MutableStateFlow(simulationState)
    val state: StateFlow<SimulationState> = _state
    val currentState: SimulationState get() = simulationState

    private val history = mutableListOf<SimulationState.StateChange>()
    val canGoBack get() = history.isNotEmpty() || tickStart != null
    val isInTick get() = stepIterator != null || tickStart != null

    // tickStart is kept for instant within-tick revert (no recomputation needed).
    private var tickStart: SimulationState? = null
    // Accumulated sparse change for the current tick, composed from per-instruction changes.
    private var tickAccumulated: SimulationState.StateChange? = null
    private var stepIterator: Iterator<SimulationState.StateChange>? = null

    init {
        startNotify()
    }

    fun step(): Iterator<SimulationState.StateChange> {
        if (stepIterator == null) {
            tickStart = simulationState
            tickAccumulated = null
            stepIterator = context.step(simulationState).iterator()
        }
        return stepIterator!!
    }

    fun advanceStep() {
        val iter = step()
        if (iter.hasNext()) {
            val change = iter.next()
            simulationState = change.perform(simulationState)
            tickAccumulated = tickAccumulated?.let { it + change } ?: change
            _state.value = simulationState
            if (!iter.hasNext()) finalizeStep()
        }
    }

    fun advanceStepSilent() {
        val iter = step()
        if (iter.hasNext()) {
            val change = iter.next()
            simulationState = change.perform(simulationState)
            tickAccumulated = tickAccumulated?.let { it + change } ?: change
            if (!iter.hasNext()) finalizeStepSilent()
        }
    }

    fun publishCurrentState() {
        _state.value = simulationState
    }

    private fun finalizeStep() {
        if (tickStart == null) return
        val endChange = context.endTick(simulationState)
        simulationState = endChange.perform(simulationState)
        _state.value = simulationState
        history.add(tickAccumulated?.let { it + endChange } ?: endChange)
        stepIterator = null
        tickStart = null
        tickAccumulated = null
        notifyTextAvailable("History at ${history.size}\n", ProcessOutputTypes.STDOUT)
    }

    private fun finalizeStepSilent() {
        if (tickStart == null) return
        val endChange = context.endTick(simulationState)
        simulationState = endChange.perform(simulationState)
        history.add(tickAccumulated?.let { it + endChange } ?: endChange)
        stepIterator = null
        tickStart = null
        tickAccumulated = null
    }

    fun tick() {
        if (stepIterator == null) {
            tickStart = simulationState
            tickAccumulated = null
        }
        val iter = step()
        while (iter.hasNext()) {
            val change = iter.next()
            simulationState = change.perform(simulationState)
            tickAccumulated = tickAccumulated?.let { it + change } ?: change
        }
        val endChange = context.endTick(simulationState)
        simulationState = endChange.perform(simulationState)
        _state.value = simulationState
        history.add(tickAccumulated?.let { it + endChange } ?: endChange)
        stepIterator = null
        tickStart = null
        tickAccumulated = null
        notifyTextAvailable("History at ${history.size}\n", ProcessOutputTypes.STDOUT)
    }

    fun stepBack() {
        val ts = tickStart
        stepIterator = null
        if (ts != null) {
            // Within-tick: revert via accumulated change, or fall back to the snapshot.
            simulationState = tickAccumulated?.revert(simulationState) ?: ts
            _state.value = simulationState
            tickStart = null
            tickAccumulated = null
        } else {
            val change = history.removeLastOrNull() ?: return
            simulationState = change.revert(simulationState)
            _state.value = simulationState
        }
        notifyTextAvailable("History at ${history.size}\n", ProcessOutputTypes.STDOUT)
    }

    fun stop() {
        simulationState = SimulationState()
        _state.value = simulationState
        history.clear()
        tickStart = null
        tickAccumulated = null
        stepIterator = null
        notifyProcessTerminated(0)
    }

    override fun destroyProcessImpl() = stop()
    override fun detachProcessImpl() = stop()

    override fun detachIsDefault(): Boolean = false
    override fun getProcessInput(): OutputStream? = null
}
