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
    const navigate = useNavigate();

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        if (name === 'name') setName(value);
        if (name === 'email') setEmail(value);
        if (name === 'password') setPassword(value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();

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
            </div>
            <div className="form-item">
                <input type="text" name="name"
                    className="form-control" placeholder="닉네임"
                    value={name} onChange={handleInputChange} required />
            </div>
            <div className="form-item">
                <input type="password" name="password"
                    className="form-control" placeholder="비밀번호"
                    value={password} onChange={handleInputChange} required />
            </div>
            <div className="form-item">
                <button type="submit" className="btn btn-block btn-primary">회원가입</button>
            </div>
        </form>
    );
};

export default SignUp;