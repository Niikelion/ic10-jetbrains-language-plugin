# `.ic10env` Format

The environment config describes every device in the simulation and how they are networked. It uses **YAML** (or JSON — JSON is a valid YAML subset, so existing JSON files work without changes).

`.ic10env` files get YAML syntax highlighting, autocompletion, and schema validation automatically — field names, types, and the `class` enum are all checked.

```yaml
devices:
  - name: Controller        # display name in the debugger (optional)
    source: controller.ic10 # path relative to this file

  - name: Memory
    prefab: StructureMemory # Stationpedia prefab name

  - name: Autolathe
    prefab: StructureAutolathe
    class: crafter          # use crafter behaviour (fabricator logic)
    properties:
      Setting: 5.0          # initial logic-property values

networks:
  - id: 0
    dataConnected: [1, 2, 3]  # full IC10 read/write access
    softConnected: []          # cable-only (channel access, no slot addressing)
```

## Device fields

| Field | Required | Description |
|---|---|---|
| `source` | one of `source`/`prefab` | Path to an `.ic10` file, relative to the config. Creates a `StructureCircuitHousing`. |
| `prefab` | one of `source`/`prefab` | Stationpedia prefab name (e.g. `StructureMemory`, `StructureAutolathe`). |
| `class` | no | Device behaviour. `crafter` for fabricators (Autolathe, PipeBender, …). Ignored for IC10 devices. |
| `id` | no | Explicit numeric ID. Auto-assigned sequentially (1, 2, …) if omitted. |
| `name` | no | Display name in the debugger panel. |
| `properties` | no | Initial logic-property values, keyed by logic-type name (`Setting`, `On`, `Pressure`, …). |

## Network fields

| Field | Default | Description |
|---|---|---|
| `id` | `0` | Unique network ID. |
| `dataConnected` | `[]` | Device IDs with full data-network access — IC10 can read/write properties and address these by slot (`d0`–`d5`). |
| `softConnected` | `[]` | Device IDs reachable via cable only — channel read/write works, direct slot addressing does not. |

If `networks` is omitted entirely, all declared devices are placed on a single shared data network.
