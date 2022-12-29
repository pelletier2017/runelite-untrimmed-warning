package runelite.untrimmed;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class UntrimmedPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(UntrimmedPlugin.class);
        RuneLite.main(args);
    }
}