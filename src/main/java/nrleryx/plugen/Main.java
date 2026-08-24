package nrleryx.plugen;

public final class Main {

    public static void main(String[] args) {
        try {
            ProjectSpec spec = args.length == 0 ? new Wizard().run() : Args.parse(args);
            new Scaffolder().generate(spec);
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            System.exit(1);
        }
    }
}
