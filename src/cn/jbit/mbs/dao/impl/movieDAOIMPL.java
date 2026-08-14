package cn.jbit.mbs.dao.impl;

import cn.jbit.mbs.dao.JDBCUtil;
import cn.jbit.mbs.dao.movieDAO;
import cn.jbit.mbs.entity.Movie;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class movieDAOIMPL extends JDBCUtil implements movieDAO {
    @Override
    public Boolean findMovie(Movie movie) {
        Boolean flag = false;
        String sql = "select * from movie where movie_title=?";
        Object[] params = {movie.getMovieTitle()};
        List<HashMap> list = new ArrayList<HashMap>();
        list = this.executeQuery(sql, params);
        if (list.size() > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public int insert(Movie movie) {
        String sql = "INSERT INTO movie(title,original_title, director,runtime , release_date,writers,actors,genres,country,language,rating,votes,box_office,poster_url) VALUES (?, ?,  ?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {
                movie.getTitle(),
                movie.getOriginal_title(),
                movie.getDirector(),
                movie.getRuntime(),
                movie.getReleaseDate(),
                movie.getWriters(),
                movie.getGenres(),
                movie.getActors(),
                movie.getCountry(),
                movie.getLanguage(),
                movie.getRating(),
                movie.getVotes(),
                movie.getBox_office(),
                movie.getPoster_url()
        };
        return this.executeUpdate(sql, params);
    }

    @Override
    public Movie findById(Long movieId) {
        String sql = "SELECT * FROM movie WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Movie movie = null;

        try {
            conn = JDBCUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, movieId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                movie = new Movie();
                movie.setId(rs.getInt("id")); // 注意：id 是 BIGINT，但 Movie 类中是 int
                movie.setTitle(rs.getString("title"));
                movie.setOriginal_title(rs.getString("original_title"));
                movie.setDirector(rs.getString("director"));
                movie.setWriters(rs.getString("writers"));
                movie.setActors(rs.getString("actors"));
                movie.setGenres(rs.getString("genres"));
                movie.setCountry(rs.getString("country"));
                movie.setLanguage(rs.getString("language"));
                movie.setReleaseDate(rs.getString("release_date")); // DATE -> String
                movie.setRuntime(rs.getInt("runtime"));
                movie.setDescription(rs.getString("description"));
                movie.setRating(rs.getDouble("rating")); // DECIMAL -> double
                movie.setVotes(rs.getInt("votes"));
                movie.setBox_office(rs.getDouble("box_office")); // DECIMAL -> double
                movie.setPoster_url(rs.getString("poster_url")); // 假设 poster_url 存在于数据库中
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return movie;
    }

    public boolean deleteMovieByTitle(String title) {
        String sql = "DELETE FROM movie WHERE title = ?";
        Object[] params = { title };
        return executeUpdate(sql, params) > 0;
    }

    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movie(title,original_title, director,runtime, release_date,writers,genres,actors,country,language,description,rating,votes,box_office,poster_url) VALUES(?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getOriginal_title());
            ps.setString(3, movie.getDirector());
            ps.setInt(4, movie.getRuntime());
            ps.setString(5, movie.getReleaseDate());
            ps.setString(6, movie.getWriters());
            ps.setString(7, movie.getGenres());
            ps.setString(8, movie.getActors());
            ps.setString(9, movie.getCountry());
            ps.setString(10, movie.getLanguage());
            ps.setString(11, movie.getDescription());
            ps.setDouble(12, movie.getRating());
            ps.setInt(13, movie.getVotes());
            ps.setDouble(14, movie.getBox_office());
            ps.setString(15, movie.getPoster_url());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
