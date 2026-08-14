
package cn.jbit.mbs.dao;

import java.util.HashMap;
import java.util.List;

public interface cinemaDAO {
    // 新增某影院的某号厅
    boolean addCinemaHall(String cinemaName, int hallNumber);

    // 删除某影院的某号厅
    boolean deleteCinemaHall(String cinemaName, int hallNumber);

    // 修改影院地址
    boolean updateCinemaAddress(String cinemaName, String newAddress);

    // 查询所有影院及其影厅信息
    List<HashMap> getAllCinemasWithHalls();

    boolean addCinema(String cinemaName, String address, int status);

    boolean deleteCinema(String cinemaName);

    boolean updateHallStatus(String cinemaName, int hallNumber, int status);

    boolean updateCinemaStatus(String cinemaName, int status);
}