import React, { useState, useRef } from 'react';
import { toast } from 'react-toastify';

import { useOutsideClick } from '../../common/UtilCollection'
import { findPassword } from '../../util/UserAPIUtils';
import './FindPasswordForm.css';

const FindPasswordForm = ({ onClose }) => {
    const [email, setEmail] = useState('');
    const modalRef = useRef();

    const handleSubmit = (event) => {
        event.preventDefault();

        const findPasswordRequest = { email };

        findPassword(findPasswordRequest)
            .then(response => {
                toast.success('비밀번호 찾기 이메일이 발송되었습니다.');
                onClose();
            }).catch(error => {
                toast.error((error && error.message) || '비밀번호 찾기에 실패하였습니다.');
            });
    };

    useOutsideClick(modalRef, onClose);

    return (
        <div className="find-password-form-overlay">
            <div className="find-password-form-container" ref={modalRef}>
                <form onSubmit={handleSubmit} className="find-password-form">
                    <h2>임시 비밀번호</h2>
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
