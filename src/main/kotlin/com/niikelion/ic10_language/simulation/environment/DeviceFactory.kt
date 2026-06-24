package com.niikelion.ic10_language.simulation.environment

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.niikelion.ic10_language.Ic10FileType
import com.niikelion.ic10_language.Ic10PsiUtils
import com.niikelion.ic10_language.logic.CompilationError
import com.niikelion.ic10_language.logic.DeviceDataRegistry
import com.niikelion.ic10_language.logic.ProgramCode
import com.niikelion.ic10_language.logic.devices.Device
import com.niikelion.ic10_language.logic.devices.DeviceAspectsBuilder
import com.niikelion.ic10_language.logic.devices.DeviceState
import com.niikelion.ic10_language.logic.devices.StructureCircuitHousing
import com.niikelion.ic10_language.logic.devices.StructureCrafter
import com.niikelion.ic10_language.logic.state.SimulationState
import com.niikelion.ic10_language.psi.Ic10File
import java.io.File

/**
 * Converts a [DeviceConfig] entry into a live [Device] instance.
 *
 * Returns `null` (and prints diagnostics to [console]) when the device cannot be created
 * (compilation error, missing prefab, etc.).
 */
object DeviceFactory {

    fun create(
        config: DeviceConfig,
        id: Long,
        baseDir: File,
        project: Project,
        console: ConsoleView
    ): Pair<Device, Map<String, Double>>? {
        val label = config.name ?: config.source ?: config.prefab ?: "device#$id"

        val device = if (config.source != null) {
            createIc10Device(id, config.source, label, baseDir, project, console)
        } else {
            createPrefabDevice(id, config.prefab!!, config.deviceClass, config.name, console)
        } ?: return null

        return Pair(device, config.properties)
    }

    // ── IC10 ────────────────────────────────────────────────────────────────

    private fun createIc10Device(
        id: Long,
        sourcePath: String,
        label: String,
        baseDir: File,
        project: Project,
        console: ConsoleView
    ): Device? {
        val sourceFile = baseDir.resolve(sourcePath)
        return try {
            val vf = LocalFileSystem.getInstance().findFileByPath(sourceFile.canonicalPath)
            val psiFile = vf?.findPsiFile(project)
                ?: ReadAction.compute<PsiFile, Throwable> {
                    val text = sourceFile.readText()
                    PsiFileFactory.getInstance(project)
                        .createFileFromText("Program.ic10", Ic10FileType.Instance, text)
                }

            if (psiFile !is Ic10File) {
                console.print(
                    "ERROR: $label is not an IC10 file\n",
                    ConsoleViewContentType.LOG_ERROR_OUTPUT
                )
                return null
            }

            console.print("Compiling $label\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
            val code = ProgramCode.compile(psiFile)
            StructureCircuitHousing(id, code)
        } catch (t: Throwable) {
            if (t is CompilationError) {
                val line = t.target?.let { Ic10PsiUtils.getLineNumber(it) }?.let { "($it)" } ?: ""
                console.print(
                    "Compilation error$line in $label: ${t.message}\n",
                    ConsoleViewContentType.LOG_ERROR_OUTPUT
                )
            } else {
                console.print(
                    "ERROR: Could not load $label: ${t.message}\n",
                    ConsoleViewContentType.LOG_ERROR_OUTPUT
                )
            }
            null
        }
    }

    // ── Stationpedia-backed ─────────────────────────────────────────────────

    private fun createPrefabDevice(
        id: Long,
        prefabName: String,
        deviceClass: String?,
        customName: String?,
        console: ConsoleView
    ): Device? {
        val entry = DeviceDataRegistry.devices[prefabName]
        if (entry == null) {
            console.print(
                "ERROR: Unknown prefab \"$prefabName\". Check stationpedia data.\n",
                ConsoleViewContentType.LOG_ERROR_OUTPUT
            )
            return null
        }

        return when (deviceClass?.lowercase()) {
            "crafter" -> StructureCrafter(id, entry, customName)
            else      -> Device(id, entry, emptyList(), customName)
        }
    }
}

/**
 * Applies [overrides] (logic-type name → value) to a freshly initialised [DeviceState][com.niikelion.ic10_language.logic.devices.DeviceState].
 * Unknown property names are logged as warnings rather than errors.
 */
fun SimulationState.applyPropertyOverrides(
    deviceId: Long,
    overrides: Map<String, Double>,
    console: ConsoleView
): SimulationState {
    if (overrides.isEmpty()) return this

    val deviceState = devices[deviceId] ?: return this
    val logicTypeIndex = Device.properties

    // Resolve all overrides up front, logging warnings for unknowns, then apply in one pass.
    val resolved = mutableMapOf<Int, Double>()
    for ((name, value) in overrides) {
        val propId = logicTypeIndex[name]
        if (propId == null) {
            console.print(
                "WARNING: Unknown logic type \"$name\" in property override — skipped\n",
                ConsoleViewContentType.LOG_WARNING_OUTPUT
            )
            continue
        }
        if (!deviceState.properties.containsKey(propId)) {
            console.print(
                "WARNING: Device $deviceId does not expose logic type \"$name\" — skipped\n",
                ConsoleViewContentType.LOG_WARNING_OUTPUT
            )
            continue
        }
        resolved[propId] = value
    }

    if (resolved.isEmpty()) return this

    val newDeviceState = DeviceState(deviceState.properties + resolved, deviceState.slots, deviceState.aspects)
    return SimulationState(devices + (deviceId to newDeviceState), networks)
}
