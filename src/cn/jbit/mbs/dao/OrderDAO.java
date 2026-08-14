package cn.jbit.mbs.dao;

import cn.jbit.mbs.entity.Order;
import java.util.List;

public interface OrderDAO {
    boolean addOrder(Order order);
    List<Order> getOrdersByUserId(String userId);
    Order getOrderById(String orderId);
    boolean updateOrder(Order order);
    boolean deleteOrder(String orderId);
}