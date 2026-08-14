package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyType extends JPanel {
    private User currentUser;
    private JPanel typePanel;
    private JPanel movieListPanel;
    private String currentType = "全部";

    public MyType(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(235, 228, 240));

        // 顶部标题
        JLabel titleLabel = new JLabel("电影类型", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 70, 70));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(200, 180, 220));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(235, 228, 240));

        // 左侧类型选择面板
        typePanel = createTypePanel();
        mainPanel.add(typePanel, BorderLayout.WEST);

        // 右侧电影列表面板
        movieListPanel = createMovieListPanel(currentType);
        JScrollPane scrollPane = new JScrollPane(movieListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTypePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 245, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(150, 0));

        String[] types = {"全部", "动作", "喜剧", "爱情", "科幻", "恐怖", "动画", "悬疑", "剧情", "纪录片"};

        for (String type : types) {
            JButton typeButton = new JButton(type);
            typeButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            typeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            typeButton.setMaximumSize(new Dimension(120, 35));
            typeButton.setBackground(type.equals(currentType) ?
                    new Color(180, 160, 220) : new Color(230, 225, 240));
            typeButton.setForeground(type.equals(currentType) ?
                    Color.WHITE : new Color(70, 70, 70));
            typeButton.setFocusPainted(false);
            typeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            typeButton.addActionListener(e -> {
                // 更新按钮样式
                for (Component c : panel.getComponents()) {
                    if (c instanceof JButton) {
                        JButton btn = (JButton) c;
                        btn.setBackground(new Color(230, 225, 240));
                        btn.setForeground(new Color(70, 70, 70));
                    }
                }
                typeButton.setBackground(new Color(180, 160, 220));
                typeButton.setForeground(Color.WHITE);

                // 更新电影列表
                currentType = type;
                updateMovieList(type);
            });
            panel.add(typeButton);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        return panel;
    }

    private JPanel createMovieListPanel(String type) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 4, 20, 20));  // 每行4个电影
        panel.setBackground(new Color(235, 228, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 0));

        List<Movie> movies = getMoviesByType(type);

        for (Movie movie : movies) {
            panel.add(createMovieCard(movie));
        }

        return panel;
    }

    private void updateMovieList(String type) {
        movieListPanel.removeAll();

        List<Movie> movies = getMoviesByType(type);

        for (Movie movie : movies) {
            movieListPanel.add(createMovieCard(movie));
        }

        movieListPanel.revalidate();
        movieListPanel.repaint();
    }

    // 将createMovieCard方法移到MyType类中
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        // 使用与Home类相同的边框样式
        Border border = BorderFactory.createLineBorder(new Color(200, 180, 220), 1);
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        card.setBorder(BorderFactory.createCompoundBorder(border, margin));

        card.setPreferredSize(new Dimension(200, 300));  // 统一尺寸

        // 电影海报（使用相同加载逻辑）
        JLabel posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            ImageIcon originalIcon = new ImageIcon(movie.getPoster());
            Image scaledImage = originalIcon.getImage().getScaledInstance(180, 240, Image.SCALE_SMOOTH);
            posterLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // 使用默认图片
            posterLabel.setText("海报加载失败");
            posterLabel.setPreferredSize(new Dimension(180, 240));
            posterLabel.setOpaque(true);
            posterLabel.setBackground(Color.LIGHT_GRAY);
        }
        card.add(posterLabel, BorderLayout.CENTER);

        // 电影信息面板（使用相同布局）
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        // 电影标题（居中显示）
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(titleLabel, BorderLayout.NORTH);

        // 评分和分类（水平流式布局）
        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        detailPanel.setBackground(Color.WHITE);

        // 评分
        JPanel ratingPanel = new JPanel();
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.add(new JLabel(String.format("评分: %.1f", movie.getRating())));

        // 分类
        JLabel categoryLabel = new JLabel(movie.getGenres());
        categoryLabel.setForeground(new Color(150, 50, 200));  // 使用相同颜色

        detailPanel.add(ratingPanel);
        detailPanel.add(categoryLabel);
        infoPanel.add(detailPanel, BorderLayout.SOUTH);

        card.add(infoPanel, BorderLayout.SOUTH);

        // 添加鼠标点击事件
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openMovieDetail(movie.getId());
            }
        });

        // 为标题添加点击事件
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openMovieDetail(movie.getId());
            }
        });

        return card;
    }

    // 打开电影详情页
    private void openMovieDetail(int movieId) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MyType.this);
        topFrame.getContentPane().removeAll();

        MovieDetail detailPanel = new MovieDetail(movieId, currentUser); // ✅ 正确传入用户
        topFrame.add(detailPanel);

        topFrame.revalidate();
        topFrame.repaint();
    }

    // 从数据库获取电影数据
    private List<Movie> getMoviesByType(String genres) {
        List<Movie> movies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtil.getConnection();
            String sql;
            if ("全部".equals(genres)) {
                sql = "SELECT id, title, genres, rating, poster_url FROM movie"; // 添加id字段
                pstmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT id, title, genres, rating, poster_url FROM movie WHERE genres = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, genres);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id"); // 获取ID
                String title = rs.getString("title");
                String movieType = rs.getString("genres");
                double rating = rs.getDouble("rating");
                String poster_url = rs.getString("poster_url");
                movies.add(new Movie(id, title, movieType, rating, poster_url));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }

        return movies;
    }

    // 内部Movie类，用于存储电影信息
    private static class Movie {
        private int id;
        private String title;
        private String genres;
        private double rating;
        private String poster_url;

        public Movie(int id, String title, String genres, double rating, String poster_url) {
            this.id = id;
            this.title = title;
            this.genres = genres;
            this.rating = rating;
            this.poster_url = poster_url;
        }

        public String getTitle() {
            return title;
        }

        public String getGenres() {
            return genres;
        }

        public double getRating() {
            return rating;
        }

        public String getPoster() {
            return poster_url;
        }

        public int getId() {
            return id;
        }
    }

    public static void main(String[] args) {

    }
}