package nrleryx.plugen;

import java.util.Locale;
import java.util.Set;

final class Args {

    private static final Set<String> APIS = Set.of("paper", "spigot", "folia");
    private static final Set<String> BUILD_TOOLS = Set.of("maven", "gradle");

    private Args() {
    }

    static ProjectSpec parse(String[] raw) {
        String name = null;
        String pkg = null;
        String author = "Nrleryx";
        String api = "paper";
        String mc = "1.21.4";
        String builder = "maven";
        boolean command = true;
        boolean listener = true;
        boolean config = false;
        boolean shade = false;
        boolean runServer = false;

        for (int i = 0; i < raw.length; i++) {
            switch (raw[i]) {
                case "--help" -> {
                    usage();
                    System.exit(0);
                }
                case "--api" -> api = value(raw, ++i).toLowerCase(Locale.ROOT);
                case "--builder" -> builder = value(raw, ++i).toLowerCase(Locale.ROOT);
                case "--mc" -> mc = value(raw, ++i);
                case "--author" -> author = value(raw, ++i);
                case "--package" -> pkg = value(raw, ++i);
                case "--command" -> command = true;
                case "--no-command" -> command = false;
                case "--listener" -> listener = true;
                case "--no-listener" -> listener = false;
                case "--config" -> config = true;
                case "--no-config" -> config = false;
                case "--shade" -> shade = true;
                case "--no-shade" -> shade = false;
                case "--run-server" -> runServer = true;
                default -> {
                    if (raw[i].startsWith("-")) {
                        throw new IllegalStateException("unknown option: " + raw[i]);
                    }
                    if (name != null) {
                        throw new IllegalStateException("unexpected argument: " + raw[i]);
                    }
                    name = raw[i];
                }
            }
        }

        if (name == null) {
            throw new IllegalStateException("plugin name required");
        }
        if (!ProjectSpec.validName(name)) {
            throw new IllegalStateException("invalid plugin name: " + name);
        }
        if (!APIS.contains(api)) {
            throw new IllegalStateException("--api must be paper/spigot/folia");
        }
        if (!BUILD_TOOLS.contains(builder)) {
            throw new IllegalStateException("--builder must be maven/gradle");
        }
        if (!ProjectSpec.validMcVersion(mc)) {
            throw new IllegalStateException("invalid minecraft version: " + mc);
        }
        if (pkg == null) {
            pkg = ProjectSpec.defaultPackage(author);
            if (pkg == null) {
                throw new IllegalStateException("author '" + author + "' can't form a default package, pass --package");
            }
        }
        if (!ProjectSpec.validPackage(pkg)) {
            throw new IllegalStateException("invalid package: " + pkg);
        }
        return new ProjectSpec(name, pkg, author, api, mc, builder, command, listener, config, shade, runServer);
    }

    private static String value(String[] raw, int i) {
        if (i >= raw.length) {
            throw new IllegalStateException("missing value for " + raw[i - 1]);
        }
        return raw[i];
    }

    private static void usage() {
        System.out.println("""
                plugen :: minecraft plugin scaffold

                usage:
                  plugen                      interactive mode
                  plugen <name> [options]     non-interactive

                options:
                  --api <paper|spigot|folia>   default paper
                  --builder <maven|gradle>     default maven
                  --mc <version>               default 1.21.4
                  --author <name>              default Nrleryx
                  --package <package>          default me.<author>
                  --command / --no-command     example command, default on
                  --listener / --no-listener   example join listener, default on
                  --config / --no-config       config.yml, default off
                  --shade / --no-shade         dependency shading, default off
                  --run-server                 test server task, gradle only

                example:
                  plugen KitPvP --api paper --builder gradle --package me.nrleryx.kitpvp
                """);
    }
}
