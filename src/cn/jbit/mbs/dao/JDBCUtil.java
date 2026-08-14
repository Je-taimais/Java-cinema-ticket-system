package cn.jbit.mbs.dao;

import java.sql.*;
import java.util.*;

public class JDBCUtil {
    private static String url = "jdbc:mysql://localhost:3306/电影院";
    private static String name = "root";
    private static String password = "root";
    private static Connection conn = null;
    private static PreparedStatement pst = null;
    private static ResultSet rs = null;
    private static JDBCUtil jdbcUtil = null;

    public JDBCUtil() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized JDBCUtil getInstance() {
        if (jdbcUtil == null) {
            jdbcUtil = new JDBCUtil();
        }
        return jdbcUtil;
    }

    public static Connection getConnection() {
        try {
            conn = DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private ResultSet executeQueryRS(String sql, Object[] params) {
        try {
            pst = getConnection().prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }
            rs = pst.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }


        public List<HashMap> executeQuery(String sql, Object[] params) {
        List<HashMap> lists = new ArrayList<>();
        ResultSet rs = executeQueryRS(sql, params);
        ResultSetMetaData rsmd = null;
        int columnCount = 0;
        try {
            rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            while (rs.next()){
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < columnCount; i++){
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                lists.add(map);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            closeAll();
        }
        return lists;
    }

    public int executeUpdate(String sql, Object[] params) {
        int count = 0;
        try {
            pst = getConnection().prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }
            count = pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll();
        }
        return count;
    }

    public void closeAll(){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(pst != null){
            try {
                pst.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
