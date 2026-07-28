# template-examples

Example project templates for [CodeOnTheGo](https://github.com/appdevforall/CodeOnTheGo). Each folder is a templatized Android project — a normal Gradle project whose machine- and project-specific values have been replaced with Pebble tokens, so CoGo's New Project wizard can stamp out a fresh app from it.

Templates are packaged and installed as `.cgt` bundles. See the "Template Creation and Installation" documentation for the install workflow, and [`templatize-project`](https://github.com/appdevforall/templatize-project) for the script that turns an existing project into one of these.

## Examples

| Template                        | Creates                                                                             | Language | Built on                            |
| ------------------------------- | ----------------------------------------------------------------------------------- | -------- | ----------------------------------- |
| [`bubblewand/`](bubblewand/)    | A bubble wand shooter game — touch-aimed wand, floating targets, score overlay.      | Kotlin   | jMonkeyEngine 3.9.0                 |
| [`demo/`](demo/)                | A minimal 3D demo — drag to spin the Earth, orbiting rocket, starfield, bloom glow.  | Java     | jMonkeyEngine 3.6.1                 |
| [`pianoman/`](pianoman/)        | A scrollable 88-key piano that plays resampled instrument recordings, with chords.   | Kotlin   | AndroidX views + `AudioTrack`       |
| [`tetris/`](tetris/)            | A Tetris-like falling-block game with a 3D board.                                    | Kotlin   | jMonkeyEngine 3.9.0                 |

Several templates carry their own `README.md` with gameplay, controls, and build notes — see [`demo/README.md`](demo/README.md) and [`pianoman/README.md`](pianoman/README.md).

## Anatomy of a template

```
tetris/
├── template/
│   ├── template.json          # manifest: name, description, version, parameters
│   ├── thumb.png              # thumbnail shown in the New Project wizard
│   └── icon.png               # generated app launcher icon (optional)
├── settings.gradle.peb        # ${{APP_NAME}} → rootProject.name
├── app/
│   ├── build.gradle.peb       # ${{PACKAGE_NAME}}, ${{COMPILE_SDK}}, ${{MIN_SDK}}, …
│   └── src/main/
│       ├── java/PACKAGE_NAME/ # directory renamed to the chosen package
│       │   └── MainActivity.kt.peb
│       └── res/values/strings.xml.peb
└── gradle/wrapper/gradle-wrapper.properties.peb
```

Two conventions do the work:

- **`.peb` suffix** — the file is run through [Pebble](https://pebbletemplates.io/) during project creation, and the suffix is dropped from the generated file. Tokens are written `${{TOKEN_NAME}}`.
- **`PACKAGE_NAME/` directory** — renamed to the package the user chose, and the matching `package` declaration inside each source file is substituted.

Everything else (assets, resources, `libs.versions.toml`, the Gradle wrapper) is copied through untouched.

## Tokens

Each template declares which tokens it consumes in `template/template.json`. The names below are the ones used across these examples.

| Token                                        | Source                                              |
| -------------------------------------------- | --------------------------------------------------- |
| `APP_NAME`                                   | Required — app name entered in the wizard           |
| `PACKAGE_NAME`                               | Required — application ID / namespace               |
| `SAVE_LOCATION`                              | Required — where the generated project is written   |
| `MIN_SDK`                                    | Optional — minimum SDK, defaulted if omitted        |
| `AGP_VERSION`, `KOTLIN_VERSION`, `GRADLE_VERSION` | Supplied by the IDE                             |
| `COMPILE_SDK`, `TARGET_SDK`                  | Supplied by the IDE                                 |
| `JAVA_SOURCE_COMPAT`, `JAVA_TARGET_COMPAT`, `JAVA_TARGET` | Supplied by the IDE                     |

A manifest maps a wizard parameter to its token, so the two names can differ:

```json
"parameters": {
    "required": {
        "appName":     { "identifier": "APP_NAME" },
        "packageName": { "identifier": "PACKAGE_NAME" },
        "saveLocation":{ "identifier": "SAVE_LOCATION" }
    },
    "optional": {
        "minsdk": { "identifier": "MIN_SDK" }
    }
}
```

## Working on a template

A fully templatized folder is **not** a buildable Gradle project — `settings.gradle` and the module's `build.gradle` only exist in `.peb` form, so Gradle has nothing to read until the template is instantiated. To iterate on one:

1. Generate a project from the template in CoGo (or run the substitutions by hand).
2. Build and test the generated project as an ordinary Android app.
3. Port the fix back into the `.peb` source, keeping the tokens intact.

The quickest way to check a change hasn't broken the tokens is to generate a project twice with different app and package names and confirm both compile.

`demo/` and `tetris/` currently keep their original pre-templatized sources alongside the `.peb` versions (`app/build.gradle` next to `app/build.gradle.peb`, `com/example/…` next to `PACKAGE_NAME/`). That makes them convenient to open directly, but the duplicates have to be kept in sync by hand — edit both, or the generated project and the checked-in project will drift.

## Adding a template

Use [`templatize-project`](https://github.com/appdevforall/templatize-project) on a working Android project:

```sh
python templatize_project.py path/to/MyProject --module app
```

It copies the project (never modifying the original), rewrites concrete values to tokens, renames the touched files to `.peb`, strips `build/` and keystores, and scaffolds a `template/` directory. Then:

- Fill in `template/template.json` with a real name, description, and version.
- Replace the placeholder `template/icon.png` and add a `template/thumb.png`.
- Check that nothing machine-specific survived — `local.properties`, `google-services.json`, signing config, absolute paths.
- Add the folder to the table above.
