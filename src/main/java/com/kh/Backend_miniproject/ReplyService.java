package com.kh.Backend_miniproject;

import com.kh.Backend_miniproject.dao.AlarmDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {
    @Autowired
    private AlarmDAO alarmDAO;
    @Autowired
    private EmailService emailService;

    public void writeReply(int postNum, int memberNum, String content) {
        alarmDAO.writeReplyTest(postNum, memberNum, content);

        String userEmail = alarmDAO.getAuthorEmailByPostNum(postNum);
        String subject = "[개발러스] 댓글 파티가 시작됐어요! ";
        String text = "안녕하세요!\n\n" +
                "작성하신 게시물에 댓글이 달렸어요! \n" +
                "지금 바로 확인하러 가보세요! \n\n" +
                "개발러스에서 즐거운 시간 보내시기 바랍니다. ";
        emailService.sendNotification(userEmail, subject, text);
    }
}
