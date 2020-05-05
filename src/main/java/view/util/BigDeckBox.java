package view.util;

import view.model.BigDeckOverview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static view.util.Constant.*;

public class BigDeckBox extends Box<BigDeckOverview, BigDeckBox.BigDeckViewer, DeckActionListener> {
    public BigDeckBox(int width, int height, JPanel parent, DeckActionListener deckActionListener) {
        super(width, height, parent, deckActionListener, BIG_DECK_WIDTH, BIG_DECK_HEIGHT, BIG_DECK_SPACE);
    }

    @Override
    protected BigDeckViewer createNew(BigDeckOverview overview) {
        return new BigDeckViewer(overview);
    }


    public class BigDeckViewer extends JPanel implements MouseListener {
        private final BigDeckOverview bigDeckOverview;

        private BigDeckViewer(BigDeckOverview bigDeckOverview) {
            this.bigDeckOverview = bigDeckOverview;
            this.setSize(Constant.BIG_DECK_WIDTH, Constant.BIG_DECK_HEIGHT);
            this.setOpaque(false);
            this.addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            bigDeckOverview.paint(g);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (action != null)
                    action.action(bigDeckOverview.getName());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
