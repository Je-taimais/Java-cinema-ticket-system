
// OrderDAOImpl.java
package cn.jbit.mbs.dao.impl;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.dao.OrderDAO;
import cn.jbit.mbs.entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOIMPL extends JDBCUtil implements OrderDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/电影院";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    @Override
    public boolean addOrder(Order order) {
        return false;
    }

    public List<Order> getOrdersByUserId(String userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT o.*, " +
                             "s.start_time, s.end_time, s.hall_id, " +
                             "h.name AS hall_name, " +
                             "c.name AS cinema_name, " +
                             "m.title AS movie_title " +
                             "FROM `order` o " +
                             "JOIN screening s ON o.screening_id = s.id " +
                             "JOIN hall h ON s.hall_id = h.id " +
                             "JOIN cinema c ON h.cinema_id = c.id " +
                             "JOIN movie m ON s.movie_id = m.id " +
                             "WHERE o.user_id = ?")) {
            ps.setLong(1, Long.parseLong(userId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    // 填充订单基本信息
                    order.setId(rs.getLong("id"));
                    order.setOrderNo(rs.getString("order_no"));
                    order.setUserId(rs.getLong("user_id"));
                    order.setScreeningId(rs.getLong("screening_id"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentMethod(rs.getInt("payment_method"));
                    order.setPaymentTime(rs.getTimestamp("payment_time"));
                    order.setOrderStatus(rs.getInt("order_status"));
                    order.setCreateTime(rs.getTimestamp("create_time"));
                    order.setUpdateTime(rs.getTimestamp("update_time"));

                    // 填充场次时间
                    order.setStartTime(rs.getTimestamp("start_time"));
                    order.setEndTime(rs.getTimestamp("end_time"));

                    // 填充影厅信息
                    order.setHallId(rs.getLong("hall_id"));
                    order.setMovieTitle(rs.getString("movie_title"));  // 设置电影名称
                    order.setCinemaName(rs.getString("cinema_name"));  // 设置影院名称
                    order.setHallName(rs.getString("hall_name"));

                    List<String> seats = new ArrayList<>();
                    try (PreparedStatement seatPs = conn.prepareStatement(
                            "SELECT seat_row, seat_col FROM order_seat WHERE order_id = ?")) {
                        seatPs.setLong(1, order.getId());
                        try (ResultSet seatRs = seatPs.executeQuery()) {
                            while (seatRs.next()) {
                                int row = seatRs.getInt("seat_row");
                                int col = seatRs.getInt("seat_col");
                                seats.add(row + "排" + col + "座");
                            }
                        }
                    }
                    order.setSeats(seats);

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Order getOrderById(String orderId) {
        return null;
    }

    @Override
    public boolean updateOrder(Order order) {
        return false;
    }

    @Override
    public boolean deleteOrder(String orderId) {
        return false;
    }
}