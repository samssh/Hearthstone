package ir.sam.hearthstone.view.panel;

import ir.sam.hearthstone.client.Actions.ShopAction;
import ir.sam.hearthstone.resource_manager.Config;
import ir.sam.hearthstone.resource_manager.ImageLoader;
import ir.sam.hearthstone.view.graphics_engine.AnimationManger;
import ir.sam.hearthstone.view.graphics_engine.effects.LinearMotion;
import ir.sam.hearthstone.view.graphics_engine.effects.OverviewPainter;
import ir.sam.hearthstone.view.graphics_engine.effects.Rotary;
import ir.sam.hearthstone.view.model.CardOverview;
import ir.sam.hearthstone.view.util.CardBox;
import ir.sam.hearthstone.view.util.Constant;
import lombok.Setter;
import ir.sam.hearthstone.resource_manager.ConfigFactory;
import ir.sam.hearthstone.util.Updatable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ShopPanel extends JPanel implements Updatable {
    private CardBox sell, buy;
    @Setter
    private int coins;
    private JButton exit, back, backMainMenu;
    private final BufferedImage image;
    private int coinX, coinY;
    private int sellX, sellY, sellWidth, sellHeight;
    private int buyX, buyY, buyWidth, buyHeight;
    private int exitX, exitY, exitWidth, exitHeight, exitSpace;
    private final ShopAction shopAction;
    private final AnimationManger animationManger;

    public ShopPanel(ShopAction shopAction) {
        setLayout(null);
        this.shopAction = shopAction;
        this.image = ImageLoader.getInstance().getBackground("shop");
        config();
        initialize();
        this.add(sell);
        this.add(buy);
        this.add(exit);
        this.add(back);
        this.add(backMainMenu);
        animationManger = new AnimationManger();
    }

    private void initialize() {
        initializeBack();
        initializeBackMainMenu();
        initializeExit();
        initializeSell();
        initializeBuy();
    }

    private void initializeSell() {
        sell = new CardBox(sellWidth, sellHeight, this, shopAction::sell, true);
        sell.setLocation(sellX, sellY);
        sell.setTitle("card you can sell");
    }

    private void initializeBuy() {
        buy = new CardBox(buyWidth, buyHeight, this, shopAction::buy, true);
        buy.setLocation(buyX, buyY);
        buy.setTitle("card you can buy");
    }

    private void initializeExit() {
        exit = new JButton("exit");
        exit.setBounds(exitX, exitY, exitWidth, exitHeight);
        exit.addActionListener(e -> shopAction.exit());
        Constant.makeTransparent(exit);
    }

    private void initializeBack() {
        back = new JButton("back");
        int y = exitY - 2 * (exitHeight + exitSpace);
        back.setBounds(exitX, y, exitWidth, exitHeight);
        back.addActionListener(e -> shopAction.back());
        Constant.makeTransparent(back);
    }

    private void initializeBackMainMenu() {
        backMainMenu = new JButton("back to main menu");
        int y = exitY - (exitHeight + exitSpace);
        backMainMenu.setBounds(exitX, y, exitWidth, exitHeight);
        backMainMenu.addActionListener(e -> shopAction.backMainMenu());
        Constant.makeTransparent(backMainMenu);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(17F));
        g.drawString("\u0024" + coins, coinX, coinY);
        animationManger.paint((Graphics2D) g);
    }

    private void config() {
        Config shopConfig = ConfigFactory.getInstance().getConfig("SHOP_CONFIG");
        setBounds(shopConfig.getProperty(Integer.class, "x"),
                shopConfig.getProperty(Integer.class, "y"),
                shopConfig.getProperty(Integer.class, "width"),
                shopConfig.getProperty(Integer.class, "height"));
        sellX = shopConfig.getProperty(Integer.class, "sellX");
        sellY = shopConfig.getProperty(Integer.class, "sellY");
        sellWidth = shopConfig.getProperty(Integer.class, "sellWidth");
        sellHeight = shopConfig.getProperty(Integer.class, "sellHeight");
        buyX = shopConfig.getProperty(Integer.class, "buyX");
        buyY = shopConfig.getProperty(Integer.class, "buyY");
        exitX = shopConfig.getProperty(Integer.class, "exitX");
        exitY = shopConfig.getProperty(Integer.class, "exitY");
        buyWidth = shopConfig.getProperty(Integer.class, "buyWidth");
        buyHeight = shopConfig.getProperty(Integer.class, "buyHeight");
        exitWidth = shopConfig.getProperty(Integer.class, "exitWidth");
        exitHeight = shopConfig.getProperty(Integer.class, "exitHeight");
        exitSpace = shopConfig.getProperty(Integer.class, "exitSpace");
        coinX = shopConfig.getProperty(Integer.class, "coinX");
        coinY = shopConfig.getProperty(Integer.class, "coinY");
    }

    public void setSell(List<CardOverview> sellList) {
        sell.setModels(sellList);
    }

    public void setBuy(List<CardOverview> buyList) {
        buy.setModels(buyList);
    }

    public void putShopEvent(String cardName, String type, int coins) {
        if ("buy".equalsIgnoreCase(type)) AnimationManger.moveCard(cardName, buy, sell, animationManger);
        else if ("sell".equalsIgnoreCase(type)) AnimationManger.moveCard(cardName, sell, buy, animationManger);
        this.coins = coins;
    }

    @Override
    public void update() {
        shopAction.update();
    }
}

