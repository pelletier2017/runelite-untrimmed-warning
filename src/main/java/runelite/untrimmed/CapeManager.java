package runelite.untrimmed;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.config.ConfigManager;

import java.util.*;
import java.util.stream.Collectors;

//@ToString
@Slf4j
public class CapeManager {

    private Client client;

    private List<Integer> untrimmedCapeIds = new ArrayList<>(Arrays.asList(
            ItemID.FARMING_CAPE,
            ItemID.AGILITY_CAPE,
            ItemID.ATTACK_CAPE,
            ItemID.CONSTRUCT_CAPE,
            ItemID.COOKING_CAPE,
            ItemID.CRAFTING_CAPE,
            ItemID.DEFENCE_CAPE,
            ItemID.FIREMAKING_CAPE,
            ItemID.FISHING_CAPE,
            ItemID.FLETCHING_CAPE,
            ItemID.HERBLORE_CAPE,
            ItemID.HITPOINTS_CAPE,
            ItemID.HUNTER_CAPE,
            ItemID.MAGIC_CAPE,
            ItemID.MINING_CAPE,
            ItemID.PRAYER_CAPE,
            ItemID.RANGING_CAPE,
            ItemID.RUNECRAFT_CAPE,
            ItemID.SLAYER_CAPE,
            ItemID.SMITHING_CAPE,
            ItemID.STRENGTH_CAPE,
            ItemID.THIEVING_CAPE,
            ItemID.WOODCUTTING_CAPE
    ));

    private CapeTracker capeTracker;

    private static final String CAPE_TRACKER_KEY = "capeTracker";

    private DeathStatus deathStatus = null;

    public CapeManager(Client client, ConfigManager configManager) {
        this.client = client;
        this.capeTracker = new CapeTracker(configManager);
    }

    public void shutDown() {
        capeTracker.persistData();
    }

    public CapeStorageLocation capeLocation() {
        List<CapeStorageLocation> capeLocations = new ArrayList<>();

        for (Map.Entry<CapeStorageLocation, Integer> entry : capeTracker.entrySet()) {
            CapeStorageLocation location = entry.getKey();
            int count = entry.getValue();
            if (count > 0) {
                capeLocations.add(location);
            }
        }

        if (capeLocations.isEmpty()) {
            return CapeStorageLocation.UNKNOWN;
        }
        log.debug("CapeLocations=" + capeLocations);

        List<CapeStorageLocation> safeLocations = capeLocations.stream()
                .filter(CapeStorageLocation::isSafe)
                .collect(Collectors.toList());

        List<CapeStorageLocation> dangerousLocations = capeLocations.stream()
                .filter(location -> !location.isSafe())
                .collect(Collectors.toList());

        if (!dangerousLocations.isEmpty()) {
            return dangerousLocations.get(0);
        } else {
            return safeLocations.get(0);
        }
    }

    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (deathStatus != null) {
            // death storage container does not get onItemContainerChanged event so we move inventory items to death after reviving
            moveInventoryItemsToDeath();
            deathStatus = null;
        }

        ItemContainer itemContainer = itemContainerChanged.getItemContainer();
        int deathVarpId = 261;
        int deathbankVarpValue = client.getVarpValue(deathVarpId);
        log.debug("deathBankVarpValue=" + deathbankVarpValue);
        CapeStorageLocation location = CapeStorageLocation.of(itemContainer.getId(), deathbankVarpValue);
        capeTracker.put(location, untrimmedCapeCount(itemContainer));
    }

    private void moveInventoryItemsToDeath() {
        Item[] itemsBeforeDeath = deathStatus.getItems();
        Region regionBeforeDeath = deathStatus.getDeathRegion();
        CapeStorageLocation capeStorageLocation = CapeStorageLocation.fromDeathRegion(regionBeforeDeath);

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) {
            log.error("Inventory was null when checking what items remained after reviving");
            return;
        }
        Item[] itemsAfterReviving = inventory.getItems();

        int countBeforeDeath = untrimmedCapeCount(itemsBeforeDeath);
        int countAfterReviving = untrimmedCapeCount(itemsAfterReviving);
        int countOfCapesMovedToDeath = countBeforeDeath - countAfterReviving;

        capeTracker.put(capeStorageLocation, countOfCapesMovedToDeath);
    }

    private int untrimmedCapeCount(ItemContainer itemContainer) {
        Item[] items = itemContainer.getItems();
        return untrimmedCapeCount(items);
    }

    private int untrimmedCapeCount(Item[] items) {
        for (Item item : items) {
            if (isUntrimmedCape(item.getId())) {
                // doesnt need to keep checking all items because only 1 untrimmed cape is allowed on any account
                return item.getQuantity();
            }
        }
        return 0;
    }

    public boolean isUntrimmedCape(int itemId) {
        return untrimmedCapeIds.contains(itemId);
    }

    public void onActorDeath(ActorDeath actorDeath) {
        WorldPoint location =
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
        Region region = Region.get(location.getRegionID());

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) {
            log.error("Died and inventory was null.");
            return;
        }
        Item[] items = inventory.getItems();
        deathStatus = new DeathStatus(items, region);
    }

    public void onItemSpawned(ItemSpawned itemSpawned) {
        TileItem item = itemSpawned.getItem();

        if (isUntrimmedCape(item.getId())) {
            capeTracker.put(CapeStorageLocation.GROUND, item.getQuantity());
        }
    }

    public void onItemDespawned(ItemDespawned itemDespawned) {
        TileItem item = itemDespawned.getItem();

        if (isUntrimmedCape(item.getId())) {
            capeTracker.put(CapeStorageLocation.GROUND, 0);
        }
    }

    class DeathStatus {
        private Item[] items;
        private Region deathRegion;

        public DeathStatus(Item[] items, Region deathRegion) {
            this.items = items;
            this.deathRegion = deathRegion;
        }

        public Item[] getItems() {
            return items;
        }

        public Region getDeathRegion() {
            return deathRegion;
        }
    }

}


