package cn.jbit.mbs.view;

import cn.jbit.mbs.entity.User;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JMenu homeMenu;
    private JMenu typeMenu;
    private JMenu mineMenu;
    private JMenuItem homeItem;
    private JMenuItem myOrders;
    private JMenuItem myInformation;
    private JMenuItem outLogin;

    private User currentUser;

    public Main(User user) {
        this.currentUser = user;
        init();
        setTitle("鱼眼电影");
        setSize(1200, 800);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(235, 228, 240));
        getContentPane().setLayout(new BorderLayout()); // 添加布局管理器
        showHomePage(); // 初始显示首页
    }

    private void init() {
        menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

        homeMenu = new JMenu("首页");
        typeMenu = new JMenu("类型");
        mineMenu = new JMenu("我的");

        menuBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 检查鼠标是否在菜单栏上但不在任何菜单项上
                if (!isMouseOverMenu()) {
                    resetMenuSelection();
                }
            }
        });

        homeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHomePage();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                homeMenu.setSelected(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 延迟检查，避免立即取消选中
                Timer timer = new Timer(200, evt -> {
                    if (!isMouseOverMenu()) {
                        resetMenuSelection();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        typeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTypePage();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                typeMenu.setSelected(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Timer timer = new Timer(200, evt -> {
                    if (!isMouseOverMenu()) {
                        resetMenuSelection();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        mineMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mineMenu.setSelected(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Timer timer = new Timer(200, evt -> {
                    if (!isMouseOverMenu()) {
                        resetMenuSelection();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        myOrders = new JMenuItem("我的订单");
        myOrders.addActionListener(this);

        myInformation = new JMenuItem("基本信息");
        myInformation.addActionListener(this);

        outLogin = new JMenuItem("退出登录");
        outLogin.addActionListener(this);

        Font menuFont = new Font("微软雅黑", Font.PLAIN, 16);

        homeMenu.setFont(menuFont);
        typeMenu.setFont(menuFont);
        mineMenu.setFont(menuFont);
        myOrders.setFont(menuFont);
        myInformation.setFont(menuFont);
        outLogin.setFont(menuFont);

        mineMenu.add(myOrders);
        mineMenu.add(myInformation);
        mineMenu.add(outLogin);

        menuBar.add(homeMenu);
        menuBar.add(typeMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(mineMenu);

        mineMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // 只有当子菜单未显示时才取消选中
                if (!mineMenu.isPopupMenuVisible()) {
                    mineMenu.setSelected(false);
                }
            }
        });

        this.setJMenuBar(menuBar);
    }

    private boolean isMouseOverMenu() {
        Point mousePos = menuBar.getMousePosition();
        if (mousePos == null) return false;

        return homeMenu.getBounds().contains(mousePos) ||
                typeMenu.getBounds().contains(mousePos) ||
                mineMenu.getBounds().contains(mousePos);
    }

    // 重置所有菜单项的选中状态
    private void resetMenuSelection() {
        homeMenu.setSelected(false);
        typeMenu.setSelected(false);
        mineMenu.setSelected(false);
    }

    private void showHomePage() {
        getContentPane().removeAll();
        getContentPane().add(new Home(currentUser), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showTypePage() {
        getContentPane().removeAll();
        getContentPane().add(new MyType(currentUser), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeItem) {
            showHomePage();
        } else if (e.getSource() == typeMenu) {
            showTypePage();
        } else if (e.getSource() == myOrders) {
            new MyOrder(currentUser).setVisible(true);
        } else if (e.getSource() == myInformation) {
            new MyInformation(currentUser);
        } else if (e.getSource() == outLogin) {
            this.dispose();
            new Login();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main(null);
        });
    }
}