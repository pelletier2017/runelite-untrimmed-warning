package runelite.untrimmed;

import net.runelite.api.InventoryID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CapeStorageLocation {

    // deaths office is safe
    // https://www.reddit.com/r/2007scape/comments/hodnxg/practical_confirmation_deaths_office_works_to/

    // doesnt need to be free-to-play world
    // https://www.reddit.com/r/2007scape/comments/yh2zov/comment/iuc8feo/?utm_source=share&utm_medium=web2x&context=3

    GRAVESTONE("at gravestone", AlternateInventoryId.DEATH_INVENTORY_ID, Collections.emptyList(), true),
    DEATH_OFFICE("at Death's Office", AlternateInventoryId.DEATH_INVENTORY_ID, Collections.emptyList(), true),
    ZULRAH("in death storage at Zulrah", AlternateInventoryId.DEATH_INVENTORY_ID, Arrays.asList(33, 0), true),
    HESPORI("in death storage at Hespori", AlternateInventoryId.DEATH_INVENTORY_ID, Arrays.asList(15, 16), true),
    COX("in private storage at Chambers of Xeric", 583, Collections.emptyList(), true),
    GROUND("on the ground (TELE-GRAB ONLY!)", AlternateInventoryId.UNKNOWN_INVENTORY_ID, Collections.emptyList(), true),

    UNKNOWN("at an unknown location", AlternateInventoryId.UNKNOWN_INVENTORY_ID, Collections.emptyList(), false),
    BANK("in the Bank", InventoryID.BANK.getId(), Collections.emptyList(), false),
    // TODO bank placeholder, does this affect anything?
//    BANK_PLACEHOLDER("in bank placeholder", InventoryID.BANK.getId(), false),
    INVENTORY("in the inventory", InventoryID.INVENTORY.getId(), Collections.emptyList(), false),
    EQUIPMENT("equipped", InventoryID.EQUIPMENT.getId(), Collections.emptyList(), false),

    // according to wiki these are untested
    HYDRA("in death storage at Hydra", AlternateInventoryId.DEATH_INVENTORY_ID, Arrays.asList(13, 14), false),
    VORKATH("in death storage at Vorkath", AlternateInventoryId.DEATH_INVENTORY_ID, Arrays.asList(5, 6), false),
    TOB("in death storage at Theatre of Blood", AlternateInventoryId.DEATH_INVENTORY_ID, Arrays.asList(11, 12), false),
    ;

    private final String description;
    private final int inventoryID;
    private final List<Integer> deathVarbits; // id that marks which death storage is currently in-use because they all share the same inventoryID
    private final boolean isSafe;

    CapeStorageLocation(String description, int inventoryID, List<Integer> deathVarbits, boolean isSafe) {
        this.description = description;
        this.inventoryID = inventoryID;
        this.deathVarbits = deathVarbits;
        this.isSafe = isSafe;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public String capeStatus() {
        if (isSafe) {
            return "Your untrimmed cape is SAFE because it is " + description;
        } else {
            return "Your untrimmed cape is in DANGER because it is " + description;
        }
    }

    public static boolean isCapeStorageId(int inventoryID) {
        return Arrays.stream(CapeStorageLocation.values()).anyMatch(capeStorageLocation -> inventoryID == capeStorageLocation.inventoryID);
    }

    public static CapeStorageLocation of(int inventoryID, int deathVarbit) {
        if (inventoryID == AlternateInventoryId.DEATH_INVENTORY_ID) {
            for (CapeStorageLocation location : CapeStorageLocation.values()) {
                if (location.deathVarbits.contains(deathVarbit)) {
                    return location;
                }
            }
        }
        for (CapeStorageLocation location : CapeStorageLocation.values()) {
            if (location.inventoryID == inventoryID) {
                return location;
            }
        }
        return CapeStorageLocation.UNKNOWN;
    }

    public static CapeStorageLocation fromDeathRegion(Region region) {
        if (region.equals(Region.BOSS_HESPORI)) {
            return CapeStorageLocation.HESPORI;
        } else if (region.equals(Region.BOSS_VORKATH)) {
            return CapeStorageLocation.VORKATH;
        } else if (region.equals(Region.BOSS_HYDRA)) {
            return CapeStorageLocation.HYDRA;
        } else if (region.equals(Region.BOSS_ZULRAH)) {
            return CapeStorageLocation.ZULRAH;
        } else if (region.equals(Region.RAIDS_THEATRE_OF_BLOOD)) {
            return CapeStorageLocation.TOB;
        } else {
            return CapeStorageLocation.GRAVESTONE;
        }
    }

    private static class AlternateInventoryId {
        public static final int DEATH_INVENTORY_ID = 525;
        public static final int UNKNOWN_INVENTORY_ID = -1;
    }
}