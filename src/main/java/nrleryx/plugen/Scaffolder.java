package nrleryx.plugen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class Scaffolder {

    void generate(ProjectSpec spec) throws IOException {
        Path root = spec.root();
        if (Files.exists(root)) {
            throw new IllegalStateException(spec.name() + "/ already exists here");
        }

        Map<String, String> vars = vars(spec);

        write(root.resolve(spec.maven() ? "pom.xml" : "build.gradle.kts"), buildFile(spec, vars));
        if (!spec.maven()) {
            write(root.resolve("settings.gradle.kts"), Templates.fill(Templates.GRADLE_SETTINGS, vars));
        }
        write(root.resolve(".gitignore"), Templates.fill(Templates.GITIGNORE, vars));
        write(root.resolve("src/main/resources/plugin.yml"), Templates.fill(Templates.PLUGIN_YML, vars));

        Path javaRoot = root.resolve("src/main/java/" + spec.packageName().replace('.', '/'));
        write(javaRoot.resolve(spec.name() + ".java"), Templates.fill(Templates.MAIN_SOURCE, vars));
        if (spec.withCommand()) {
            write(javaRoot.resolve("command/ExampleCommand.java"), Templates.fill(Templates.COMMAND_SOURCE, vars));
        }
        if (spec.withListener()) {
            write(javaRoot.resolve("listener/JoinListener.java"), Templates.fill(Templates.LISTENER_SOURCE, vars));
        }
        if (spec.withConfig()) {
            write(root.resolve("src/main/resources/config.yml"), Templates.fill(Templates.CONFIG_YML, vars));
        }

        boolean runServerActive = spec.runServer() && !spec.maven() && !spec.api().equals("folia");
        System.out.println();
        System.out.println("created " + spec.name() + "/");
        if (spec.runServer() && !runServerActive) {
            System.out.println("note: runServer needs gradle + paper/spigot, skipped");
        }
        System.out.println();
        System.out.println("next:");
        System.out.println("  cd " + spec.name());
        System.out.println("  " + (spec.maven() ? "mvn package" : "gradle build"));
        if (runServerActive) {
            System.out.println("  gradle runServer");
        }
    }

    private Map<String, String> vars(ProjectSpec spec) {
        Map<String, String> v = new HashMap<>();
        boolean paperLike = spec.api().equals("paper") || spec.api().equals("folia");

        v.put("NAME", spec.name());
        v.put("PACKAGE", spec.packageName());
        v.put("MAIN", spec.mainClass());
        v.put("VERSION", version());
        v.put("AUTHOR", spec.author());
        v.put("API_VERSION", spec.apiVersion());
        v.put("RELEASE", String.valueOf(spec.javaRelease()));
        v.put("DEP_GROUP", paperLike ? "io.papermc.papermc-api" : "org.spigotmc");
        v.put("DEP_ARTIFACT", paperLike ? "paper-api" : "spigot-api");
        v.put("DEP_VERSION", spec.mcVersion() + "-R0.1-SNAPSHOT");

        if (paperLike) {
            v.put("REPO", repoBlock("papermc", "https://repo.papermc.io/repository/maven-public/"));
            v.put("GRADLE_REPO", gradleRepo("https://repo.papermc.io/repository/maven-public/"));
        } else {
            v.put("REPO", repoBlock("spigotmc", "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
            v.put("GRADLE_REPO", gradleRepo("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
        }
        v.put("FOLIA", spec.api().equals("folia") ? "\nfolia-supported: true" : "");
        v.put("COMMANDS", spec.withCommand()
                ? "\n\ncommands:\n  example:\n    description: shows the player count\n    usage: /example"
                : "");

        StringBuilder onEnable = new StringBuilder();
        if (spec.withConfig()) {
            onEnable.append("\n        saveDefaultConfig();\n");
        }
        if (spec.withListener()) {
            onEnable.append("\n        getServer().getPluginManager().registerEvents(new JoinListener(), this);\n");
        }
        if (spec.withCommand()) {
            onEnable.append("\n        getCommand(\"example\").setExecutor(new ExampleCommand());\n");
        }
        v.put("ON_ENABLE", onEnable.toString());

        StringBuilder imports = new StringBuilder();
        if (spec.withListener()) {
            imports.append("\nimport ").append(spec.packageName()).append(".listener.JoinListener;\n");
        }
        if (spec.withCommand()) {
            imports.append("\nimport ").append(spec.packageName()).append(".command.ExampleCommand;\n");
        }
        v.put("IMPORT", imports.toString());

        v.put("MC", spec.mcVersion());
        String shadePad = " ".repeat(12);
        v.put("SHADE", spec.shade() && spec.maven()
                ? "\n" + shadePad + Templates.SHADE_PLUGIN.replace("\n", "\n" + shadePad)
                : "");
        v.put("GRADLE_PLUGINS", gradlePlugins(spec));
        v.put("TASKS", spec.runServer() && !spec.maven() && !spec.api().equals("folia")
                ? "\n\ntasks {\n    runServer {\n        minecraftVersion('" + spec.mcVersion() + "')\n    }\n}"
                : "");
        return v;
    }

    private String gradlePlugins(ProjectSpec spec) {
        var lines = new java.util.ArrayList<String>();
        if (spec.shade()) {
            lines.add("    id('com.gradleup.shadow') version '8.3.11'");
        }
        if (spec.runServer() && !spec.api().equals("folia")) {
            lines.add("    id('xyz.jpenilla.run-paper') version '2.3.0'");
        }
        return lines.isEmpty() ? "" : "\n" + String.join("\n", lines) + "\n";
    }

    private String buildFile(ProjectSpec spec, Map<String, String> vars) {
        return Templates.fill(spec.maven() ? Templates.MAVEN_POM : Templates.GRADLE_BUILD, vars);
    }

    private String version() {
        return "1.0.0";
    }

    private String repoBlock(String id, String url) {
        return "\n        <repository>"
                + "\n            <id>" + id + "</id>"
                + "\n            <url>" + url + "</url>"
                + "\n        </repository>";
    }

    private String gradleRepo(String url) {
        return "\n    maven(\"" + url + "\")";
    }

    private void write(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}
