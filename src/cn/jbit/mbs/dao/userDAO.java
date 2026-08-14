package cn.jbit.mbs.dao;

import cn.jbit.mbs.entity.User;

public interface userDAO {
    public Boolean findUser(User user);
    public int insert(User user);

    int delete(User user);

    public int updatePassword(String username, String password, String email, String newPassword);
}
