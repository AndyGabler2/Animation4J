package com.andronikus.animation4j.rig.scenario;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RigScenarioRunner extends JPanel implements ActionListener {

    private final QwertyAnimationRig rig;
    private final QwertyState qwertyState = new QwertyState();
    private final Timer timer;

    public RigScenarioRunner() {
        rig = new QwertyAnimationRig(qwertyState);

        final JFrame frame = new JFrame("QWERTY Rig Scenario");
        frame.add(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final KeyBoardListener keyBoardListener = new KeyBoardListener();
        this.addKeyListener(keyBoardListener);
        frame.addKeyListener(keyBoardListener);

        timer = new Timer(30, this);
        timer.start();

        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        final GraphicsContext context = new GraphicsContext();
        context.setGraphics2d((Graphics2D)graphics);
        context.setObserver(this);
        context.setComponentHeight(this.getHeight());

        // Note, this is not proper.
        rig.renderFromCenter(context, new Object(), qwertyState, 400, 750, 0);
        rig.renderFromCenter(context, new Object(), qwertyState, 800, 750, Math.PI / 4);
        rig.renderFromCenter(context, new Object(), qwertyState, 400, 305, Math.PI / 2);
        rig.renderFromCenter(context, new Object(), qwertyState, 800, 305, Math.PI);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private class KeyBoardListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_H) {
                qwertyState.setQwertyHappy(true);
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                qwertyState.setQwertySad(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_H) {
                qwertyState.setQwertyHappy(false);
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                qwertyState.setQwertySad(false);
            }
        }
    }

    public static void main(String[] args) {
        new RigScenarioRunner();
    }
}
