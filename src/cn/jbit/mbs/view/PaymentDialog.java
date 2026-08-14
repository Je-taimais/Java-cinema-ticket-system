package cn.jbit.mbs.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PaymentDialog extends JDialog {
    private boolean paymentCompleted = false;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;  // 使用选项卡面板
    private int paymentMethod = 2; // 默认微信支付（2）

    public PaymentDialog(JFrame parent, double amount) {
        super(parent, "选择支付方式", true);  // 修改标题
        setSize(500, 600);  // 增加高度以容纳选项卡
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 顶部标题
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("选择支付方式");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 支付金额
        JPanel amountPanel = new JPanel();
        amountPanel.setBackground(Color.WHITE);
        amountPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel amountLabel = new JLabel(String.format("¥ %.2f", amount));
        amountLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        amountLabel.setForeground(new Color(220, 20, 60));
        amountPanel.add(amountLabel);
        add(amountPanel, BorderLayout.NORTH);  // 放在顶部区域

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // 添加微信支付选项卡
        addWeChatPaymentTab();
        // 添加支付宝支付选项卡
        addAlipaymentTab();

        add(tabbedPane, BorderLayout.CENTER);

        // 支付状态
        statusLabel = new JLabel("请选择支付方式并扫码支付", JLabel.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);

        // 取消支付按钮
        JButton cancelButton = new JButton("取消支付");
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());

        // 支付成功按钮
        JButton successButton = new JButton("支付成功");
        successButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        successButton.setBackground(new Color(50, 205, 50)); // 绿色背景
        successButton.setForeground(Color.WHITE);
        successButton.addActionListener(e -> {
            // 根据当前选中的tab判断支付方式
            int selectedTab = tabbedPane.getSelectedIndex();
            paymentMethod = selectedTab == 0 ? 2 : 1; // 0是微信tab，1是支付宝tab

            paymentCompleted = true;
            statusLabel.setText("支付成功！正在处理订单...");

            Timer closeTimer = new Timer(100, evt -> dispose());
            closeTimer.setRepeats(false);
            closeTimer.start();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(successButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    private void addWeChatPaymentTab() {
        JPanel weChatPanel = new JPanel(new BorderLayout());
        weChatPanel.setBackground(Color.WHITE);
        weChatPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // 微信支付标题
        JLabel titleLabel = new JLabel("微信支付", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 150, 0)); // 微信绿色
        weChatPanel.add(titleLabel, BorderLayout.NORTH);

        // 微信二维码
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        try {
            // 加载微信收款码图片
            ImageIcon originalIcon = new ImageIcon("picture/微信支付.png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(300, 310, Image.SCALE_SMOOTH);
            JLabel qrLabel = new JLabel(new ImageIcon(scaledImage));
            qrPanel.add(qrLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("无法加载微信支付二维码");
            errorLabel.setForeground(Color.RED);
            qrPanel.add(errorLabel);
        }
        weChatPanel.add(qrPanel, BorderLayout.CENTER);

        // 添加说明
        JLabel instructionLabel = new JLabel("<html><div style='text-align:center;'>打开手机微信，扫一扫上方二维码完成支付</div></html>", JLabel.CENTER);
        instructionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        instructionLabel.setForeground(Color.GRAY);
        weChatPanel.add(instructionLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("微信支付", weChatPanel);
    }

    private void addAlipaymentTab() {
        JPanel aliPanel = new JPanel(new BorderLayout());
        aliPanel.setBackground(Color.WHITE);
        aliPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // 支付宝支付标题
        JLabel titleLabel = new JLabel("支付宝支付", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 120, 215)); // 支付宝蓝色
        aliPanel.add(titleLabel, BorderLayout.NORTH);

        // 支付宝二维码
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        try {
            // 加载支付宝收款码图片
            ImageIcon originalIcon = new ImageIcon("picture/支付宝支付.jpg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(250, 310, Image.SCALE_SMOOTH);
            JLabel qrLabel = new JLabel(new ImageIcon(scaledImage));
            qrPanel.add(qrLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("无法加载支付宝支付二维码");
            errorLabel.setForeground(Color.RED);
            qrPanel.add(errorLabel);
        }
        aliPanel.add(qrPanel, BorderLayout.CENTER);

        // 添加说明
        JLabel instructionLabel = new JLabel("<html><div style='text-align:center;'>打开手机支付宝，扫一扫上方二维码完成支付</div></html>", JLabel.CENTER);
        instructionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        instructionLabel.setForeground(Color.GRAY);
        aliPanel.add(instructionLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("支付宝支付", aliPanel);
    }

    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }

    public void startPaymentMonitoring() {
        // 空实现，已移除定时器逻辑
    }
}