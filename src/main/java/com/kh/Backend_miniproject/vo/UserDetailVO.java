package com.kh.Backend_miniproject.vo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailVO {
    private String pfImg;
    private String nickname;
    private String job;
    private int year;
    private String gradeIconUrl;
    private List<String> stackIconUrls;
    private List<Integer> stackNums;
}
