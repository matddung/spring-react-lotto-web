import React, { Component } from 'react';
import { Link, NavLink } from 'react-router-dom';
import './AppHeader.css';

class AppHeader extends Component {
    render() {
        return (
            <header className="app-header">
                <div className="container">
                    <div className="app-branding">
                        <Link to="/" className="app-title">로또 번호 추천</Link>
                    </div>
                    <div className="app-options">
                        <nav className="app-nav">
                            {this.props.authenticated ? (
                                <ul>
                                    <li>
                                        <NavLink to="/question-service">고객센터</NavLink>
                                    </li>
                                    <li>
                                        <NavLink to="/profile">내 정보</NavLink>
                                    </li>
                                    <li>
                                        <a href="/" onClick={(e) => { e.preventDefault(); this.props.onLogout(); }}>로그아웃</a>
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
    }
}

export default AppHeader;