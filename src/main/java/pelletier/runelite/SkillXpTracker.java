package pelletier.runelite;

import net.runelite.api.Client;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class SkillXpTracker {

    private final Map<String, Integer> skillXp;

    private static final int LVL_99_XP = 13_034_431;

    public SkillXpTracker(Client client) {
        // TODO grab skill xp from client
        this.skillXp = null;
        // TODO assert all skills are included and nonzero values
    }

    public boolean hasAny99() {
        for (int xp : skillXp.values()) {
            if (xp >= LVL_99_XP) {
                return true;
            }
        }
        return false;
    }

    public boolean isMaxed() {
        for (int xp : skillXp.values()) {
            if (xp < LVL_99_XP) {
                return false;
            }
        }
        return true;
    }

    public boolean isCloseToNext99(int remainingXp) {
        for (int xp : skillXp.values()) {
            if (xp < LVL_99_XP && xp >= LVL_99_XP - remainingXp) {
                return false;
            }
        }
        return true;
    }

    public String closestNext99() {
        Optional<Map.Entry<String, Integer>> closestSkill = this.skillXp.entrySet().stream()
                .filter(entry -> entry.getValue() < LVL_99_XP)
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .findFirst();
        return closestSkill.map(Map.Entry::getKey).get();
    }

    // TODO replace String with skills enum
}
