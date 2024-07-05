import React, { useState } from 'react';
import './SignUp.css';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { NAVER_AUTH_URL, KAKAO_AUTH_URL, GOOGLE_AUTH_URL } from '../../constants';
import { signup } from '../../util/UserAPIUtils';
import googleLogo from '../../img/google-logo.png';
import kakaoLogo from '../../img/kakao-logo.png';
import naverLogo from '../../img/naver-logo.png';

import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const SignUp = ({ authenticated }) => {
    if (authenticated) {
        return <Navigate to="/" />;
    }

    return (
        <div className="signup-container">
            <div className="signup-content">
                <h1 className="signup-title">회원가입</h1>
                <SocialSignUp />
                <div className="or-separator">
                    <span className="or-text">OR</span>
                </div>
                <SignUpForm />
                <span className="login-link">이미 아이디가 있으신가요? <Link to="/login">로그인</Link></span>
            </div>
        </div>
    );
};

const SocialSignUp = () => {
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

const SignUpForm = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const validateNickname = (nickname) => {
        const nicknameRegex = /^[A-Za-z0-9가-힣]{2,8}$/; // 특수문자 제외, 2자 이상 8자 이하
        return nicknameRegex.test(nickname);
    };

    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        return passwordRegex.test(password);
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        switch (name) {
            case 'name':
                setName(value);
                break;
            case 'email':
                setEmail(value);
                break;
            case 'password':
                setPassword(value);
                break;
            case 'confirmPassword':
                setConfirmPassword(value);
                break;
            default:
                break;
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const newErrors = {};

        if (!validateEmail(email)) {
            newErrors.email = '유효한 이메일 주소를 입력하세요.';
        }

        if (!validateNickname(name)) {
            newErrors.nickname = '닉네임은 2자 이상 8자 이하로 입력하고, 특수 문자를 포함할 수 없습니다.';
        }

        if (!validatePassword(password)) {
            newErrors.password = '비밀번호는 8자 이상이어야 하며, 숫자와 특수 문자를 포함해야 합니다.';
        }

        if (password !== confirmPassword) {
            newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const signUpRequest = { name, email, password };

        signup(signUpRequest)
            .then(response => {
                toast.success(<div>회원가입에 성공하셨습니다.<br />3초후 로그인 페이지로 이동합니다.</div>);
                setTimeout(() => {
                    navigate("/login");
                }, 3000);
            }).catch(error => {
                console.error('Signup error:', error);
                const errorMessage = error.information?.message || '예기치 않은 문제가 발생하였습니다.';
                toast.error(errorMessage);
            });
    };

    return (
        <form onSubmit={handleSubmit}>
            <div className="form-item">
                <input type="email" name="email"
                    className="form-control" placeholder="이메일"
                    value={email} onChange={handleInputChange} required />
                {errors.email && (
                    <div className="error-message">
                        <span className="error-icon">⚠️</span>
                        {errors.email}
                    </div>
                )}
            </div>
            <div className="form-item">
                <input type="text" name="name"
                    className="form-control" placeholder="닉네임"
                    value={name} onChange={handleInputChange} required />
                {errors.nickname && (
                    <div className="error-message">
                        <span className="error-icon">⚠️</span>
                        {errors.nickname}
                    </div>
                )}
            </div>
            <div className="form-item">
                <input type="password" name="password"
                    className="form-control" placeholder="비밀번호"
                    value={password} onChange={handleInputChange} required />
                {errors.password && (
                    <div className="error-message">
                        <span className="error-icon">⚠️</span>
                        {errors.password}
                    </div>
                )}
            </div>
            <div className="form-item">
                <input type="password" name="confirmPassword"
                    className="form-control" placeholder="비밀번호 확인"
                    value={confirmPassword} onChange={handleInputChange} required />
                {errors.confirmPassword && (
                    <div className="error-message">
                        <span className="error-icon">⚠️</span>
                        {errors.confirmPassword}
                    </div>
                )}
            </div>
            <div className="form-item">
                <button type="submit" className="btn btn-block btn-primary">회원가입</button>
            </div>
        </form>
    );
};

export default SignUp;
