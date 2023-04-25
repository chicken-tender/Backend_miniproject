package com.kh.Backend_miniproject.vo;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PostVO {
    private int postNum;
    private int boardNum;
    private int memberNum;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private Date writeDate;
    private String imgUrl;
    private String tag;
    private String nickname;
    private String boardName;
    private String pfImg;

    public PostVO() {}
}
