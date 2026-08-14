package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.userDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class OperateUsers extends JFrame {
    private User currentUser;
    private JTextField usernameText;
    private JTextArea userInfoArea;

    public OperateUsers(User user) {
        this.currentUser = user;

        // 窗口设置
        setTitle("用户管理 - 操作用户");
        setSize(900, 650);
        setLocationRelativeTo(null); // 屏幕居中
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 主面板 - 使用渐变背景
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15)); // 移除透明绘制
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255)); // 设置背景色
        JLabel titleLabel = new JLabel("用户管理操作", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 搜索面板
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE); // 设置背景色
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                        "用户查询",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("微软雅黑", Font.BOLD, 16),
                        new Color(80, 100, 120)),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        searchPanel.add(usernameLabel, gbc);

        // 用户名输入框
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameText = new JTextField();
        usernameText.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameText.setPreferredSize(new Dimension(300, 35));
        usernameText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(usernameText, gbc);

        // 查询按钮
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton searchButton = createStyledButton("查询用户", new Color(70, 130, 180));
        searchButton.addActionListener(this::searchUser);
        searchPanel.add(searchButton, gbc);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // 用户信息显示区域
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE); // 设置背景色
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 200, 220), 2),
                        "用户详细信息",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("微软雅黑", Font.BOLD, 16),
                        new Color(80, 100, 120)),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));

        userInfoArea = new JTextArea();
        userInfoArea.setEditable(false);
        userInfoArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        userInfoArea.setLineWrap(true);
        userInfoArea.setWrapStyleWord(true);
        userInfoArea.setBackground(Color.WHITE); // 改为完全不透明背景
        userInfoArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JScrollPane scrollPane = new JScrollPane(userInfoArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        infoPanel.add(scrollPane);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        buttonPanel.setOpaque(false);

        JButton banButton = createStyledButton("拉黑用户", new Color(220, 80, 70));
        JButton unbanButton = createStyledButton("解除拉黑", new Color(60, 170, 100));
        JButton cancelButton = createStyledButton("返回", new Color(120, 140, 160));

        banButton.addActionListener(e -> banUser());
        unbanButton.addActionListener(e -> unbanUser());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(banButton);
        buttonPanel.add(unbanButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 查询用户信息
    private void searchUser(ActionEvent e) {
        String username = usernameText.getText().trim();
        if (username.isEmpty()) {
            showMessage("请输入用户名！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        userDAOIMPL dao = new userDAOIMPL();
        HashMap<String, Object> userMap = dao.getUserByUsername(username);

        if (userMap == null) {
            userInfoArea.setText("未找到用户 [" + username + "]，请检查用户名是否正确。");
        } else {
            Integer userType = (Integer) userMap.get("user_type");
            String status = switch (userType) {
                case -1 -> "【该用户已被拉黑】";
                case 0 -> "【普通用户】";
                case 2 -> "【管理员账号，不可操作】";
                default -> "【未知状态】";
            };

            String userTypeDesc = switch (userType) {
                case -1 -> "已拉黑用户";
                case 0 -> "普通用户";
                case 2 -> "系统管理员";
                default -> "未知类型";
            };

            userInfoArea.setText(String.format(
                    "════════ 用户信息 ════════\n" +
                            "用户名：%s\n" +
                            "手机号：%s\n" +
                            "电子邮箱：%s\n" +
                            "用户类型：%s\n" +
                            "状态：%s\n" +
                            "═══════════════════════",
                    userMap.get("username"),
                    userMap.get("phone"),
                    userMap.get("email"),
                    userTypeDesc,
                    status
            ));
        }
    }

    // 执行拉黑操作
    private void banUser() {
        String username = usernameText.getText().trim();
        if (username.isEmpty()) {
            showMessage("请输入用户名！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = showConfirmDialog("确认要拉黑用户 [" + username + "] 吗？此操作将限制该用户登录。", "确认拉黑操作");
        if (confirm != JOptionPane.YES_OPTION) return;

        userDAOIMPL dao = new userDAOIMPL();
        if (dao.banUser(username)) {
            showMessage("用户 [" + username + "] 已成功拉黑！", "操作成功", JOptionPane.INFORMATION_MESSAGE);
            searchUser(null);
        } else {
            showMessage("拉黑用户 [" + username + "] 失败，请检查用户名或重试。", "操作失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 执行取消拉黑操作
    private void unbanUser() {
        String username = usernameText.getText().trim();
        if (username.isEmpty()) {
            showMessage("请输入用户名！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = showConfirmDialog("确认要解除用户 [" + username + "] 的黑名单状态吗？", "确认解除拉黑");
        if (confirm != JOptionPane.YES_OPTION) return;

        userDAOIMPL dao = new userDAOIMPL();
        if (dao.unbanUser(username)) {
            showMessage("用户 [" + username + "] 已成功解除拉黑！", "操作成功", JOptionPane.INFORMATION_MESSAGE);
            searchUser(null);
        } else {
            showMessage("解除用户 [" + username + "] 拉黑状态失败，请检查用户名或重试。", "操作失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(160, 45));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 1),
                        BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
        });

        return button;
    }

    // 简化消息显示方法
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // 简化确认对话框方法
    private int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setUsername("admin");
            testUser.setUserType(2);
            new OperateUsers(testUser);
        });
    }
}