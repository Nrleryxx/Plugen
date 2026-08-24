package nrleryx.plugen;

import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

final class Wizard {

    private static final Set<String> APIS = new TreeSet<>(Set.of("paper", "spigot", "folia"));
    private static final Set<String> BUILD_TOOLS = new TreeSet<>(Set.of("maven", "gradle"));

    private final Scanner in = new Scanner(System.in);

    ProjectSpec run() {
        System.out.println("plugen :: minecraft plugin scaffold");
        System.out.println();

        String name = askName();
        String author = ask("author", "Nrleryx");
        String pkg = askPackage(author);
        String api = choose("api (paper/spigot/folia)", APIS, "paper");
        String mc = ask("minecraft version", "1.21.4");
        String buildTool = choose("build tool", BUILD_TOOLS, "maven");
        boolean command = yesNo("add example command", true);
        boolean listener = yesNo("add example listener", true);
        boolean config = yesNo("add config.yml", false);
        boolean shade = yesNo("add shade support", false);
        boolean runServer = buildTool.equals("gradle") && yesNo("add runServer task", true);

        ProjectSpec spec = new ProjectSpec(name, pkg, author, api, mc, buildTool, command, listener, config, shade, runServer);

        System.out.println();
        System.out.println("  name     " + spec.name());
        System.out.println("  package  " + spec.packageName());
        System.out.println("  main     " + spec.mainClass());
        System.out.println("  api      " + spec.api() + " / " + spec.apiVersion() + " / java " + spec.javaRelease());
        System.out.println("  builder  " + spec.builder());
        System.out.println("  extras   " + extras(spec));
        System.out.println();
        if (!yesNo("looks good", true)) {
            throw new IllegalStateException("cancelled");
        }
        return spec;
    }

    private String extras(ProjectSpec spec) {
        var list = new java.util.ArrayList<String>();
        if (spec.withCommand()) list.add("command");
        if (spec.withListener()) list.add("listener");
        if (spec.withConfig()) list.add("config");
        if (spec.shade()) list.add("shade");
        if (spec.runServer()) list.add("runServer");
        return list.isEmpty() ? "-" : String.join(", ", list);
    }

    private String readLine() {
        if (!in.hasNextLine()) {
            throw new IllegalStateException("input closed");
        }
        return in.nextLine().trim();
    }

    private String ask(String label, String def) {
        System.out.print(def == null ? label + ": " : label + " [" + def + "]: ");
        String line = readLine();
        return line.isEmpty() ? def : line;
    }

    private boolean yesNo(String label, boolean def) {
        while (true) {
            String v = ask(label, def ? "y" : "n").toLowerCase(Locale.ROOT);
            if (v.equals("y") || v.equals("yes")) return true;
            if (v.equals("n") || v.equals("no")) return false;
        }
    }

    private String choose(String label, Set<String> options, String def) {
        while (true) {
            String v = ask(label, def).toLowerCase(Locale.ROOT);
            if (options.contains(v)) {
                return v;
            }
            System.out.println("pick one: " + String.join("/", options));
        }
    }

    private String askName() {
        while (true) {
            String name = ask("plugin name", null);
            if (ProjectSpec.validName(name)) {
                return name;
            }
            System.out.println("letters/digits/underscore, must start with a letter");
        }
    }

    private String askPackage(String author) {
        while (true) {
            String pkg = ask("package", ProjectSpec.defaultPackage(author));
            if (pkg != null && ProjectSpec.validPackage(pkg)) {
                return pkg;
            }
            System.out.println("invalid package name");
        }
    }
}
