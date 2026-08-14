package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageUsers extends JFrame {
    private User currentUser;

    public ManageUsers(User user) {
        this.currentUser = user;
        setTitle("用户管理系统");
        setSize(800, 600); // 调整窗口大小
        setLocationRelativeTo(null); // 屏幕居中
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 修改为DISPOSE_ON_CLOSE
        setResizable(false);

        // 主面板 - 使用渐变色背景
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
        JLabel titleLabel = new JLabel("用户管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 功能按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 30));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        // 操作按钮
        JButton operateButton = createStyledButton("用户操作", new Color(70, 130, 180));
        operateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OperateUsers(currentUser);
            }
        });

        // 返回按钮
        JButton returnButton = createStyledButton("返回主界面", new Color(119, 136, 153));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminMain(currentUser);
            }
        });

        // 添加按钮到面板
        buttonPanel.add(operateButton);
        buttonPanel.add(Box.createVerticalStrut(20)); // 添加垂直间距
        buttonPanel.add(returnButton);

        // 中心面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 底部信息栏
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 电影票务系统 - 管理员用户管理界面");
        footerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 20));
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
            new ManageUsers(testUser);
        });
    }
}