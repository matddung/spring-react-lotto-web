import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import './AppHeader.css';
import { toast } from 'react-toastify';

const AppHeader = ({ authenticated, currentUser, onLogout }) => {
    return (
        <header className="app-header">
            <div className="container">
                <div className="app-branding">
                    <Link to="/" className="app-title">로또 번호 추천</Link>
                </div>
                <div className="app-options">
                    <nav className="app-nav">
                        {authenticated ? (
                            <ul>
                                <li>
                                    <NavLink to="/question-service">고객센터</NavLink>
                                </li>
                                {currentUser && currentUser.information.role === 'ADMIN' ? (
                                    <>
                                        <li>
                                            <NavLink to="/admin">관리자페이지</NavLink>
                                        </li>
                                    </>
                                ) : (
                                    <li>
                                        <NavLink to="/profile">내 정보</NavLink>
                                    </li>
                                )}
                                <li>
                                    <a href="/" onClick={(e) => {
                                        e.preventDefault();
                                        onLogout();
                                        toast.success('로그아웃 되었습니다.');
                                    }}>로그아웃</a>
                                </li>
                            </ul>
                        ) : (
                            <ul>
                                <li>
                                    <NavLink to="/login">로그인</NavLink>
                                </li>
                                <li>
                                    <NavLink to="/signup">회원가입</NavLink>
                                </li>
                            </ul>
                        )}
                    </nav>
                </div>
            </div>
        </header>
    );
};

export default AppHeader;