package cn.jbit.mbs.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Screening {
    private Long id;
    private Long movie_id;
    private Long hallId;
    private Timestamp startTime;
    private Timestamp endTime;
    private BigDecimal price;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private Integer status; // 0-未开始，1-售票中，2-已结束，3-已取消

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movie_id;
    }

    public void setMovieId(Long movie_id) {
        this.movie_id = movie_id;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}