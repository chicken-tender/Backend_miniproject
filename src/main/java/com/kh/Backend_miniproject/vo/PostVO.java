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

    // 닉네임, 게시판이름, 프로필사진 제외한 생성자
    public PostVO(int postNum, int boardNum, int memberNum, String title, String content, int viewCount, int likeCount, Date writeDate, String imgUrl, String tag) {
        this.postNum = postNum;
        this.boardNum = boardNum;
        this.memberNum = memberNum;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.writeDate = writeDate;
        this.imgUrl = imgUrl;
        this.tag = tag;
    }
}
