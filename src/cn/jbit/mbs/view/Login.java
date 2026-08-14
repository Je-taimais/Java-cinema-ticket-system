package cn.jbit.mbs.view;

import cn.jbit.mbs.dao.impl.userDAOIMPL;
import cn.jbit.mbs.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

public class Login extends JFrame implements ActionListener {
    private JLabel nameLabel;
    private JTextField nameText;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JButton forgetButton;
    private JButton registerButton;



    public Login() {
        init();
        this.setTitle("鱼眼电影");
        int windowWidth = 1000;
        int windowHeight = 600;
        this.setSize(windowWidth, windowHeight);

        // 屏幕居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - windowWidth) / 2;
        int y = (screenSize.height - windowHeight) / 2;
        this.setLocation(x, y);

        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(235, 228, 240));
    }

    // 自定义背景面板类
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            this.backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public void init() {
        String imagePath = "picture/background.png"; // 背景图路径
        BackgroundPanel backgroundPanel = new BackgroundPanel(imagePath);
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcPanel = new GridBagConstraints();
        gbcPanel.insets = new Insets(20, 10, 20, 10);

        // 用户名标签和输入框
        nameLabel = new JLabel("用户名：");
        nameText = new JTextField(15);
        gbcPanel.gridx = 0;
        gbcPanel.gridy = 0;
        gbcPanel.anchor = GridBagConstraints.EAST;
        backgroundPanel.add(nameLabel, gbcPanel);
        gbcPanel.gridx = 1;
        gbcPanel.anchor = GridBagConstraints.WEST;
        backgroundPanel.add(nameText, gbcPanel);

        // 密码标签和输入框
        passwordLabel = new JLabel("密码：");
        passwordText = new JPasswordField(15);
        gbcPanel.gridx = 0;
        gbcPanel.gridy = 1;
        gbcPanel.anchor = GridBagConstraints.EAST;
        backgroundPanel.add(passwordLabel, gbcPanel);
        gbcPanel.gridx = 1;
        gbcPanel.anchor = GridBagConstraints.WEST;
        backgroundPanel.add(passwordText, gbcPanel);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerButton = new JButton("注册");
        loginButton = new JButton("登录");
        forgetButton = new JButton("忘记密码");

        registerButton.addActionListener(this);
        loginButton.addActionListener(this);
        forgetButton.addActionListener(this);

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(forgetButton);

        gbcPanel.gridx = 0;
        gbcPanel.gridy = 2;
        gbcPanel.gridwidth = 2;
        gbcPanel.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(buttonPanel, gbcPanel);

        // 将整个面板放入窗体中央
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        // 将整个面板放入窗体中央
        this.add(backgroundPanel, BorderLayout.CENTER);

        //设置字体
        nameLabel.setFont(new Font("宋体", Font.BOLD, 50));
        passwordLabel.setFont(new Font("宋体", Font.BOLD, 50));
        nameText.setFont(new Font("宋体", Font.BOLD, 20));
        passwordText.setFont(new Font("宋体", Font.BOLD, 20));
        loginButton.setFont(new Font("宋体", Font.BOLD, 20));
        forgetButton.setFont(new Font("宋体", Font.BOLD, 20));
        registerButton.setFont(new Font("宋体", Font.BOLD, 20));


// 设置文本框大小
        nameText.setPreferredSize(new Dimension(300, 40));
        passwordText.setPreferredSize(new Dimension(300, 40));

        nameText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordText.requestFocus();
            }
        });
        passwordText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.requestFocus();
                loginButton.doClick();
            }
        });

// 设置按钮大小
        loginButton.setPreferredSize(new Dimension(120, 40));
        forgetButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setPreferredSize(new Dimension(120, 40));
    }



    public static void main(String[] args) {
        new Login();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            login();
        }else if(e.getSource() == registerButton) {
            new UserReg();
        }else if(e.getSource() == forgetButton) {
            new UserForget();
        }
    }



    private void login() {
        String name = nameText.getText();
        String password = new String(passwordText.getPassword());
        if (name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User();
        user.setUsername(name);
        user.setPassword(password);

        userDAOIMPL userDAO = new userDAOIMPL();

        // 先查询完整信息（包含 user_type）
        String sql = "SELECT id, phone, email, user_type FROM user WHERE username = ? AND password = ?";
        Object[] params = {user.getUsername(), user.getPassword()};
        List<HashMap> list = userDAO.executeQuery(sql, params);

        if (list.size() > 0) {
            HashMap map = list.get(0);
            user.setId((Long) map.get("id"));
            user.setPhone((String) map.get("phone"));
            user.setEmail((String) map.get("email"));
            user.setUserType((Integer) map.get("user_type"));

            // 打印用户信息用于调试
            System.out.println("登录成功 - 用户信息: " + user.getUsername()
                    + ", 电话: " + user.getPhone()
                    + ", 邮箱: " + user.getEmail()
                    + ", 用户类型: " + user.getUserType());

            //判断是否被拉黑
            if(user.getUserType() == -1){
                JOptionPane.showMessageDialog(this, "您的账号已被拉黑，请联系管理员", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 根据 user_type 判断角色
            if (user.getUserType() == 2) {
                new AdminMain(user); // 管理员界面
            } else {
                new Main(user); // 普通用户界面
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}