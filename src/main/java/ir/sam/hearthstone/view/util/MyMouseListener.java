package ir.sam.hearthstone.view.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface MyMouseListener extends MouseListener {
    default void mousePressed(MouseEvent e) {
    }

    default void mouseReleased(MouseEvent e) {
    }

    default void mouseEntered(MouseEvent e) {
    }

    default void mouseExited(MouseEvent e) {
    }
}
