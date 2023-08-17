package com.andronikus.animation4j.featuredemo.interruption;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Test scenario meant to demonstrate the following features:
 *  - Keyframes on fulcrum distance multiplier
 *  - Interruptibility of an DFA state being based on the transition, not the state
 */
public class TransitionBasedInterruptibilityDemo extends JPanel implements ActionListener {

    private final RetractablePusherAnimationController controller;
    private final RetractablePusher pusher = new RetractablePusher();
    private final Timer timer;

    public TransitionBasedInterruptibilityDemo() {
        controller = new RetractablePusherAnimationController(pusher);

        final JFrame frame = new JFrame("Transition-Based Interruptibility Demo");
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
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

        final GraphicsContext context = new GraphicsContext();
        context.setGraphics2d((Graphics2D)graphics);
        context.setObserver(this);
        context.setComponentHeight(this.getHeight());

        controller.renderNext(context, new Object(), pusher, 800, 400, 0);

        graphics.setColor(Color.GREEN);
        graphics.setFont(Font.getFont(Font.DIALOG));
        ((Graphics2D) graphics).drawString("Broken: " + pusher.isBroken() + ", Extending: " + pusher.isExtending(), 50, 50);
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
            if (e.getKeyCode() == KeyEvent.VK_E) {
                pusher.setExtending(true);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                pusher.setBroken(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                pusher.setExtending(false);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                pusher.setBroken(false);
            }
        }
    }

    public static void main(String[] args) {
        new TransitionBasedInterruptibilityDemo();
    }
}
