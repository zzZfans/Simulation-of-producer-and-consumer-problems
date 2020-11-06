import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Zfans
 * @date 2020/10/18 10:49
 */

enum ProcessType {
    PRODUCE, BUFFER, CONSUME
}

class Constant {
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 400;
    public static final int THREAD_SLEEP_MILLIS = 5;
    public static final int PRI_X = 0;
    public static final int PRI_Y = 35;
    public static final int HIDE_X = -50;
    public static final int END_X = 250;

    public static final String IMAGE_FILE_PATH1 = "src/猪.png";
    public static final String IMAGE_FILE_PATH2 = "src/乘.png";
    public static final String ICON_FILE_PATH = "src/生产.png";

    public static int PRODUCE_X_AXIS_SPEED = 3;
    public static int CONSUME_X_AXIS_SPEED = 3;
    public static boolean produce = false;
    public static boolean consume = false;
    public static Integer product = 0;
    public static Integer max_product = 10;
}

public class SimulationOfProducerAndConsumerProblems {

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        //改变swing控件的风格为当前系统(windwos 10)风格的代码
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame jFrame = new JFrame();
        jFrame.setLayout(null);
        jFrame.setSize(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //把窗口位置设置到屏幕中心
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setTitle("模拟生产者与消费者问题");
        jFrame.getContentPane().setBackground(Color.white);

        jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Constant.ICON_FILE_PATH));

        ProcessPanel producerPanel = new ProcessPanel(ProcessType.PRODUCE, Constant.HIDE_X, Constant.PRI_Y, Constant.IMAGE_FILE_PATH1, null);
        ProcessPanel bufferPanel = new ProcessPanel(ProcessType.BUFFER, Constant.PRI_X, Constant.PRI_Y, Constant.IMAGE_FILE_PATH1, Constant.IMAGE_FILE_PATH2);
        ProcessPanel consumerPanel = new ProcessPanel(ProcessType.CONSUME, Constant.HIDE_X, Constant.PRI_Y, Constant.IMAGE_FILE_PATH1, null);

        producerPanel.setBackground(Color.white);
        bufferPanel.setBackground(Color.white);
        consumerPanel.setBackground(Color.white);

        producerPanel.setBounds(50, 130, 250, 100);
        bufferPanel.setBounds(300, 130, 200, 100);
        consumerPanel.setBounds(500, 130, 250, 100);

        Font btnAndPanelFont = new Font("微软雅黑", Font.PLAIN, 14);

        producerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "生产者进程", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, btnAndPanelFont));
        bufferPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "缓冲区（大小：" + Constant.max_product + "）", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, btnAndPanelFont));
        consumerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "消费者进程", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, btnAndPanelFont));

        jFrame.add(producerPanel);
        jFrame.add(bufferPanel);
        jFrame.add(consumerPanel);

        JButton producerControlBtn = new JButton("开始生产");
        producerControlBtn.setBackground(Color.WHITE);
        producerControlBtn.setFont(btnAndPanelFont);
        producerControlBtn.setFocusPainted(false);
        producerControlBtn.setBounds(130, 250, 90, 34);
        producerControlBtn.addActionListener(actionEvent -> {
            Constant.produce = !Constant.produce;
            if (Constant.produce) {
                producerControlBtn.setText("停止生产");
            } else {
                producerControlBtn.setText("开始生产");
            }
        });

        JButton bufferControlBtn = new JButton("设置缓冲区大小");
        bufferControlBtn.setBackground(Color.WHITE);
        bufferControlBtn.setFont(btnAndPanelFont);
        bufferControlBtn.setFocusPainted(false);
        bufferControlBtn.setBounds(335, 250, 130, 34);
        bufferControlBtn.addActionListener(actionEvent -> {
            String input = JOptionPane.showInputDialog(null, "请输入缓冲区大小（1~N整数）：", "设置缓冲区大小", JOptionPane.PLAIN_MESSAGE);
            if (input == null || "".equals(input)) {
                return;
            }
            if (isNumeric(input)) {
                int num = Integer.parseInt(input);
                if (num <= 0) {
                    JOptionPane.showMessageDialog(null, "非法数值！", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    Constant.max_product = num;
                    bufferPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "缓冲区（大小：" + Constant.max_product + "）", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, btnAndPanelFont));
                    if (Constant.product > num) {
                        Constant.product = num;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "非法字符串！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton consumerControlBtn = new JButton("开始消费");
        consumerControlBtn.setEnabled(false);
        consumerControlBtn.setBackground(Color.WHITE);
        consumerControlBtn.setFont(btnAndPanelFont);
        consumerControlBtn.setFocusPainted(false);
        consumerControlBtn.setBounds(580, 250, 90, 34);
        consumerControlBtn.addActionListener(actionEvent -> {
            Constant.consume = !Constant.consume;
            if (Constant.consume) {
                consumerControlBtn.setText("停止消费");
            } else {
                consumerControlBtn.setText("开始消费");
            }
        });

        jFrame.add(producerControlBtn);
        jFrame.add(bufferControlBtn);
        jFrame.add(consumerControlBtn);

        Font sliderFont = new Font("微软雅黑", Font.PLAIN, 10);

        JSlider producerSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, Constant.PRODUCE_X_AXIS_SPEED);
        producerSpeedSlider.setBounds(75, 290, 200, 50);
        producerSpeedSlider.setMajorTickSpacing(5);
        producerSpeedSlider.setMinorTickSpacing(1);
        producerSpeedSlider.setPaintTicks(true);
        producerSpeedSlider.setPaintLabels(true);
        producerSpeedSlider.setFont(sliderFont);
        producerSpeedSlider.setBackground(Color.WHITE);
        producerSpeedSlider.setFocusable(false);
        producerSpeedSlider.addChangeListener(changeEvent -> {
            Constant.PRODUCE_X_AXIS_SPEED = producerSpeedSlider.getValue();
        });

        JSlider consumerSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, Constant.CONSUME_X_AXIS_SPEED);
        consumerSpeedSlider.setBounds(525, 290, 200, 50);
        consumerSpeedSlider.setMajorTickSpacing(5);
        consumerSpeedSlider.setMinorTickSpacing(1);
        consumerSpeedSlider.setPaintTicks(true);
        consumerSpeedSlider.setPaintLabels(true);
        consumerSpeedSlider.setFont(sliderFont);
        consumerSpeedSlider.setBackground(Color.WHITE);
        consumerSpeedSlider.setFocusable(false);
        consumerSpeedSlider.addChangeListener(changeEvent -> {
            Constant.CONSUME_X_AXIS_SPEED = consumerSpeedSlider.getValue();
        });

        jFrame.add(producerSpeedSlider);
        jFrame.add(consumerSpeedSlider);

        JLabel titleLabel = new JLabel("生产与消费过程模拟");
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 32));
        titleLabel.setBounds(255, 40, 290, 40);

        jFrame.add(titleLabel);

        jFrame.setVisible(true);

        while (true) {

            if (Constant.produce) {
                producerPanel.moveThread.run();
            } else {
                if (Constant.product.equals(Constant.max_product) && producerControlBtn.isEnabled() && "停止生产".equals(producerControlBtn.getText())) {
                    producerControlBtn.setEnabled(false);
                    producerControlBtn.setText("开始生产");
                    JOptionPane.showMessageDialog(jFrame, "当前缓冲区已满，已自动停止生产！", "提示 ^_^", JOptionPane.INFORMATION_MESSAGE);
                } else if (Constant.product < Constant.max_product) {
                    producerControlBtn.setEnabled(true);
                }
            }

            if (Constant.consume) {
                consumerPanel.moveThread.run();
            }

            if (Constant.product <= 0) {
                if (consumerPanel.imageX >= Constant.END_X) {
                    consumerControlBtn.setEnabled(false);
                    consumerControlBtn.setText("开始消费");
                }
            } else {
                consumerControlBtn.setEnabled(true);
            }

            bufferPanel.repaint();
        }
    }
}

class ProcessPanel extends JPanel {
    public String imageFilePath1;
    public String imageFilePath2;
    public final MoveThread moveThread = new MoveThread(this);
    public int imageX, imageY;

    public ProcessType processType;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (processType == ProcessType.BUFFER) {
            Image image = Toolkit.getDefaultToolkit().getImage(imageFilePath1);
            g.drawImage(image, imageX + 20, imageY, image.getWidth(this) / 4, image.getHeight(this) / 4, this);

            Image image2 = Toolkit.getDefaultToolkit().getImage(imageFilePath2);
            g.drawImage(image2, imageX + 80, imageY + 4, image2.getWidth(this) / 5, image2.getHeight(this) / 5, this);
            g.setFont(new Font("微软雅黑", Font.BOLD, 34));
            g.drawString(Constant.product.toString(), 120, 71);
        } else {
            Image image = Toolkit.getDefaultToolkit().getImage(imageFilePath1);
            g.drawImage(image, imageX, imageY, image.getWidth(this) / 4, image.getHeight(this) / 4, this);
        }
    }

    public ProcessPanel(ProcessType processType, int imageX, int imageY, String imageFilePath1, String imageFilePath2) {
        this.processType = processType;
        this.imageX = imageX;
        this.imageY = imageY;
        this.imageFilePath1 = imageFilePath1;
        this.imageFilePath2 = imageFilePath2;
    }
}

class MoveThread extends Thread {
    private final ProcessPanel processPanel;

    MoveThread(ProcessPanel processPanel) {
        this.processPanel = processPanel;
    }

    @Override
    public void run() {

        if (processPanel.processType == ProcessType.CONSUME) {
            if (Constant.product > 0 && processPanel.imageX == Constant.HIDE_X) {
                --Constant.product;
            }
        }

        if (processPanel.imageX < Constant.END_X) {
            if (processPanel.processType == ProcessType.PRODUCE) {
                processPanel.imageX += Constant.PRODUCE_X_AXIS_SPEED;
            } else if (processPanel.processType == ProcessType.CONSUME) {
                processPanel.imageX += Constant.CONSUME_X_AXIS_SPEED;
            }

            if (processPanel.processType == ProcessType.PRODUCE) {
                if (processPanel.imageX > Constant.END_X + Constant.HIDE_X - Constant.PRODUCE_X_AXIS_SPEED && Constant.product.equals(Constant.max_product)) {
                    Constant.produce = !Constant.produce;
                }
            }

        } else {
            if (processPanel.processType == ProcessType.PRODUCE) {
                if (Constant.product.equals(Constant.max_product)) {
                    Constant.produce = !Constant.produce;
                } else {
                    ++Constant.product;
                }
            } else if (processPanel.processType == ProcessType.CONSUME) {
                if (Constant.product <= 0) {
                    Constant.consume = !Constant.consume;
                    JOptionPane.showMessageDialog(processPanel.getParent(), "当前无产品可供消费，已自动停止！", "提示 ^_^", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            processPanel.imageX = Constant.HIDE_X;
        }

        try {
            if (Constant.produce && Constant.consume) {
                Thread.sleep(Constant.THREAD_SLEEP_MILLIS);
            } else {
                Thread.sleep(Constant.THREAD_SLEEP_MILLIS * 2);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        processPanel.repaint();
    }
}