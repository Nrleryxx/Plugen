package nrleryx.plugen;

import java.nio.file.Path;

record ProjectSpec(
        String name,
        String packageName,
        String author,
        String api,
        String mcVersion,
        String builder,
        boolean withCommand,
        boolean withListener,
        boolean withConfig,
        boolean shade,
        boolean runServer
) {

    static boolean validName(String s) {
        return s.matches("[A-Za-z][A-Za-z0-9_]{1,31}");
    }

    static boolean validPackage(String s) {
        return s.matches("[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*");
    }

    static boolean validMcVersion(String s) {
        return s.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?");
    }

    static String defaultPackage(String author) {
        String clean = author.replaceAll("[^A-Za-z0-9_]", "");
        if (clean.isEmpty() || !Character.isLetter(clean.charAt(0))) {
            return null;
        }
        return "me." + clean;
    }

    Path root() {
        return Path.of(name);
    }

    String mainClass() {
        return packageName + "." + name;
    }

    String apiVersion() {
        String[] parts = mcVersion.split("\\.");
        return parts.length >= 2 ? parts[0] + "." + parts[1] : mcVersion;
    }

    int javaRelease() {
        String[] p = mcVersion.split("\\.");
        if (p.length < 2) {
            return 21;
        }
        int minor = Integer.parseInt(p[1]);
        int patch = p.length > 2 ? Integer.parseInt(p[2]) : 0;
        return minor > 20 || (minor == 20 && patch >= 5) ? 21 : 17;
    }

    boolean maven() {
        return builder.equals("maven");
    }
}
