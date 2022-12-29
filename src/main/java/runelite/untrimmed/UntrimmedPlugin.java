package runelite.untrimmed;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Skill;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "Untrimmed Warning"
)
public class UntrimmedPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private UntrimmedConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WarningOverlay overlay;

    private CapeManager capeManager;

    private SkillXpManager skillXpManager;

    private boolean finishedInitializingCharacter = false;

    @Override
    protected void startUp() throws Exception {
        finishedInitializingCharacter = false;
        capeManager = new CapeManager(client, configManager);
        skillXpManager = new SkillXpManager(client);
    }

    @Override
    protected void shutDown() {
        capeManager.shutDown();
    }

    @Subscribe
    void onActorDeath(ActorDeath actorDeath) {
        if (client.getLocalPlayer() == null || actorDeath.getActor() != client.getLocalPlayer()) {
            return;
        }
        capeManager.onActorDeath(actorDeath);
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (finishedInitializingCharacter) {
            skillXpManager.fetchSkillXp();
            updateCapeInfoBox();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // on login it will add xp to the character 1 skill at a time, we know this is finished after the first game tick happens
        if (!finishedInitializingCharacter) {
            finishedInitializingCharacter = true;
            skillXpManager.fetchSkillXp();
            updateCapeInfoBox();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        capeManager.onItemContainerChanged(itemContainerChanged);
        updateCapeInfoBox();
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        capeManager.onItemSpawned(itemSpawned);
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned) {
        capeManager.onItemDespawned(itemDespawned);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        MenuAction type = MenuAction.of(event.getType());
        if (type == MenuAction.GROUND_ITEM_FIRST_OPTION || type == MenuAction.GROUND_ITEM_SECOND_OPTION ||
                type == MenuAction.GROUND_ITEM_THIRD_OPTION || type == MenuAction.GROUND_ITEM_FOURTH_OPTION ||
                type == MenuAction.GROUND_ITEM_FIFTH_OPTION || type == MenuAction.WIDGET_TARGET_ON_GROUND_ITEM) {
            final int itemId = event.getIdentifier();

            MenuEntry[] menuEntries = client.getMenuEntries();
            MenuEntry lastEntry = menuEntries[menuEntries.length - 1];

            if (config.hidePickup() && capeManager.isUntrimmedCape(itemId)) {
                lastEntry.setDeprioritized(true);
            }
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(UntrimmedConfig.CONFIG_GROUP)) {
            return;
        }

        if (finishedInitializingCharacter) {
            updateCapeInfoBox();
        }
    }

    private void updateCapeInfoBox() {
        if (!skillXpManager.hasAny99() || skillXpManager.isMaxed()) {
            hideOverlay();
            return;
        }

        if (skillXpManager.isCloseToNew99(config.warningXpRange())) {
            showOverlay(skillXpManager.closestNew99(), capeManager.capeLocation());
        } else {
            hideOverlay();
        }
    }

    private void showOverlay(Skill closestNew99, CapeStorageLocation capeStorageLocation) {
        overlayManager.remove(overlay);
        String message = "Near 99 " + closestNew99.getName() + ". " + capeStorageLocation.capeStatus();
        overlay.setMessage(message);
        overlay.setIsSafe(capeStorageLocation.isSafe());
        overlayManager.add(overlay);
    }

    private void hideOverlay() {
        overlayManager.remove(overlay);
    }

    @Provides
    UntrimmedConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(UntrimmedConfig.class);
    }

    // TODO add ability to switch between profiles like dudewheresmystuff does it
}
