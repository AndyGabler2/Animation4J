package com.andronikus.animation4j.featuredemo.renderratio;

import com.andronikus.animation4j.animation.scenario.QwertyAnimationController;
import com.andronikus.animation4j.featuredemo.interruption.RetractablePusher;
import com.andronikus.animation4j.featuredemo.interruption.RetractablePusherAnimationController;
import com.andronikus.animation4j.rig.graphics.GraphicsContext;
import com.andronikus.animation4j.stopmotion.scenario.QwertyHeadStopMotionController;
import com.andronikus.animation4j.stopmotion.scenario.QwertyState;
import com.andronikus.animation4j.util.RenderRatio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Test scenario meant to demonstrate the render ratio feature.
 */
public class RenderRatioDemo extends JPanel {

    private final Timer timer;

    // Slider variables
    private final JSlider widthSlider;
    private final JSlider heightSlider;

    // Control variables for the head display on screen
    private final QwertyState robotHeadState = new QwertyState();
    private final QwertyHeadStopMotionController robotHeadStopMotionController = new QwertyHeadStopMotionController();
    private final RenderRatio robotHeadRenderRatio = new RenderRatio(
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT,
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT
    );
    private final JRadioButton robotHeadRadioButton = new JRadioButton("QWERTY Head");
    private int robotHeadWidthPercentage = 100;
    private int robotHeadHeightPercentage = 100;

    // Control variables for the entire robot on screen
    private final QwertyState embodiedRobotState = new QwertyState();
    private final QwertyAnimationController embodiedRobotAnimationController = new QwertyAnimationController(embodiedRobotState);
    private final RenderRatio embodiedRobotRenderRatio = new RenderRatio(
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT,
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT
    );
    private final JRadioButton embodiedRobotRadioButton = new JRadioButton("Robot");
    private int embodiedRobotWidthPercentage = 100;
    private int embodiedRobotHeightPercentage = 100;

    // Control variables for the retractable pusher on screen
    private final RetractablePusher pusherState = new RetractablePusher();
    private final RetractablePusherAnimationController pusherAnimationController = new RetractablePusherAnimationController(pusherState);
    private final RenderRatio pusherRenderRatio = new RenderRatio(
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT,
        RenderRatio.DEFAULT_RESOLUTION_WIDTH,
        RenderRatio.DEFAULT_RESOLUTION_HEIGHT
    );
    private final JRadioButton pusherRadioButton = new JRadioButton("Pusher");
    private int pusherWidthPercentage = 100;
    private int pusherHeightPercentage = 100;

    public RenderRatioDemo() {

        final JFrame frame = new JFrame("Render Ratio Demo");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final BorderLayout mainLayout = new BorderLayout();
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 0));

        // TODO rotation slider?
        buttonPanel.add(new JLabel("Width Ratio"));
        final RenderRatioSlider changeListener = new RenderRatioSlider();
        widthSlider = new JSlider(0, 200, 100);
        widthSlider.addChangeListener(changeListener);
        buttonPanel.add(widthSlider);
        buttonPanel.add(new JLabel("Height Ratio"));
        heightSlider = new JSlider(0, 200, 100);
        heightSlider.addChangeListener(changeListener);
        buttonPanel.add(heightSlider);

        final ButtonGroup radioButtonGroup = new ButtonGroup();
        final SelectionListener selectionListener = new SelectionListener();
        robotHeadRadioButton.setSelected(true);
        buttonPanel.add(robotHeadRadioButton);
        buttonPanel.add(embodiedRobotRadioButton);
        buttonPanel.add(pusherRadioButton);
        radioButtonGroup.add(robotHeadRadioButton);
        radioButtonGroup.add(embodiedRobotRadioButton);
        radioButtonGroup.add(pusherRadioButton);
        robotHeadRadioButton.addItemListener(selectionListener);
        embodiedRobotRadioButton.addItemListener(selectionListener);
        pusherRadioButton.addItemListener(selectionListener);

        mainLayout.addLayoutComponent(buttonPanel, BorderLayout.WEST);
        mainLayout.addLayoutComponent(this, BorderLayout.CENTER);
        frame.setLayout(mainLayout);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "H-Press");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released H"), "H-Release");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "S-Press");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "S-Release");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "E-Press");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released E"), "E-Release");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "B-Press");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released B"), "B-Release");
        this.getActionMap().put("H-Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyPressed("H");
            }
        });
        this.getActionMap().put("H-Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyReleased("H");
            }
        });
        this.getActionMap().put("S-Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyPressed("S");
            }
        });
        this.getActionMap().put("S-Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyReleased("S");
            }
        });
        this.getActionMap().put("E-Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyPressed("E");
            }
        });
        this.getActionMap().put("E-Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyReleased("E");
            }
        });
        this.getActionMap().put("B-Press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyPressed("B");
            }
        });
        this.getActionMap().put("B-Release", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyReleased("B");
            }
        });
        
        frame.add(buttonPanel);
        frame.add(this);
        frame.setVisible(true);

        timer = new Timer(30, new TimerEventHandler());
        timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

        BufferedImage headSprite = robotHeadStopMotionController.nextSprite(new Object(), robotHeadState);
        graphics.drawImage(
            headSprite, 0, 0, robotHeadRenderRatio.scaleHorizontal(350),
            robotHeadRenderRatio.scaleVertical(350), this
        );

        final GraphicsContext graphicsContext = new GraphicsContext();
        graphicsContext.setGraphics2d((Graphics2D) graphics);
        graphicsContext.setObserver(this);
        embodiedRobotAnimationController.forceSetRenderRatio(embodiedRobotRenderRatio);
        embodiedRobotAnimationController.renderNext(
            graphicsContext, new Object(), embodiedRobotState, 600, 600, 0
        );

        pusherAnimationController.forceSetRenderRatio(pusherRenderRatio);
        pusherAnimationController.renderNext(
            graphicsContext, new Object(), pusherState, 950, 250, 0
        );
    }

    public void keyPressed(String keyName) {
        if (robotHeadRadioButton.isSelected()) {
            if (keyName.equalsIgnoreCase("h")) {
                robotHeadState.setQwertyHappy(true);
            } else if (keyName.equalsIgnoreCase("s")) {
                robotHeadState.setQwertySad(true);
            }
        } else if (embodiedRobotRadioButton.isSelected()) {
            if (keyName.equalsIgnoreCase("h")) {
                embodiedRobotState.setQwertyHappy(true);
            } else if (keyName.equalsIgnoreCase("s")) {
                embodiedRobotState.setQwertySad(true);
            }
        } else {
            if (keyName.equalsIgnoreCase("e")) {
                pusherState.setExtending(true);
            } else if (keyName.equalsIgnoreCase("b")) {
                pusherState.setBroken(true);
            }
        }
    }
    public void keyReleased(String keyName) {
        if (robotHeadRadioButton.isSelected()) {
            if (keyName.equalsIgnoreCase("h")) {
                robotHeadState.setQwertyHappy(false);
            } else if (keyName.equalsIgnoreCase("s")) {
                robotHeadState.setQwertySad(false);
            }
        } else if (embodiedRobotRadioButton.isSelected()) {
            if (keyName.equalsIgnoreCase("h")) {
                embodiedRobotState.setQwertyHappy(false);
            } else if (keyName.equalsIgnoreCase("s")) {
                embodiedRobotState.setQwertySad(false);
            }
        } else {
            if (keyName.equalsIgnoreCase("e")) {
                pusherState.setExtending(false);
            } else if (keyName.equalsIgnoreCase("b")) {
                pusherState.setBroken(false);
            }
        }
    }

    private class TimerEventHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            repaint();
        }
    }

    private class RenderRatioSlider implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            RenderRatio renderRatioToAdjust;
            int widthPercentage = widthSlider.getValue();
            int heightPercentage = heightSlider.getValue();
            // Need to do separate blocks since width/height sliders being set in code activate this method
            if (event.getSource() == widthSlider) {
                if (robotHeadRadioButton.isSelected()) {
                    robotHeadWidthPercentage = widthPercentage;
                    renderRatioToAdjust = robotHeadRenderRatio;
                } else if (embodiedRobotRadioButton.isSelected()) {
                    embodiedRobotWidthPercentage = widthPercentage;
                    renderRatioToAdjust = embodiedRobotRenderRatio;
                } else {
                    pusherWidthPercentage = widthPercentage;
                    renderRatioToAdjust = pusherRenderRatio;
                }
            } else {
                if (robotHeadRadioButton.isSelected()) {
                    robotHeadHeightPercentage = heightPercentage;
                    renderRatioToAdjust = robotHeadRenderRatio;
                } else if (embodiedRobotRadioButton.isSelected()) {
                    embodiedRobotHeightPercentage = heightPercentage;
                    renderRatioToAdjust = embodiedRobotRenderRatio;
                } else {
                    pusherHeightPercentage = heightPercentage;
                    renderRatioToAdjust = pusherRenderRatio;
                }
            }

            renderRatioToAdjust.calculate(
                RenderRatio.DEFAULT_RESOLUTION_WIDTH * widthPercentage / 100,
                RenderRatio.DEFAULT_RESOLUTION_HEIGHT * heightPercentage / 100
            );
        }
    }

    private class SelectionListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getSource() == robotHeadRadioButton) {
                widthSlider.setValue(robotHeadWidthPercentage);
                heightSlider.setValue(robotHeadHeightPercentage);
            } else if (event.getSource() == embodiedRobotRadioButton) {
                widthSlider.setValue(embodiedRobotWidthPercentage);
                heightSlider.setValue(embodiedRobotHeightPercentage);
            } else if (event.getSource() == pusherRadioButton) {
                widthSlider.setValue(pusherWidthPercentage);
                heightSlider.setValue(pusherHeightPercentage);
            }
        }
    }

    public static void main(String[] args) {
        new RenderRatioDemo();
    }
}
