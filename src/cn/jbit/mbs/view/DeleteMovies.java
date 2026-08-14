package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.movieDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteMovies extends JFrame {

    private User currentUser;
    private JTextField movieTitleField;

    public DeleteMovies(User user) {
        this.currentUser = user;

        setTitle("删除电影");
        setSize(500, 350); // 调整窗口大小

        // 屏幕居中
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(248, 249, 250));
        JLabel titleLabel = new JLabel("删除电影信息");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 中心表单面板
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 电影名称标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel movieLabel = new JLabel("电影名称:");
        movieLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        centerPanel.add(movieLabel, gbc);

        // 电影名称输入框
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        movieTitleField = new JTextField(20);
        movieTitleField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        movieTitleField.setPreferredSize(new Dimension(200, 35));
        centerPanel.add(movieTitleField, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));

        JButton deleteButton = createStyledButton("删除", new Color(231, 76, 60));
        JButton cancelButton = createStyledButton("取消", new Color(149, 165, 166));

        // 删除按钮事件
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = movieTitleField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(DeleteMovies.this,
                            "请输入电影名称！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                movieDAOIMPL dao = new movieDAOIMPL();
                boolean success = dao.deleteMovieByTitle(title);

                if (success) {
                    JOptionPane.showMessageDialog(DeleteMovies.this,
                            "电影《" + title + "》删除成功！");
                    movieTitleField.setText("");
                } else {
                    JOptionPane.showMessageDialog(DeleteMovies.this,
                            "未找到该电影，请检查名称是否正确。", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 取消按钮事件
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        // 测试入口
        User testUser = new User();
        testUser.setUsername("testAdmin");
        testUser.setUserType(2); // 管理员
        SwingUtilities.invokeLater(() -> new DeleteMovies(testUser));
    }
}