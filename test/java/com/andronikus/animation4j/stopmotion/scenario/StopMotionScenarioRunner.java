package com.andronikus.animation4j.stopmotion.scenario;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class StopMotionScenarioRunner extends JPanel implements ActionListener {

    private final QwertyHeadStopMotionController headController = new QwertyHeadStopMotionController();
    private final QwertyState qwertyState = new QwertyState();
    private final Timer timer;

    public StopMotionScenarioRunner() {
        final JFrame frame = new JFrame("QWERTY Head Animation Scenario");
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
        final BufferedImage sprite = headController.nextSprite(new Object(), qwertyState);
        int magnitude = 10;
        graphics.drawImage(sprite, 400, 150, 48 * magnitude, 32 * magnitude, this);
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
        new StopMotionScenarioRunner();
    }
}
