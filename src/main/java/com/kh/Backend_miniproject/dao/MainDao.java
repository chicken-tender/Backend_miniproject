package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.PostInfoVO;
import com.kh.Backend_miniproject.vo.TopWriterVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MainDao {
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    // ✅글을 많이 작성한 상위 5명의 회원정보 get
    public List<TopWriterVO> getTopWriters() {
        List<TopWriterVO> list = new ArrayList<>();

        String sql = "SELECT * FROM (" +
                "  SELECT PF_IMG, NICKNAME, COUNT(POST_NUM_PK) AS c" +
                "  FROM MEMBERS_TB m JOIN POST_TB p" +
                "  ON m.MEMBER_NUM_PK = p.MEMBER_NUM_FK" +
                "  GROUP BY PF_IMG, NICKNAME" +
                "  ORDER BY c DESC)" +
                "  WHERE ROWNUM <= 5";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String nickName = rs.getString("NICKNAME");
                String pfImg = rs.getString("PF_IMG");
                int count = rs.getInt("c");

                TopWriterVO vo = new TopWriterVO();
                vo.setNickname(nickName);
                vo.setPfImg(pfImg);
                vo.setCount(count);
                list.add(vo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // ✅전체 회원 수 get
    public int getTotalMemberCount() {
        int count = 0;
        String sql = "SELECT COUNT(MEMBER_NUM_PK) AS c FROM MEMBERS_TB";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                count = rs.getInt("c");
            }

            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    // ✅오늘 새로 올라온 글 갯수
    public int getTodayPostCount() {
        int count = 0;
        String sql = "SELECT COUNT(POST_NUM_PK) AS c FROM POST_TB WHERE TRUNC(WRITE_DATE) = TRUNC(SYSDATE)";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                count = rs.getInt("c");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    // ✅오늘 새로 올라온 댓글 갯수
    public int getTodayReplyCount() {
        int count = 0;
        String sql = "SELECT COUNT(REPLY_NUM_PK) AS c FROM REPLY_TB WHERE TRUNC(WRITE_DATE) = TRUNC(SYSDATE)";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                count = rs.getInt("c");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    // ✅포트폴리오 게시판 글 전체 갯수
    public int getPortfolioPostCount() {
        int count = 0;
        String sql = "SELECT COUNT(POST_NUM_PK) AS c FROM POST_TB WHERE BOARD_NUM_FK = 4";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                count = rs.getInt("c");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ✅전체 글 갯수
    public int getTotalPostCount() {
        int count = 0;
        String sql = "SELECT COUNT(POST_NUM_PK) AS c FROM POST_TB";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                count = rs.getInt("c");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ✅회원 프로필 사진 (by email)
    public String getProfileImageByEmail(String email) {
        String profileImage = null;
        String sql = "SELECT PF_IMG FROM MEMBERS_TB WHERE EMAIL = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                profileImage = rs.getString("PF_IMG");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return profileImage;
    }

    // ✅회원 프로필 사진 (by memberNum)
    public String getProfileImageByMemberNum(int memberNum) {
        String profileImage = null;
        String sql = "SELECT PF_IMG FROM MEMBERS_TB WHERE MEMBER_NUM_PK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                profileImage = rs.getString("PF_IMG");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return profileImage;
    }

    // ✅회원 닉네임 (by memberNum)
    public String getNicknameByMemberNum(int memberNum) {
        String nickname = null;
        String sql = "SELECT NICKNAME FROM MEMBERS_TB WHERE MEMBER_NUM_PK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                nickname = rs.getString("NICKNAME");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return nickname;
    }

    // ✅회원 닉네임 get
    public String getNickNameByEmail(String email) {
        String nickname = null;
        String sql = "SELECT NICKNAME FROM MEMBERS_TB WHERE EMAIL = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                nickname = rs.getString("NICKNAME");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return nickname;
    }

    // ✅각 게시판 별로 데이터 가져오기
    public List<PostInfoVO> getLatestPosts(int boardNum) {
        List<PostInfoVO> list = new ArrayList<>();
        String sql = "SELECT * FROM (" +
                "SELECT P.POST_NUM_PK, P.TITLE, M.NICKNAME, M.PF_IMG, P.VIEW_COUNT, P.REPLY_COUNT, P.WRITE_DATE " +
                "FROM (" +
                "SELECT POST.POST_NUM_PK, POST.TITLE, POST.MEMBER_NUM_FK, POST.VIEW_COUNT, POST.WRITE_DATE, COUNT(REPLY.REPLY_NUM_PK) AS REPLY_COUNT " +
                "FROM POST_TB POST " +
                "LEFT JOIN REPLY_TB REPLY ON POST.POST_NUM_PK = REPLY.POST_NUM_FK " +
                "WHERE POST.BOARD_NUM_FK = ? " +
                "GROUP BY POST.POST_NUM_PK, POST.TITLE, POST.MEMBER_NUM_FK, POST.VIEW_COUNT, POST.WRITE_DATE " +
                ") P " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "ORDER BY P.WRITE_DATE DESC " +
                ") " +
                "WHERE ROWNUM <= 5";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                PostInfoVO pv = new PostInfoVO();
                pv.setPostNum(rs.getInt("POST_NUM_PK"));
                pv.setTitle(rs.getString("TITLE"));
                pv.setNickname(rs.getString("NICKNAME"));
                pv.setPfImg(rs.getString("PF_IMG"));
                pv.setViewCount(rs.getInt("VIEW_COUNT"));
                pv.setCommentCount(rs.getInt("REPLY_COUNT"));
                pv.setWriteDate(rs.getDate("WRITE_DATE"));

                list.add(pv);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 📍메인 검색 기능
    public List<PostInfoVO> mainSearchPosts(String keyword) {
        List<PostInfoVO> list = new ArrayList<>();
        String sql = "SELECT P.POST_NUM_PK, P.TITLE, M.NICKNAME, P.CONTENT, P.IMG_URL, P.WRITE_DATE, P.TAG, M.PF_IMG, P.VIEW_COUNT, P.REPLY_COUNT " +
                "FROM (" +
                "SELECT POST.POST_NUM_PK, POST.TITLE, POST.CONTENT, POST.MEMBER_NUM_FK, POST.IMG_URL, POST.WRITE_DATE, POST.TAG, POST.VIEW_COUNT, COUNT(REPLY.REPLY_NUM_PK) AS REPLY_COUNT " +
                "FROM POST_TB POST " +
                "LEFT JOIN REPLY_TB REPLY ON POST.POST_NUM_PK = REPLY.POST_NUM_FK " +
                "WHERE (POST.TITLE LIKE ? OR POST.CONTENT LIKE ? OR POST.TAG LIKE ?) " +
                "GROUP BY POST.POST_NUM_PK, POST.TITLE, POST.CONTENT, POST.MEMBER_NUM_FK, POST.IMG_URL, POST.WRITE_DATE, POST.TAG, POST.VIEW_COUNT " +
                ") P " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "ORDER BY P.WRITE_DATE DESC";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            rs = pstmt.executeQuery();

            while(rs.next()) {
                PostInfoVO pv = new PostInfoVO();
                pv.setPostNum(rs.getInt("POST_NUM_PK"));
                pv.setTitle(rs.getString("TITLE"));
                pv.setNickname(rs.getString("NICKNAME"));
                pv.setContent(rs.getString("CONTENT"));
                pv.setImgUrl(rs.getString("IMG_URL"));
                pv.setWriteDate(rs.getDate("WRITE_DATE"));
                pv.setTag(rs.getString("TAG"));
                pv.setPfImg(rs.getString("PF_IMG"));
                pv.setViewCount(rs.getInt("VIEW_COUNT"));
                pv.setReplyCount(rs.getInt("REPLY_COUNT"));

                list.add(pv);
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
