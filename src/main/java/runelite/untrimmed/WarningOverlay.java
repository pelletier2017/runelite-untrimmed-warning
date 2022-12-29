package runelite.untrimmed;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class WarningOverlay extends OverlayPanel {

    private final UntrimmedConfig config;
    private final Client client;

    private String message;

    private boolean isSafe;

    @Inject
    private WarningOverlay(UntrimmedConfig config, Client client) {
        this.config = config;
        this.client = client;
        this.message = "Placeholder";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIsSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add((LineComponent.builder())
                .left(message)
                .build());

        if (isSafe) {
            panelComponent.setBackgroundColor(config.safeColor());
        } else if (config.shouldFlash()) {
            if (client.getGameCycle() % 40 >= 20) {
                panelComponent.setBackgroundColor(config.flashColor1());
            } else {
                panelComponent.setBackgroundColor(config.flashColor2());
            }
        } else {
            panelComponent.setBackgroundColor(config.flashColor1());
        }

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        return panelComponent.render(graphics);
    }
}