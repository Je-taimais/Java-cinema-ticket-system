package cn.jbit.mbs.entity;

public enum MovieStatus {
    NOT_RELEASED(0, "未上映"),
    RELEASING(1, "上映中"),
    RELEASED(2, "已下映");

    private final int code;
    private final String description;

    MovieStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 新增方法：根据描述获取枚举常量
    public static MovieStatus fromDescription(String description) {
        for (MovieStatus status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with description: " + description);
    }
}

