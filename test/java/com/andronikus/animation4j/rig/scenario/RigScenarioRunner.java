package com.andronikus.animation4j.rig.scenario;

import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RigScenarioRunner extends JPanel implements ActionListener {

    private boolean doTilts = false;
    private boolean torsoBox = false;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics graphics) {
        drawBackGroundSquares(graphics);

        final GraphicsContext context = new GraphicsContext();
        context.setGraphics2d((Graphics2D)graphics);
        context.setObserver(this);

        graphics.setColor(Color.CYAN);
        ((Graphics2D) graphics).setStroke(new BasicStroke());

        // Note, this is not proper. Normally don't want to call the rig like this for stop motion reasons
        rig.renderFromCenter(context, new Object(), qwertyState, 400, 500, 0);
        if (torsoBox) {
            graphics.drawRect(400 - 16, this.getHeight() - (500 + 32), 32, 64);
        }
        rig.renderFromCenter(context, new Object(), qwertyState, 800, 500, !doTilts ? 0 : Math.PI / 4);
        if (torsoBox) {
            graphics.drawRect(800 - 16, this.getHeight() - (500 + 32), 32, 64);
        }
        rig.renderFromCenter(context, new Object(), qwertyState, 400, 200, !doTilts ? 0 : Math.PI / 2);
        if (torsoBox) {
            graphics.drawRect(400 - 16, this.getHeight() - (200 + 32), 32, 64);
        }
        rig.renderFromCenter(context, new Object(), qwertyState, 800, 200, !doTilts ? 0 : Math.PI);
        if (torsoBox) {
            graphics.drawRect(800 - 16, this.getHeight() - (200 + 32), 32, 64);
        }
    }

    /**
     * Draw squares in the background. Helps with debug measurements.
     *
     * @param graphics The graphics
     */
    private void drawBackGroundSquares(Graphics graphics) {
        int tileSize = 18;

        int width = this.getWidth();
        int height = this.getHeight();

        int xCounter = 0;
        boolean isMagentaRow;
        while (xCounter * tileSize < width) {
            isMagentaRow = true;
            if (xCounter % 2 == 0) {
                isMagentaRow = false;
            }
            int yCounter = 0;
            while (yCounter * tileSize < height) {
                if (isMagentaRow) {
                    isMagentaRow = false;
                    graphics.setColor(Color.BLACK);
                } else {
                    graphics.setColor(Color.MAGENTA);
                    isMagentaRow = true;
                }

                graphics.fillRect(xCounter * tileSize, yCounter * tileSize, tileSize, tileSize);
                yCounter++;
            }

            xCounter++;
        }
    }

    /**
     * {@inheritDoc}
     */
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
            } else if (e.getKeyCode() == KeyEvent.VK_T) {
                doTilts = !doTilts;
            } else if (e.getKeyCode() == KeyEvent.VK_B) {
                torsoBox = !torsoBox;
            }
        }
    }

    public static void main(String[] args) {
        new RigScenarioRunner();
    }
}
