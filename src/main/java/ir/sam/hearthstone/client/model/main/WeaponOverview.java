package ir.sam.hearthstone.client.model.main;

import ir.sam.hearthstone.client.resource_manager.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WeaponOverview extends CardOverview {
    private final static BufferedImage closeWeapon = ImageLoader.getInstance().getEffect("close weapon");
    private boolean hasAttack;

    public WeaponOverview(String name, String imageName, String toolkit) {
        super(name, imageName, toolkit);
    }

    @Override
    public void paint(Graphics2D graphics2D) {
        if (hasAttack) {
            graphics2D.drawImage(small, 0, 0, null);
            int w = small.getWidth(), h = small.getHeight();
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(graphics2D.getFont().deriveFont(Font.BOLD));
            graphics2D.setFont(graphics2D.getFont().deriveFont(21.0F));
            graphics2D.drawString("" + hp, 73 * w / 100, 77 * h / 90);
            graphics2D.drawString("" + att, 14 * w / 100, 73 * h / 90);
        } else graphics2D.drawImage(closeWeapon, 0, 0, null);
    }
}