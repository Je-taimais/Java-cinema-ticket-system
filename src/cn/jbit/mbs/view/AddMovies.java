package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.movieDAOIMPL;
import cn.jbit.mbs.entity.Movie;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AddMovies extends JFrame {

    private static final Color MAIN_COLOR = new Color(70, 130, 180); // 主色调
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245); // 次要色调
    private static final Font LABEL_FONT = new Font("微软雅黑", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 24);

    private User currentUser;
    private List<JTextField> fieldList = new ArrayList<>();

    // 输入组件
    private JTextField titleField, original_titleField, directorField, runtimeField,
            releaseDateField, writersField, genresField, actorsField,
            countryField, languageField, descriptionField, ratingField,
            votesField, box_officeField, poster_urlField;

    public AddMovies(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("新增电影 - 电影管理系统");
        setSize(1000, 800);
        centerWindow();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(SECONDARY_COLOR);

        // 标题面板
        JLabel titleLabel = new JLabel("新增电影信息", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(MAIN_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 表单面板（使用滚动面板）
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        formPanel.setBackground(SECONDARY_COLOR);

        // 必填字段组
        addFormField(formPanel, "电影名称*:", titleField = new JTextField());
        addFormField(formPanel, "原  名*:", original_titleField = new JTextField());
        addFormField(formPanel, "导  演*:", directorField = new JTextField());
        addFormField(formPanel, "时  长(分钟)*:", runtimeField = new JTextField());
        addFormField(formPanel, "上映日期*:", releaseDateField = new JTextField());

        // 其他字段
        addFormField(formPanel, "编  剧:", writersField = new JTextField());
        addFormField(formPanel, "类  型:", genresField = new JTextField());
        addFormField(formPanel, "主  演:", actorsField = new JTextField());
        addFormField(formPanel, "国  家:", countryField = new JTextField());
        addFormField(formPanel, "语  言:", languageField = new JTextField());

        // 大文本字段
        descriptionField = new JTextField();
        addFormField(formPanel, "剧情简介:", descriptionField);

        // 数字字段
        addFormField(formPanel, "评  分:", ratingField = new JTextField());
        addFormField(formPanel, "评分人数:", votesField = new JTextField());
        addFormField(formPanel, "票房(万元):", box_officeField = new JTextField());
        addFormField(formPanel, "海报链接:", poster_urlField = new JTextField());

        setupEnterKeyNavigation();
        return formPanel;
    }

    private void addFormField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(Color.DARK_GRAY);

        textField.setFont(FIELD_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        panel.add(label);
        panel.add(textField);
        fieldList.add(textField);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        buttonPanel.setBackground(SECONDARY_COLOR);

        JButton submitButton = createButton("提交", MAIN_COLOR);
        JButton cancelButton = createButton("取消", new Color(120, 120, 120));

        submitButton.addActionListener(this::handleSubmit);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleSubmit(ActionEvent e) {
        // 收集数据
        String title = titleField.getText().trim();
        String original_title = original_titleField.getText().trim();
        String director = directorField.getText().trim();
        String runtimeStr = runtimeField.getText().trim();
        String releaseDate = releaseDateField.getText().trim();

        // 验证必填字段
        if (title.isEmpty() || original_title.isEmpty() || director.isEmpty() ||
                runtimeStr.isEmpty() || releaseDate.isEmpty()) {
            showError("请填写所有带*号的必填字段！");
            return;
        }

        // 验证数字字段
        try {
            int runtime = Integer.parseInt(runtimeStr);
            if (runtime <= 0) {
                showError("时长必须为正整数！");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("请输入正确的时长数字！");
            return;
        }

        // 验证日期格式
        if (!releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showError("请输入正确格式的上映日期（YYYY-MM-DD）");
            return;
        }

        // 构建Movie对象
        Movie movie = buildMovieObject();

        // 数据库操作
        movieDAOIMPL dao = new movieDAOIMPL();
        if (dao.addMovie(movie)) {
            JOptionPane.showMessageDialog(this,
                    "电影《" + title + "》添加成功！",
                    "操作成功",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            dispose();
        } else {
            showError("电影添加失败，请检查输入或重试！");
        }
    }

    private Movie buildMovieObject() {
        Movie movie = new Movie();
        movie.setTitle(titleField.getText().trim());
        movie.setOriginal_title(original_titleField.getText().trim());
        movie.setDirector(directorField.getText().trim());
        movie.setRuntime(Integer.parseInt(runtimeField.getText().trim()));
        movie.setReleaseDate(releaseDateField.getText().trim());
        movie.setWriters(writersField.getText().trim());
        movie.setGenres(genresField.getText().trim());
        movie.setActors(actorsField.getText().trim());
        movie.setCountry(countryField.getText().trim());
        movie.setLanguage(languageField.getText().trim());
        movie.setDescription(descriptionField.getText().trim());

        // 处理可选数字字段
        movie.setRating(parseDouble(ratingField.getText().trim(), 0.0));
        movie.setVotes(parseInt(votesField.getText().trim(), 0));
        movie.setBox_office(parseDouble(box_officeField.getText().trim(), 0.0));

        movie.setPoster_url(poster_urlField.getText().trim());
        return movie;
    }

    private int parseInt(String text, int defaultValue) {
        try {
            return text.isEmpty() ? defaultValue : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDouble(String text, double defaultValue) {
        try {
            return text.isEmpty() ? defaultValue : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        fieldList.forEach(field -> field.setText(""));
    }

    private void setupEnterKeyNavigation() {
        for (int i = 0; i < fieldList.size(); i++) {
            final int index = i;
            fieldList.get(i).addActionListener(e -> {
                if (index < fieldList.size() - 1) {
                    fieldList.get(index + 1).requestFocusInWindow();
                }
            });
        }
    }

    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2,
                (screenSize.height - getHeight()) / 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setUsername("testAdmin");
            testUser.setUserType(2);
            new AddMovies(testUser);
        });
    }
}