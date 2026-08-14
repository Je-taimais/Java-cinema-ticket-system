package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.Order;
import cn.jbit.mbs.dao.impl.OrderDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class MyOrder extends JFrame {
    private final String currentUserId;

    // 颜色定义
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color CARD_BG = new Color(250, 250, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(230, 126, 34);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DISABLED_COLOR = new Color(149, 165, 166);

    public MyOrder(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("用户ID为空，无法加载订单");
        }
        this.currentUserId = user.getId().toString();
        setTitle("我的订单");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI(currentUserId);
    }

    private void initUI(String userId) {
        // 使用现代布局
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(240, 242, 245));

        // 标题栏美化
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("我的订单");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 主内容区域
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(240, 242, 245));

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(new Color(240, 242, 245));

        OrderDAOIMPL orderDAO = new OrderDAOIMPL();
        List<Order> orders = orderDAO.getOrdersByUserId(userId);

        if (orders.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(new Color(240, 242, 245));
            emptyPanel.setBorder(new EmptyBorder(50, 0, 50, 0));

            JLabel noOrderLabel = new JLabel("暂无订单记录", SwingConstants.CENTER);
            noOrderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            noOrderLabel.setForeground(new Color(120, 120, 120));
            emptyPanel.add(noOrderLabel, BorderLayout.CENTER);

            orderPanel.add(emptyPanel);
        } else {
            // 添加间隔
            orderPanel.add(Box.createVerticalStrut(10));

            for (Order order : orders) {
                orderPanel.add(createOrderCard(order));
                orderPanel.add(Box.createVerticalStrut(15));  // 卡片间距
            }
        }

        JScrollPane scrollPane = new JScrollPane(orderPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 230)),
                new EmptyBorder(15, 20, 15, 20) ));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 240));

        // 顶部信息（订单号+状态）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel orderNoLabel = new JLabel("订单号: " + order.getOrderNo());
        orderNoLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        orderNoLabel.setForeground(new Color(60, 60, 60));
        topPanel.add(orderNoLabel, BorderLayout.WEST);

        JLabel statusLabel = new JLabel(getOrderStatusDesc(order.getOrderStatus()));
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(order.getOrderStatus()));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(statusLabel, BorderLayout.EAST);

        card.add(topPanel, BorderLayout.NORTH);

        // 中间信息（电影详情）
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 2, 10, 8));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        addInfoRow(centerPanel, "电影名称:", order.getMovieTitle());
        addInfoRow(centerPanel, "影院名称:", order.getCinemaName());
        addInfoRow(centerPanel, "座位信息:", String.join(", ", order.getSeats()));
        // 修改场次信息显示
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String screeningTimeValue = "未知";
        if (order.getStartTime() != null && order.getEndTime() != null) {
            screeningTimeValue = sdf.format(order.getStartTime()) + " - " + sdf.format(order.getEndTime());
        }
        // 添加影厅信息
        String hallInfo = order.getHallName() != null ? order.getHallName() : "未知";
        String screeningInfo = screeningTimeValue + " | " + hallInfo;

        addInfoRow(centerPanel, "场次信息:", screeningInfo);

        addInfoRow(centerPanel, "支付方式:", getPaymentMethodDesc(order.getPaymentMethod()));
        addInfoRow(centerPanel, "订单金额:", "¥" + order.getTotalPrice());

        String createTimeValue = "未知";
        if (order.getCreateTime() != null) {
            createTimeValue = sdf.format(order.getCreateTime());
        }
        addInfoRow(centerPanel, "下单时间:", createTimeValue);

        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        lbl.setForeground(new Color(100, 100, 100));
        panel.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        val.setForeground(new Color(40, 40, 40));
        panel.add(val);
    }

    private Color getStatusColor(Integer status) {
        if (status == null) return Color.DARK_GRAY;
        switch (status) {
            case 0: return WARNING_COLOR; // 待支付
            case 1: return new Color(52, 152, 219); // 已支付
            case 2: return SUCCESS_COLOR; // 已完成
            case 3: return DISABLED_COLOR; // 已取消
            case 4: return DANGER_COLOR; // 已退票
            default: return Color.DARK_GRAY;
        }
    }

    private String getPaymentMethodDesc(Integer code) {
        if (code == null) return "未知";
        switch (code) {
            case 1: return "支付宝";
            case 2: return "微信支付";
            default: return "未支付";
        }
    }

    private String getOrderStatusDesc(Integer code) {
        if (code == null) return "未知";
        switch (code) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "已退票";
            default: return "未知";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            User testUser = new User();
            testUser.setId(1L);
            MyOrder frame = new MyOrder(testUser);
            frame.setVisible(true);
        });
    }
}