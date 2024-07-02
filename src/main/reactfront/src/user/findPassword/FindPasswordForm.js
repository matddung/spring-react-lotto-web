import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import { findPassword } from '../../util/UserAPIUtils'; // 실제로 비밀번호 찾기 API를 호출해야 합니다.
import './FindPasswordForm.css';

const FindPasswordForm = ({ onClose }) => {
    const [email, setEmail] = useState('');
    const modalRef = useRef();

    const handleSubmit = (event) => {
        event.preventDefault();

        findPassword(email) // 실제로 비밀번호 찾기 API를 호출하는 함수로 대체하세요.
            .then(response => {
                toast.success('비밀번호 찾기 이메일이 발송되었습니다.');
                onClose();
            }).catch(error => {
                toast.error((error && error.message) || '비밀번호 찾기에 실패하였습니다.');
            });
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (modalRef.current && !modalRef.current.contains(event.target)) {
                onClose();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onClose]);

    return (
        <div className="find-password-form-overlay">
            <div className="find-password-form-container" ref={modalRef}>
                <form onSubmit={handleSubmit} className="find-password-form">
                    <h2>비밀번호 찾기</h2>
                    <div className="form-item">
                        <input type="email" name="email"
                            className="form-control" placeholder="이메일"
                            value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <div className="form-buttons">
                        <button type="button" className="btn btn-secondary" onClick={onClose}>취소</button>
                        <button type="submit" className="btn btn-primary">임시 비밀번호 전송</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default FindPasswordForm;
