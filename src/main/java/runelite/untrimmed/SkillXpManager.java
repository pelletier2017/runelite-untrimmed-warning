package runelite.untrimmed;

import lombok.ToString;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;

import java.util.*;

@ToString
public class SkillXpManager {

    private Map<Skill, Integer> skillXp;

    private static final int LVL_99_XP = 13_034_431;

    private final Client client;

    public SkillXpManager(Client client) {
        // TODO create unit tests mocking client
        this.client = client;
        initializeSkillXp();
    }

    private void initializeSkillXp() {
        skillXp = new HashMap<>();
        for (Skill skill : Skill.values()) {
            if (skill.equals(Skill.OVERALL)) {
                continue;
            }
            skillXp.put(skill, client.getSkillExperience(skill));
        }
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

    public boolean isCloseToNew99(int remainingXp) {
        for (int xp : skillXp.values()) {
            if (xp >= LVL_99_XP - remainingXp && xp < LVL_99_XP) {
                return true;
            }
        }
        return false;
    }

    public Skill closestNew99() {
        Optional<Map.Entry<Skill, Integer>> closestSkill = this.skillXp.entrySet().stream()
                .filter(entry -> entry.getValue() < LVL_99_XP)
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .findFirst();
        return closestSkill.map(Map.Entry::getKey).get();
    }

    public void fetchSkillXp() {
        initializeSkillXp();
    }
}
