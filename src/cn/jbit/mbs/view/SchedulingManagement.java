package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.movieDAOIMPL;
import cn.jbit.mbs.dao.impl.ScreeningDaoIMPL;
import cn.jbit.mbs.entity.Movie;
import cn.jbit.mbs.entity.Screening;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SchedulingManagement extends JFrame {
    private User currentUser;
    private JTable screeningTable;
    private DefaultTableModel tableModel;
    private ScreeningDaoIMPL screeningDao = new ScreeningDaoIMPL();
    private movieDAOIMPL movieDao = new movieDAOIMPL();

    public SchedulingManagement(User user) {
        this.currentUser = user;
        setTitle("排片管理");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("影院排片管理", SwingConstants.CENTER));
        titlePanel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 表格面板
        JScrollPane tableScrollPane = new JScrollPane();
        tableModel = new DefaultTableModel(new Object[]{"排片ID", "电影名称", "影厅", "开始时间", "结束时间", "价格", "状态"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        screeningTable = new JTable(tableModel);
        screeningTable.setRowHeight(30);
        screeningTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = screeningTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                // 选中行高亮显示
                screeningTable.setSelectionBackground(new Color(200, 220, 255));
            }
        });
        tableScrollPane.setViewportView(screeningTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = createActionButton("新增排片", new Color(70, 130, 180));
        JButton editButton = createActionButton("修改排片", new Color(100, 149, 237));
        JButton deleteButton = createActionButton("删除排片", new Color(220, 100, 100));
        JButton refreshButton = createActionButton("刷新数据", new Color(100, 180, 100));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 事件监听
        addButton.addActionListener(e -> openSchedulingDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = screeningTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要修改的排片", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            try {
                Screening screening = screeningDao.findById(id);
                if (screening != null) {
                    openSchedulingDialog(screening);
                }
            } catch (SQLException ex) {
                showError("数据库错误: " + ex.getMessage());
            }
        });
        refreshButton.addActionListener(e -> loadScreeningData());
        deleteButton.addActionListener(e -> deleteSelectedScreening());

        // 初始加载数据
        loadScreeningData();

        add(mainPanel);
        setVisible(true);
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }

    private void loadScreeningData() {
        tableModel.setRowCount(0); // 清空表格
        try {
            List<Screening> screenings = screeningDao.findAllScreenings();
            for (Screening sc : screenings) {
                Movie movie = movieDao.findById(sc.getMovieId());
                Object[] row = {
                        sc.getId(),
                        movie != null ? movie.getTitle() : "未知电影",
                        "影厅 " + sc.getHallId(),
                        formatDateTime(sc.getStartTime()),
                        formatDateTime(sc.getEndTime()),
                        String.format("¥%.2f", sc.getPrice()),
                        getStatusText(sc.getStatus())
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            showError("加载排片数据失败: " + ex.getMessage());
        }
    }

    private String formatDateTime(Timestamp timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0: return "未开始";
            case 1: return "售票中";
            case 2: return "已结束";
            case 3: return "已取消";
            default: return "未知";
        }
    }

    private void openSchedulingDialog(Screening existingScreening) {
        JDialog dialog = new JDialog(this, existingScreening == null ? "新增排片" : "修改排片", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 20));
        dialog.setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 表单字段
        JLabel movieLabel = new JLabel("电影ID:");
        JTextField movieField = new JTextField();
        if (existingScreening != null) {
            movieField.setText(String.valueOf(existingScreening.getMovieId()));
        }

        JLabel hallLabel = new JLabel("影厅ID:");
        JTextField hallField = new JTextField();
        if (existingScreening != null) {
            hallField.setText(String.valueOf(existingScreening.getHallId()));
        }

        JLabel startLabel = new JLabel("开始时间:");
        JTextField startField = new JTextField();
        if (existingScreening != null) {
            startField.setText(formatDateTime(existingScreening.getStartTime()));
        } else {
            startField.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        }

        JLabel endLabel = new JLabel("结束时间:");
        JTextField endField = new JTextField();
        if (existingScreening != null) {
            endField.setText(formatDateTime(existingScreening.getEndTime()));
        }

        JLabel priceLabel = new JLabel("票价:");
        JTextField priceField = new JTextField();
        if (existingScreening != null) {
            priceField.setText(String.format("%.2f", existingScreening.getPrice()));
        } else {
            priceField.setText("35.00");
        }

        JLabel statusLabel = new JLabel("状态:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"未开始", "售票中", "已结束", "已取消"});
        if (existingScreening != null) {
            statusCombo.setSelectedIndex(existingScreening.getStatus());
        }

        // 添加组件到表单
        formPanel.add(movieLabel);
        formPanel.add(movieField);
        formPanel.add(hallLabel);
        formPanel.add(hallField);
        formPanel.add(startLabel);
        formPanel.add(startField);
        formPanel.add(endLabel);
        formPanel.add(endField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveButton = new JButton("保存");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                Screening screening = existingScreening != null ? existingScreening : new Screening();
                screening.setMovieId(Long.parseLong(movieField.getText()));
                screening.setHallId(Long.parseLong(hallField.getText()));

                // 解析日期时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                screening.setStartTime(new Timestamp(sdf.parse(startField.getText()).getTime()));
                screening.setEndTime(new Timestamp(sdf.parse(endField.getText()).getTime()));

                screening.setPrice(BigDecimal.valueOf(Double.parseDouble(priceField.getText())));
                screening.setStatus(statusCombo.getSelectedIndex());

                if (existingScreening == null) {
                    screeningDao.addScreening(screening);
                    JOptionPane.showMessageDialog(dialog, "排片添加成功！");
                } else {
                    screeningDao.updateScreening(screening);
                    JOptionPane.showMessageDialog(dialog, "排片更新成功！");
                }

                dialog.dispose();
                loadScreeningData();
            } catch (Exception ex) {
                showError("保存失败: " + ex.getMessage());
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.setBackground(new Color(150, 150, 150));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedScreening() {
        int selectedRow = screeningTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("请先选择要删除的排片");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除选中的排片吗？", "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (screeningDao.deleteScreening(id)) {
                    JOptionPane.showMessageDialog(this, "删除成功");
                    loadScreeningData();
                } else {
                    showError("删除失败");
                }
            } catch (SQLException ex) {
                showError("数据库错误: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.WARNING_MESSAGE);
    }
}