package com.kh.Backend_miniproject.vo;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class PostInfoVO {
    private int postNum;
    private String title;
    private String nickname;
    private String pfImg;
    private int viewCount;
    private int commentCount;
    private Date writeDate;
}
