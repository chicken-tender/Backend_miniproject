package com.kh.Backend_miniproject.vo;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class MembersVO {
    private int memberNum;
    private int gradeNum;
    private String email;
    private String pwd;
    private String nickName;
    private String job;
    private int year;
    private Date regDate;
    private String pfImg;
    private String isWithDrawn;
}
