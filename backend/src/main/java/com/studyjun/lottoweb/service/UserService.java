package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.config.Provider;
import com.studyjun.lottoweb.dto.TokenDto;
import com.studyjun.lottoweb.dto.request.*;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.AuthResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.dto.response.UserInfoResponse;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.Token;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.TokenRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.util.DefaultAssert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomTokenProviderService customTokenProviderService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final QuestionRepository questionRepository;

    public UserInfoResponse getCurrentUser(UserPrincipal userPrincipal) {
        User user = getUser(userPrincipal.getId());
        return UserInfoResponse.from(user);
    }

    public Message delete(UserPrincipal userPrincipal) {
        User user = getUser(userPrincipal.getId());

        Optional<Token> token = tokenRepository.findByUserEmail(user.getEmail());
        DefaultAssert.isTrue(token.isPresent(), "토큰이 유효하지 않습니다.");

        List<Question> questions = questionRepository.findByAuthorId(user.getId());
        questionRepository.deleteAll(questions);

        userRepository.delete(user);
        tokenRepository.delete(token.get());

        return Message.builder().message("회원 탈퇴가 성공하셨습니다.").build();
    }

    public Message passwordModify(UserPrincipal userPrincipal, ChangePasswordRequest passwordChangeRequest) {
        User user = getUser(userPrincipal.getId());

        boolean passwordCheck = passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword());
        DefaultAssert.isTrue(passwordCheck, "잘못된 비밀번호 입니다.");

        boolean newPasswordCheck = passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getReNewPassword());
        DefaultAssert.isTrue(newPasswordCheck, "신규 등록 비밀번호 값이 일치하지 않습니다.");

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

        userRepository.save(user);

        return Message.builder().message("비밀번호 변경에 성공하였습니다.").build();
    }

    public Message nicknameModify(UserPrincipal userPrincipal, ChangeNicknameRequest nicknameRequest) {
        User user = getUser(userPrincipal.getId());

        boolean nicknameCheck = userRepository.existsByNickname(nicknameRequest.getNewNickname());
        DefaultAssert.isTrue(!nicknameCheck, "중복된 닉네임입니다.");

        user.updateNickname(nicknameRequest.getNewNickname());

        userRepository.save(user);

        return Message.builder().message("닉네임 변경에 성공하였습니다.").build();
    }

    public AuthResponse signIn(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail(),
                        signInRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = customTokenProviderService.createToken(authentication);

        Token token = Token.builder()
                .refreshToken(tokenDto.getRefreshToken())
                .userEmail(tokenDto.getUserEmail())
                .build();
        tokenRepository.save(token);

        return AuthResponse.builder().accessToken(tokenDto.getAccessToken()).refreshToken(token.getRefreshToken()).build();
    }

    public Message signUp(SignUpRequest signUpRequest) {
        DefaultAssert.isTrue(!userRepository.existsByEmail(signUpRequest.getEmail()), "해당 이메일이 이미 존재합니다.");

        DefaultAssert.isTrue(!userRepository.existsByNickname(signUpRequest.getName()), "해당 닉네임이 이미 존재합니다.");

        User user = User.builder()
                .nickname(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(Provider.local)
                .providerId("local")
                .role("USER")
                .build();

        userRepository.save(user);

        return Message.builder().message("회원가입에 성공하였습니다.").build();
    }

    public AuthResponse refresh(RefreshTokenRequest tokenRefreshRequest) {
        valid(tokenRefreshRequest.getRefreshToken());

        Optional<Token> token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken());
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        TokenDto tokenDto;

        Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
        if (expirationTime > 0) {
            tokenDto = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        } else {
            tokenDto = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.get().updateRefreshToken(tokenDto.getRefreshToken());
        tokenRepository.save(updateToken);

        AuthResponse authResponse = AuthResponse.builder().accessToken(tokenDto.getAccessToken()).refreshToken(updateToken.getRefreshToken()).build();

        log.info("authResponse : {}", authResponse);

        return authResponse;
    }

    public Message logout(RefreshTokenRequest tokenRefreshRequest) {
        valid(tokenRefreshRequest.getRefreshToken());

        Optional<Token> token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken());
        tokenRepository.delete(token.get());

        return Message.builder().message("로그아웃 하였습니다.").build();
    }

    private void valid(String refreshToken) {
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());
        DefaultAssert.isTrue(token.get().getUserEmail().equals(authentication.getName()), "사용자 인증에 실패하였습니다.");
    }

    private User getUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");
        return userOptional.get();
    }
}