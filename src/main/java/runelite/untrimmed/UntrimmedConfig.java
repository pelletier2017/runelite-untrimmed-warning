package runelite.untrimmed;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup(UntrimmedConfig.CONFIG_GROUP)
public interface UntrimmedConfig extends Config {

    String CONFIG_GROUP = "untrimmed-warning";

    @ConfigItem(
            keyName = "remainingXp",
            name = "Remaining Xp",
            description = "The amount of xp from hitting 99 before you will be alerted that your untrimmed cape is in danger of being lost."
    )
    default int warningXpRange() {
        return 50_000;
    }

    @ConfigItem(
            keyName = "shouldFlash",
            name = "Flash Warning",
            description = "Whether or not to flash warning when it pops up."
    )
    default boolean shouldFlash() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor1",
            name = "Flash Color #1",
            description = "The first color to flash between, also controls the non-flashing color.",
            position = 6
    )
    default Color flashColor1() {
        return new Color(255, 0, 0, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor2",
            name = "Flash Color #2",
            description = "The second color to flash between.",
            position = 7
    )
    default Color flashColor2() {
        return new Color(70, 61, 50, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "safeColor",
            name = "Safe Color",
            description = "The color of the overlay if you are near a new 99 but your cape is safe.",
            position = 8
    )
    default Color safeColor() {
        return new Color(8, 132, 60, 150);
    }

    @ConfigItem(
            keyName = "hidePickup",
            name = "Hide Untrimmed Pickup",
            description = "Whether or not to hide left click pickup for untrimmed capes when it would automatically trim on pickup."
    )
    default boolean hidePickup() {
        return true;
    }

    default String secretWord() {
        return "Something";
    }
}