package pelletier.runelite;

import net.runelite.api.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UntrimmedCapeTracker {

    private Client client;

    public UntrimmedCapeTracker(Client client) {
        this.client = client;
    }

    public enum StorageLocation {

        // deaths office is safe
        // https://www.reddit.com/r/2007scape/comments/hodnxg/practical_confirmation_deaths_office_works_to/

        // doesnt need to be free-to-play world
        // https://www.reddit.com/r/2007scape/comments/yh2zov/comment/iuc8feo/?utm_source=share&utm_medium=web2x&context=3

        DEATH("Death's Office", true),
        ZULRAH("Zulrah", true),
        HESPORI("Hespori", true),
        COX("Chambers of Xeric", true),
        // TODO can runelite tell if an item is on the ground visible and owned by you?
        GROUND("On the ground (TELEGRAB ONLY!)", true),

        NOWHERE("Unable to locate cape", false),
        BANK_PLACEHOLDER("Death's Office", false),
        BANK("Bank", false),
        INVENTORY("Inventory", false),

        // according to wiki these are untested
        HYDRA("Hydra", false),
        VORKATH("Vorkath", false),
        TOB("Theatre of Blood", false),
        TOA("Tombs of Amascut", false),
        ;

        private final String name;
        private final boolean isSafe;

        StorageLocation(String name, boolean isSafe) {
            this.name = name;
            this.isSafe = isSafe;
        }

        public String getName() {
            return name;
        }

        public boolean isSafe() {
            return isSafe;
        }

    }

    // TODO
    private boolean hasBankPlaceholder() {
        return false;
    }

    // TODO look at where is my stuff plugin to see where everything could be
    private boolean inBank() {
        return false;
    }

    private boolean inInventory() {
        return false;
    }

    private boolean inDeathStorage() {
        return false;
    }

    private boolean inHesporiStorage() {
        return false;
    }

    private boolean inZulrahStorage() {
        return false;
    }

    private boolean inVorkathStorage() {
        return false;
    }

    public StorageLocation capeLocation() {
        List<StorageLocation> capeLocations = new ArrayList<>();

        // TODO add methods for all the enums
        if (hasBankPlaceholder()) {
            capeLocations.add(StorageLocation.BANK_PLACEHOLDER);
        } else if (inBank()) {
            capeLocations.add(StorageLocation.BANK);
        } else if (inInventory()) {
            capeLocations.add(StorageLocation.INVENTORY);
        } else if (inDeathStorage()) {
            capeLocations.add(StorageLocation.DEATH);
        } else if (inHesporiStorage()) {
            capeLocations.add(StorageLocation.HESPORI);
        } else if (inZulrahStorage()) {
            capeLocations.add(StorageLocation.ZULRAH);
        } else if (inVorkathStorage()) {
            capeLocations.add(StorageLocation.VORKATH);
        }

        if (capeLocations.isEmpty()) {
            return StorageLocation.NOWHERE;
        }

        List<StorageLocation> safeLocations = capeLocations.stream()
                .filter(StorageLocation::isSafe)
                .collect(Collectors.toList());

        List<StorageLocation> dangerousLocations = capeLocations.stream()
                .filter(location -> !location.isSafe())
                .collect(Collectors.toList());

        if (!dangerousLocations.isEmpty()) {
            return dangerousLocations.get(0);
        } else {
            return safeLocations.get(0);
        }
    }

    public List<String> dangerousStorage() {



        return null;
    }
}

