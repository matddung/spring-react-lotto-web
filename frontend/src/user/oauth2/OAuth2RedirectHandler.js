import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../../constants';

const OAuth2RedirectHandler = ({ onLoginSuccess, onLoginFailure }) => {
    const navigate = useNavigate();

    const getUrlParameter = (name) => {
        name = name.replace(/[\\[]/, '\\[').replace(/[\]]/, '\\]');
        const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        const results = regex.exec(window.location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    useEffect(() => {
        const token = getUrlParameter('token');
        const error = getUrlParameter('error');

        if (error) {
            onLoginFailure(error);
            navigate('/login', { state: { error } });
            return;
        }

        if (token) {
            localStorage.setItem(ACCESS_TOKEN, token);
            localStorage.setItem(REFRESH_TOKEN, null);
            onLoginSuccess();
            navigate('/');
            return;
        }

        onLoginFailure('로그인 정보가 존재하지 않습니다.');
        navigate('/login', { state: { error: '로그인 정보가 존재하지 않습니다.' } });
    }, [navigate, onLoginSuccess, onLoginFailure]);

    return null;
};

export default OAuth2RedirectHandler;