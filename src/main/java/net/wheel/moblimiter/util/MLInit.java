package net.wheel.moblimiter.util;

public final class MLInit {
    private MLInit() {
    }

    public static void MINT(String version) {
        System.out.println(
                "\n" +
                        "  _____ ______   ___          \n" +
                        "|\\   _ \\  _   \\|\\  \\         \n" +
                        "\\ \\  \\\\\\__\\ \\  \\ \\  \\        \n" +
                        " \\ \\  \\\\|__| \\  \\ \\  \\       \n" +
                        "  \\ \\  \\    \\ \\  \\ \\  \\____  \n" +
                        "   \\ \\__\\    \\ \\__\\ \\_______\\\n" +
                        "    \\|__|     \\|__|\\|_______|\n" +
                        "\nInitializing MobLimit v" + version + "\n");
    }
}
