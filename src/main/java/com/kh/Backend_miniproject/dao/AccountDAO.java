package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import com.kh.Backend_miniproject.vo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    // 📍로그인 체크 - 경미 테스트
    public boolean isLoginValid(String email, String pwd) {
        String sql = "SELECT * FROM MEMBERS_TB WHERE EMAIL = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String sqlEmail = rs.getString("EMAIL");
                String sqlPwd = rs.getString("PWD");
                if(email.equals(sqlEmail) && pwd.equals(sqlPwd)) {
                    Common.close(rs);
                    Common.close(pstmt);
                    Common.close(conn);
                    return true;
                }
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 👤(회원가입) 기본 정보 저장
        // 🔥INSERT 문은 프론트엔드에게 성공여부만 알려주면 되기 때문에 return 타입 boolean으로 하면 됨
    public boolean createMember(int gradeNumber, String email, String password, String nickName, String job, int year, String pfImg) {
        int result = 0;
        String sql = "INSERT INTO MEMBERS_TB (MEMBER_NUM_PK, GRADE_NUM_FK, EMAIL, PWD, NICKNAME, JOB, YEAR, PF_IMG)" +
                " VALUES (seq_MEMBER_NUM.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gradeNumber);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, nickName);
            pstmt.setString(5, job);
            pstmt.setInt(6, year);
            pstmt.setString(7, pfImg);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result == 1) return true;
        else return false;
    }

    /* 🔥1. DAO 생성 : SELECT MEMBER_NUM_PK FROM MEMBERS_TB WHERE EMAIL = '';
            ✨return 타입은 int
         2. DAO 수정 : 회원의 기술 스택 저장 DAO 수정
         3. 1,2 쿼리문을 사용하여 기술스택 응답하는 컨트롤러 생성 */

    // 🔥이메일을 가지고 회원번호 얻기
    public int getMemberNumbyEmail(String email) {
        int memberNum = 0;
        String sql = "SELECT MEMBER_NUM_PK FROM MEMBERS_TB WHERE EMAIL = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                memberNum = rs.getInt("MEMBER_NUM_PK");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return memberNum;
    }

    // 👤(회원가입) 회원의 기술 스택 저장 -> 🔥수정
    public boolean createMemberTechStack(String email, int stackNum) {
        int result = 0;
        String sql = "INSERT INTO MEMBER_TS_TB (MEMBER_NUM_FK, STACK_NUM_FK) VALUES (?, ?)";
        AccountDAO ado = new AccountDAO();
        int memberNum = ado.getMemberNumbyEmail(email);

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setObject(2, stackNum);
            result = pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result == 1) return true;
        else return false;
    }

    // 🔥회원의 기술스택 조회
    public List<MemberTechStackVO> getMemberTechStack(int memberNum) {
        List<MemberTechStackVO> list = new ArrayList<>();
        String sql = "SELECT * FROM MEMBER_TS_TB WHERE MEMBER_NUM_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                MemberTechStackVO mtsVo = new MemberTechStackVO();
                memberNum = rs.getInt("MEMBER_NUM_FK");
                mtsVo.setStackNum(rs.getInt("STACK_NUM_FK"));

                list.add(mtsVo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔥


    // [5.1 추가] GET🔑 회원가입시 닉네임 중복확인
    public boolean findMemberByNickname(String nickname) {
        boolean result = false;
        String sql = "SELECT * FROM MEMBERS_TB WHERE NICKNAME = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            ResultSet resultSet = pstmt.executeQuery();
            result = !resultSet.next();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // [5.2 추가] GET🔑 기술스택 전체 호출
    public List<TechStackVO> getAllTechStacks() {
        List<TechStackVO> list = new ArrayList<>();
        String sql = "SELECT * FROM TECH_STACK_TB";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                TechStackVO tsvo = new TechStackVO();
                tsvo.setStackNum(rs.getInt("STACK_NUM_PK"));
                tsvo.setStackIconUrl(rs.getString("STACK_ICON_URL"));
                tsvo.setStackName(rs.getString("STACK_NAME"));
                list.add(tsvo);
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    // [5.3 추가] GET🔑입력받은 닉네임으로 사용자의 이메일 호출
    public String getMemberEmailByNickname(String nickname) {
        String memberEmail = "";
        String sql = "SELECT EMAIL FROM MEMBERS_TB WHERE NICKNAME = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                memberEmail = rs.getString("EMAIL");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return memberEmail;
    }

    // [5.3 추가] GET🔑 입력받은 닉네임&이메일로 회원 존재 여부 확인
    public boolean getMemberByNicknameAndEmail(String nickname, String email) {
        boolean result = false;
        String sql = "SELECT * FROM MEMBERS_TB WHERE NICKNAME = ? AND EMAIL = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            pstmt.setString(2, email);
            ResultSet resultSet = pstmt.executeQuery();
            result = resultSet.next();

            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 🔐회원 정보 read
    public SignUpVO readMemberInfoByNumber(int memberNum) {
        SignUpVO vo = null;
        String sql = "SELECT * FROM MEMBERS_TB WHERE MEMBER_NUM_PK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vo = new SignUpVO();
                vo.setGradeNum(rs.getInt("GRADE_NUM_FK"));
                vo.setEmail(rs.getString("EMAIL"));
                vo.setPwd(rs.getString("PWD"));
                vo.setNickname(rs.getString("NICKNAME"));
                vo.setJob(rs.getString("JOB"));
                vo.setYear(rs.getInt("YEAR"));
                vo.setPfImg(rs.getString("PF_IMG"));
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    // 🔑(마이페이지) 회원의 최근 게시글 5개 (카테고리, 제목, 본문, 날짜)
    public List<MyPageVO> getMemberLatestPosts(int memberNum) {
        List<MyPageVO> list = new ArrayList<>();
        String sql = "SELECT *" +
                " FROM (" +
                " SELECT p.TITLE, p.CONTENT, b.BOARD_NAME, p.WRITE_DATE " +
                " FROM POST_TB p " +
                " JOIN BOARD_TB b ON p.BOARD_NUM_FK = b.BOARD_NUM_PK " +
                " JOIN MEMBERS_TB m ON p.MEMBER_NUM_FK = m.MEMBER_NUM_PK " +
                " WHERE m.MEMBER_NUM_PK = ?" +
                " ORDER BY p.WRITE_DATE DESC) " +
                " WHERE ROWNUM <=5";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MyPageVO vo = new MyPageVO();
                vo.setPostTitle(rs.getString("TITLE"));
                vo.setPostContent(rs.getString("CONTENT"));
                vo.setBoardName(rs.getString("BOARD_NAME"));
                vo.setWriteDate(rs.getDate("WRITE_DATE"));

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

    // 🔑(마이페이지) 회원의 최근 댓글 5개 (카테고리, 댓글내용, 게시글 제목, 날짜)
    public List<MyPageVO> getMemberLatestReplies(int memberNum) {
        List<MyPageVO> list = new ArrayList<>();
        String sql = "SELECT *" +
                " FROM (" +
                " SELECT r.REPLY_CONTENT, p.TITLE, b.BOARD_NAME, p.WRITE_DATE" +
                " FROM REPLY_TB r" +
                " JOIN POST_TB p ON r.POST_NUM_FK = p.POST_NUM_PK" +
                " JOIN BOARD_TB b ON p.BOARD_NUM_FK = b.BOARD_NUM_PK" +
                " JOIN MEMBERS_TB m ON p.MEMBER_NUM_FK = m.MEMBER_NUM_PK" +
                " WHERE m.MEMBER_NUM_PK = ?" +
                " ORDER BY p.WRITE_DATE DESC" +
                ")" +
                " WHERE ROWNUM <=5";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MyPageVO vo = new MyPageVO();
                vo.setReplyContent(rs.getString("REPLY_CONTENT"));
                vo.setPostTitle(rs.getString("TITLE"));
                vo.setBoardName(rs.getString("BOARD_NAME"));
                vo.setWriteDate(rs.getDate("WRITE_DATE"));
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

    // 🔑(마이페이지 > 내 게시글 관리) 회원의 모든 게시글
    public List<MyPageVO> getMemberAllPosts(int memberNumber) {
        List<MyPageVO> list = new ArrayList<>();
        String sql = "SELECT p.TITLE, p.CONTENT, b.BOARD_NAME, p.WRITE_DATE" +
                " FROM POST_TB p" +
                " JOIN BOARD_TB b ON p.BOARD_NUM_FK = b.BOARD_NUM_PK" +
                " JOIN MEMBERS_TB m ON p.MEMBER_NUM_FK = m.MEMBER_NUM_PK" +
                " WHERE m.MEMBER_NUM_PK = ? " +
                " ORDER BY p.WRITE_DATE DESC";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNumber);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MyPageVO vo = new MyPageVO();
                vo.setPostTitle(rs.getString("TITLE"));
                vo.setPostContent(rs.getString("CONTENT"));
                vo.setBoardName(rs.getString("BOARD_NAME"));
                vo.setWriteDate(rs.getDate("WRITE_DATE"));
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

    // 🔑(마이페이지 > 내 댓글 관리) 회원의 모든 댓글
    public List<MyPageVO> getMemberAllReplies(int memberNumber) {
        List<MyPageVO> list = new ArrayList<>();
        String sql = "SELECT r.REPLY_CONTENT, p.TITLE, b.BOARD_NAME, p.WRITE_DATE" +
                " FROM REPLY_TB r" +
                " JOIN POST_TB p ON r.POST_NUM_FK = p.POST_NUM_PK" +
                " JOIN BOARD_TB b ON p.BOARD_NUM_FK = b.BOARD_NUM_PK" +
                " JOIN MEMBERS_TB m ON p.MEMBER_NUM_FK = m.MEMBER_NUM_PK" +
                " WHERE m.MEMBER_NUM_PK = ?" +
                " ORDER BY p.WRITE_DATE DESC";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNumber);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MyPageVO vo = new MyPageVO();
                vo.setReplyContent(rs.getString("REPLY_CONTENT"));
                vo.setPostTitle(rs.getString("TITLE"));
                vo.setBoardName(rs.getString("BOARD_NAME"));
                vo.setWriteDate(rs.getDate("WRITE_DATE"));
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

    // 🔐회원 정보 변경 : 이메일
    public void updateMemberEmail(String memberEmail, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET EMAIL = ?" +
                " WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
        if (memberEmail != null) {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberEmail);
            pstmt.setInt(2, memberNum);
            pstmt.executeUpdate();
        }
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 정보 변경 : 비밀번호
    public void updateMemberPassword(String memberPwd, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET PWD = ?" +
                " WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberPwd);
            pstmt.setInt(2, memberNum);
            pstmt.execute();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 정보 변경 : 닉네임
    public void updateMemberNickname(String memberNickname, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET NICKNAME = ?" +
                " WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberNickname);
            pstmt.setInt(2, memberNum);
            pstmt.execute();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 정보 변경 : 직업
    public void updateMemberJob(String memberJob, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET JOB = ?" +
                " WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberJob);
            pstmt.setInt(2, memberNum);
            pstmt.execute();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 정보 변경 : 연차
    public void updateMemberYear(int memberYear, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET YEAR = ?" +
                " WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberYear);
            pstmt.setInt(2, memberNum);
            pstmt.execute();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 정보 변경 : 기술 스택 추가
    public boolean addMemberTechStack(int memberNum, int techStackNum) {
        int result = 0;
        String sql = "INSERT INTO MEMBER_TS_TB (MEMBER_NUM_FK, STACK_NUM_FK) VALUES (?, ?)";
        AccountDAO adao = new AccountDAO();
            try {
                conn = Common.getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, memberNum);
                pstmt.setInt(2, techStackNum);
                result = pstmt.executeUpdate();

                Common.close(pstmt);
                Common.close(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(result == 1) return true;
            else return false;
        }

    // 🔐회원 정보 변경 : 기술 스택 삭제
    public void deteleMemberTechStack(int memberNum, int memberTechStackNum) {
        String sql = "DELETE FROM MEMBER_TS_TB WHERE MEMBER_NUM_FK = ? AND STACK_NUM_FK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, memberTechStackNum);
            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐회원 탈퇴시 isWithdrawn 변경
    public void updateMemberIsWithdrawn(String memberIsWithdrawn, int memberNum) {
        String sql = "UPDATE MEMBERS_TB SET IS_WITHDRAWN = ? WHERE MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberIsWithdrawn);
            pstmt.setInt(2, memberNum);
            pstmt.execute();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ❌(마이페이지) 회원 등급 아이콘
    public String getMemberGradeIcon(int memberNum) {
        String gradeIcon = null;
        String sql = "SELECT g.GRADE_ICON_URL" +
                " FROM GRADE_TB g JOIN MEMBERS_TB m" +
                " ON g.GRADE_NUM_PK = m.GRADE_NUM_FK" +
                " WHERE m.MEMBER_NUM_PK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                gradeIcon = rs.getString("GRADE_ICON_URL");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gradeIcon;
    }

    // ❌(마이페이지) 회원의 전체 게시글 수
    public int getMemberPostCount(int memberNumber) {
        int count = 0;
        String sql = "SELECT COUNT(p.MEMBER_NUM_FK) AS mpn FROM POST_TB p WHERE p.MEMBER_NUM_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNumber);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                count = rs.getInt("mpn");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ❌(마이페이지) 회원의 전체 댓글 수
    public int getMemberReplyCount(int memberNumber) {
        int count = 0;
        String sql = "SELECT COUNT(r.MEMBER_NUM_FK) AS mrc FROM REPLY_TB r WHERE r.MEMBER_NUM_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNumber);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                count = rs.getInt("mrc");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ❌(마이페이지) 회원정보 기본 (프로필사진, 가입일, 닉네임, 이메일, 직업, 연차)
    public List<MembersVO> getMemberInfoBasicByNumber(int memberNum) {
        List<MembersVO> list = new ArrayList<>();

        String sql = "SELECT m.PF_IMG, m.REG_DATE, m.NICKNAME, m.EMAIL, m.JOB, m.YEAR" +
                " FROM MEMBERS_TB m" +
                " JOIN GRADE_TB g ON m.GRADE_NUM_FK = g.GRADE_NUM_PK" +
                " WHERE m.MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String pfImg = rs.getString("PF_IMG");
                Date regDate = rs.getDate("REG_DATE");
                String nickname = rs.getString("NICKNAME");
                String email = rs.getString("EMAIL");
                String job = rs.getString("JOB");
                int year = rs.getInt("YEAR");

                MembersVO vo = new MembersVO();
                vo.setPfImg(pfImg);
                vo.setRegDate(regDate);
                vo.setNickname(nickname);
                vo.setEmail(email);
                vo.setJob(job);
                vo.setYear(year);
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

    // ❌ 마이페이지: 회원정보 조회 (등급아이콘, 총 게시글 수, 총 댓글 수)
    public List<MyPageVO> getMemberInfoByNum(int memberNum) {
        List<MyPageVO> list = new ArrayList<>();

        String sql = "SELECT m.PF_IMG, g.GRADE_ICON_URL, m.REG_DATE, m.NICKNAME, m.EMAIL, m.JOB, m.YEAR," +
                "  (SELECT COUNT(p.MEMBER_NUM_FK) FROM POST_TB p WHERE p.MEMBER_NUM_FK = ?) AS mpc," +
                " (SELECT COUNT(r.MEMBER_NUM_FK) FROM REPLY_TB r WHERE r.MEMBER_NUM_FK = ?) AS mrc" +
                " FROM MEMBERS_TB m" +
                " JOIN GRADE_TB g ON m.GRADE_NUM_FK = g.GRADE_NUM_PK" +
                " WHERE m.MEMBER_NUM_PK = ?";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, memberNum);
            pstmt.setInt(3, memberNum);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String pfImg = rs.getString("PF_IMG");
                String gradeIconUrl = rs.getString("GRADE_ICON_URL");
                Date regDate = rs.getDate("REG_DATE");
                String nickname = rs.getString("NICKNAME");
                String email = rs.getString("EMAIL");
                String job = rs.getString("JOB");
                int year = rs.getInt("YEAR");
                int myPostCount = rs.getInt("mpc");
                int myReplyCount = rs.getInt("mrc");

                MyPageVO vo = new MyPageVO();
                vo.setPfImg(pfImg);
                vo.setGradeIconUrl(gradeIconUrl);
                vo.setRegDate(regDate);
                vo.setNickname(nickname);
                vo.setEmail(email);
                vo.setJob(job);
                vo.setYear(year);
                vo.setMyPostCount(myPostCount);
                vo.setMyReplyCount(myReplyCount);
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

    // ❌(마이페이지) 회원 기술 스택
    public List<TechStackVO> getMemberTechStackByNum(int memberNum) {
        List<TechStackVO> list = new ArrayList<>();

        String sql = "SELECT ts.STACK_ICON_URL" +
                " FROM MEMBER_TS_TB mts JOIN TECH_STACK_TB ts" +
                " ON ts.STACK_NUM_PK = mts.STACK_NUM_FK" +
                " WHERE MEMBER_NUM_FK = ?";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String stackIconUrl = rs.getString("STACK_ICON_URL");

                TechStackVO vo = new TechStackVO();
                vo.setStackIconUrl(stackIconUrl);
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


}
