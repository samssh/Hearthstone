package ir.sam.hearthstone.view.util;

import ir.sam.hearthstone.view.model.BigDeckOverview;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BigDeckBox extends Box<BigDeckOverview, BigDeckBox.BigDeckViewer> {
    public BigDeckBox(int width, int height, JPanel parent, MyActionListener deckActionListener) {
        super(width, height, parent, deckActionListener, Constant.BIG_DECK_WIDTH, Constant.BIG_DECK_HEIGHT, Constant.BIG_DECK_SPACE);
    }

    @Override
    protected BigDeckViewer createNew() {
        return new BigDeckViewer();
    }

    @Override
    protected void set(BigDeckViewer bigDeckViewer, BigDeckOverview bigDeckOverview) {
        bigDeckViewer.setBigDeckOverview(bigDeckOverview);
    }

    @Override
    protected BigDeckViewer[][] createTArray(int i, int j) {
        return new BigDeckViewer[i][j];
    }

    class BigDeckViewer extends JPanel implements MyMouseListener {
        @Setter
        private BigDeckOverview bigDeckOverview;

        private BigDeckViewer() {
            this.setSize(Constant.BIG_DECK_WIDTH, Constant.BIG_DECK_HEIGHT);
            this.setOpaque(false);
            this.addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bigDeckOverview != null)
                bigDeckOverview.paint((Graphics2D) g);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (action != null && bigDeckOverview != null)
                    action.action(bigDeckOverview.getName());
            }
        }
    }
}
