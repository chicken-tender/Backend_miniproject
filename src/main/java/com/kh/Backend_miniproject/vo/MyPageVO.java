package com.kh.Backend_miniproject.vo;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class MyPageVO {
    private String pfImg;
    private String gradeIconUrl;
    private Date regDate;
    private String nickname;
    private String email;
    private String job;
    private int year;
    private int myPostCount;
    private int myReplyCount;
    private String boardName;
    private String postTitle;
    private String postContent;
    private String replyContent;
    private Date writeDate;
    private int postNum;
    private int replyNum;
}
