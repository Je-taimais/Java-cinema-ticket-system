package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.ArrayList;
import java.util.List;

public class Home extends JPanel {
    private User currentUser;
    // 数据库连接信息 - 请根据您的实际配置修改
    private static final String DB_URL = "jdbc:mysql://localhost:3306/电影院";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // 电影信息类
    private class Movie {
        int id;
        String title;
        String poster_url;
        String genres;
        double rating;

        Movie(int id, String title, String posterPath, String category, double rating) {
            this.id = id;
            this.title = title;
            this.poster_url = posterPath;
            this.genres = category;
            this.rating = rating;
        }
    }

    public Home(User user) {
        this.currentUser = user;
        initUI();
        loadMoviesFromDB();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(235, 228, 240));

        // 顶部标题
        JLabel titleLabel = new JLabel("热门电影推荐", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 30, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void loadMoviesFromDB() {
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title, poster_url, genres, rating FROM movie WHERE rating >= 9.0")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String poster_url = rs.getString("poster_url");
                String genres = rs.getString("genres");
                double rating = rs.getDouble("rating");

                movies.add(new Movie(id, title, poster_url, genres, rating));
            }

            displayMovies(movies);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "数据库连接失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void displayMovies(List<Movie> movies) {
        // 创建电影展示面板
        JPanel moviePanel = new JPanel(new GridLayout(0, 4, 20, 30)); // 每行4个电影
        moviePanel.setBackground(new Color(235, 228, 240));
        moviePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        for (Movie movie : movies) {
            moviePanel.add(createMovieCard(movie));
        }

        // 添加到滚动面板
        JScrollPane scrollPane = new JScrollPane(moviePanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        // 修正边框创建方式
        Border border = BorderFactory.createLineBorder(new Color(200, 180, 220), 1);
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        card.setBorder(BorderFactory.createCompoundBorder(border, margin));

        card.setPreferredSize(new Dimension(200, 300));

        // 电影海报
        JLabel posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            ImageIcon originalIcon = new ImageIcon(movie.poster_url);
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

        // 电影信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);

        // 电影标题
        JLabel titleLabel = new JLabel(movie.title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(titleLabel, BorderLayout.NORTH);

        // 评分和分类
        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        detailPanel.setBackground(Color.WHITE);

        // 评分
        JPanel ratingPanel = new JPanel();
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.add(new JLabel(String.format("评分: %.1f", movie.rating)));

        // 分类
        JLabel categoryLabel = new JLabel(movie.genres);
        categoryLabel.setForeground(new Color(150, 50, 200));

        detailPanel.add(ratingPanel);
        detailPanel.add(categoryLabel);
        infoPanel.add(detailPanel, BorderLayout.SOUTH);

        card.add(infoPanel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openMovieDetail(movie.id);
            }
        });

        // 为标题添加点击事件
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openMovieDetail(movie.id);
            }
        });

        return card;
    }
    private void openMovieDetail(int movieId) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(Home.this);
        topFrame.getContentPane().removeAll();

        MovieDetail detailPanel = new MovieDetail(movieId, currentUser); // ✅ 使用 currentUser
        topFrame.add(detailPanel);

        topFrame.revalidate();
        topFrame.repaint();
    }

    public static void main(String[] args) {

    }
}