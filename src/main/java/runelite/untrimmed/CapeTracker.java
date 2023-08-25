package runelite.untrimmed;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// this class wraps a Map to be able to store it in config without worrying about generics when recovering it from configManager
@Slf4j
public class CapeTracker implements Serializable {

    private Map<CapeStorageLocation, Integer> capesFound = new HashMap<>();

    private ConfigManager configManager;

    public CapeTracker(ConfigManager configManager) {
        this.configManager = configManager;
        startUp();
    }

    public void persistData() {
        log.debug("shutting down");
        log.debug("CapesFound=" + capesFound);
        for (CapeStorageLocation location : CapeStorageLocation.values()) {
            String key = "capetracker." + location.name();
            configManager.setConfiguration(UntrimmedConfig.CONFIG_GROUP, key, capesFound.getOrDefault(location, 0));
        }
    }

    private void startUp() {
        log.debug("starting up");
        for (CapeStorageLocation location : CapeStorageLocation.values()) {
            String key = "capetracker." + location.name();
            Integer count = configManager.getConfiguration(UntrimmedConfig.CONFIG_GROUP, key, Integer.class);
            if (count == null) {
                capesFound.put(location, 0);
            } else {
                capesFound.put(location, count);
            }
        }
        log.debug("CapesFound=" + capesFound);
    }

    public int put(CapeStorageLocation capeStorageLocation, int quantity) {
        Integer oldValue = capesFound.put(capeStorageLocation, quantity);
        persistData();
        if (oldValue == null) {
            return 0;
        } else {
            return oldValue;
        }
    }

    public Set<Map.Entry<CapeStorageLocation, Integer>> entrySet() {
        return capesFound.entrySet();
    }
}
