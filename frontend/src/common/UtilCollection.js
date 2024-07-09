import { useEffect } from 'react';

import { NAVER_AUTH_URL, KAKAO_AUTH_URL, GOOGLE_AUTH_URL } from '../constants';
import googleLogo from '../img/google-logo.png';
import kakaoLogo from '../img/kakao-logo.png';
import naverLogo from '../img/naver-logo.png';

export const formatDate = (dateString) => {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('ko-KR', options).replace(',', '');
};

export const SocialLogin = () => {
    return (
        <div className="social-signup">
            <a className="btn btn-block social-btn google" href={GOOGLE_AUTH_URL}>
                <img src={googleLogo} alt="Google" /> with Google</a>
            <a className="btn btn-block social-btn kakao" href={KAKAO_AUTH_URL}>
                <img src={kakaoLogo} alt="Kakao" /> with Kakao</a>
            <a className="btn btn-block social-btn naver" href={NAVER_AUTH_URL}>
                <img src={naverLogo} alt="Naver" /> with Naver</a>
        </div>
    );
};

export const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

export const validateNickname = (nickname) => {
    const nicknameRegex = /^[A-Za-z0-9가-힣]{2,8}$/;
    return nicknameRegex.test(nickname);
};

export const validatePassword = (password) => {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    return passwordRegex.test(password);
};

export const useOutsideClick = (ref, callback) => {
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (ref.current && !ref.current.contains(event.target)) {
                callback();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [ref, callback]);
};