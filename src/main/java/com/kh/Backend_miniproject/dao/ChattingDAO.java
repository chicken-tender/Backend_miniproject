package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.ChatMessagesVO;
import com.kh.Backend_miniproject.vo.MentorMenteeVO;
import com.kh.Backend_miniproject.vo.UserDetailVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class ChattingDAO {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    // ✅멘토 멘티 매칭 후 생성된 채팅방 저장
    public Integer createChatRoom(int mentorMemberNum, int menteeMemberNum) {
        int result = 0;
        Integer chatNum = null;
        String sql = "INSERT INTO CHAT_ROOM_TB (CHAT_NUM_PK, MENTOR_FK, MENTEE_FK) " +
                "VALUES (seq_CHAT_NUM.NEXTVAL, ?, ?)";
        Map<String, Integer> rm = null;
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"CHAT_NUM_PK"});
            pstmt.setInt(1, mentorMemberNum);
            pstmt.setInt(2, menteeMemberNum);
            result = pstmt.executeUpdate();

            if(result == 1) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    chatNum = generatedKeys.getInt(1);
                }
            }

            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return chatNum;
    }

    // ✅채팅 메시지 저장
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

    // ✅채팅이 매칭된 회원번호 가져오기
    public List<MentorMenteeVO> getAllMentorMenteeNum() {
        List<MentorMenteeVO> list = new ArrayList<>();

        String sql = "SELECT MENTOR_FK, MENTEE_FK FROM CHAT_ROOM_TB";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MentorMenteeVO mmvo = new MentorMenteeVO();
                mmvo.setMentorNum(rs.getInt("MENTOR_FK"));
                mmvo.setMenteeNum(rs.getInt("MENTEE_FK"));
                list.add(mmvo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🤮로그인 한 유저가 속한 채팅방 가져오기
    public int getChatRoomByMemberNum(int memberNum) {
        int chatRoom = 0;
        String sql = "SELECT CHAT_NUM_PK FROM CHAT_ROOM_TB WHERE MENTOR_FK = ? OR MENTEE_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, memberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                chatRoom = rs.getInt("CHAT_NUM_PK");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return chatRoom;
    }

    // 🤮채팅방 번호 가지고 대화 정보 가져오기
    public List<ChatMessagesVO> getMessagesByChatNum(int chatRoom) {
        List<ChatMessagesVO> list = new ArrayList<>();
        String sql = "SELECT * FROM CHAT_MESSAGES_TB WHERE CHAT_NUM_FK = ? ORDER BY CREATED_AT";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, chatRoom);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ChatMessagesVO cmvo = new ChatMessagesVO();
                cmvo.setMessageNum(rs.getInt("MSG_NUM_PK"));
                cmvo.setChatNum(rs.getInt("CHAT_NUM_FK"));
                cmvo.setSenderId(rs.getInt("SENDER_ID_FK"));
                cmvo.setReceiverId(rs.getInt("RECEIVER_ID_FK"));
                cmvo.setMessage(rs.getString("MESSAGE"));
                cmvo.setCodeBlock(rs.getString("CODE_BLOCK"));
                cmvo.setMessageType(rs.getInt("MSG_TYPE"));
                cmvo.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                cmvo.setIsRead(rs.getString("IS_READ").charAt(0));

                list.add(cmvo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
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

    // ✨특정 사용자와의 대화 내용 조회
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

    // ✨안읽은 메시지 조회
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

    // ✨메시지를 읽었다고 알려주기
    public boolean markMessageAsRead(int messageId) {
        int result = 0;
        String sql = "UPDATE CHAT_MESSAGES_TB SET IS_READ = 'Y' WHERE RECEIVER_ID_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, messageId);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result == 1;
    }

    // ✨상대방 프로필 사진, 등급뱃지, 닉네임, 개발 스택, 직업, 연차 가져오기
    public UserDetailVO getUserDetailsByMemberNum(int memberNum) {
        UserDetailVO uvo = null;
        String sql = "SELECT M.PF_IMG, M.NICKNAME, M.JOB, M.YEAR, " +
                "LISTAGG(T.STACK_ICON_URL, ',') WITHIN GROUP (ORDER BY T.STACK_NUM_PK) AS STACK_ICON_URLS, " +
                "LISTAGG(MT.STACK_NUM_FK, ',') WITHIN GROUP (ORDER BY T.STACK_NUM_PK) AS STACK_NUM_FKS " +
                "FROM MEMBERS_TB M " +
                "JOIN MEMBER_TS_TB MT ON M.MEMBER_NUM_PK = MT.MEMBER_NUM_FK " +
                "JOIN TECH_STACK_TB T ON MT.STACK_NUM_FK = T.STACK_NUM_PK " +
                "WHERE M.MEMBER_NUM_PK = ? " +
                "GROUP BY M.PF_IMG, M.NICKNAME, M.JOB, M.YEAR";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                uvo = new UserDetailVO();
                uvo.setPfImg(rs.getString("PF_IMG"));
                uvo.setNickname(rs.getString("NICKNAME"));
                uvo.setJob(rs.getString("JOB"));
                uvo.setYear(rs.getInt("YEAR"));
                List<String> stackIconUrls = Arrays.asList(rs.getString("STACK_ICON_URLS").split(","));
                uvo.setStackIconUrls(stackIconUrls);
                List<Integer> stackNums = Arrays.stream(rs.getString("STACK_NUM_FKS").split(","))
                        .map(Integer::parseInt).
                        collect(Collectors.toList());
                uvo.setStackNums(stackNums);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return uvo;
    }
}
