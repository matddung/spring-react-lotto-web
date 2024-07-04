import React, { useEffect, useState } from 'react';
import './Login.css';
import { NAVER_AUTH_URL, KAKAO_AUTH_URL, GOOGLE_AUTH_URL, ACCESS_TOKEN, REFRESH_TOKEN } from '../../constants';
import { login } from '../../util/UserAPIUtils';
import { Link, useLocation, useNavigate, Navigate } from 'react-router-dom';
import googleLogo from '../../img/google-logo.png';
import kakaoLogo from '../../img/kakao-logo.png';
import naverLogo from '../../img/naver-logo.png';
import { toast } from 'react-toastify';
import FindPasswordForm from '../findPassword/FindPasswordForm';

const Login = ({ authenticated, onLoginSuccess, onLoginFailure }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const [showFindPasswordForm, setShowFindPasswordForm] = useState(false);

    useEffect(() => {
        if (location.state && location.state.error) {
            setTimeout(() => {
                toast.error(location.state.error, {
                    autoClose: 5000
                });
                navigate(location.pathname, { replace: true, state: {} });
            }, 100);
        }
    }, [location, navigate]);

    if (authenticated) {
        return <Navigate to="/" />;
    }

    const toggleFindPasswordForm = () => {
        setShowFindPasswordForm(!showFindPasswordForm);
    };

    return (
        <div className="login-container">
            <div className="login-content">
                <h1 className="login-title">로그인</h1>
                <SocialLogin />
                <div className="or-separator">
                    <span className="or-text">OR</span>
                </div>
                <LoginForm onLoginSuccess={onLoginSuccess} onLoginFailure={onLoginFailure} />
                <span className="signup-link">아이디가 없으신가요? <Link to="/signup">회원가입<br/></Link></span>
                <span className="find-password-link"><button type="button" className="link-button" onClick={toggleFindPasswordForm}>비밀번호를 잊으셨나요?</button></span>
            </div>
            {showFindPasswordForm && (
                <div className="password-modal">
                    <FindPasswordForm onClose={toggleFindPasswordForm} />
                </div>
            )}
        </div>
    );
};

const SocialLogin = () => {
    return (
        <div className="social-login">
            <a className="btn btn-block social-btn google" href={GOOGLE_AUTH_URL}>
                <img src={googleLogo} alt="Google" /> with Google</a>
            <a className="btn btn-block social-btn kakao" href={KAKAO_AUTH_URL}>
                <img src={kakaoLogo} alt="Kakao" /> with Kakao</a>
            <a className="btn btn-block social-btn naver" href={NAVER_AUTH_URL}>
                <img src={naverLogo} alt="Naver" /> with Naver</a>
        </div>
    );
};

const LoginForm = ({ onLoginSuccess, onLoginFailure }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        if (name === 'email') setEmail(value);
        if (name === 'password') setPassword(value);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
    
        const loginRequest = { email, password };
    
        try {
            const response = await login(loginRequest);
            localStorage.setItem(ACCESS_TOKEN, response.accessToken);
            localStorage.setItem(REFRESH_TOKEN, response.refreshToken);
            onLoginSuccess(); // 로그인 성공 처리
            navigate("/");
        } catch (error) {
            onLoginFailure(error); // 로그인 실패 처리
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div className="form-item">
                <input type="email" name="email"
                    className="form-control" placeholder="이메일"
                    value={email} onChange={handleInputChange} required />
            </div>
            <div className="form-item">
                <input type="password" name="password"
                    className="form-control" placeholder="비밀번호"
                    value={password} onChange={handleInputChange} required />
            </div>
            <div className="form-item">
                <button type="submit" className="btn btn-block btn-primary">로그인</button>
            </div>
        </form>
    );
};

export default Login;