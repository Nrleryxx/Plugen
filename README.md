# plugen

Scaffold Minecraft plugin projects in seconds - Paper, Spigot and Folia, Maven or Gradle.

plugen asks a handful of questions and generates a ready-to-build plugin project with the correct repositories, dependencies and plugin.yml. No more digging through old forum posts for pom snippets.

## Features

- Paper, Spigot and Folia targets with correct repos and dependency coordinates
- Maven or Gradle (Kotlin DSL) build files
- Optional example command and join listener, generated and registered for you
- Optional config.yml with saveDefaultConfig()
- Optional dependency shading (maven-shade-plugin or Gradle shadow)
- Optional runServer task via xyz.jpenilla.run-paper, test your plugin with a single command
- api-version derived automatically (1.21.4 -> 1.21), Java release picked based on target version (1.20.5+ -> 21)

## Requirements

- Java 17 or newer

## Usage

Interactive mode:

```
java -jar plugen.jar
```

Non-interactive mode:

```
java -jar plugen.jar MyPlugin --api paper --builder gradle --shade --run-server
```

Options:

| Option | Description | Default |
| --- | --- | --- |
| `--api <paper\|spigot\|folia>` | Server API | paper |
| `--builder <maven\|gradle>` | Build tool | maven |
| `--mc <version>` | Target Minecraft version | 1.21.4 |
| `--author <name>` | Plugin author | Nrleryx |
| `--package <package>` | Base package | me.author |
| `--command` / `--no-command` | Example command | on |
| `--listener` / `--no-listener` | Example join listener | on |
| `--config` / `--no-config` | config.yml | off |
| `--shade` / `--no-shade` | Dependency shading | off |
| `--run-server` | Test server task, Gradle only | off |

Run `java -jar plugen.jar --help` for the same list inside the tool.

## Example

```
> plugen KitPvP --api paper --builder gradle --shade --run-server

created KitPvP/

next:
  cd KitPvP
  gradle build
  gradle runServer
```

Generated project layout:

```
KitPvP/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
└── src/main/
    ├── java/me/nrleryx/
    │   ├── KitPvP.java
    │   ├── command/ExampleCommand.java
    │   └── listener/JoinListener.java
    └── resources/
        └── plugin.yml
```

## Building from source

```
mvn package
```

The jar lands at `target/plugen.jar`. Prebuilt jars are attached to GitHub Releases, tagging a commit with `v*` publishes one automatically.

On Windows there is `plugen.bat`, on Linux and macOS there is `plugen.sh`. Put the folder on your PATH and call it as `plugen`.
