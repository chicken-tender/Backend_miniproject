package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.ChatMessagesVO;
import com.kh.Backend_miniproject.vo.MembersVO;
import com.kh.Backend_miniproject.vo.UserDetailVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MatchingDAO {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    // ✨매칭 조건에 맞는 멘토 선택 후 회원번호 get
    public int getMentorMemberNum(int menteeMemberNum) {
        int mentorMemberNum = 0;
        String sql = "SELECT * " +
                "FROM ( " +
                "  SELECT m1.MEMBER_NUM_FK AS MENTOR " +
                "  FROM MEMBER_TS_TB m1 " +
                "  JOIN MEMBER_TS_TB m2 ON m1.STACK_NUM_FK = m2.STACK_NUM_FK " +
                "  JOIN MEMBERS_TB mem ON m1.MEMBER_NUM_FK = mem.MEMBER_NUM_PK " +
                "  WHERE m2.MEMBER_NUM_FK = ? " +
                "    AND m1.MEMBER_NUM_FK != m2.MEMBER_NUM_FK " +
                "    AND mem.JOB NOT IN ('학생', '구직자') " +
                "    AND NOT EXISTS ( " +
                "      SELECT 1 " +
                "      FROM CHAT_ROOM_TB cr " +
                "      WHERE cr.MENTOR_FK = m1.MEMBER_NUM_FK " +
                "    ) " +
                "  GROUP BY m1.MEMBER_NUM_FK " +
                "  HAVING COUNT(*) >= 2 " +
                "  ORDER BY DBMS_RANDOM.RANDOM " +
                ") " +
                "WHERE ROWNUM <= 1";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, menteeMemberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                mentorMemberNum = rs.getInt("MENTOR");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return mentorMemberNum;
    }

    // ✨매칭 성공시 회원 번호, 멘토 프로필 사진, 닉네임 get
    public List<MembersVO> getMentorInfoByMemberNum(int mentorMemberNum) {
        List<MembersVO> list = new ArrayList<>();
        String sql = "SELECT MEMBER_NUM_PK, PF_IMG, NICKNAME " +
                "FROM MEMBERS_TB " +
                "WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mentorMemberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MembersVO mv = new MembersVO();
                mv.setMemberNum(rs.getInt("MEMBER_NUM_PK"));
                mv.setPfImg(rs.getString("PF_IMG"));
                mv.setNickname(rs.getString("NICKNAME"));
                list.add(mv);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔴멘티 회원번호 get
    public int getMenteeMemberNumByEmail(String menteeEmail) {
        int menteeMembernum = 0;
        String sql = "SELECT MEMBER_NUM_PK FROM MEMBERS_TB WHERE EMAIL = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, menteeEmail);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                menteeMembernum = rs.getInt("MEMBER_NUM_PK");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return menteeMembernum;
    }

    // ✨매칭 성공시 회원 번호, 멘티 프로필 사진, 닉네임 get
    public List<MembersVO> getMenteeInfoByEmail(String email) {
        List<MembersVO> list = new ArrayList<>();
        String sql = "SELECT MEMBER_NUM_PK, PF_IMG, NICKNAME " +
                "FROM MEMBERS_TB " +
                "WHERE EMAIL = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MembersVO mv = new MembersVO();
                mv.setMemberNum(rs.getInt("MEMBER_NUM_PK"));
                mv.setPfImg(rs.getString("PF_IMG"));
                mv.setNickname(rs.getString("NICKNAME"));
                list.add(mv);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
