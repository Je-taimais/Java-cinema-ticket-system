package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MyInformation extends JFrame implements ActionListener {
    private JLabel nameLabel;
    private JTextField nameText;
    private JLabel phoneLabel;
    private JTextField phoneText;
    private JLabel emailLabel;
    private JTextField emailText;
    private JLabel passwordLabel;
    private JTextField passwordText;
    private JButton returnButton;
    private JButton editButton;
    private JButton saveButton;

    private User user;//声明用户对象


    public MyInformation(User user) {
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户信息为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.user = user;//初始化用户对象
        initComponents();//初始化组件
        displayUserInfo();//显示用户信息
        this.setTitle("我的信息");
        this.setSize(600, 500);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(240, 240, 240));
    }


    private void initComponents() {
        // 设置布局和初始化组件
        setLayout(null);

        // 初始化标签和文本框
        nameLabel = new JLabel("用户名：");
        nameText = new JTextField(20);

        passwordLabel = new JLabel("密码：");
        passwordText = new JPasswordField(20);

        phoneLabel = new JLabel("手机号：");
        phoneText = new JTextField(20);

        emailLabel = new JLabel("邮箱：");
        emailText = new JTextField(20);

        returnButton = new JButton("返回");
        editButton = new JButton("修改");
        saveButton = new JButton("保存");

        // 设置组件位置
        nameLabel.setBounds(100, 100, 100, 30);
        nameText.setBounds(220, 100, 200, 30);

        passwordLabel.setBounds(100, 150, 100, 30);
        passwordText.setBounds(220, 150, 200, 30);

        phoneLabel.setBounds(100, 200, 100, 30);
        phoneText.setBounds(220, 200, 200, 30);

        emailLabel.setBounds(100, 250, 100, 30);
        emailText.setBounds(220, 250, 200, 30);

        returnButton.setBounds(220, 300, 80, 30);
        returnButton.addActionListener(this);

        editButton.setBounds(330, 300, 80, 30);
        editButton.addActionListener(this);

        saveButton.setBounds(330, 350, 80, 30);
        saveButton.addActionListener(this);

        // 添加组件到窗口
        add(nameLabel);
        add(nameText);
        add(passwordLabel);
        add(passwordText);
        add(phoneLabel);
        add(phoneText);
        add(emailLabel);
        add(emailText);
        add(returnButton);
        add(editButton);
        add(saveButton);

        // 设置不可编辑
        nameText.setEditable(false);
        passwordText.setEditable(false);
        phoneText.setEditable(false);
        emailText.setEditable(false);
    }

    private void displayUserInfo() {
        // 显示用户信息，增加空值检查
        nameText.setText(user.getUsername());
        passwordText.setText(user.getPassword());
        phoneText.setText(user.getPhone() != null ? (String) user.getPhone() : "未设置");
        emailText.setText(user.getEmail() != null ? (String) user.getEmail() : "未设置");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            dispose();
        }else if (e.getSource() == editButton) {
            nameText .setEditable(true);
            passwordText.setEditable(true);
            phoneText.setEditable(true);
            emailText.setEditable(true);
        }else if (e.getSource() == saveButton) {
            String newName = nameText.getText();
            String newPassword = passwordText.getText();
            String newPhone = phoneText.getText();
            String newEmail = emailText.getText();

            user.setPhone(newPhone);
            user.setPassword(newPassword);
            user.setEmail(newEmail);
            user.setUsername(newName);

            //调用DAO更新数据库
            boolean success = updateUserInDatabase(user);
            if (success){
                JOptionPane.showMessageDialog(this, "更新成功！");
                nameText.setEditable(false);
                passwordText.setEditable(false);
                phoneText.setEditable(false);
                emailText.setEditable(false);
            }else {
                JOptionPane.showMessageDialog(this, "更新失败！");
            }
        }
    }

    public boolean updateUserInDatabase(User user) {
        String sql = "UPDATE user SET phone = ?, email = ? WHERE username = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 设置参数
            ps.setObject(1, user.getPhone());   // 手机号
            ps.setObject(2, user.getEmail());   // 邮箱
            ps.setObject(3, user.getUsername()); // 用户名作为条件

            // 执行更新
            int rowsAffected = ps.executeUpdate();

            // 返回是否更新成功
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库更新出错: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        new MyInformation(null);
    }
}