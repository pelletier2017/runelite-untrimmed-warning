package pelletier.runelite;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Optional;

@Slf4j
@PluginDescriptor(
        name = "Example"
)
public class ExamplePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ExampleConfig config;

    // TODO subscribe to onXpGained??
    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        SkillXpTracker skillXpTracker = new SkillXpTracker(client);
        UntrimmedCapeTracker untrimmedCapeTracker = new UntrimmedCapeTracker(client);

        if (!skillXpTracker.hasAny99() || skillXpTracker.isMaxed()) {
            hideInfoBox();
            return;
        }

        int remainingXp = 50_000;
        if (skillXpTracker.isCloseToNext99(remainingXp) && untrimmedCapeTracker.hasCapeInDanger()) {
            // what is the skill close to 99 and where is cape?
            String closestNext99 = skillXpTracker.closestNext99();
            UntrimmedCapeTracker.StorageLocation capeLocation = untrimmedCapeTracker.capeLocation();
            showInfoBox(closestNext99, capeLocation.getName(), capeLocation.isSafe());
        } else {
            hideInfoBox();
        }

    }

    private void showInfoBox(String closestNext99, String capeLocationInDanger, boolean isSafe) {
        // TODO figure out how to add infobox like the party defense tracker
        // ICON depends on skill about to hit 99?
        // green hint if its safe, red hint if its unsafe?
    }

    private void hideInfoBox() {

    }

    @Provides
    ExampleConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleConfig.class);
    }
}
