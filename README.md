# IC10 Language — JetBrains Plugin

Full IDE support for the **IC10 MIPS-like scripting language** used in
[Stationeers](https://store.steampowered.com/app/544550/Stationeers/).

**Current version: 2.1**

## Features

### Editor
- **Syntax highlighting** — instructions, registers, device references, labels, numbers, enum literals
- **Code completion** — instructions, registers (`r0`–`r17`, `ra`, `sp`), device slots (`db`, `d0`–`d5`), logic types, batch modes, and enum values (`LogicType.Activate` style)
- **Parameter info** — Ctrl+P shows argument names and types for every instruction
- **Inlay hints** — parameter names displayed inline next to each argument
- **Error annotations** — unknown instructions, wrong argument count, undefined labels, duplicate defines, deprecated instructions
- **Go-to-definition & find usages** — for labels and named registers (`define`/`alias`)
- **Formatting & smart Enter** — auto-indent and alignment on Enter
- **Structure view** — file outline with labels and defines
- **Status bar widget** — live line count vs. the 512-line IC10 limit

### Documentation
- **Quick-docs** (Ctrl+Q) — full descriptions for all instructions, registers, device slots, and enum values
- **Stationpedia integration** — game device data (logic properties, type hashes, reagent lists) downloaded at build time and used for completions and docs
- **Deprecation notices** — deprecated logic types and instructions are highlighted with a warning and a migration hint

### Debugger
Two run configuration types — accessible from **Run → Edit Configurations**:

| Type | Use case |
|---|---|
| **IC10** | Single device — pick one or more `.ic10` source files |
| **IC10 Environment** | Multi-device — point to an `.ic10env` config file |

Both support:
- Line **breakpoints** — click the gutter to set/clear
- **Step** (F8) — execute one instruction
- **Full tick** — advance one complete game tick (128 instructions across all devices)
- **Step back** — rewind to the previous tick
- **Variable inspection** — registers (`r0`–`r17`, `ra`, `sp`), connected device properties, and stack memory


## Debugger Quick Start

### Single device

1. Open an `.ic10` file.
2. **Run → Edit Configurations → + → IC10**.
3. Add your `.ic10` file(s) to the list and click **Debug**.

### Multi-device environment

1. Create an `.ic10env` file in your project (see the [`.ic10env` format reference](docs/ic10env-format.md)).
2. **Run → Edit Configurations → + → IC10 Environment**.
3. Point it at the `.ic10env` file and click **Debug**.

`.ic10env` files get YAML syntax highlighting, autocompletion, and schema validation automatically — field names, types, and the `class` enum are all checked.

See the [`.ic10env` format reference](docs/ic10env-format.md) for the full list of device and network fields.


## Known Limitations

Slot instructions are defined and documented but not yet simulated. Using them will compile without errors but have no effect during a debug session:

| Instruction | Description |
|---|---|
| `ls` | Load slot property from a device |
| `ss` | Write to a slot property of a device |
| `lbs` | Batch-load slot property by type hash |
| `lbns` | Batch-load slot property by type + name hash |
| `sbs` | Batch-write to slot property by type hash |


## Development

### Requirements

- JDK 21
- IntelliJ IDEA (any edition — Community works for development)

### Build

```bash
./gradlew build
```

### Run in a sandboxed IDE

```bash
./gradlew runIde
```

This launches a fresh IntelliJ IDEA instance with the plugin installed.

### Stationpedia data

Device data (logic properties, type hashes, recipes) is downloaded from
[StationeersStationpediaExtractor](https://github.com/aproposmath/StationeersStationpediaExtractor)
at build time and bundled with the plugin. Run `./gradlew downloadStationpedia` to refresh it manually.

### Tests

```bash
./gradlew test
```

Tests live in `src/test/kotlin/` and use a minimal IntelliJ fixture (`BareTestFixtureTestCase`). Instruction simulation is covered by a DSL in `InstructionTestDsl.kt`.

### Project layout

```
src/main/kotlin/…/ic10_language/
├── logic/              # Pure Kotlin simulation engine (no IntelliJ dependencies)
│   ├── Instructions.kt         # All instruction definitions and actions
│   ├── Registers.kt            # r0–r17, ra, sp
│   ├── DeviceSlots.kt          # db, d0–d5
│   ├── Data.kt                 # DeviceDataRegistry (stationpedia)
│   ├── Context.kt              # Tick execution coordinator
│   ├── Network.kt              # Data / soft network topology
│   ├── aspects/                # Device capability aspects (program, memory, crafting, …)
│   ├── devices/                # Device types (StructureCircuitHousing, StructureCrafter, …)
│   └── state/                  # Immutable simulation state + change builders
├── simulation/         # Run configurations, debugger, simulation service
│   ├── environment/            # .ic10env parser, device factory, JSON Schema provider
│   ├── debug/                  # XDebugger integration (process, stack frame, breakpoints)
│   └── SimulationService.kt    # Manages the active SimulationProcess
├── psi/                # IntelliJ PSI elements for the IC10 language
├── annotations/        # Annotators, inlay hints, syntax highlighting
├── documentation/      # Quick-doc providers
├── formatting/         # Formatter and Enter handler
└── navigation/         # Go-to-definition, find usages, structure view
```


## Changelog

### 2.1
- Multi-device simulation via a new **IC10 Environment** run configuration driven by an `.ic10env` config file.
- Environment config supports IC10 circuit housings, passive stationpedia devices (e.g. `StructureMemory`), and fabricator-class devices (`StructureAutolathe`, `StructurePipeBender`, …) with initial property value overrides.
- Network topology is fully configurable; defaults to a single shared data network when omitted.
- `.ic10env` files accept YAML or JSON, with schema-based field completion, type checking, and validation in the editor.

### 2.0
- IC10 debugger with breakpoints, step-over, full-tick, and step-back via JetBrains XDebugger.
- Complete IC10 instruction simulation engine with device model, network model, and stack memory.
- Stationpedia integration: all game devices and their logic properties loaded from live game data.
- Enum syntax support: `LogicType.Activate` style values with highlighting, completions, and quick-docs.
- Quick-docs for registers, device references, and enum values (including deprecation notices).

### 1.5
- Improved quick-docs, parameter info hints, and formatting support.

### 1.x
- Navigation (go-to-definition, find usages), channel and printer constant support, instruction documentation.
