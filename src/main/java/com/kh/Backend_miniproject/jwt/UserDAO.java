package com.kh.Backend_miniproject.jwt;

import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.MembersVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    // 사용자 이메일을 가지고...회원 정보 가져오기
    public List<MembersVO> getUserInfoByEmail(String email) {
        List<MembersVO> list = new ArrayList<>();
        String sql = "SELECT * FROM MEMBERS_TB WHERE EMAIL = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MembersVO mv = new MembersVO();
                mv.setEmail(rs.getString("EMAIL"));
                mv.setPwd(rs.getString("PWD"));
                mv.setPfImg(rs.getString("PF_IMG"));
                mv.setMemberNum(rs.getInt("MEMBER_NUM_PK"));
                mv.setNickname(rs.getString("NICKNAME"));
                mv.setIsWithdrawn(rs.getString("IS_WITHDRAWN"));
                mv.setIsActive(rs.getString("IS_ACTIVE"));
                list.add(mv);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
