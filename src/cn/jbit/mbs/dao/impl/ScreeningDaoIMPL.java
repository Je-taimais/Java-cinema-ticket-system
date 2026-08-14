// ScreeningDaoImpl.java
package cn.jbit.mbs.dao.impl;

import cn.jbit.mbs.dao.ScreeningDAO;
import cn.jbit.mbs.entity.Screening;
import cn.jbit.mbs.dao.JDBCUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreeningDaoIMPL implements ScreeningDAO {

    @Override
    public List<Screening> findAllScreenings() throws SQLException {
        List<Screening> screenings = new ArrayList<>();
        String sql = "SELECT * FROM screening";

        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                screenings.add(extractScreening(rs));
            }
        }
        return screenings;
    }

    @Override
    public Screening findById(Long id) throws SQLException {
        String sql = "SELECT * FROM screening WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractScreening(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean addScreening(Screening screening) throws SQLException {
        String sql = "INSERT INTO screening (movie_id, hall_id, start_time, end_time, price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, screening.getMovieId());
            stmt.setLong(2, screening.getHallId());
            stmt.setTimestamp(3, screening.getStartTime());
            stmt.setTimestamp(4, screening.getEndTime());
            stmt.setDouble(5, screening.getPrice() != null ? screening.getPrice().doubleValue() : 0.0);

            stmt.setInt(6, screening.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        screening.setId(rs.getLong(1));
                    }
                }
            }
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateScreening(Screening screening) throws SQLException {
        String sql = "UPDATE screening SET movie_id=?, hall_id=?, start_time=?, end_time=?, price=?, status=? " +
                "WHERE id=?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, screening.getMovieId());
            stmt.setLong(2, screening.getHallId());
            stmt.setTimestamp(3, screening.getStartTime());
            stmt.setTimestamp(4, screening.getEndTime());
            stmt.setDouble(5, screening.getPrice() != null ? screening.getPrice().doubleValue() : 0.0);

            stmt.setInt(6, screening.getStatus());
            stmt.setLong(7, screening.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteScreening(Long id) throws SQLException {
        String sql = "DELETE FROM screening WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Screening extractScreening(ResultSet rs) throws SQLException {
        Screening screening = new Screening();
        screening.setId(rs.getLong("id"));
        screening.setMovieId(rs.getLong("movie_id"));
        screening.setHallId(rs.getLong("hall_id"));
        screening.setStartTime(rs.getTimestamp("start_time"));
        screening.setEndTime(rs.getTimestamp("end_time"));
        screening.setPrice(BigDecimal.valueOf(rs.getDouble("price")));
        screening.setStatus(rs.getInt("status"));
        return screening;
    }
}