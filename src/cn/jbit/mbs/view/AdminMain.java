package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;

public class AdminMain extends JFrame {

    public AdminMain(User user) {
        setTitle("管理员主页 - 欢迎：" + user.getUsername());
        int windowWidth = 1000;
        int windowHeight = 600;
        setSize(windowWidth, windowHeight);

        // 屏幕居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - windowWidth) / 2;
        int y = (screenSize.height - windowHeight) / 2;
        setLocation(x, y);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        // 设置背景颜色或使用背景图
        String imagePath = "picture/admin_background.jpg"; // 替换为你自己的管理员背景图路径
        Login.BackgroundPanel backgroundPanel = new Login.BackgroundPanel(imagePath);
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBackground(new Color(245, 240, 255)); // 浅紫色背景备用

        // 添加欢迎标签
        JLabel welcomeLabel = new JLabel("欢迎管理员：" + user.getUsername(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("宋体", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(50, 50, 150));
        backgroundPanel.add(welcomeLabel, BorderLayout.NORTH);

        // 示例功能按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton manageMoviesBtn = new JButton("影片管理");
        JButton manageUsersBtn = new JButton("用户管理");
        JButton manageCinemaBtn = new JButton("影院管理");
        JButton logoutBtn = new JButton("退出登录");
        manageMoviesBtn.addActionListener(e -> {
            // 跳转到影片管理界面
            new ManageMovies(user);
            this.dispose();
        });
        manageUsersBtn.addActionListener(e -> {
            // 跳转到用户管理界面
            new ManageUsers(user);
            this.dispose();
        });
        manageCinemaBtn.addActionListener(e -> {
            // 跳转到影院管理界面
            new ManageCinemas(user);
            this.dispose();
        });

        // 设置按钮样式
        setButtonStyle(manageMoviesBtn);
        setButtonStyle(manageUsersBtn);
        setButtonStyle(manageCinemaBtn);
        setButtonStyle(logoutBtn);

        buttonPanel.add(manageMoviesBtn);
        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(manageCinemaBtn);
        buttonPanel.add(logoutBtn);

        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // 添加到主窗口
        this.add(backgroundPanel);

        // 退出登录按钮监听器
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new Login(); // 返回登录界面
        });
    }

    // 按钮统一样式方法
    private void setButtonStyle(JButton button) {
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(new Color(100, 150, 250));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
}