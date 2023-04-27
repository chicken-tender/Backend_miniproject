package com.kh.Backend_miniproject.vo;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class SignUpVO {
    private int gradeNum;
    private String email;
    private String pwd;
    private String nickname;
    private String job;
    private int year;
    private String pfImg;
    private List<MemberTechStackVO> techStacks;
}
