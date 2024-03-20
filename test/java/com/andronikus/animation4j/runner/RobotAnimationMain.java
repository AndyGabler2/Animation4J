package com.andronikus.animation4j.runner;

import com.andronikus.animation4j.animation.scenario.QwertyAnimationController;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RobotAnimationMain extends JPanel implements ActionListener {

    private final QwertyAnimationController controller;
    private final QwertyState qwertyState = new QwertyState();
    private final Timer timer;

    public RobotAnimationMain() {
        controller = new QwertyAnimationController(qwertyState);

        final JFrame frame = new JFrame("QWERTY Animation Scenario");
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
        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

        final GraphicsContext context = new GraphicsContext();
        context.setGraphics2d((Graphics2D)graphics);
        context.setObserver(this);

        controller.renderNext(context, new Object(), qwertyState, 800, 400, 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        new RobotAnimationMain();
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
}
