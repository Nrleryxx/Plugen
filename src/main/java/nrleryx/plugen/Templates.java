package nrleryx.plugen;

import java.util.Map;

final class Templates {

    private Templates() {
    }

    static String fill(String template, Map<String, String> vars) {
        String out = template;
        for (var e : vars.entrySet()) {
            out = out.replace("%" + e.getKey() + "%", e.getValue());
        }
        return out;
    }

    static final String MAIN_SOURCE = """
            package %PACKAGE%;

            import org.bukkit.plugin.java.JavaPlugin;
            %IMPORT%
            public final class %NAME% extends JavaPlugin {

                @Override
                public void onEnable() {%ON_ENABLE%
                    getLogger().info("%NAME% enabled");
                }

                @Override
                public void onDisable() {
                    getLogger().info("%NAME% disabled");
                }
            }
            """;

    static final String COMMAND_SOURCE = """
            package %PACKAGE%.command;

            import org.bukkit.Bukkit;
            import org.bukkit.command.Command;
            import org.bukkit.command.CommandExecutor;
            import org.bukkit.command.CommandSender;

            public class ExampleCommand implements CommandExecutor {

                @Override
                public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                    sender.sendMessage("players online: " + Bukkit.getOnlinePlayers().size());
                    return true;
                }
            }
            """;

    static final String LISTENER_SOURCE = """
            package %PACKAGE%.listener;

            import org.bukkit.event.EventHandler;
            import org.bukkit.event.Listener;
            import org.bukkit.event.player.PlayerJoinEvent;

            public class JoinListener implements Listener {

                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    event.getPlayer().sendMessage("welcome back, " + event.getPlayer().getName());
                }
            }
            """;

    static final String PLUGIN_YML = """
            name: %NAME%
            version: '%VERSION%'
            main: %MAIN%
            api-version: '%API_VERSION%'
            authors: [%AUTHOR%]%FOLIA%%COMMANDS%
            """;

    static final String CONFIG_YML = """
            messages:
              prefix: "[%NAME%]"
              no-permission: "you don't have permission for that"
            """;

    static final String GITIGNORE = """
            target/
            build/
            .gradle/
            .idea/
            *.iml
            run/
            """;

    static final String MAVEN_POM = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>

                <groupId>%PACKAGE%</groupId>
                <artifactId>%NAME%</artifactId>
                <version>%VERSION%</version>
                <packaging>jar</packaging>

                <properties>
                    <java.version>%RELEASE%</java.version>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                </properties>

                <repositories>%REPO%
                </repositories>

                <dependencies>
                    <dependency>
                        <groupId>%DEP_GROUP%</groupId>
                        <artifactId>%DEP_ARTIFACT%</artifactId>
                        <version>%DEP_VERSION%</version>
                        <scope>provided</scope>
                    </dependency>
                </dependencies>

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-compiler-plugin</artifactId>
                            <version>3.13.0</version>
                            <configuration>
                                <release>${java.version}</release>
                            </configuration>
                        </plugin>%SHADE%
                    </plugins>
                </build>
            </project>
            """;

    static final String SHADE_PLUGIN = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>""";

    static final String GRADLE_BUILD = """
            plugins {
                `java-library`
            %GRADLE_PLUGINS%}

            group = '%PACKAGE%'
            version = '%VERSION%'

            java {
                toolchain.languageVersion = JavaLanguageVersion.of(%RELEASE%)
            }

            repositories {
                mavenCentral()%GRADLE_REPO%
            }

            dependencies {
                compileOnly('%DEP_GROUP%:%DEP_ARTIFACT%:%DEP_VERSION%')
            }%TASKS%
            """;

    static final String GRADLE_SETTINGS = """
            rootProject.name = '%NAME%'
            """;
}
