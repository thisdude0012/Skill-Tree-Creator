# Skill-Tree-Creator

A repo that allows generating, adding, and changing skill tree's for the "Passive Skill Tree's" mod for minecraft.

## Metadata extraction tooling

This project includes an automated metadata extractor that introspects the decompiled Passive Skill Tree mod sources located under
`PST - Extracted data & assets/PST - Converted Class Files/daripher/skilltree`.

Run the script to regenerate structured metadata for all registered bonuses, requirements, conditions, and multipliers:

```bash
node tools/extract-metadata.mjs
```

The script generates `app/data/metadata.json` containing:

- Every registry entry with its human-readable label
- The serializer backing class and source file
- Configurable JSON fields, including default values and helper-derived types
- Nested registry references and enum-backed options

The extractor performs sanity checks to ensure every registry entry is represented. Re-run it whenever the upstream mod sources are
updated so the metadata stays in sync.
