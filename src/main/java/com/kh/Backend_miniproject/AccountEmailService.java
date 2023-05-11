//package com.kh.Backend_miniproject;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.MailException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//import javax.mail.Message;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.util.Random;
//
//@RequiredArgsConstructor
//// 해당 클래스의 생성자를 자동으로 생성
//@Service
//// 해당 클래스가 서비스 역할을 하는 빈으로 등록되도록 표시
//public class AccountEmailService {
//    private final JavaMailSender emailSender;
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    // 로거: 로깅기능 제공(서비스에서 로그를 남길 수 있도록 해줌)
//
//    // 인증키
//    private final String ePw = createKey();
//
//    public String getEPw() {
//        return ePw;
//    }
//
//    @Value("${spring.mail.username}")
//    private String email;
//
//
//
//    private MimeMessage createMessages(String to)throws Exception{
//        logger.info("받는 사용자 이메일 :" + to);
//        logger.info("인증 번호: " + ePw);
//        MimeMessage message = emailSender.createMimeMessage();
//
//        String code = createCode(ePw);
//        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
//        message.setSubject("이메일 인증 코드: " + code); // 제목
//
//        String msg="";
//        msg += "<h1>이메일 주소 확인</h1>";
//        msg += "<p>아래 인증 코드를 회원가입 창에 입력하세요.</p>";
//        msg += code;
//        msg += "<h1>개발러스</h1>";
//
//        message.setText(msg, "utf-8", "html"); // 내용
//        message.setFrom(new InternetAddress(email, "Developers")); // 보내는 사람
//
//        return message;
//    }
//
//    public static String createKey() {
//        StringBuffer key = new StringBuffer();
//        Random rnd = new Random();
//
//        for (int i = 0; i < 6; i++) {
//            key.append((rnd.nextInt(10)));
//        }
//        return key.toString();
//    }
//
//    public String createCode(String ePw) {
//        return ePw;
////        return ePw.substring(0, 3) + "-" + ePw.substring(3, 6);
//
//    }
//
//    // 메일 발송
//    public void sendSimpleMessage(String to) throws Exception {
//        MimeMessage message = createMessages(to);
//        try { // 예외처리
//            emailSender.send(message);
//        } catch (MailException es) {
//            es.printStackTrace();
//            throw new IllegalArgumentException();
//        }
//    }
//
//}
package com.kh.Backend_miniproject;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@RequiredArgsConstructor
// 해당 클래스의 생성자를 자동으로 생성
@Service
// 해당 클래스가 서비스 역할을 하는 빈으로 등록되도록 표시
public class AccountEmailService {
    private final JavaMailSender emailSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 로거: 로깅기능 제공(서비스에서 로그를 남길 수 있도록 해줌)

    // 인증키
    private final String ePw = createKey();

    public String getEPw() {
        return ePw;
    }

    @Value("${spring.mail.username}")
    private String email;

    public void sendEmailWithAuthCode(String to, String authCode) throws Exception {
        MimeMessage message = createEmailMessage(to, authCode);
        emailSender.send(message);
    }

    private MimeMessage createEmailMessage(String to, String authCode) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        String subject = "개발러스 이메일 인증 코드";
        String msg="";
        msg += "<h2>이메일 인증</h2>";
        msg += "<p>아래 인증 코드를 회원가입 창에 입력하세요.</p>";
        msg += "<h1>이메일 인증 코드: " + authCode + "</h1>";
        msg += "<h3>개발러스</h3>";
        String content = "<h1>이메일 인증 코드: " + authCode + "</h1>";

        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
        message.setSubject(subject); // 제목
        message.setText(msg, "utf-8", "html"); // 내용
        message.setFrom(new InternetAddress(email, "개발러스")); // 보내는 사람

        return message;
    }


    public void sendEmailWithTempPwd(String to, String tempPwd) throws Exception {
        MimeMessage message = createTempPwdMessage(to, tempPwd);
        emailSender.send(message);
    }
    private MimeMessage createTempPwdMessage(String to, String tempPwd)throws Exception{
        MimeMessage message = emailSender.createMimeMessage();

        String subject = "개발러스 임시 비밀번호";
        String msg="";
        msg += "<h2>개발러스 임시 비밀번호 입니다.</h2>";
        msg += "<p>보안을 위해 로그인 후 비밀번호를 변경해주세요 제발! 부탁! 소원! </p>";
        msg += "<h1>임시비밀번호: " + tempPwd + "</h1>";
        msg += "<h3>개발러스</h3>";
        String content = "<h1>이메일 인증 코드: " + tempPwd + "</h1>";

        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
        message.setSubject(subject); // 제목
        message.setText(msg, "utf-8", "html"); // 내용
        message.setFrom(new InternetAddress(email, "개발러스")); // 보내는 사람


        return message;
    }



    private MimeMessage createMessages(String to)throws Exception{
        logger.info("받는 사용자 이메일 :" + to);
        logger.info("인증 번호: " + ePw);
        MimeMessage message = emailSender.createMimeMessage();

        String code = createCode(ePw);
        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
        message.setSubject("이메일 인증 코드: " + code); // 제목

        String msg="";
        msg += "<h1>이메일 주소 확인</h1>";
        msg += "<p>아래 인증 코드를 회원가입 창에 입력하세요.</p>";
        msg += code;
        msg += "<h1>개발러스</h1>";

        message.setText(msg, "utf-8", "html"); // 내용
        message.setFrom(new InternetAddress(email, "개발러스")); // 보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public String createCode(String ePw) {
        return ePw;
//        return ePw.substring(0, 3) + "-" + ePw.substring(3, 6);

    }

    // 메일 발송
    public void sendSimpleMessage(String to) throws Exception {
        MimeMessage message = createMessages(to);
        try { // 예외처리
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

}
