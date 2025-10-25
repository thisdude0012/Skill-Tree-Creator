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

## Running the application

The Skill Creator relies on browser modules and JSON fetch requests. Browsers block these features when the project is opened directly via
`file://`, so always run the site through a local HTTP server.

From the repository root, start a server with one of the following options:

- Python 3: `python -m http.server 8000`
- Node.js: `npx serve`
- Node.js (http-server): `npx http-server`
- VS Code: use the Live Server extension

Once the server is running, open `http://localhost:8000/app/` in your browser. Navigating to `app/index.html` through the `file://`
protocol will result in CORS errors and missing metadata.
