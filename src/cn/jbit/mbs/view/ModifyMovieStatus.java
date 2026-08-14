package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.entity.MovieStatus;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ModifyMovieStatus extends JFrame {
    private User currentUser;

    // 定义颜色常量
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color LIGHT_COLOR = new Color(245, 245, 245);
    private static final Color DARK_COLOR = new Color(52, 73, 94);

    public ModifyMovieStatus(User user) {
        this.currentUser = user;
        setTitle("修改电影状态");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 使用半透明渐变背景
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(240, 248, 255, 230);
                Color color2 = new Color(224, 236, 248, 230);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };

        // 创建标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("电影状态修改");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(DARK_COLOR);
        titlePanel.add(titleLabel);

        // 创建内容面板
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        // 电影下拉框
        JComboBox<MovieItem> movieComboBox = new JComboBox<>();
        loadMovies(movieComboBox);

        // 美化下拉框
        movieComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        movieComboBox.setPreferredSize(new Dimension(200, 35));
        movieComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 状态下拉框
        JComboBox<String> statusComboBox = new JComboBox<>();
        for (MovieStatus status : MovieStatus.values()) {
            statusComboBox.addItem(status.getDescription());
        }

        // 美化下拉框
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(new Dimension(200, 35));
        statusComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 标签美化
        JLabel movieLabel = new JLabel("选择电影：");
        movieLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        movieLabel.setForeground(DARK_COLOR);

        JLabel statusLabel = new JLabel("选择状态：");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        statusLabel.setForeground(DARK_COLOR);

        // 提交按钮
        JButton submitButton = new JButton("提交修改");

        // 美化按钮
        submitButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 添加按钮悬停效果
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.setBackground(PRIMARY_COLOR);
            }
        });

        // 按钮事件处理
        submitButton.addActionListener(e -> {
            MovieItem selectedMovie = (MovieItem) movieComboBox.getSelectedItem();
            String selectedStatus = (String) statusComboBox.getSelectedItem();

            if (selectedMovie == null || selectedStatus == null) {
                JOptionPane.showMessageDialog(this, "请选择电影和状态", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int statusCode = MovieStatus.fromDescription(selectedStatus).getCode();

            try (Connection conn = JDBCUtil.getConnection()) {
                String sql = "UPDATE movie SET status = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, statusCode);
                    pstmt.setLong(2, selectedMovie.getId());
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "电影状态修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "修改失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "数据库连接失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 添加组件到内容面板
        contentPanel.add(movieLabel);
        contentPanel.add(movieComboBox);
        contentPanel.add(statusLabel);
        contentPanel.add(statusComboBox);
        contentPanel.add(new JLabel()); // 占位
        contentPanel.add(submitButton);

        // 创建卡片面板
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        cardPanel.add(titlePanel, BorderLayout.NORTH);
        cardPanel.add(contentPanel, BorderLayout.CENTER);

        // 添加阴影效果
        JPanel shadowPanel = new JPanel(new GridBagLayout());
        shadowPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        shadowPanel.add(cardPanel, gbc);

        mainPanel.add(shadowPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    // 加载电影数据
    private void loadMovies(JComboBox<MovieItem> comboBox) {
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM movie")) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                comboBox.addItem(new MovieItem(id, title));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载电影失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 电影项类
    private static class MovieItem {
        private final long id;
        private final String title;

        public MovieItem(long id, String title) {
            this.id = id;
            this.title = title;
        }

        public long getId() {
            return id;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}