package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.AccountEmailService;
import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.kh.Backend_miniproject.AccountEmailService.createKey;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AccountController {
    public AccountController(AccountEmailService emailService) {
        this.emailService = emailService;
    }

    // 📍 POST : 로그인 - 경미 테스트
    @PostMapping("/login")
    public ResponseEntity<Boolean> loginMember(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String pwd = loginData.get("pwd");
        AccountDAO aDao = new AccountDAO();
        boolean result = aDao.isLoginValid(email, pwd);

        if (result) { // 입력한 email,pwd가 유효한 정보이면 'true'와 Http 상태 코드 '200'을 전달
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else { // 입력한 email, pwd가 유효하지 않은 정보이면 'false'와 Http 상태 코드 '401'을 전달
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    // 경미🔥POST : 회원가입시 생성된 회원번호를 이용해서 기술스택 저장
        // [5.7] ❗️등급&프로필사진 -> 디비에서 기본값 설정 완료
//    @PostMapping("/signup")
//    public ResponseEntity<Boolean> signUp(@RequestBody Map<String, Object> request) {
//        boolean result = false;
//        AccountDAO ado = new AccountDAO();
//
////        int gradeNum = (int) request.get("gradeNum");
//        String email = (String) request.get("email");
//        String pwd = (String) request.get("pwd");
//        String nickname = (String) request.get("nickname");
//        String job = (String) request.get("job");
//        int year = (int) request.get("year");
////        String pfImg = (String) request.get("pfImg");
//
//        result = ado.createMember(email, pwd, nickname, job, year);
//
//        if(result) {
//            List<Map<String, Object>> techStacks = (List<Map<String, Object>>) request.get("techStacks");
//            for (Map<String, Object> stack : techStacks) {
//                MemberTechStackVO svo = new MemberTechStackVO();
//                int stackNum = (int) stack.get("stackNum");
//                svo.setStackNum(stackNum);
//                result = ado.createMemberTechStack(email, svo.getStackNum());
//                if (!result) {
//                    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
//                }
//            }
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }


    @Autowired
    private final AccountEmailService emailService;
    @PostMapping("/signup")
    public ResponseEntity<Boolean> signUp(@RequestBody Map<String, Object> request) {
        boolean result = false;
        AccountDAO ado = new AccountDAO();

        String email = (String) request.get("email");
        String pwd = (String) request.get("pwd");
        String nickname = (String) request.get("nickname");
        String job = (String) request.get("job");
        int year = (int) request.get("year");

        // 인증 코드 생성
        String authKey = createKey();

        // 회원 생성 및 인증 코드 저장
        result = ado.createMember(email, pwd, nickname, job, year, authKey);

        if(result) {
            List<Map<String, Object>> techStacks = (List<Map<String, Object>>) request.get("techStacks");
            for (Map<String, Object> stack : techStacks) {
                MemberTechStackVO svo = new MemberTechStackVO();
                int stackNum = (int) stack.get("stackNum");
                svo.setStackNum(stackNum);
                result = ado.createMemberTechStack(email, svo.getStackNum());
                if (!result) {
                    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
                }
            }
            try {
                // 이메일 발송
                emailService.sendEmailWithAuthCode(email, authKey);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 경미🔥GET :  마이페이지 회원정보 조회
    @GetMapping("/mypage/edit")
    public ResponseEntity<SignUpVO> fetchMyPageMemberInfo(@RequestParam("memberNum") int memberNum) {
        AccountDAO ado = new AccountDAO();
        SignUpVO svo = ado.readMemberInfoByNumber(memberNum);

        if(svo != null) {
            List<MemberTechStackVO> techStacks = ado.getMemberTechStack(memberNum);
            svo.setTechStacks(techStacks);
            return new ResponseEntity<>(svo, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // [5.1 추가] GET🔑 회원가입시 닉네임 중복확인
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        AccountDAO ado = new AccountDAO();
        return new ResponseEntity<>(ado.findMemberByNickname(nickname), HttpStatus.OK);
    }

    // [5.2 추가] GET🔑 전체 기술스택 리스트 호출
    @GetMapping("/techstacks/all")
    public ResponseEntity<List<TechStackVO>> fetchAllTechStacks() {
        AccountDAO adao = new AccountDAO();
        return new ResponseEntity<>(adao.getAllTechStacks(), HttpStatus.OK);
    }

    // [5.3 추가] GET🔑 입력받은 닉네임으로 사용자의 이메일 호출
    @GetMapping("/findaccount/check")
    public ResponseEntity<String> fetchMemberEmailByNickname(@RequestParam String nickname) {
        AccountDAO adao = new AccountDAO();
        return new ResponseEntity<>(adao.getMemberEmailByNickname(nickname), HttpStatus.OK);
    }

    // [5.3 추가] GET🔑 입력받은 닉네임&이메일로 회원 존재 여부 확인
    @GetMapping("/check/ismember")
    public ResponseEntity<Boolean> fetchIsMemberByNicknameAndEmail(@RequestParam String nickname, String email) {
        boolean result = false;
        AccountDAO ado = new AccountDAO();
        result = ado.getMemberByNicknameAndEmail(nickname, email);

        if(result) {
            // 인증 코드 생성 -> 임시 비밀번호
            String tempPwd = createKey();

            // 임시비번 db에 저장
            ado.updateMemberPassword(tempPwd, email);
            try {
                // 이메일 발송
                emailService.sendEmailWithTempPwd(email, tempPwd);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // [5.5 수정] GET🔑(마이페이지) 회원의 최근 게시글 5개 (카테고리, 제목, 본문, 날짜)
    @GetMapping("/mypage/my-5-latest-post")
    public ResponseEntity<List<MyPageVO>> fetchMyLatestPostsByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberLatestPosts(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // [5.5 수정] GET🔑(마이페이지) 회원의 최근 댓글 5개 (카테고리, 댓글내용, 게시글 제목, 날짜)
    @GetMapping("/mypage/my-5-latest-reply")
    public ResponseEntity<List<MyPageVO>> fetchMyLatestRepliesByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberLatestReplies(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // GET🔑(마이페이지 > 내 게시글 관리) 회원의 모든 게시글
    @GetMapping("/mypage/my-all-post")
    public ResponseEntity<List<MyPageVO>> fetchAllMyPosts(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberAllPosts(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // GET🔑(마이페이지 > 내 댓글 관리) 회원의 모든 댓글
    @GetMapping("/mypage/my-all-reply")
    public ResponseEntity<List<MyPageVO>> fetchAllMyReplies(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberAllReplies(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // DELETE✅[5.6] 마이페이지 내 게시글 (다중)삭제
    @DeleteMapping("/mypage/mypost")
    public ResponseEntity<Void> fetchDeleteMyPost(@RequestBody List<Integer> postNums) {
        AccountDAO adao = new AccountDAO();
        adao.deleteMyPost(postNums);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DELETE✅[5.6] 마이페이지 내 댓글 (다중)삭제
    @DeleteMapping("/mypage/myreply")
    public ResponseEntity<Void> fetchDeleteMyReply(@RequestBody List<Integer> replyNums) {
        AccountDAO adao = new AccountDAO();
        adao.deleteMyReply(replyNums);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // POST⚙️ [5.8 추가] UPDATE 마이페이지 > 회원정보 수정
    @PostMapping("/mypage/edit")
    public ResponseEntity<String> fetchUpdateMemberInfo(@RequestBody Map<String, Object> memberInfo) {
        String memberNickname = (String) memberInfo.get("memberNickname");
        String memberPwd = (String) memberInfo.get("memberPwd");
        String memberJob = (String) memberInfo.get("memberJob");
        int memberYear = (int) memberInfo.get("memberYear");
        int memberNum = (int) memberInfo.get("memberNum");
//        List<Integer> selectedStacks = (List<Integer>) memberInfo.get("selectedStacks");

        AccountDAO adao = new AccountDAO();

        // 기술스택 수정
//        adao.updateMemberTechStacks(memberNum, selectedStacks);

        adao.updateMemberInfo(memberNickname, memberPwd, memberJob, memberYear, memberNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // ❗️❗️❗️❗️❗️❗️[5.9 추가]프로필 사진 변경
    @PutMapping("/mypage/myprofile")
    public ResponseEntity<String> fetchUpdateMemberPfImg(@RequestBody Map<String, Object> memberInfo) {
        String memberPfImgUrl = (String) memberInfo.get("memberPfImgUrl");
        int memberNum = (int) memberInfo.get("memberNum");
        AccountDAO adao = new AccountDAO();
        adao.updateMemberPfImg(memberPfImgUrl, memberNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // [5.9 추가] GET🔑 회원가입시 이메일 중복확인
//    @GetMapping("/members")
//    public ResponseEntity<Boolean> fetchCheckEmail(@RequestParam String memberEmail) {
//        AccountDAO ado = new AccountDAO();
//        boolean result = ado.getMemberByEmail(memberEmail);
//
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    // [5.11 추가] GET🔑 이메일로 회원가입 인증키 확인
    @PostMapping("/signup/authkey")
    public ResponseEntity<Boolean> checkMemberEmailAuth(@RequestBody Map<String, String> memberInfo) {
        String memberEmail = (String) memberInfo.get("memberEmail");
        String memberAuthKey = (String) memberInfo.get("memberAuthKey");
        AccountDAO adao = new AccountDAO();
        boolean isAuth = adao.isMemberEmailAuth(memberEmail, memberAuthKey);

        if(isAuth) {
            adao.updateMemberStatus(memberAuthKey);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/signup" )
    public ResponseEntity<String> updateMemberIsActive(@RequestBody Map<String, String> memberInfo) {
        String memberAuthKey = (String) memberInfo.get("memberAuthKey");
        AccountDAO adao = new AccountDAO();
        adao.updateMemberStatus(memberAuthKey);;
        return new ResponseEntity<>("True", HttpStatus.OK);
    }


    // POST⚙️ (마이페이지 > 내 정보 관리) 기술 스택 추가
    @PostMapping("/mypage/add/{memberNum}/{techStackNum}")
    public ResponseEntity<Boolean> fetchAddMemberTechStack(@RequestBody Map<String, Integer> memberInfo) {
        int memberNum = memberInfo.get("memberNum");
        int techStackNum = memberInfo.get("techStackNum");

        AccountDAO adao = new AccountDAO();
        boolean isTrue = adao.addMemberTechStack(memberNum, techStackNum);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }

    // DELETE⚙️ (마이페이지 > 내 정보 관리) 기술 스택 삭제
    @DeleteMapping("/mypage/edit/{memberNum}/{memberTechStackNum}")
    public ResponseEntity<String> fetchDeleteMemberTechStack(@RequestBody Map<String, Object> memberInfo) {
        int memberNum = (int) memberInfo.get("memberNum");
        int memberTechStackNum = (int)memberInfo.get("memberTechStackNum");

        AccountDAO adao = new AccountDAO();
        adao.deteleMemberTechStack(memberNum, memberTechStackNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // [5.10] 사용 PUT🔑
    // PUT ⚙️ 회원 탈퇴 : isWithdrawn 변경
     @PutMapping("/members/is-withdrawn{memberNum}")
        public ResponseEntity<String> fetchUpdateMemberIsWithdrawn(@RequestBody Map<String, Object> memberInfo) {
            String memberIsWithdrawn = "Y";
            int memberNum = (int) memberInfo.get("memberNum");

            AccountDAO adao = new AccountDAO();
            adao.updateMemberIsWithdrawn(memberIsWithdrawn, memberNum);
            return new ResponseEntity<>("True", HttpStatus.OK);
        }


    // [5.5] 사용 GET🔑
    // ✅ 마이페이지: 회원정보 조회 (등급아이콘, 총 게시글 수, 총 댓글 수)
    @GetMapping("/mypage/myprofile")
    public ResponseEntity<List<MyPageVO>> fetchMemberInfoByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberInfoByNum(memberNum);
        if (!list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // [5.5] 사용 GET🔑
    // ✅ 마이페이지: 회원 기술 스택
    @GetMapping("/mypage/mytechstacks")
    public ResponseEntity<List<TechStackVO>> fetchMemberTechStackByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<TechStackVO> list = dao.getMemberTechStackByNum(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }





}

