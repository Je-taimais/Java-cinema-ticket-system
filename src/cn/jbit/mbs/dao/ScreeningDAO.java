package cn.jbit.mbs.dao;

import cn.jbit.mbs.entity.Screening;

import java.sql.SQLException;
import java.util.List;

public interface ScreeningDAO {
    List<Screening> findAllScreenings() throws SQLException;

    Screening findById(Long id) throws SQLException;

    boolean addScreening(Screening screening) throws SQLException;

    boolean updateScreening(Screening screening) throws SQLException;

    boolean deleteScreening(Long id) throws SQLException;
}
