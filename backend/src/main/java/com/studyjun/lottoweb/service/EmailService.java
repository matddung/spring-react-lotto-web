package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.FindPasswordRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.util.DefaultAssert;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private static final String ADMIN_ADDRESS = "matddung76@naver.com";

    @Async
    public ResponseEntity<?> sendTempPasswordMail(FindPasswordRequest findPasswordRequest) throws UnsupportedEncodingException, MessagingException {
        DefaultAssert.isTrue(userRepository.existsByEmail(findPasswordRequest.getEmail()), "해당 이메일이 존재하지 않습니다.");
        String tempPassword = getTempString();
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(jakarta.mail.Message.RecipientType.TO, findPasswordRequest.getEmail());
        message.setSubject("[로또 번호 추천] 임시 비밀번호 안내");
        String text = "임시 비밀번호 : " + tempPassword + " 입니다.";
        message.setText(text, "utf-8");
        message.setFrom(new InternetAddress(ADMIN_ADDRESS, findPasswordRequest.getEmail()));
        updatePassword(tempPassword, findPasswordRequest.getEmail());
        mailSender.send(message);
        return ResponseEntity.ok(ApiResponse.success(Message.builder().message("임시 비밀번호를 이메일로 발송했습니다.").build()));
    }

    public String getTempString() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String str = "";
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    public void updatePassword(String str, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");
        User user = userOptional.get();
        user.setPassword(encoder.encode(str));
        userRepository.save(user);
    }
}
