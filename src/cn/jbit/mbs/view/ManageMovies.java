package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;

public class ManageMovies extends JFrame {
    private User currentUser;

    public ManageMovies(User user) {
        this.currentUser = user;
        setTitle("电影管理");
        setSize(800, 600);
        setLocationRelativeTo(null); // 屏幕居中
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 与ManageUsers一致
        setResizable(false);

        // 主面板 - 渐变色背景
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255); // 浅蓝色
                Color color2 = new Color(230, 240, 255); // 更深的浅蓝色
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("电影管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 功能按钮面板 - 改为2列布局
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 30, 30)); // 2列布局
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100)); // 增加边距

        // 创建按钮 - 使用更协调的颜色
        JButton addButton = createStyledButton("新增电影", new Color(70, 130, 180)); // 钢蓝色
        addButton.addActionListener(e -> new AddMovies(currentUser));

        JButton deleteButton = createStyledButton("删除电影", new Color(100, 149, 237)); // 矢车菊蓝
        deleteButton.addActionListener(e -> new DeleteMovies(currentUser));

        JButton scheduleButton = createStyledButton("排片管理", new Color(65, 105, 225)); // 皇家蓝
        scheduleButton.addActionListener(e -> new SchedulingManagement(currentUser));

        JButton modifyStatusButton = createStyledButton("修改电影状态", new Color(255, 165, 0)); // 橙色
        modifyStatusButton.addActionListener(e -> new ModifyMovieStatus(currentUser));

        // 添加按钮到面板 (2x2布局)
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(modifyStatusButton);

        // 中心面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 底部面板 - 添加返回按钮
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // 返回按钮
        JButton returnButton = createStyledButton("返回主界面", new Color(119, 136, 153)); // 石板灰
        returnButton.setPreferredSize(new Dimension(180, 50)); // 稍小尺寸
        returnButton.addActionListener(e -> {
            dispose();
            new AdminMain(currentUser);
        });
        JPanel returnPanel = new JPanel();
        returnPanel.setOpaque(false);
        returnPanel.add(returnButton);
        bottomPanel.add(returnPanel, BorderLayout.NORTH);

        // 底部信息栏
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 电影票务系统 - 电影管理界面");
        footerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        bottomPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 18)); // 字体稍小
        button.setPreferredSize(new Dimension(250, 60));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setUsername("admin");
            new ManageMovies(testUser);
        });
    }
}