package com.company;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Labyrinth {

    Labyrinth() {
        new FrameForLabyrinth();
    }

    static class LabyrinthUtils {
        private ArrayList<ArrayList<Integer>> labyrinth = new ArrayList<>();
        private ArrayList<int[]> way = new ArrayList<>();

        void setLabyrinth(ArrayList<ArrayList<Integer>> labyrinth) {
            this.labyrinth = labyrinth;
        }

        ArrayList<int[]> getWay() {
            return way;
        }

        boolean hasAWidthGreaterThanOne() {
            for (int i = 1; i < labyrinth.size(); i++) {
                for (int j = 1; j < labyrinth.get(i).size(); j++) {
                    if (labyrinth.get(i).get(j) == 0 &&
                            labyrinth.get(i - 1).get(j - 1) == 0 &&
                            labyrinth.get(i - 1).get(j) == 0 &&
                            labyrinth.get(i).get(j - 1) == 0
                    ) {
                        return true;
                    }
                }
            }
            return false;
        }

        void searchForAWay() {
            int[] startCoordinates = new int[2];
            for (int i = 0; i < labyrinth.size(); i++) {
                for (int j = 0; j < labyrinth.get(i).size(); j++) {
                    if (labyrinth.get(i).get(j) == 2) {
                        startCoordinates[0] = i;
                        startCoordinates[1] = j;
                    }
                }
            }
            way.add(startCoordinates);
            if (labyrinth.get(startCoordinates[0] + 1).get(startCoordinates[1]) == 0) {
                way.add(new int[]{startCoordinates[0] + 1, startCoordinates[1]});
                searchForAWay(startCoordinates[0], startCoordinates[1], startCoordinates[0] + 1, startCoordinates[1]);
            }
            if (labyrinth.get(startCoordinates[0] - 1).get(startCoordinates[1]) == 0) {
                way.add(new int[]{startCoordinates[0] - 1, startCoordinates[1]});
                searchForAWay(startCoordinates[0], startCoordinates[1], startCoordinates[0] - 1, startCoordinates[1]);
            }
            if (labyrinth.get(startCoordinates[0]).get(startCoordinates[1] + 1) == 0) {
                way.add(new int[]{startCoordinates[0], startCoordinates[1] + 1});
                searchForAWay(startCoordinates[0], startCoordinates[1], startCoordinates[0], startCoordinates[1] + 1);
            }
            if (labyrinth.get(startCoordinates[0]).get(startCoordinates[1] - 1) == 0) {
                way.add(new int[]{startCoordinates[0], startCoordinates[1] - 1});
                searchForAWay(startCoordinates[0], startCoordinates[1], startCoordinates[0], startCoordinates[1] - 1);
            }
        }

        private void searchForAWay(int lastI, int lastJ, int newI, int newJ) {
            if (newI + 1 != lastI && labyrinth.get(way.get(way.size() - 1)[0]).get(way.get(way.size() - 1)[1]) != 3) {
                if (labyrinth.get(newI + 1).get(newJ) == 0) {
                    way.add(new int[]{newI + 1, newJ});
                    searchForAWay(newI, newJ, newI + 1, newJ);
                } else if (labyrinth.get(newI + 1).get(newJ) == 3) {
                    way.add(new int[]{newI + 1, newJ});
                }
            }
            if (newI - 1 != lastI && labyrinth.get(way.get(way.size() - 1)[0]).get(way.get(way.size() - 1)[1]) != 3) {
                if (labyrinth.get(newI - 1).get(newJ) == 0) {
                    way.add(new int[]{newI - 1, newJ});
                    searchForAWay(newI, newJ, newI - 1, newJ);
                } else if (labyrinth.get(newI - 1).get(newJ) == 3) {
                    way.add(new int[]{newI - 1, newJ});
                }
            }
            if (newJ + 1 != lastJ && labyrinth.get(way.get(way.size() - 1)[0]).get(way.get(way.size() - 1)[1]) != 3) {
                if (labyrinth.get(newI).get(newJ + 1) == 0) {
                    way.add(new int[]{newI, newJ + 1});
                    searchForAWay(newI, newJ, newI, newJ + 1);
                } else if (labyrinth.get(newI).get(newJ + 1) == 3) {
                    way.add(new int[]{newI, newJ + 1});
                }
            }
            if (newJ - 1 != lastJ && labyrinth.get(way.get(way.size() - 1)[0]).get(way.get(way.size() - 1)[1]) != 3) {
                if (labyrinth.get(newI).get(newJ - 1) == 0) {
                    way.add(new int[]{newI, newJ - 1});
                    searchForAWay(newI, newJ, newI, newJ - 1);
                } else if (labyrinth.get(newI).get(newJ - 1) == 3) {
                    way.add(new int[]{newI, newJ - 1});
                }
            }
            if (labyrinth.get(way.get(way.size() - 1)[0]).get(way.get(way.size() - 1)[1]) != 3) {
                way.remove(way.get(way.size() - 1));
            }
        }
    }

    static class DrawLabyrinthElement extends JComponent {
        static int BORDER = 0;
        static int START = 1;
        static int FINISH = 2;
        static int WAY = 3;
        static int EXIT_RIGHT = 4;
        static int EXIT_LEFT = 5;
        static int EXIT_UP = 6;
        static int EXIT_DOWN = 7;
        private final int state;

        DrawLabyrinthElement(int state) {
            this.state = state;
        }

        @Override
        public void paint(Graphics g) {
            if (state == BORDER) {
                for (int i = 0; i < 15; i++) {
                    g.setColor(new Color(10 * i, 10 * i, 10 * i));
                    g.fillPolygon(
                            new int[]{i / 2, 16 - i / 2, 16 - i / 2, i / 2},
                            new int[]{i / 2, i / 2, 15 - i / 2, 15 - i / 2},
                            4
                    );
                }
            } else if (state == START) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
                for (int i = 0; i < 15; i++) {
                    g.setColor(new Color(255, i * 16, i * 16));
                    g.fillOval(i / 2, i / 2, 15 - i, 15 - i);
                }
            } else if (state == FINISH) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 16; j += 4) {
                        if (i % 2 == 0) {
                            g.setColor(new Color(0, 0, 0));
                            g.fillPolygon(
                                    new int[]{j, j + 2, j + 2, j},
                                    new int[]{i * 2, i * 2, i * 2 + 2, i * 2 + 2},
                                    4
                            );
                            g.setColor(new Color(255, 255, 255));
                        } else {
                            g.setColor(new Color(255, 255, 255));
                            g.fillPolygon(
                                    new int[]{j, j + 2, j + 2, j},
                                    new int[]{i * 2, i * 2, i * 2 + 2, i * 2 + 2},
                                    4
                            );
                            g.setColor(new Color(0, 0, 0));
                        }
                        g.fillPolygon(
                                new int[]{j + 2, j + 4, j + 4, j + 2},
                                new int[]{i * 2, i * 2, i * 2 + 2, i * 2 + 2},
                                4
                        );
                    }
                }
            } else if (state == WAY) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
            } else if (state == EXIT_RIGHT) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
                g.setColor(new Color(0, 255, 0));
                g.fillPolygon(new int[]{2, 10, 10, 14, 10, 10, 2}, new int[]{6, 6, 2, 8, 14, 10, 10}, 7);
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(new int[]{2, 10, 10, 14, 10, 10, 2}, new int[]{6, 6, 2, 8, 14, 10, 10}, 7);
            } else if (state == EXIT_LEFT) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
                g.setColor(new Color(0, 255, 0));
                g.fillPolygon(new int[]{14, 6, 6, 2, 6, 6, 14}, new int[]{6, 6, 2, 8, 14, 10, 10}, 7);
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(new int[]{14, 6, 6, 2, 6, 6, 14}, new int[]{6, 6, 2, 8, 14, 10, 10}, 7);
            } else if (state == EXIT_UP) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
                g.setColor(new Color(0, 255, 0));
                g.fillPolygon(new int[]{6, 6, 2, 8, 14, 10, 10}, new int[]{2, 10, 10, 14, 10, 10, 2}, 7);
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(new int[]{6, 6, 2, 8, 14, 10, 10}, new int[]{2, 10, 10, 14, 10, 10, 2}, 7);
            } else if (state == EXIT_DOWN) {
                g.setColor(new Color(250, 230, 150));
                g.fillPolygon(new int[]{0, 16, 16, 0}, new int[]{0, 0, 16, 16}, 4);
                g.setColor(new Color(0, 255, 0));
                g.fillPolygon(new int[]{6, 6, 2, 8, 14, 10, 10}, new int[]{14, 6, 6, 2, 6, 6, 14}, 7);
                g.setColor(new Color(0, 0, 0));
                g.drawPolygon(new int[]{6, 6, 2, 8, 14, 10, 10}, new int[]{14, 6, 6, 2, 6, 6, 14}, 7);
            }
        }
    }

    static class FrameForLabyrinth extends JFrame {
        static int rowCount = 10;
        static int columnCount = 10;
        static int lastRowValue = -1;
        static int lastColumnValue = -1;
        static ArrayList<ArrayList<Integer>> elementPosition = new ArrayList<>();
        static ArrayList<ArrayList<JRadioButton>> radioButtons = new ArrayList<>();
        static JSlider rowSlider = new JSlider(JSlider.VERTICAL, 0, 30, rowCount);
        static JSlider columnSlider = new JSlider(JSlider.VERTICAL, 0, 30, columnCount);
        static ButtonGroup group = new ButtonGroup();
        static JCheckBox start = new JCheckBox("Старт");
        static JCheckBox finish = new JCheckBox("Финиш");
        static JCheckBox border = new JCheckBox("Граница", true);
        static JLabel label = new JLabel("Название:");
        static JTextField textField = new JTextField();
        static boolean wasModeClick = false;
        static boolean wasStartChoose = false;
        static boolean wasFinishChoose = false;
        static boolean wasSaveClick = false;
        static boolean wasOpenClick = false;

        FrameForLabyrinth() {
            for (int i = 0; i < rowCount; i++) {
                ArrayList<Integer> list = new ArrayList<>();
                for (int j = 0; j < columnCount; j++) {
                    if (j == 0 || i == 0 || i == rowCount - 1 || j == columnCount - 1) {
                        list.add(1);
                    } else {
                        list.add(0);
                    }
                }
                elementPosition.add(list);
            }
            group.add(start);
            group.add(finish);
            group.add(border);
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(800, 800);
            setLocationRelativeTo(null);
            setUndecorated(true);
            JPanel mainPanel = new JPanel();
            add(mainPanel);
            mainPanel.setLayout(null);
            mainPanel.setBackground(new Color(210, 210, 210));
            JPanel labyrinthPanel = new JPanel();
            labyrinthPanel.setBorder(new EtchedBorder());
            labyrinthPanel.setSize(dimension.width - 130, dimension.height - 20);
            labyrinthPanel.setLocation(120, 10);
            labyrinthPanel.setBackground(new Color(220, 220, 220));
            labyrinthPanel.setLayout(null);
            mainPanel.add(labyrinthPanel);
            for (int i = 0; i < rowCount; i++) {
                ArrayList<JRadioButton> radioButtons1 = new ArrayList<>();
                for (int j = 0; j < columnCount; j++) {
                    JRadioButton radioButton = new JRadioButton();
                    radioButton.setSize(16, 15);
                    radioButton.setLocation(5 + 16 * j, 5 + 15 * i);
                    radioButton.setBackground(new Color(220, 220, 220));
                    labyrinthPanel.add(radioButton);
                    radioButtons1.add(radioButton);
                    radioButton.addActionListener(new RadioButtonListener());
                    if (j == 0 || i == 0 || i == rowCount - 1 || j == columnCount - 1) {
                        radioButton.setSelected(true);
                        radioButton.setEnabled(false);
                    }
                }
                radioButtons.add(radioButtons1);
            }
            mainPanel.add(controlPanel(dimension, labyrinthPanel));
            setVisible(true);
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }

        static JButton closeButton(Dimension dimension) {
            JButton button = new NewButton("Закрыть", 10, dimension.height - 60);
            button.addMouseListener(new ButtonListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.exit(0);
                }
            });
            return button;
        }

        static JButton changeModeButton(JPanel labyrinthPanel, JPanel modePanel, JPanel sliderPanel, JPanel controlPanel) {
            JButton button = new NewButton("Показать", 10, 10);
            button.addMouseListener(new ButtonListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!wasModeClick) {
                        for (int i = 0; i < rowCount; i++) {
                            for (int j = 0; j < columnCount; j++) {
                                int position = elementPosition.get(i).get(j);
                                radioButtons.get(i).get(j).setVisible(false);
                                DrawLabyrinthElement element = new DrawLabyrinthElement(
                                        position == 1 ? DrawLabyrinthElement.BORDER :
                                                position == 2 ? DrawLabyrinthElement.START :
                                                        position == 3 ? DrawLabyrinthElement.FINISH :
                                                                DrawLabyrinthElement.WAY
                                );
                                element.setSize(16, 15);
                                element.setLocation(5 + 16 * j, 5 + 15 * i);
                                labyrinthPanel.add(element);
                            }
                        }
                        for (int i = 0; i < controlPanel.getComponents().length; i++) {
                            if (controlPanel.getComponents()[i].getClass().getName().equals("com.company.Labyrinth$FrameForLabyrinth$NewButton") &&
                                    controlPanel.getComponents()[i].getX() == 10 &&
                                    controlPanel.getComponents()[i].getY() == 50
                            ) {
                                controlPanel.getComponents()[i].setVisible(true);
                            }
                        }
                        controlPanel.getComponentAt(10, 370).setVisible(false);
                        controlPanel.getComponentAt(10, 410).setVisible(false);
                        button.setText("Изменить");
                        wasModeClick = true;
                        modePanel.setVisible(false);
                        sliderPanel.setVisible(false);
                    } else {
                        for (int i = 0; i < labyrinthPanel.getComponents().length; i++) {
                            if (labyrinthPanel.getComponents()[i].getClass().getName().equals("com.company.Labyrinth$DrawLabyrinthElement")) {
                                labyrinthPanel.remove(labyrinthPanel.getComponents()[i]);
                                i--;
                            }
                        }
                        for (int i = 0; i < rowCount; i++) {
                            for (int j = 0; j < columnCount; j++) {
                                radioButtons.get(i).get(j).setVisible(true);
                            }
                        }
                        for (int i = 0; i < controlPanel.getComponents().length; i++) {
                            if (controlPanel.getComponents()[i].getClass().getName().equals("com.company.Labyrinth$FrameForLabyrinth$NewButton") &&
                                    controlPanel.getComponents()[i].getX() == 10 &&
                                    controlPanel.getComponents()[i].getY() == 50
                            ) {
                                controlPanel.getComponents()[i].setVisible(false);
                            }
                        }
                        controlPanel.getComponentAt(10, 370).setVisible(true);
                        controlPanel.getComponentAt(10, 410).setVisible(true);
                        button.setText("Показать");
                        wasModeClick = false;
                        modePanel.setVisible(true);
                        sliderPanel.setVisible(true);
                    }
                }
            });
            return button;
        }

        static JButton saveButton(JPanel controlPanel, JPanel labyrinthPanel) {
            JButton button = new NewButton("Сохранить", 10, 370);
            button.addMouseListener(new ButtonListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!wasSaveClick) {
                        wasSaveClick = true;
                        controlPanel.add(label);
                        label.setSize(80, 20);
                        label.setLocation(20, 500);
                        controlPanel.add(textField);
                        textField.setSize(90, 20);
                        textField.setLocation(5, 525);
                        label.setVisible(true);
                        textField.setVisible(true);
                        ((JButton) e.getSource()).setText("Ок");
                        controlPanel.getComponentAt(10, 410).setVisible(false);
                        controlPanel.remove(controlPanel.getComponentAt(10, 410));
                    } else {
                        wasSaveClick = false;
                        if (textField.getText() != null) {
                            try {
                                FileWriter fileWriter = new FileWriter(textField.getText() + ".txt");
                                StringBuilder builder = new StringBuilder();
                                for (ArrayList<Integer> integers : elementPosition) {
                                    for (Integer integer : integers) {
                                        builder.append(integer).append(" ");
                                    }
                                    builder.append("\n");
                                }
                                fileWriter.write(String.valueOf(builder));
                                fileWriter.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        controlPanel.add(openButton(controlPanel, labyrinthPanel));
                        controlPanel.getComponentAt(10, 410).setVisible(false);
                        controlPanel.getComponentAt(10, 410).setVisible(true);
                        ((JButton) e.getSource()).setText("Сохранить");
                        label.setVisible(false);
                        textField.setVisible(false);
                        controlPanel.remove(label);
                        controlPanel.remove(textField);
                    }
                }
            });
            return button;
        }

        static JButton openButton(JPanel controlPanel, JPanel labyrinthPanel) {
            JButton button = new NewButton("Открыть", 10, 410);
            button.addMouseListener(new ButtonListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!wasOpenClick) {
                        wasOpenClick = true;
                        controlPanel.add(label);
                        label.setSize(80, 20);
                        label.setLocation(20, 500);
                        controlPanel.add(textField);
                        textField.setSize(90, 20);
                        textField.setLocation(5, 525);
                        label.setVisible(true);
                        textField.setVisible(true);
                        ((JButton) e.getSource()).setText("Ок");
                        controlPanel.getComponentAt(10, 370).setVisible(false);
                        controlPanel.remove(controlPanel.getComponentAt(10, 370));
                    } else {
                        wasOpenClick = false;
                        if (textField.getText() != null) {
                            try {
                                File file = new File(textField.getText() + ".txt");
                                Scanner readFile = new Scanner(file);
                                ArrayList<ArrayList<Integer>> list = new ArrayList<>();
                                while (readFile.hasNextLine()) {
                                    String[] strings = readFile.nextLine().split(" ");
                                    ArrayList<Integer> list1 = new ArrayList<>();
                                    for (String v : strings) {
                                        list1.add(Integer.parseInt(v));
                                    }
                                    list.add(list1);
                                }
                                elementPosition = new ArrayList<>(list);
                                rowCount = elementPosition.size();
                                columnCount = elementPosition.get(0).size();
                                while (labyrinthPanel.getComponents().length != 0) {
                                    labyrinthPanel.getComponents()[0].setVisible(false);
                                    labyrinthPanel.remove(labyrinthPanel.getComponents()[0]);
                                }
                                ArrayList<ArrayList<JRadioButton>> lists = new ArrayList<>();
                                for (int i = 0; i < rowCount; i++) {
                                    ArrayList<JRadioButton> radioButtons1 = new ArrayList<>();
                                    for (int j = 0; j < columnCount; j++) {
                                        JRadioButton radioButton = new JRadioButton();
                                        radioButton.setSize(16, 15);
                                        radioButton.setLocation(5 + 16 * j, 5 + 15 * i);
                                        radioButton.setBackground(new Color(220, 220, 220));
                                        labyrinthPanel.add(radioButton);
                                        radioButtons1.add(radioButton);
                                        radioButton.addActionListener(new RadioButtonListener());
                                    }
                                    lists.add(radioButtons1);
                                }
                                radioButtons = new ArrayList<>(lists);
                                for (int i = 0; i < labyrinthPanel.getComponents().length; i++) {
                                    labyrinthPanel.getComponents()[i].setVisible(false);
                                    labyrinthPanel.getComponents()[i].setVisible(true);
                                }
                                border.setSelected(true);
                                for (int i = 0; i < rowCount; i++) {
                                    for (int j = 0; j < columnCount; j++) {
                                        if (j != 0 && i != 0 && i != rowCount - 1 && j != columnCount - 1) {
                                            if (elementPosition.get(i).get(j) == 1) {
                                                radioButtons.get(i).get(j).setEnabled(true);
                                                radioButtons.get(i).get(j).setSelected(true);
                                            } else if (elementPosition.get(i).get(j) == 2 || elementPosition.get(i).get(j) == 3) {
                                                radioButtons.get(i).get(j).setSelected(true);
                                                radioButtons.get(i).get(j).setEnabled(false);
                                            } else {
                                                radioButtons.get(i).get(j).setEnabled(true);
                                                radioButtons.get(i).get(j).setSelected(false);
                                            }
                                        } else {
                                            radioButtons.get(i).get(j).setEnabled(false);
                                            radioButtons.get(i).get(j).setSelected(true);
                                        }
                                    }
                                }
                                ChangeListener rowSliderListener = rowSlider.getChangeListeners()[0];
                                ChangeListener columnSliderListener = columnSlider.getChangeListeners()[0];
                                rowSlider.removeChangeListener(rowSlider.getChangeListeners()[0]);
                                columnSlider.removeChangeListener(columnSlider.getChangeListeners()[0]);
                                rowSlider.setValue(rowCount);
                                columnSlider.setValue(columnCount);
                                lastRowValue = rowSlider.getValue();
                                lastColumnValue = columnSlider.getValue();
                                rowSlider.addChangeListener(rowSliderListener);
                                columnSlider.addChangeListener(columnSliderListener);
                                readFile.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        controlPanel.add(saveButton(controlPanel, labyrinthPanel));
                        controlPanel.getComponentAt(10, 370).setVisible(false);
                        controlPanel.getComponentAt(10, 370).setVisible(true);
                        ((JButton) e.getSource()).setText("Открыть");
                        label.setVisible(false);
                        textField.setVisible(false);
                        controlPanel.remove(label);
                        controlPanel.remove(textField);
                    }
                }
            });
            return button;
        }

        static JButton wayButton(JPanel labyrinthPanel) {
            JButton button = new NewButton("Путь", 10, 50);
            button.setVisible(false);
            button.addMouseListener(new ButtonListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    LabyrinthUtils utils = new LabyrinthUtils();
                    utils.setLabyrinth(elementPosition);
                    if (!utils.hasAWidthGreaterThanOne()) {
                        try {
                            utils.searchForAWay();
                            for (int i = 0; i < labyrinthPanel.getComponents().length; i++) {
                                for (int j = 1; j < utils.getWay().size() - 1; j++) {
                                    if (labyrinthPanel.getComponents()[i].getClass().getName().equals("com.company.Labyrinth$DrawLabyrinthElement") &&
                                            utils.getWay().get(j)[1] == (labyrinthPanel.getComponents()[i].getX() - 5) / 16 &&
                                            utils.getWay().get(j)[0] == (labyrinthPanel.getComponents()[i].getY() - 5) / 15
                                    ) {
                                        labyrinthPanel.getComponents()[i].setVisible(false);
                                    }
                                }
                            }
                            for (int i = 1; i < utils.getWay().size() - 1; i++) {
                                DrawLabyrinthElement element = new DrawLabyrinthElement(
                                        (utils.getWay().get(i)[0] > utils.getWay().get(i - 1)[0]) ? DrawLabyrinthElement.EXIT_UP :
                                                (utils.getWay().get(i)[0] < utils.getWay().get(i - 1)[0]) ? DrawLabyrinthElement.EXIT_DOWN :
                                                        (utils.getWay().get(i)[1] > utils.getWay().get(i - 1)[1]) ? DrawLabyrinthElement.EXIT_RIGHT :
                                                                (utils.getWay().get(i)[1] < utils.getWay().get(i - 1)[1]) ? DrawLabyrinthElement.EXIT_LEFT : -1
                                );
                                labyrinthPanel.add(element);
                                element.setSize(16, 15);
                                element.setLocation(5 + 16 * utils.getWay().get(i)[1], 5 + 15 * utils.getWay().get(i)[0]);
                            }
                        } catch (StackOverflowError e1) {
                            JOptionPane.showMessageDialog(null, "Лабиринт имеет более одного пути");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Путь лабиринта имеет недопустимое значение ширины");
                    }
                }
            });
            return button;
        }

        static JPanel controlPanel(Dimension dimension, JPanel labyrinthPanel) {
            JPanel controlPanel = new JPanel();
            controlPanel.setBorder(new EtchedBorder());
            controlPanel.setLocation(10, 10);
            controlPanel.setSize(100, dimension.height - 20);
            controlPanel.setLayout(null);
            controlPanel.setBackground(new Color(220, 220, 220));
            controlPanel.add(closeButton(dimension));
            JPanel modePanel = new JPanel();
            modePanel.setLocation(10, 50);
            modePanel.setSize(80, 100);
            modePanel.setBorder(new EtchedBorder());
            modePanel.setBackground(new Color(230, 230, 230));
            modePanel.add(start);
            start.setLocation(10, 10);
            start.setSize(60, 30);
            start.setBackground(null);
            start.setFocusPainted(false);
            start.addActionListener(new CheckBoxListener());
            modePanel.add(finish);
            finish.setLocation(10, 50);
            finish.setSize(60, 30);
            finish.setBackground(null);
            finish.setFocusPainted(false);
            finish.addActionListener(new CheckBoxListener());
            modePanel.add(border);
            border.setLocation(10, 90);
            border.setSize(60, 30);
            border.setBackground(null);
            border.setFocusPainted(false);
            border.addActionListener(new CheckBoxListener());
            controlPanel.add(modePanel);
            rowSlider.setSize(20, 180);
            rowSlider.setLocation(15, 10);
            rowSlider.setBackground(null);
            lastRowValue = rowSlider.getValue();
            rowSlider.addChangeListener(e -> {
                if (rowSlider.getValue() - lastRowValue > 0) {
                    for (int j = 0; j < rowSlider.getValue() - lastRowValue; j++) {
                        ArrayList<JRadioButton> list = new ArrayList<>();
                        ArrayList<Integer> list1 = new ArrayList<>();
                        for (int i = 0; i < columnCount; i++) {
                            if (i != 0 && i != columnCount - 1) {
                                radioButtons.get(rowCount - 1).get(i).setEnabled(true);
                                radioButtons.get(rowCount - 1).get(i).setSelected(false);
                                elementPosition.get(rowCount - 1).set(i, 0);
                            }
                            JRadioButton radioButton = new JRadioButton();
                            labyrinthPanel.add(radioButton);
                            radioButton.setLocation(5 + 16 * i, 5 + 15 * rowCount);
                            radioButton.setSize(16, 15);
                            radioButton.setBackground(null);
                            radioButton.addActionListener(new RadioButtonListener());
                            radioButton.setEnabled(false);
                            radioButton.setSelected(true);
                            list.add(radioButton);
                            list1.add(1);
                        }
                        radioButtons.add(list);
                        elementPosition.add(list1);
                        rowCount++;
                    }
                } else if (rowSlider.getValue() - lastRowValue < 0) {
                    for (int i = 0; i < lastRowValue - rowSlider.getValue(); i++) {
                        for (int j = 0; j < columnCount; j++) {
                            if (elementPosition.get(rowCount - 2).get(j) == 2) {
                                wasStartChoose = false;
                            }
                            if (elementPosition.get(rowCount - 2).get(j) == 3) {
                                wasFinishChoose = false;
                            }
                            radioButtons.get(rowCount - 2).get(j).setSelected(true);
                            radioButtons.get(rowCount - 2).get(j).setEnabled(false);
                            elementPosition.get(rowCount - 2).set(j, 1);
                            radioButtons.get(rowCount - 1).get(j).setVisible(false);

                        }
                        radioButtons.remove(rowCount - 1);
                        elementPosition.remove(rowCount - 1);
                        rowCount--;
                    }
                }
                lastRowValue = rowSlider.getValue();
            });
            columnSlider.setSize(20, 180);
            columnSlider.setLocation(45, 10);
            columnSlider.setBackground(null);
            lastColumnValue = columnSlider.getValue();
            columnSlider.addChangeListener(e -> {
                if (columnSlider.getValue() - lastColumnValue > 0) {
                    for (int j = 0; j < columnSlider.getValue() - lastColumnValue; j++) {
                        for (int i = 0; i < rowCount; i++) {
                            if (i != 0 && i != rowCount - 1) {
                                radioButtons.get(i).get(columnCount - 1).setSelected(false);
                                radioButtons.get(i).get(columnCount - 1).setEnabled(true);
                                elementPosition.get(i).set(columnCount - 1, 0);
                            }
                            JRadioButton radioButton = new JRadioButton();
                            labyrinthPanel.add(radioButton);
                            radioButton.setLocation(5 + 16 * columnCount, 5 + 15 * i);
                            radioButton.setSize(16, 15);
                            radioButton.setBackground(null);
                            radioButton.addActionListener(new RadioButtonListener());
                            radioButton.setEnabled(false);
                            radioButton.setSelected(true);
                            elementPosition.get(i).add(1);
                            radioButtons.get(i).add(radioButton);
                        }
                        columnCount++;
                    }
                } else if (columnSlider.getValue() - lastColumnValue < 0) {
                    for (int i = 0; i < lastColumnValue - columnSlider.getValue(); i++) {
                        for (int j = 0; j < rowCount; j++) {
                            if (elementPosition.get(j).get(columnCount - 2) == 3) {
                                wasFinishChoose = false;
                            }
                            if (elementPosition.get(j).get(columnCount - 2) == 2) {
                                wasStartChoose = false;
                            }
                            radioButtons.get(j).get(columnCount - 2).setEnabled(false);
                            radioButtons.get(j).get(columnCount - 2).setSelected(true);
                            elementPosition.get(j).set(columnCount - 2, 1);
                            radioButtons.get(j).get(columnCount - 1).setVisible(false);
                            elementPosition.get(j).remove(columnCount - 1);
                            radioButtons.get(j).remove(columnCount - 1);
                        }
                        columnCount--;
                    }
                }
                lastColumnValue = columnSlider.getValue();
            });
            JPanel sliderPanel = new JPanel();
            sliderPanel.setBorder(new EtchedBorder());
            sliderPanel.setSize(80, 200);
            sliderPanel.setLocation(10, 160);
            sliderPanel.setLayout(null);
            sliderPanel.add(rowSlider);
            sliderPanel.add(columnSlider);
            sliderPanel.setBackground(new Color(230, 230, 230));
            controlPanel.add(sliderPanel);
            controlPanel.add(changeModeButton(labyrinthPanel, modePanel, sliderPanel, controlPanel));
            controlPanel.add(saveButton(controlPanel, labyrinthPanel));
            controlPanel.add(openButton(controlPanel, labyrinthPanel));
            controlPanel.add(wayButton(labyrinthPanel));
            return controlPanel;
        }

        static class RadioButtonListener extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JRadioButton) e.getSource()).isSelected()) {
                    if (start.isSelected() &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 3 &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 1
                    ) {
                        if (!wasStartChoose) {
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).set((((JRadioButton) e.getSource()).getX() - 5) / 16, 2);
                            wasStartChoose = true;
                        } else {
                            ((JRadioButton) e.getSource()).setSelected(false);
                        }
                    } else if (finish.isSelected() &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 2 &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 1
                    ) {
                        if (!wasFinishChoose) {
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).set((((JRadioButton) e.getSource()).getX() - 5) / 16, 3);
                            wasFinishChoose = true;
                        } else {
                            ((JRadioButton) e.getSource()).setSelected(false);
                        }
                    } else if (border.isSelected() &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 2 &&
                            elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).get((((JRadioButton) e.getSource()).getX() - 5) / 16) != 3
                    ) {
                        elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).set((((JRadioButton) e.getSource()).getX() - 5) / 16, 1);
                    } else {
                        ((JRadioButton) e.getSource()).setSelected(false);
                    }
                } else if (!((JRadioButton) e.getSource()).isSelected()) {
                    if (start.isSelected()) {
                        wasStartChoose = false;
                    } else if (finish.isSelected()) {
                        wasFinishChoose = false;
                    }
                    elementPosition.get((((JRadioButton) e.getSource()).getY() - 5) / 15).set((((JRadioButton) e.getSource()).getX() - 5) / 16, 0);
                }
            }
        }

        static class ButtonListener extends MouseAdapter {

            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.RAISED));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).setBorder(new EtchedBorder());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ((JButton) e.getSource()).setEnabled(false);
                ((JButton) e.getSource()).setBorder(new BevelBorder(BevelBorder.LOWERED));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ((JButton) e.getSource()).setEnabled(true);
                ((JButton) e.getSource()).setBorder(new EtchedBorder());
            }
        }

        static class CheckBoxListener extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indexes =
                        e.getSource().equals(start) ? new int[]{2, 3, 1} :
                                e.getSource().equals(finish) ? new int[]{3, 2, 1} :
                                        e.getSource().equals(border) ? new int[]{1, 3, 2} :
                                                new int[]{-1, -1, -1};
                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < columnCount; j++) {
                        if (j != 0 && i != 0 && i != rowCount - 1 && j != columnCount - 1) {
                            if (elementPosition.get(i).get(j) == indexes[0]) {
                                radioButtons.get(i).get(j).setEnabled(true);
                                radioButtons.get(i).get(j).setSelected(true);
                            } else if (elementPosition.get(i).get(j) == indexes[1] || elementPosition.get(i).get(j) == indexes[2]) {
                                radioButtons.get(i).get(j).setEnabled(false);
                            } else {
                                radioButtons.get(i).get(j).setEnabled(true);
                                radioButtons.get(i).get(j).setSelected(false);
                            }
                        }
                    }
                }
            }
        }

        static class NewButton extends JButton {
            NewButton(String name, int x, int y) {
                super(name);
                setLocation(x, y);
                setSize(80, 30);
                setFocusPainted(false);
                setBorder(new EtchedBorder());
                setBackground(new Color(230, 230, 230));
            }
        }
    }
}
