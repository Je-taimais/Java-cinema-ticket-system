package cn.jbit.mbs.dao.impl;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.dao.userDAO;
import cn.jbit.mbs.entity.User;

import java.util.HashMap;
import java.util.List;

public class userDAOIMPL extends JDBCUtil implements userDAO {
    @Override
    public Boolean findUser(User user) {
        String sql = "SELECT id, phone, email, user_type FROM user WHERE username = ? AND password = ?";
        List<HashMap> list = executeQuery(sql, new Object[]{user.getUsername(), user.getPassword()});
        if (list.size() > 0) {
            HashMap map = list.get(0);
            user.setId((Long) map.get("id"));
            user.setPhone((String) map.get("phone"));
            user.setEmail((String) map.get("email"));
            user.setUserType((Integer) map.get("user_type"));
            return true;
        }
        return false;
    }


    @Override
    public int insert(User user) {
        String sql = "insert into user (username, password,phone,email) values(?, ?,?,?)";
        Object[] params = {user.getUsername(), user.getPassword(), user.getPhone(), user.getEmail()};
        return this.executeUpdate(sql, params);
    }


    @Override
    public int delete(User user) {
        return 0;
    }
    // 根据用户名获取用户信息
    public HashMap<String, Object> getUserByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        List<HashMap> list = executeQuery(sql, new Object[]{username});
        return list.isEmpty() ? null : list.get(0);
    }

    // 拉黑用户
    public boolean banUser(String username) {
        String sql = "UPDATE user SET user_type = -1 WHERE username = ?";
        int rowsAffected = executeUpdate(sql, new Object[]{username});
        return rowsAffected > 0;
    }
    //取消拉黑
    public boolean unbanUser(String username) {
        String sql = "UPDATE user SET user_type = 0 WHERE username = ?";
        int rowsAffected = executeUpdate(sql, new Object[]{username});
        return rowsAffected > 0;
    }


    @Override
    public int updatePassword(String username, String phone, String email, String newPassword) {
        String sql = "update user set password = ? where username = ? and phone = ? and email = ?";
        Object[] params = {newPassword, username, phone, email};
        return this.executeUpdate(sql, params);
    }
}