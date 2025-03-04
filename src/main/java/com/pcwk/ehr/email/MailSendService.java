package com.pcwk.ehr.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.pcwk.ehr.RedisUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class MailSendService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisUtil redisUtil;
    private int authNumber;

    // 추가된 메서드: 서버 로그를 통해 예외 원인을 찾기 위해 사용
    public String joinEmail(String email) {
        try {
            makeRandomNumber();
            String setFrom = "1026rjsdnd@naver.com"; // 발신자 이메일 주소와 네이버 계정 일치
            String toMail = email;
            String title = "회원 가입 인증 이메일 입니다.";
            String content = "저희 사이트를 방문해주셔서 감사합니다." + "<br><br>" + "인증 번호는 " + authNumber + "입니다.";
            mailSend(setFrom, toMail, title, content);
            return Integer.toString(authNumber);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 예외 출력
            return "Error: " + e.getMessage(); // 예외 메시지 반환
        }
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisUtil.setDataExpire(Integer.toString(authNumber), toMail, 60 * 5L);
    }

    public boolean CheckAuthNum(String email, String authNum) {
        if (redisUtil.getData(authNum) == null) {
            return false;
        } else if (redisUtil.getData(authNum).equals(email)) {
            return true;
        } else {
            return false;
        }
    }

    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }
}
