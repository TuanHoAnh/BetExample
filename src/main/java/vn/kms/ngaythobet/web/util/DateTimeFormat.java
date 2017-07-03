package vn.kms.ngaythobet.web.util;

public enum DateTimeFormat {
    DATE("yyyy-MM-dd"),
    TIME("HH:mm"),
    DATETIME("yyyy-MM-dd HH:mm");

    private String format;
    DateTimeFormat(String format){
        this.format = format;
    }
    public String getFormat(){
        return this.format;
    }
}
