package com.kh.Backend_miniproject.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostInfoVO {
    private String title;
    private String nickName;
    private String pfImg;
    private int viewCount;
    private int commentCount;
}
