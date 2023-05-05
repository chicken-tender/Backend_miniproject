package com.kh.Backend_miniproject.dao;

import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.BoardVO;
import com.kh.Backend_miniproject.vo.PostVO;
import com.kh.Backend_miniproject.vo.ReplyVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    //✨총 게시물 수 구하기
    public int getTotalPosts(int boardNum) {
        int totalPosts = 0;
        String sql = "SELECT COUNT(*) FROM POST_TB WHERE BOARD_NUM_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                totalPosts = rs.getInt(1);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return totalPosts;
    }


    // ✨일반 게시판 글 목록 (한 페이지당 8개씩)
    public List<PostVO> generalPostList(int boardNum, int pageNum) {
        int numPerPage = 8; // 페이지 당 보여주는 항목 개수
        List<PostVO> list = new ArrayList<>();
        int startRow = (pageNum - 1) * numPerPage + 1;
        int endRow = pageNum * numPerPage;

        String sql = "SELECT P.POST_NUM_PK, P.TITLE, M.NICKNAME, P.WRITE_DATE, P.VIEW_COUNT " +
                "FROM (" +
                "  SELECT POST_NUM_PK, TITLE, MEMBER_NUM_FK, WRITE_DATE, VIEW_COUNT, ROWNUM AS RN " +
                "  FROM ( " +
                "   SELECT POST_NUM_PK, TITLE, MEMBER_NUM_FK, WRITE_DATE, VIEW_COUNT " +
                "   FROM POST_TB " +
                "   WHERE BOARD_NUM_FK = ? " + // 먼저, 게시판 번호가 일치하는 게시물을 WRITE_DATE 기준으로 내림차순 정렬
                "   ORDER BY WRITE_DATE DESC " +
                "  ) " +
                "   WHERE ROWNUM <= ?" + //각 게시물에 일련 번호를 부여하고, 주어진 페이지의 마지막 행 번호(endRow)보다 작거나 같은 게시물만 선택
                ") P " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "WHERE P.RN BETWEEN ? AND ? " + //주어진 페이지 시작 행 번호(startRow)와 마지막 행 번호(endRow) 사이에 있는 게시물만 선택
                "ORDER BY WRITE_DATE DESC";


        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, boardNum);
            pstmt.setInt(2, endRow);
            pstmt.setInt(3, startRow);
            pstmt.setInt(4, endRow);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PostVO pv = new PostVO();
                pv.setPostNum(rs.getInt("POST_NUM_PK"));
                pv.setTitle(rs.getString("TITLE"));
                pv.setWriteDate(rs.getDate("WRITE_DATE"));
                pv.setNickname(rs.getString("NICKNAME"));
                pv.setViewCount(rs.getInt("VIEW_COUNT"));
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


    // ✨베스트 게시판으로 글 이동
    public void moveToBestBoard() {
        String sql = "UPDATE POST_TB SET BOARD_NUM_FK = 5 WHERE BOARD_NUM_FK = 2 AND LIKE_COUNT >= 100";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ✨포트폴리오 게시판 글 목록 (한 페이지당 6개씩)
    public List<PostVO> portfolioList(int pageNum) {
        int numPerPage = 6;
        List<PostVO> list = new ArrayList<>();
        int startRow = (pageNum - 1) * numPerPage + 1;
        int endRow = pageNum * numPerPage;

        String sql = "SELECT P.POST_NUM_PK, P.TITLE, P.IMG_URL, M.PF_IMG, M.NICKNAME, P.VIEW_COUNT, P.LIKE_COUNT " +
                "FROM POST_TB P " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "WHERE P.BOARD_NUM_FK = 4 AND P.POST_NUM_PK IN (" +
                "SELECT POST_NUM_PK " +
                "FROM (" +
                "SELECT POST_NUM_PK, ROWNUM AS RNUM " +
                "FROM (" +
                "   SELECT POST_NUM_PK " +
                "   FROM POST_TB " +
                "   WHERE BOARD_NUM_FK = 4 " +
                "   ORDER BY POST_NUM_PK DESC " +
                " ) " +
                "  WHERE ROWNUM <= ? " +
                ") " +
                "WHERE RNUM BETWEEN ? AND ? " +
                ") " +
                "ORDER BY P.POST_NUM_PK DESC";


        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, endRow);
            pstmt.setInt(2, startRow);
            pstmt.setInt(3, endRow);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                PostVO pv = new PostVO();
                pv.setPostNum(rs.getInt("POST_NUM_PK"));
                pv.setTitle(rs.getString("TITLE"));
                pv.setImgUrl(rs.getString("IMG_URL"));
                pv.setLikeCount(rs.getInt("LIKE_COUNT"));
                pv.setViewCount(rs.getInt("VIEW_COUNT"));
                pv.setNickname(rs.getString("NICKNAME"));
                pv.setPfImg(rs.getString("PF_IMG"));
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

    // ✨해당 게시판 게시글 검색
    public List<PostVO> searchPosts(int boardNum, int pageNum, String keyword) {
        int numPerPage = 8;
        int startRow = (pageNum - 1) * numPerPage + 1;
        int endRow = pageNum * numPerPage;

        List<PostVO> list = new ArrayList<>();

        String sql = "SELECT P.POST_NUM_PK, P.TITLE, M.NICKNAME, P.WRITE_DATE, P.VIEW_COUNT " +
                "FROM (" +
                "  SELECT POST_NUM_PK, TITLE,  MEMBER_NUM_FK, WRITE_DATE, VIEW_COUNT, ROWNUM AS RN " +
                "  FROM ( " +
                "   SELECT POST_NUM_PK, TITLE, MEMBER_NUM_FK, WRITE_DATE, VIEW_COUNT, CONTENT, TAG " +
                "   FROM POST_TB " +
                "   WHERE BOARD_NUM_FK = ? " +
                "   ORDER BY WRITE_DATE DESC " +
                "  ) " +
                "   WHERE ROWNUM <= ? AND (TITLE LIKE ? OR CONTENT LIKE ? OR TAG LIKE ?) " +
                ") P " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "WHERE P.RN BETWEEN ? AND ? " +
                "ORDER BY WRITE_DATE DESC";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            String kw = "%" + keyword + "%";
            pstmt.setInt(1, boardNum);
            pstmt.setInt(2, endRow);
            pstmt.setString(3, kw);
            pstmt.setString(4, kw);
            pstmt.setString(5, kw);
            pstmt.setInt(6, startRow);
            pstmt.setInt(7, endRow);


            rs = pstmt.executeQuery();

            while (rs.next()) {
                PostVO pv = new PostVO();

                pv.setPostNum(rs.getInt("POST_NUM_PK"));
                pv.setTitle(rs.getString("TITLE"));
                pv.setNickname(rs.getString("NICKNAME"));
                pv.setWriteDate(rs.getDate("WRITE_DATE"));
                pv.setViewCount(rs.getInt("VIEW_COUNT"));


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

    // ✨상세글 보기 (게시판 이름, 제목, 프로필사진, 작성자닉네임, 내용, 태그, 이미지, 작성날짜, 조회수, 추천수)
    public List<PostVO> viewPostDetail(int postNum) {
        String sql = "SELECT B.BOARD_NAME, P.TITLE, M.PF_IMG, M.NICKNAME, P.CONTENT, P.TAG, P.IMG_URL, P.WRITE_DATE, P.VIEW_COUNT, P.LIKE_COUNT " +
                "FROM POST_TB P " +
                "JOIN BOARD_TB B ON P.BOARD_NUM_FK = B.BOARD_NUM_PK " +
                "JOIN MEMBERS_TB M ON P.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "WHERE P.POST_NUM_PK = ? " +
                "ORDER BY POST_NUM_PK DESC";

        List<PostVO> list = new ArrayList<PostVO>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                PostVO post = new PostVO();
                post.setBoardName(rs.getString("BOARD_NAME"));
                post.setTitle(rs.getString("TITLE"));
                post.setNickname(rs.getString("NICKNAME"));
                post.setPfImg(rs.getString("PF_IMG"));
                post.setContent(rs.getString("CONTENT"));
                post.setTag(rs.getString("TAG"));
                post.setImgUrl(rs.getString("IMG_URL"));
                post.setWriteDate(rs.getDate("WRITE_DATE"));
                post.setViewCount(rs.getInt("VIEW_COUNT"));
                post.setLikeCount(rs.getInt("LIKE_COUNT"));
                list.add(post);
            }

            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // ✨ 게시글 작성
    public int writePost(PostVO post) {
        String sql = "INSERT INTO POST_TB (POST_NUM_PK, TITLE, CONTENT, TAG, IMG_URL, VIEW_COUNT, LIKE_COUNT, WRITE_DATE, BOARD_NUM_FK, MEMBER_NUM_FK) " +
                "VALUES (seq_POST_NUM.NEXTVAL, ?, ?, ?, ?, 0, 0, SYSDATE, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int postNum = 0;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql, new String[] {"POST_NUM_PK"});

            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getTag());
            pstmt.setString(4, post.getImgUrl());
            pstmt.setInt(5, post.getBoardNum());
            pstmt.setInt(6, post.getMemberNum());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                postNum = rs.getInt(1);
            }

            Common.close(pstmt);
            Common.close(conn);
            Common.close(rs);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return postNum;
    }


    // ✨ 게시글 수정
    public void updatePost(PostVO post) {
        String sql = "UPDATE POST_TB SET BOARD_NUM_FK = ?, TITLE = ?, CONTENT = ?, TAG = ?, IMG_URL = ? " +
                "WHERE POST_NUM_PK = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, post.getBoardNum());
            pstmt.setString(2, post.getTitle());
            pstmt.setString(3, post.getContent());
            pstmt.setString(4, post.getTag());
            pstmt.setString(5, post.getImgUrl());
            pstmt.setInt(6, post.getPostNum());
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ✨ 게시글 삭제
    public void deletePost(int postNum) {
        String sql = "DELETE FROM POST_TB WHERE POST_NUM_PK = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, postNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ✨게시글 조회수 증가
    public int increaseViews(int postNum) {
        String sql = "UPDATE POST_TB SET VIEW_COUNT = VIEW_COUNT + 1 WHERE POST_NUM_PK = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        int result = 0;
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postNum);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    // ✨게시글 추천수 업데이트
    public int updateLikes(int postNum, int memberNum) {
        // 회원(memberId)이 특정 게시물(postNum)에 이미 추천을 눌렀는지 확인
        String checkSql = "SELECT COUNT(*) FROM LIKES_TB WHERE POST_NUM_FK = ? AND MEMBER_NUM_FK = ?";
        // 추천을 누르지 않았을때, 추천수 추가
        String insertSql = "INSERT INTO LIKES_TB (POST_NUM_FK, MEMBER_NUM_FK) VALUES (?, ?)";
        // 추천을 이미 눌렀을 때, (추천버튼 두번 클릭) 기존 추천을 삭제 (중복 방지)
        String deleteSql = "DELETE FROM Likes WHERE POST_NUM_FK = ? AND MEMBER_NUM_FK = ?";
        // POST_TB의 LIKE_COUNT 업데이트
        String updatePostSql = "UPDATE POST_TB SET LIKE_COUNT = (SELECT COUNT(*) FROM Likes WHERE POST_NUM_FK = ?) WHERE POST_NUM_PK = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int result = 0;
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, postNum);
            pstmt.setInt(2, memberNum);
            rs = pstmt.executeQuery();
            rs.next();
            int isLiked = rs.getInt(1); // 첫번쨰 칼럼 값

            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

            if (isLiked == 0) { // 좋아요가 없다면(0)
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, postNum);
                pstmt.setInt(2, memberNum);
                pstmt.executeUpdate();
            } else { // 좋아요 있으면 삭제
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, postNum);
                pstmt.setInt(2, memberNum);
                pstmt.executeUpdate();
            }
            Common.close(pstmt);

            pstmt = conn.prepareStatement(updatePostSql);
            pstmt.setInt(1, postNum);
            pstmt.setInt(2, postNum);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    // ✨댓글 보기(프로필이미지, 댓글작성자 닉네임, 내용, 작성날짜)
    public List<ReplyVO> viewReply(int postNum) {
        String sql = "SELECT M.PF_IMG, M.NICKNAME, R.REPLY_CONTENT " +
                "FROM REPLY_TB R " +
                "JOIN MEMBERS_TB M ON R.MEMBER_NUM_FK = M.MEMBER_NUM_PK " +
                "WHERE R.POST_NUM_FK = ? " +
                "ORDER BY R.REPLY_NUM_PK ASC";

        List<ReplyVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ReplyVO rv = new ReplyVO();
                rv.setPfImg(rs.getString("PF_IMG"));
                rv.setNickname(rs.getString("NICKNAME"));
                rv.setReplyContent(rs.getString("REPLY_CONTENT"));
                list.add(rv);
            }

            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // ✨댓글 작성
    public void writeReply(int postNum, int memberNum, String replyContent) {
        String sql = "INSERT INTO REPLY_TB (REPLY_NUM_PK, POST_NUM_FK, MEMBER_NUM_FK, REPLY_CONTENT, WRITE_DATE) " +
                "VALUES (seq_REPLY_NUM.NEXTVAL, ?, ?, ?, SYSDATE)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postNum);
            pstmt.setInt(2, memberNum);
            pstmt.setString(3, replyContent);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ✨댓글 수정
    public void updateReply(int replyNum, String content) {
        String sql = "UPDATE REPLY_TB SET REPLY_CONTENT = ? WHERE REPLY_NUM_PK = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, content);
            pstmt.setInt(2, replyNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ✨댓글 삭제
    public void deleteReply(int replyNum) {
        String sql = "DELETE FROM REPLY_TB WHERE REPLY_NUM_PK = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;


        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, replyNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 게시판 이름으로 게시판 번호 get
    public int getBoardNum(String boardName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int boardNum = 0;

        try {
            conn = Common.getConnection();
            String sql = "SELECT BOARD_NUM_PK FROM BOARD_TB WHERE LOWER(BOARD_NAME) = LOWER(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, boardName.toLowerCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                boardNum = rs.getInt("BOARD_NUM_PK");
            }

            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return boardNum;
    }

}