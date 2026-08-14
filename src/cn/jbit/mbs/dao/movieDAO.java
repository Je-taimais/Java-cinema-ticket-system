package cn.jbit.mbs.dao;

import cn.jbit.mbs.entity.Movie;

public interface movieDAO {
    public Boolean findMovie(Movie movie);
    public boolean addMovie(Movie movie);
    public int insert(Movie movie);
    public Movie findById(Long movieId);
}
