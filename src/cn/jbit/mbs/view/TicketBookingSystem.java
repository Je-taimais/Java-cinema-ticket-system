package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.Order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

public class TicketBookingSystem extends JFrame {
    private Long currentUserId;
    private int movieId;
    private String movieTitle;
    private Map<String, JComboBox<String>> timeComboBoxes = new HashMap<>();
    private Map<String, List<String>> cinemaScreeningTimes = new HashMap<>();
    private Map<String, JButton[][]> seatButtons = new HashMap<>();
    private List<Point> selectedSeats = new ArrayList<>();
    private Map<String, Long> cinemaScreeningIds = new HashMap<>();
    private String selectedCinemaId; // 新增字段记录用户选择的影院ID
    private Long selectedScreeningId; // 新增字段记录用户选择的场次ID
    private JPanel seatMapPanel; // 座位图容器面板
    private Map<Long, Random> screeningRandomMap = new HashMap<>(); // 为每个场次ID存储一个随机数生成器

    // 新增：存储每个场次的座位状态，键为场次ID，值为二维数组表示座位是否已售
    private Map<Long, boolean[][]> screeningSeatStatus = new HashMap<>();
    // 座位布局的固定行数和列数
    private static final int SEAT_ROWS = 8;
    private static final int SEAT_COLS = 10;
    // 过道位置（固定不变）
    private static final Set<Point> AISLE_SEATS = new HashSet<>();

    // 静态初始化过道位置
    static {
        // 第5列和第6列设为过道（索引从0开始）
        for (int i = 0; i < SEAT_ROWS; i++) {
            AISLE_SEATS.add(new Point(i, 4));
            AISLE_SEATS.add(new Point(i, 5));
        }
    }

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/电影院";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public TicketBookingSystem(int movieId, String movieTitle, Long currentUserId) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.currentUserId = currentUserId;
        setTitle(movieTitle + " - 在线订票");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 248, 250));

        // 顶部标题
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel(movieTitle + " - 选择场次与座位");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // 选择影院和场次面板
        JPanel selectionPanel = new JPanel(new GridLayout(0, 1, 10, 15));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                "选择影院和场次", 0, 0, new Font("微软雅黑", Font.BOLD, 14), new Color(100, 100, 150)));


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 查询放映该电影的影院
            String cinemaSql = "SELECT c.id, c.name, c.address FROM cinema c " +
                    "JOIN hall h ON c.id = h.cinema_id " +
                    "JOIN screening s ON h.id = s.hall_id " +
                    "WHERE s.movie_id = ? AND s.status = 1 AND c.status = 1 " +
                    "GROUP BY c.id";

            try (PreparedStatement pstmt = conn.prepareStatement(cinemaSql)) {
                pstmt.setInt(1, movieId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String cinemaId = rs.getString("id");
                    String cinemaName = rs.getString("name");
                    String cinemaAddress = rs.getString("address");

                    JPanel cinemaPanel = new JPanel(new BorderLayout(10, 5));
                    cinemaPanel.setBackground(new Color(248, 250, 252));

                    // 影院信息
                    JLabel cinemaLabel = new JLabel("<html><b>" + cinemaName + "</b><br>" + cinemaAddress + "</html>");
                    cinemaLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                    cinemaPanel.add(cinemaLabel, BorderLayout.WEST);

                    // 场次选择
                    JComboBox<String> timeCombo = new JComboBox<>();
                    // 在initUI()加载场次选择框后，添加选择监听
                    timeCombo.addActionListener(e -> {
                        if (timeCombo.getSelectedItem() != null) {
                            selectedCinemaId = cinemaId; // 当用户选择场次时，更新当前选择的影院ID
                            String selectedTime = (String) timeCombo.getSelectedItem();
                            String key = cinemaId + "-" + selectedTime;
                            selectedScreeningId = cinemaScreeningIds.get(key);

                            // 清空之前选择的座位
                            selectedSeats.clear();

                            // 生成基于场次ID的固定座位图
                            generateSeatMapForScreening();
                        }
                    });

                    timeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
                    loadScreeningTimes(conn, cinemaId, timeCombo);
                    cinemaPanel.add(timeCombo, BorderLayout.CENTER);

                    selectionPanel.add(cinemaPanel);
                    timeComboBoxes.put(cinemaId, timeCombo);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载影院信息失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        mainPanel.add(selectionPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // 座位选择面板
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
        seatPanel.setBackground(Color.WHITE);
        seatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 220), 1), // 线条边框
                        "选择座位",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("微软雅黑", Font.BOLD, 14),
                        new Color(100, 100, 150)
                )
        ));

        // 屏幕标识
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(new Color(180, 200, 220));
        screenPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 150), 2));
        screenPanel.setPreferredSize(new Dimension(600, 30));
        screenPanel.setMaximumSize(new Dimension(600, 30));
        JLabel screenLabel = new JLabel("银幕方向");
        screenLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        screenLabel.setForeground(new Color(60, 80, 100));
        screenPanel.add(screenLabel);
        seatPanel.add(screenPanel);
        seatPanel.add(Box.createVerticalStrut(10));

        // 座位图容器
        seatMapPanel = new JPanel();
        seatMapPanel.setLayout(new GridLayout(0, 1, 5, 5));
        seatMapPanel.setBackground(Color.WHITE);
        seatMapPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 初始显示一个空的座位图
        JPanel emptySeatMap = new JPanel();
        emptySeatMap.setBackground(Color.WHITE);
        JLabel hintLabel = new JLabel("请先选择影院和场次", JLabel.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        hintLabel.setForeground(new Color(100, 100, 100));
        emptySeatMap.add(hintLabel);
        seatMapPanel.add(emptySeatMap);

        seatPanel.add(seatMapPanel);

        // 座位图例
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);

        legendPanel.add(createLegendItem("可选座位", new Color(100, 200, 100)));
        legendPanel.add(createLegendItem("已选座位", new Color(70, 130, 180)));
        legendPanel.add(createLegendItem("已售座位", new Color(220, 100, 100)));
        legendPanel.add(createLegendItem("过道", new Color(240, 240, 240)));

        seatPanel.add(legendPanel);
        mainPanel.add(seatPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = new JButton("确认选座");
        confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> confirmBooking());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        mainPanel.add(buttonPanel);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private void loadScreeningTimes(Connection conn, String cinemaId, JComboBox<String> comboBox) throws SQLException {
        String sql = "SELECT s.id, s.start_time, s.end_time, s.price, h.name AS hall_name " +
                "FROM screening s " +
                "JOIN hall h ON s.hall_id = h.id " +
                "WHERE s.movie_id = ? AND h.cinema_id = ? AND s.status = 1 AND h.status = 1 " +
                "ORDER BY s.start_time";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, cinemaId);
            ResultSet rs = pstmt.executeQuery();

            comboBox.removeAllItems();
            List<String> times = new ArrayList<>();

            while (rs.next()) {
                Long screeningId = rs.getLong("s.id");
                Timestamp startTime = rs.getTimestamp("start_time");
                Timestamp endTime = rs.getTimestamp("end_time");
                double price = rs.getDouble("price");
                String hallName = rs.getString("hall_name");

                // 创建日期格式化对象
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                // 格式化开始和结束时间
                String formattedStartTime = sdf.format(startTime);
                String formattedEndTime = sdf.format(endTime);

                // 修改时间格式为：年月日 时分
                String timeStr = String.format("%s - %s | %s | ¥%.2f | id:%d",
                        formattedStartTime, formattedEndTime, hallName, price, screeningId);

                comboBox.addItem(timeStr);
                times.add(timeStr);

                cinemaScreeningIds.put(cinemaId + "-" + timeStr, screeningId);

                // 为每个场次ID创建一个随机数生成器，使用场次ID作为种子
                screeningRandomMap.put(screeningId, new Random(screeningId));
            }

            cinemaScreeningTimes.put(cinemaId, times);
        }
    }

    // 为指定场次生成座位图
    private void generateSeatMapForScreening() {
        if (selectedCinemaId == null || selectedScreeningId == null) {
            return;
        }

        // 清除当前座位图
        seatMapPanel.removeAll();

        // 创建新的座位图
        JPanel newSeatMap = new JPanel(new GridLayout(SEAT_ROWS, SEAT_COLS, 5, 5));

        // 获取该场次的座位状态（从数据库加载）
        boolean[][] seatStatus = getSeatStatusFromDatabase(selectedScreeningId);

        for (int i = 0; i < SEAT_ROWS; i++) {
            for (int j = 0; j < SEAT_COLS; j++) {
                JButton seatBtn = createSeatButton(i, j, seatStatus[i][j]);
                newSeatMap.add(seatBtn);

                // 存储座位按钮
                String key = selectedCinemaId + "-" + selectedScreeningId;
                seatButtons.computeIfAbsent(key, k -> new JButton[SEAT_ROWS][SEAT_COLS]);
                seatButtons.get(key)[i][j] = seatBtn;
            }
        }

        seatMapPanel.add(newSeatMap);
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    /**
     * 生成固定的座位状态，使用场次ID作为种子确保一致性
     */
    private boolean[][] getSeatStatusFromDatabase(Long screeningId) {
        boolean[][] seatStatus = new boolean[SEAT_ROWS][SEAT_COLS];

        // 先初始化为未售出状态
        for (int i = 0; i < SEAT_ROWS; i++) {
            for (int j = 0; j < SEAT_COLS; j++) {
                seatStatus[i][j] = false;
            }
        }

        // 过道座位始终不可选
        for (Point aisle : AISLE_SEATS) {
            seatStatus[aisle.x][aisle.y] = true;
        }

        // 从数据库查询已售出的座位
        String sql = "SELECT seat_row, seat_col FROM order_seat WHERE screening_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, screeningId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int row = rs.getInt("seat_row");
                int col = rs.getInt("seat_col");
                if (row >= 0 && row < SEAT_ROWS && col >= 0 && col < SEAT_COLS) {
                    seatStatus[row][col] = true; // 标记为已售出
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "获取座位状态失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

        return seatStatus;
    }


    /**
     * 创建座位按钮，根据座位状态设置样式
     */
    private JButton createSeatButton(int row, int col, boolean isSold) {
        JButton button = new JButton(row + "-" + col);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        button.setFocusPainted(false);
        button.setMargin(new Insets(1, 1, 1, 1));

        // 过道座位
        if (AISLE_SEATS.contains(new Point(row, col))) {
            button.setBackground(new Color(240, 240, 240)); // 过道
            button.setEnabled(false);
        }
        // 已售座位
        else if (isSold) {
            button.setBackground(new Color(220, 100, 100)); // 已售座位（红色）
            button.setEnabled(false);
        }
        // 可选座位
        else {
            button.setBackground(new Color(100, 200, 100)); // 可选座位（绿色）
            button.addActionListener(e -> toggleSeatSelection(button, row, col));
        }

        button.setBorder(new LineBorder(new Color(180, 180, 200), 1));
        return button;
    }

    private void toggleSeatSelection(JButton button, int row, int col) {
        Point seat = new Point(row, col);

        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            button.setBackground(new Color(100, 200, 100)); // 恢复为可选
        } else {
            if (selectedSeats.size() >= 6) {
                JOptionPane.showMessageDialog(this, "最多只能选择6个座位", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedSeats.add(seat);
            button.setBackground(new Color(70, 130, 180)); // 设置为已选
        }
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);

        JLabel colorLabel = new JLabel();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        colorLabel.setPreferredSize(new Dimension(20, 20));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        panel.add(colorLabel);
        panel.add(textLabel);
        return panel;
    }

    private void confirmBooking() {
        if (selectedCinemaId == null) {
            JOptionPane.showMessageDialog(this, "请先选择影院场次", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> timeComboBox = timeComboBoxes.get(selectedCinemaId);
        if (timeComboBox == null || timeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择有效的场次", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedTime = (String) timeComboBox.getSelectedItem();

        // 直接从映射中获取选中的场次ID
        String key = selectedCinemaId + "-" + selectedTime;
        selectedScreeningId = cinemaScreeningIds.get(key);

        if (selectedScreeningId == null) {
            JOptionPane.showMessageDialog(this, "未找到有效的场次，请重新选择", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择至少一个座位", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 从当前选中的场次中提取实际票价
        double ticketPrice = 45.0; // 默认值
        String[] parts = selectedTime.split(" \\| ");
        if (parts.length >= 3) {
            String pricePart = parts[2];
            if (pricePart.startsWith("¥")) {
                ticketPrice = Double.parseDouble(pricePart.substring(1));
            }
        }

        double totalAmount = selectedSeats.size() * ticketPrice;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确认购买 " + selectedSeats.size() + " 张电影票？\n" +
                        "总金额: ¥" + String.format("%.2f", totalAmount),
                "确认订单",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 显示支付对话框
            PaymentDialog paymentDialog = new PaymentDialog(this, totalAmount);
            paymentDialog.setVisible(true);
            paymentDialog.startPaymentMonitoring();

            if (paymentDialog.isPaymentCompleted()) {
                int actualPaymentMethod = paymentDialog.getPaymentMethod();
                Order order = generateOrder(totalAmount, selectedScreeningId, actualPaymentMethod);

                // 保存订单到数据库
                if (saveOrderToDatabase(order)) {
                    // 保存购买的座位
                    saveOrderSeats(order.getId(), selectedScreeningId, selectedSeats);

                    JOptionPane.showMessageDialog(
                            this,
                            "购票成功！\n" +
                                    "电影: " + movieTitle + "\n" +
                                    "座位数: " + selectedSeats.size() + " 个\n" +
                                    "请凭取票码至影院取票",
                            "订票成功",
                            JOptionPane.INFORMATION_MESSAGE);

                    showTicketCodeImage();
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "支付未完成，订单已取消",
                        "支付取消",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private boolean saveOrderSeats(Long orderId, Long screeningId, List<Point> seats) {
        String sql = "INSERT INTO order_seat (order_id, screening_id, seat_row, seat_col, create_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Point seat : seats) {
                pstmt.setLong(1, orderId);
                pstmt.setLong(2, screeningId);
                pstmt.setInt(3, seat.x);
                pstmt.setInt(4, seat.y);
                pstmt.setObject(5, new Date());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "保存座位信息失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    private Order generateOrder(double totalAmount, Long screeningId, int paymentMethod) {
        // ✅ 新增参数
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setUserId(currentUserId);
        order.setScreeningId(screeningId);
        order.setTotalPrice(BigDecimal.valueOf(totalAmount));
        order.setPaymentMethod(paymentMethod); // 使用实际选择的支付方式
        order.setOrderStatus(1); // 已支付
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        return order;
    }

    private boolean saveOrderToDatabase(Order order) {
        String sql = "INSERT INTO `order` (order_no, user_id, screening_id, total_price, payment_method, " +
                "order_status, create_time, update_time, payment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, order.getOrderNo());
            pstmt.setLong(2, order.getUserId());
            pstmt.setLong(3, order.getScreeningId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            pstmt.setInt(5, order.getPaymentMethod());
            pstmt.setInt(6, order.getOrderStatus());
            pstmt.setObject(7, order.getCreateTime());
            pstmt.setObject(8, order.getUpdateTime());
            pstmt.setObject(9, new Date()); // 支付时间

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "订单保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 新增方法：显示取票码图片
    private void showTicketCodeImage() {
        JDialog imageDialog = new JDialog(this, "取票码", true);
        imageDialog.setSize(500, 400);
        imageDialog.setLocationRelativeTo(this);
        imageDialog.setLayout(new BorderLayout());

        // 提示文字
        JLabel infoLabel = new JLabel("请保存以下取票码，凭码至影院取票", SwingConstants.CENTER);
        infoLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        infoLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        imageDialog.add(infoLabel, BorderLayout.NORTH);

        try {
            // 加载图片（使用ImageIcon从文件系统加载）
            ImageIcon originalIcon = new ImageIcon("picture/ticket.png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBorder(new LineBorder(Color.GRAY, 1));

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.add(imageLabel);
            imageDialog.add(centerPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            // 图片加载失败时显示错误信息
            JLabel errorLabel = new JLabel("无法加载取票码图片: " + e.getMessage(), SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            imageDialog.add(errorLabel, BorderLayout.CENTER);
        }

        // 确定按钮
        JButton okButton = new JButton("确定");
        okButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        okButton.addActionListener(e -> imageDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        imageDialog.add(buttonPanel, BorderLayout.SOUTH);

        imageDialog.setVisible(true);
    }

    public static void main(String[] args) {
        // 示例用法，实际使用时应从登录系统获取参数
        SwingUtilities.invokeLater(() -> {
            new TicketBookingSystem(1, "复仇者联盟6", 12345L).setVisible(true);
        });
    }
}
