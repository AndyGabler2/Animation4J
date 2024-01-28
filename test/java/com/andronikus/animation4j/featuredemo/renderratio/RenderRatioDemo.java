package com.andronikus.animation4j.featuredemo.renderratio;

import com.andronikus.animation4j.featuredemo.interruption.RetractablePusherAnimationController;
import com.andronikus.animation4j.featuredemo.interruption.TransitionBasedInterruptibilityDemo;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Test scenario meant to demonstrate the render ratio feature.
 */
public class RenderRatioDemo extends JPanel implements ActionListener {

    private final Timer timer;

    public RenderRatioDemo() {
        // TODO init controllers

        final JFrame frame = new JFrame("Transition-Based Interruptibility Demo");
        frame.add(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final KeyBoardListener keyBoardListener = new KeyBoardListener();
        this.addKeyListener(keyBoardListener);
        frame.addKeyListener(keyBoardListener);

        // TODO mouse listener for the slider

        timer = new Timer(30, this);
        timer.start();

        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics graphics) {

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

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
