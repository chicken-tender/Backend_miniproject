package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.ChatMessagesVO;
import com.kh.Backend_miniproject.vo.MembersVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChattingDAO {
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

    // ✨매칭 성공시 멘토 프로필 사진, 닉네임 get
    public List<MembersVO> getMentorInfoByMemberNum(int mentorMemberNum) {
        List<MembersVO> list = new ArrayList<>();
        String sql = "SELECT PF_IMG, NICKNAME " +
                "FROM MEMBERS_TB " +
                "WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mentorMemberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MembersVO mv = new MembersVO();
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

    // ✨매칭 성공시 멘티 프로필 사진, 닉네임 get
    public List<MembersVO> getMenteeInfoByEmail(String email) {
        List<MembersVO> list = new ArrayList<>();
        String sql = "SELECT PF_IMG, NICKNAME " +
                "FROM MEMBERS_TB " +
                "WHERE EMAIL = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MembersVO mv = new MembersVO();
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

    // ✨멘토 멘티 매칭 후 생성된 채팅방 저장
    public boolean createChatRoom(int mentorMemberNum, int menteeMemberNum) {
        int result = 0;
        String sql = "INSERT INTO CHAT_ROOM_TB (CHAT_NUM_PK, MENTOR_FK, MENTEE_FK) " +
                "VALUES (seq_CHAT_NUM.NEXTVAL, ?, ?)";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, mentorMemberNum);
            pstmt.setInt(2, menteeMemberNum);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(result == 1) return true;
        else return false;
    }

    // ✨채팅 메시지 저장
    public boolean saveChatMessage(int chatNum, int senderId, int receiverId, String message, String codeBlock, int messageType, Timestamp createdAt, Character isRead) {
        int result = 0;
        String sql = "INSERT INTO CHAT_MESSAGES_TB (MSG_NUM_PK, CHAT_NUM_FK, SENDER_ID_FK, RECEIVER_ID_FK, MESSAGE, CODE_BLOCK, MSG_TYPE, CREATED_AT, IS_READ) " +
                "VALUES (seq_MSG_NUM.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, chatNum);
            pstmt.setInt(2, senderId);
            pstmt.setInt(3, receiverId);
            pstmt.setString(4, message);
            pstmt.setString(5, codeBlock);
            pstmt.setInt(6, messageType);
            pstmt.setTimestamp(7, createdAt);
            pstmt.setString(8, String.valueOf(isRead));
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        if(result == 1) return true;
        else return false;
    }

    // ✨대화 종료시 대화방 삭제
    public void deleteChatRoom(int chatNum) {
        String sql = "DELETE FROM CHAT_ROOM_TB WHERE CHAT_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, chatNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // ✨대화 종료시 채팅 메시지 삭제
    public void deleteChatMessages(int chatNum) {
        String sql = "DELETE FROM CHAT_MESSAGES_TB WHERE CHAT_NUM_FK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, chatNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥특정 사용자와의 대화 내용 조회
    public List<ChatMessagesVO> getChatMessages(int senderId, int receiverId) {
        List<ChatMessagesVO> list = new ArrayList<>();
        String sql = "SELECT * FROM CHAT_MESSAGES_TB WHERE (SENDER_ID_FK = ? AND RECEIVER_ID_FK = ?) OR (SENDER_ID_FK = ? AND RECEIVER_ID_FK = ?) ORDER BY CREATED_AT DESC";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setInt(3, senderId);
            pstmt.setInt(4, receiverId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ChatMessagesVO cvo = new ChatMessagesVO();
                cvo.setMessageNum(rs.getInt("MSG_NUM_PK"));
                cvo.setChatNum(rs.getInt("CHAT_NUM_FK"));
                cvo.setSenderId(rs.getInt("SENDER_ID_FK"));
                cvo.setReceiverId(rs.getInt("RECEIVER_ID_FK"));
                cvo.setMessage(rs.getString("MESSAGE"));
                cvo.setCodeBlock(rs.getString("CODE_BLOCK"));
                cvo.setMessageType(rs.getInt("MSG_TYPE"));
                cvo.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                cvo.setIsRead(rs.getString("IS_READ").charAt(0));

                list.add(cvo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔥안읽은 메시지 조회
    public List<ChatMessagesVO> getUnreadMessages(int memberNum) {
        List<ChatMessagesVO> list = new ArrayList<>();
        String sql = "SELECT * FROM CHAT_MESSAGES_TB WHERE RECEIVER_ID_FK = ? AND IS_READ = 'N'";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ChatMessagesVO cvo = new ChatMessagesVO();
                cvo.setMessageNum(rs.getInt("MSG_NUM_PK"));
                cvo.setChatNum(rs.getInt("CHAT_NUM_FK"));
                cvo.setSenderId(rs.getInt("SENDER_ID_FK"));
                cvo.setReceiverId(rs.getInt("RECEIVER_ID_FK"));
                cvo.setMessage(rs.getString("MESSAGE"));
                cvo.setCodeBlock(rs.getString("CODE_BLOCK"));
                cvo.setMessageType(rs.getInt("MSG_TYPE"));
                cvo.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                cvo.setIsRead(rs.getString("IS_READ").charAt(0));

                list.add(cvo);
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
