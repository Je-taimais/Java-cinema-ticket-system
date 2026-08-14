package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.userDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg extends JFrame implements ActionListener {
    private JLabel nameLabel;
    private  JTextField nameText;
    private JLabel passwordLabel;
    private JPasswordField  passwordText;
    private JLabel phoneLabel;
    private JTextField phoneText;
    private JLabel emailLabel;
    private JTextField emailText;
    private JButton registerButton;
    private JButton cancelButton;

    public UserReg(){
        init();
        this.setTitle("用户注册");
        this.setSize(550,500);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(240, 240, 240));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void init() {
        this.setLayout(null);

        nameLabel = new JLabel("用户名：");
        nameLabel.setBounds(100,100,100,30);
        this.add(nameLabel);

        passwordLabel  = new JLabel("密码：");
        passwordLabel.setBounds(100,150,100,30);
        this.add(passwordLabel);

        phoneLabel = new JLabel("手机号：");
        phoneLabel.setBounds(100,200,100,30);
        this.add(phoneLabel);

        emailLabel = new JLabel("邮箱：");
        emailLabel.setBounds(100,250,100,30);
        this.add(emailLabel);

        nameText = new JTextField();
        nameText.setBounds(200,100,200,30);
        this.add(nameText);

        passwordText = new JPasswordField();
        passwordText.setBounds(200,150,200,30);
        this.add(passwordText);

        phoneText = new JTextField();
        phoneText.setBounds(200,200,200,30);
        this.add(phoneText);

        emailText = new JTextField();
        emailText.setBounds(200,250,200,30);
        this.add(emailText);

        registerButton = new JButton("注册");
        cancelButton  = new JButton("取消");

        cancelButton.setBounds(300,300,80,30);
        cancelButton.addActionListener(this);
        this.add(cancelButton);

        registerButton.setBounds(200,300,80,30);
        registerButton.addActionListener(this);
        this.add(registerButton);

        nameText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordText.requestFocus();
            }
        });

        passwordText.addActionListener(new ActionListener() {
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
               registerButton.requestFocus();
               registerButton.doClick();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==registerButton) {
            String username = nameText.getText().trim();
            String password = new String(passwordText.getPassword());
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            System.out.println(phone + email);


            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setPhone(phone);
            user.setEmail(email);

            userDAOIMPL userDAO = new userDAOIMPL();
            int result = userDAO.insert(user);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "注册成功！");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "注册失败！");
            }
        }else if(e.getSource()==cancelButton){
            dispose();
        }
    }

    public static void main(String[] args) {
        new UserReg();
    }
}

