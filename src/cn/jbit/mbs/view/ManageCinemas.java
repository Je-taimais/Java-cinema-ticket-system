package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.cinemaDAO;
import cn.jbit.mbs.dao.impl.cinemaDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageCinemas extends JFrame {
    private User currentUser;
    private cinemaDAO cinemaDao = new cinemaDAOIMPL();

    public ManageCinemas(User user) {
        this.currentUser = user;
        setTitle("影院管理系统");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("影院影厅管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 功能按钮面板 - 使用2列布局
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        // 创建按钮
        JButton addCinemaButton = createStyledButton("新增影院", new Color(70, 130, 180));
        addCinemaButton.addActionListener(e -> addCinema());

        JButton deleteCinemaButton = createStyledButton("删除影院", new Color(178, 34, 34));
        deleteCinemaButton.addActionListener(e -> deleteCinema());

        JButton addHallButton = createStyledButton("新增影厅", new Color(65, 105, 225));
        addHallButton.addActionListener(e -> addCinemaHall());

        JButton deleteHallButton = createStyledButton("删除影厅", new Color(205, 92, 92));
        deleteHallButton.addActionListener(e -> deleteCinemaHall());

        JButton updateAddressButton = createStyledButton("修改地址", new Color(60, 179, 113));
        updateAddressButton.addActionListener(e -> updateCinemaAddress());

        JButton viewAllButton = createStyledButton("查看全部", new Color(138, 43, 226));
        viewAllButton.addActionListener(e -> viewAllCinemas());

        // 新增：修改影厅状态按钮
        JButton updateHallStatusButton = createStyledButton("修改影厅状态", new Color(184, 134, 11)); // 深金色
        updateHallStatusButton.addActionListener(e -> updateHallStatus());

        // 新增：修改影院状态按钮
        JButton updateCinemaStatusButton = createStyledButton("修改影院状态", new Color(218, 165, 32)); // 金色
        updateCinemaStatusButton.addActionListener(e -> updateCinemaStatus());


        // 添加按钮到面板
        buttonPanel.add(addCinemaButton);
        buttonPanel.add(deleteCinemaButton);
        buttonPanel.add(addHallButton);
        buttonPanel.add(deleteHallButton);
        buttonPanel.add(updateAddressButton);
        buttonPanel.add(updateCinemaStatusButton); // 新增按钮
        buttonPanel.add(updateHallStatusButton);   // 新增按钮
        buttonPanel.add(viewAllButton);

        // 中心面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 底部面板 - 返回按钮和版权信息
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // 返回按钮
        JButton returnButton = createStyledButton("返回主界面", new Color(119, 136, 153));
        returnButton.addActionListener(e -> {
            dispose();
            new AdminMain(currentUser);
        });
        JPanel returnPanel = new JPanel();
        returnPanel.setOpaque(false);
        returnPanel.add(returnButton);
        bottomPanel.add(returnPanel, BorderLayout.NORTH);

        // 版权信息
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 电影票务系统 - 影院管理界面");
        footerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        bottomPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
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

    // 以下原有方法保持不变...
    private void addCinema() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入影院名称：");
        if (cinemaName == null || cinemaName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "影院名称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String address = JOptionPane.showInputDialog(this, "请输入影院地址：");
        if (address == null || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "影院地址不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int STATUS_ACTIVE = 1;
        final int STATUS_INACTIVE = 0;

        String input = JOptionPane.showInputDialog(this, "请输入影院状态（" +
                STATUS_ACTIVE + "表示正常，" +
                STATUS_INACTIVE + "表示停业）：");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "影院状态不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int status;
        try {
            status = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字（" + STATUS_ACTIVE + " 或 " + STATUS_INACTIVE + "）！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (status != STATUS_ACTIVE && status != STATUS_INACTIVE) {
            JOptionPane.showMessageDialog(this, "影院状态只能为" + STATUS_ACTIVE + "或" + STATUS_INACTIVE + "！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = cinemaDao.addCinema(cinemaName, address, status);
        if (success) {
            JOptionPane.showMessageDialog(this, "成功添加影院【" + cinemaName + "】");
        } else {
            JOptionPane.showMessageDialog(this, "添加失败，影院可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCinema() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入要删除的影院名称：");
        if (cinemaName == null || cinemaName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "影院名称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除影院【" + cinemaName + "】吗？\n删除后将同时删除该影院的所有影厅信息！",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = cinemaDao.deleteCinema(cinemaName);
        if (success) {
            JOptionPane.showMessageDialog(this, "成功删除影院【" + cinemaName + "】");
        } else {
            JOptionPane.showMessageDialog(this, "删除失败，影院可能不存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCinemaHall() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入影院名称：");
        String hallStr = JOptionPane.showInputDialog(this, "请输入要添加的影厅编号：");

        if (cinemaName == null || cinemaName.trim().isEmpty() || hallStr == null || hallStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int hallNumber = Integer.parseInt(hallStr);

            // 添加：选择影厅状态
            String[] statusOptions = {"正常(1)", "维修中(0)"};
            int statusChoice = JOptionPane.showOptionDialog(
                    this,
                    "请选择影厅状态：",
                    "影厅状态",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    statusOptions[0]
            );
            int status = (statusChoice == 1) ? 0 : 1; // 0-维修中，1-正常

            boolean success = cinemaDao.addCinemaHall(cinemaName, hallNumber);
            if (success) {
                // 添加后设置状态（因为添加时默认是1，这里根据用户选择更新）
                cinemaDao.updateHallStatus(cinemaName, hallNumber, status);
                JOptionPane.showMessageDialog(this, "成功添加【" + cinemaName + "】的" + hallNumber + "号厅，状态：" +
                        (status == 1 ? "正常" : "维修中"));
            } else {
                JOptionPane.showMessageDialog(this, "添加失败，请检查输入或是否已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字作为影厅编号！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCinemaHall() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入影院名称：");
        String hallStr = JOptionPane.showInputDialog(this, "请输入要删除的影厅编号：");

        if (cinemaName == null || cinemaName.trim().isEmpty() || hallStr == null || hallStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int hallNumber = Integer.parseInt(hallStr);
            boolean success = cinemaDao.deleteCinemaHall(cinemaName, hallNumber);
            if (success) {
                JOptionPane.showMessageDialog(this, "成功删除【" + cinemaName + "】的" + hallNumber + "号厅");
            } else {
                JOptionPane.showMessageDialog(this, "未找到指定的影厅！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字作为影厅编号！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCinemaAddress() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入要修改的影院名称：");
        String newAddress = JOptionPane.showInputDialog(this, "请输入新的地址：");

        if (cinemaName == null || cinemaName.trim().isEmpty() ||
                newAddress == null || newAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = cinemaDao.updateCinemaAddress(cinemaName, newAddress);
        if (success) {
            JOptionPane.showMessageDialog(this, "成功将【" + cinemaName + "】的地址修改为：" + newAddress);
        } else {
            JOptionPane.showMessageDialog(this, "修改失败，请检查影院是否存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewAllCinemas() {
        List<HashMap> cinemas = cinemaDao.getAllCinemasWithHalls();

        StringBuilder sb = new StringBuilder();
        String currentCinema = "";
        for (Map<String, Object> cinema : cinemas) {
            String name = (String) cinema.get("cinema_name");
            Object hallObj = cinema.get("hall_name");
            Object hallStatusObj = cinema.get("hall_status");
            Object cinemaStatusObj = cinema.get("cinema_status"); // 新增影院状态

            if (!name.equals(currentCinema)) {
                if (!currentCinema.isEmpty()) {
                    sb.append("\n");
                }
                // 新增：显示影院状态
                sb.append("【").append(name).append("】 地址：").append(cinema.get("address"))
                        .append("，状态：").append(cinemaStatusObj != null && (int)cinemaStatusObj == 1 ? "正常" : "停业")
                        .append("\n");
                currentCinema = name;
            }

            if (hallObj != null) {
                sb.append("   影厅编号：").append(hallObj).append("，状态：");
                sb.append((hallStatusObj != null && (int)hallStatusObj == 1) ? "正常" : "维修中").append("\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "影院与影厅信息", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCinemaStatus() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入影院名称：");
        if (cinemaName == null || cinemaName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "影院名称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 状态选项
        String[] statusOptions = {"正常(1)", "停业(0)"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "请选择新的影院状态：",
                "修改影院状态",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]
        );

        // 用户取消选择
        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        int status = (choice == 0) ? 1 : 0; // 1-正常，0-停业

        boolean success = cinemaDao.updateCinemaStatus(cinemaName, status);
        if (success) {
            JOptionPane.showMessageDialog(this, "成功将【" + cinemaName + "】的状态修改为：" +
                    (status == 1 ? "正常" : "停业"));
        } else {
            JOptionPane.showMessageDialog(this, "修改失败，请检查影院是否存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============= 新增方法：修改影厅状态 =============
    private void updateHallStatus() {
        String cinemaName = JOptionPane.showInputDialog(this, "请输入影院名称：");
        String hallStr = JOptionPane.showInputDialog(this, "请输入影厅编号：");

        if (cinemaName == null || cinemaName.trim().isEmpty() ||
                hallStr == null || hallStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int hallNumber = Integer.parseInt(hallStr);

            // 状态选项
            String[] statusOptions = {"正常(1)", "维修中(0)"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "请选择新的影厅状态：",
                    "修改影厅状态",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    statusOptions[0]
            );

            // 用户取消选择
            if (choice == JOptionPane.CLOSED_OPTION) {
                return;
            }

            int status = (choice == 0) ? 1 : 0; // 1-正常，0-维修中

            boolean success = cinemaDao.updateHallStatus(cinemaName, hallNumber, status);
            if (success) {
                JOptionPane.showMessageDialog(this, "成功修改【" + cinemaName + "】的" +
                        hallNumber + "号厅状态为：" + (status == 1 ? "正常" : "维修中"));
            } else {
                JOptionPane.showMessageDialog(this, "修改失败，请检查影院和影厅是否存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字作为影厅编号！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageCinemas(new User()));
    }
}