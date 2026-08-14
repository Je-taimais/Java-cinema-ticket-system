package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MovieDetail extends JPanel {
    private User currentUser;
    private int movieId;
    private String title;
    private String original_title;
    private String director;
    private String writers;
    private String actors;
    private String genres;
    private String country;
    private String language;
    private String release_date;
    private int runtime;
    private String description;
    private double rating;
    private int votes;
    private double box_office;
    private String status;
    private String poster_url;

    public MovieDetail(int movieId,  User user) {
        this.movieId = movieId;
        this.currentUser = user;
        loadMovieDetails();
        initUI();
    }

    private void loadMovieDetails() {
        // 数据库连接信息
        String DB_URL = "jdbc:mysql://localhost:3306/电影院";
        String DB_USER = "root";
        String DB_PASSWORD = "root";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT title, original_title, director, writers, actors, genres, " +
                             "country, language, release_date, runtime, description, rating, " +
                             "votes, box_office, status, poster_url FROM movie WHERE id = ?")) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                title = rs.getString("title");
                original_title = rs.getString("original_title");
                director = rs.getString("director");
                writers = rs.getString("writers");
                actors = rs.getString("actors");
                genres = rs.getString("genres");
                country = rs.getString("country");
                language = rs.getString("language");
                release_date = rs.getString("release_date");
                runtime = rs.getInt("runtime");
                description = rs.getString("description");
                rating = rs.getDouble("rating");
                votes = rs.getInt("votes");
                box_office = rs.getDouble("box_office");
                status = rs.getString("status");
                poster_url = rs.getString("poster_url");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载电影详情失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 235, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(250, 248, 252));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 220), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 海报面板
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBackground(new Color(250, 248, 252));
        posterPanel.setPreferredSize(new Dimension(300, 450)); // 设置固定高度
        posterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setVerticalAlignment(SwingConstants.CENTER);

        // 加载并显示海报
        if (poster_url != null && !poster_url.isEmpty()) {
            try {
                // 从URL加载图片
                BufferedImage originalImage = ImageIO.read(new File(poster_url));

                // 计算缩放尺寸（保持宽高比）
                int maxWidth = 280;
                int maxHeight = 420;
                int newWidth = originalImage.getWidth();
                int newHeight = originalImage.getHeight();

                // 按宽度缩放
                if (newWidth > maxWidth) {
                    newHeight = (int) (newHeight * (maxWidth / (double) newWidth));
                    newWidth = maxWidth;
                }

                // 按高度缩放
                if (newHeight > maxHeight) {
                    newWidth = (int) (newWidth * (maxHeight / (double) newHeight));
                    newHeight = maxHeight;
                }

                // 创建缩放后的图片
                Image scaledImage = originalImage.getScaledInstance(
                        newWidth, newHeight, Image.SCALE_SMOOTH);

                posterLabel.setIcon(new ImageIcon(scaledImage));
            } catch (IOException ex) {
                // 加载失败时显示占位文本
                posterLabel.setText("<html><center>海报加载失败<br><font size=2>" +
                        ex.getMessage() + "</font></center></html>");
                posterLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                posterLabel.setForeground(Color.RED);
            }
        } else {
            // 没有海报URL时显示提示
            posterLabel.setText("暂无海报");
            posterLabel.setFont(new Font("微软雅黑", Font.ITALIC, 16));
            posterLabel.setForeground(new Color(150, 150, 150));
        }

        posterPanel.add(posterLabel, BorderLayout.CENTER);
        mainPanel.add(posterPanel, BorderLayout.WEST);

        // 信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(250, 248, 252));

        // 标题
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(80, 40, 120));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 原标题
        if (original_title != null && !original_title.isEmpty()) {
            JLabel originalTitleLabel = new JLabel("原名: " + original_title);
            originalTitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            originalTitleLabel.setForeground(Color.DARK_GRAY);
            originalTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(originalTitleLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // 基本信息网格
        JPanel basicInfo = new JPanel(new GridLayout(0, 2, 10, 10));
        basicInfo.setBackground(new Color(250, 248, 252));
        basicInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        addInfoRow(basicInfo, "导演：", director);
        addInfoRow(basicInfo, "编剧：", writers);
        addInfoRow(basicInfo, "主演：", actors);
        addInfoRow(basicInfo, "类型：", genres);
        addInfoRow(basicInfo, "国家/地区：", country);
        addInfoRow(basicInfo, "语言：", language);
        addInfoRow(basicInfo, "上映日期：", release_date);
        addInfoRow(basicInfo, "片长：", runtime + "分钟");
        addInfoRow(basicInfo, "评分：", String.valueOf(rating));
        addInfoRow(basicInfo, "评分人数：", String.valueOf(votes) + "人");
        addInfoRow(basicInfo, "票房：", String.valueOf(box_office) + "万元");
        addInfoRow(basicInfo, "状态(0-未上映， 1-上映中， 2-已下映)：", status);

        infoPanel.add(basicInfo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 剧情简介
        JLabel descTitle = new JLabel("剧情简介");
        descTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        descTitle.setForeground(new Color(100, 50, 150));
        descTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(descTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea descArea = new JTextArea(description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descArea.setBackground(new Color(250, 248, 252));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setBorder(null);
        descScroll.setPreferredSize(new Dimension(0, 200)); // 固定高度
        infoPanel.add(descScroll);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // 添加返回按钮
        JButton backButton = new JButton("返回");
        backButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        backButton.setBackground(new Color(180, 160, 220));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.add(new Home(currentUser)); // 返回到首页
            topFrame.revalidate();
            topFrame.repaint();
        });

        // 创建购票按钮
        JButton buyButton = new JButton("购票");
        buyButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        buyButton.setBackground(new Color(220, 60, 80)); // 醒目的红色
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);

        // 根据电影状态设置按钮可用性 (1表示上映中)
        boolean isShowing = "1".equals(status);
        buyButton.setEnabled(isShowing);

        // 添加购票按钮的提示文本
        if (!isShowing) {
            buyButton.setToolTipText("该影片当前不可购票");
        }

        // 添加购票按钮的事件监听
        buyButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "请先登录", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TicketBookingSystem bookingFrame = new TicketBookingSystem(movieId, title, currentUser.getId());
            bookingFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // 增加按钮间距
        buttonPanel.setBackground(new Color(250, 248, 252));

        // 添加按钮到面板（购票按钮在前，返回按钮在后）
        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // 格式化票数显示
    private String formatVotes(int votes) {
        if (votes >= 10000) {
            return String.format("%.1f万", votes / 10000.0);
        }
        return String.valueOf(votes);
    }

    // 格式化票房显示
    private String formatBoxOffice(double boxOffice) {
        if (boxOffice >= 100000000) {
            return String.format("%.2f亿元", boxOffice / 100000000.0);
        } else if (boxOffice >= 10000) {
            return String.format("%.2f万元", boxOffice / 10000.0);
        }
        return String.format("%.2f元", boxOffice);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) return;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("微软雅黑", Font.BOLD, 14));
        lbl.setForeground(new Color(90, 90, 90));
        panel.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(val);
    }

    public static void main(String[] args) {

    }
}