package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.userDAOIMPL;
import cn.jbit.mbs.dao.userDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserForget extends JFrame implements ActionListener {
    private JLabel  nameLabel;
    private JTextField nameText;
    private JLabel  phoneLabel;
    private JTextField phoneText;
    private JLabel  emailLabel;
    private JTextField emailText;
    private JLabel  newpasswordLabel;
    private JTextField newpasswordText;
    private JLabel  confirmpasswordLabel;
    private JTextField confirmpasswordText;
    private JButton  submitButton;
    private JButton  cancelButton;

    public UserForget() {
        init();
        this.setTitle("找回密码");
        this.setSize(550,500);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(240, 240, 240));
    }

    private void init() {
        this.setLayout(null);

        nameLabel = new JLabel("用户名：");
        nameLabel.setBounds(100,100,100,30);
        this.add(nameLabel);
        nameText = new JTextField();
        nameText.setBounds(200,100,200,30);
        this.add(nameText);
        phoneLabel = new JLabel("手机号：");
        phoneLabel.setBounds(100,150,100,30);
        this.add(phoneLabel);
        phoneText = new JTextField();
        phoneText.setBounds(200,150,200,30);
        this.add(phoneText);
        emailLabel = new JLabel("邮箱：");
        emailLabel.setBounds(100,200,100,30);
        this.add(emailLabel);
        emailText = new JTextField();
        emailText.setBounds(200,200,200,30);
        this.add(emailText);
        newpasswordLabel = new JLabel("新密码：");
        newpasswordLabel.setBounds(100,250,100,30);
        this.add(newpasswordLabel);
        newpasswordText = new JTextField();
        newpasswordText.setBounds(200,250,200,30);
        this.add(newpasswordText);
        confirmpasswordLabel = new JLabel("确认密码：");
        confirmpasswordLabel.setBounds(100,300,100,30);
        this.add(confirmpasswordLabel);
        confirmpasswordText = new JTextField();
        confirmpasswordText.setBounds(200,300,200,30);
        this.add(confirmpasswordText);
        submitButton = new JButton("提交");
        submitButton.setBounds(200,350,80,30);
        submitButton.addActionListener(this);
        this.add(submitButton);
        cancelButton = new JButton("取消");
        cancelButton.setBounds(300,350,80,30);
        cancelButton.addActionListener(this);
        this.add(cancelButton);

        nameText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                phoneText.requestFocus();
            }
        });

        phoneText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailText.requestFocus();
            }
        });

        emailText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newpasswordText.requestFocus();
            }
        });

        newpasswordText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmpasswordText.requestFocus();
            }
        });

        confirmpasswordText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButton.requestFocus();
                submitButton.doClick();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==submitButton){
            String username = nameText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();
            String newPassword = newpasswordText.getText();
            String confirmPassword = confirmpasswordText.getText();
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致，请重新输入！");
                return;
            }
            userDAO dao = new userDAOIMPL();
            int result = dao.updatePassword(username, phone, email, newPassword);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "密码更新成功！");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "密码更新失败，请检查输入的信息！");
            }
        }else if (e.getSource()==cancelButton){
            this.dispose();
        }
    }

    public static void main(String[] args) {
        new UserForget();
    }
}
