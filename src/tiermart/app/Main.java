package tiermart.app;

/**
 * Single entry point. Picks a front-end from the first argument:
 *
 * <pre>
 *   java -cp out tiermart.app.Main cli     # text menu (default)
 *   java -cp out tiermart.app.Main gui     # Swing desktop app
 *   java -cp out tiermart.app.Main demo    # scripted State-pattern self-check
 * </pre>
 */
public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "cli";
        switch (mode) {
            case "gui"  -> Gui.main(args);
            case "demo" -> SelfCheck.main(args);
            case "cli"  -> Cli.main(args);
            default -> {
                System.err.println("usage: Main [cli|gui|demo]");
                System.exit(2);
            }
        }
    }
}
