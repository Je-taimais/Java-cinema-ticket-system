
package cn.jbit.mbs.dao.impl;

import cn.jbit.mbs.dao.cinemaDAO;
import cn.jbit.mbs.dao.JDBCUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class cinemaDAOIMPL implements cinemaDAO {

    private final JDBCUtil jdbcUtil = JDBCUtil.getInstance();

    @Override
    public boolean addCinemaHall(String cinemaName, int hallNumber) {
        try {
            String checkSql = "SELECT id FROM cinema WHERE name = ?";
            List<HashMap> result = jdbcUtil.executeQuery(checkSql, new Object[]{cinemaName});
            int cinemaId;

            if (result.isEmpty()) {
                String address = JOptionPane.showInputDialog("请输入影院地址：");
                if (address == null || address.trim().isEmpty()) {
                    address = "未知地址";
                }
                String insertCinemaSql = "INSERT INTO cinema (name, address) VALUES (?, ?)";
                jdbcUtil.executeUpdate(insertCinemaSql, new Object[]{cinemaName, address});

                result = jdbcUtil.executeQuery(checkSql, new Object[]{cinemaName});
                if (result.isEmpty()) return false;
            }

            Object idObj = result.get(0).get("id");
            if (!(idObj instanceof Number)) {
                throw new RuntimeException("Invalid ID value from database: " + idObj);
            }
            cinemaId = ((Number) idObj).intValue();

            // 修改：添加影厅时设置默认状态为1（正常）
            String insertHallSql = "INSERT INTO hall (cinema_id, name, status) VALUES (?, ?, 1)";
            int rows = jdbcUtil.executeUpdate(insertHallSql, new Object[]{cinemaId, hallNumber});
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCinemaHall(String cinemaName, int hallNumber) {
        try {
            String getCinemaSql = "SELECT id FROM cinema WHERE name = ?";
            List<HashMap> result = jdbcUtil.executeQuery(getCinemaSql, new Object[]{cinemaName});
            if (result.isEmpty()) return false;

            int cinemaId = ((Number) result.get(0).get("id")).intValue();

            String deleteSql = "DELETE FROM hall WHERE cinema_id = ? AND name = ?";
            int rows = jdbcUtil.executeUpdate(deleteSql, new Object[]{cinemaId, hallNumber});
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateHallStatus(String cinemaName, int hallNumber, int status) {
        try {
            String getCinemaSql = "SELECT id FROM cinema WHERE name = ?";
            List<HashMap> result = jdbcUtil.executeQuery(getCinemaSql, new Object[]{cinemaName});
            if (result.isEmpty()) return false;

            int cinemaId = ((Number) result.get(0).get("id")).intValue();

            String getHallSql = "SELECT id FROM hall WHERE cinema_id = ? AND name = ?";
            result = jdbcUtil.executeQuery(getHallSql, new Object[]{cinemaId, hallNumber});
            if (result.isEmpty()) return false;

            int hallId = ((Number) result.get(0).get("id")).intValue();

            String updateSql = "UPDATE hall SET status = ? WHERE id = ?";
            int rows = jdbcUtil.executeUpdate(updateSql, new Object[]{status, hallId});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCinemaAddress(String cinemaName, String newAddress) {
        try {
            String sql = "UPDATE cinema SET address = ? WHERE name = ?";
            int rowsAffected = jdbcUtil.executeUpdate(sql, new Object[]{newAddress, cinemaName});
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<HashMap> getAllCinemasWithHalls() {
        try {
            // 修改：添加影院状态字段
            String sql = "SELECT c.name AS cinema_name, c.address, c.status AS cinema_status, " +
                    "h.name AS hall_name, h.status AS hall_status " +
                    "FROM cinema c LEFT JOIN hall h ON c.id = h.cinema_id " +
                    "ORDER BY c.name, h.name";
            return jdbcUtil.executeQuery(sql, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addCinema(String cinemaName, String address, int status) {
        String sql = "INSERT INTO cinema (name, address, status) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cinemaName);
            pstmt.setString(2, address);
            pstmt.setInt(3, status);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            // 处理唯一约束冲突（影院名重复）
            if (e.getErrorCode() == 1062 || e.getSQLState().equals("23000")) {
                System.out.println("影院名称已存在: " + cinemaName);
            }
            return false;
        }
    }

    @Override
    public boolean updateCinemaStatus(String cinemaName, int status) {
        try {
            String sql = "UPDATE cinema SET status = ? WHERE name = ?";
            int rows = jdbcUtil.executeUpdate(sql, new Object[]{status, cinemaName});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCinema(String cinemaName) {
        // 新增：先删除关联的排片数据
        String deleteScreeningsSql = "DELETE FROM screening WHERE hall_id IN " +
                "(SELECT id FROM hall WHERE cinema_id = " +
                "(SELECT id FROM cinema WHERE name = ?))";

        String deleteHallsSql = "DELETE FROM hall WHERE cinema_id = (SELECT id FROM cinema WHERE name = ?)";
        String deleteCinemaSql = "DELETE FROM cinema WHERE name = ?";


        try (Connection conn = JDBCUtil.getConnection()) {
            conn.setAutoCommit(false); // 开启事务

            try (PreparedStatement screeningStmt = conn.prepareStatement(deleteScreeningsSql);
                 PreparedStatement hallStmt = conn.prepareStatement(deleteHallsSql);
                 PreparedStatement cinemaStmt = conn.prepareStatement(deleteCinemaSql)) {

                // 新增：先删除关联的排片记录
                screeningStmt.setString(1, cinemaName);
                screeningStmt.executeUpdate();

                // 删除影厅
                hallStmt.setString(1, cinemaName);
                hallStmt.executeUpdate();

                // 删除影院
                cinemaStmt.setString(1, cinemaName);
                int cinemaRows = cinemaStmt.executeUpdate();

                conn.commit(); // 提交事务
                return cinemaRows > 0;

            } catch (SQLException e) {
                conn.rollback(); // 回滚事务
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}