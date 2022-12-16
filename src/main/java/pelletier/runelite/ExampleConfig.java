package pelletier.runelite;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface ExampleConfig extends Config {

    @ConfigItem(
            keyName = "remainingXp",
            name = "Remaining Xp",
            description = "The amount of xp from hitting 99 before you will be alerted that your untrimmed cape is in danger of being lost."
    )
    default int remainingXp() {
        return 50_000;
    }
    // TODO how to validate integers for config item?
    // TODO configuration to make it flash (default false)

}
