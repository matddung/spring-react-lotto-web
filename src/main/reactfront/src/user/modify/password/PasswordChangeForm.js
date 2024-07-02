import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import { changePassword } from '../../../util/UserAPIUtils';
import './PasswordChangeForm.css';

const PasswordChangeForm = ({ onClose }) => {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [reNewPassword, setReNewPassword] = useState('');
    const modalRef = useRef();

    const handleSubmit = (event) => {
        event.preventDefault();

        if (newPassword !== reNewPassword) {
            toast.error('신규 등록 비밀번호 값이 일치하지 않습니다.');
            return;
        }

        const passwordChangeRequest = { oldPassword, newPassword, reNewPassword };

        changePassword(passwordChangeRequest)
            .then(response => {
                toast.success('비밀번호가 성공적으로 변경되었습니다.');
                onClose();
            }).catch(error => {
                toast.error((error && error.message) || '비밀번호 변경에 실패하였습니다.');
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
        <div className="password-change-form-overlay">
            <div className="password-change-form-container" ref={modalRef}>
                <form onSubmit={handleSubmit} className="password-change-form">
                    <h2>비밀번호 수정</h2>
                    <div className="form-item">
                        <input type="password" name="oldPassword"
                            className="form-control" placeholder="기존 비밀번호"
                            value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} required />
                    </div>
                    <div className="form-item">
                        <input type="password" name="newPassword"
                            className="form-control" placeholder="신규 비밀번호"
                            value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                    </div>
                    <div className="form-item">
                        <input type="password" name="reNewPassword"
                            className="form-control" placeholder="신규 비밀번호 확인"
                            value={reNewPassword} onChange={(e) => setReNewPassword(e.target.value)} required />
                    </div>
                    <div className="form-buttons">
                        <button type="button" className="btn btn-secondary" onClick={onClose}>취소</button>
                        <button type="submit" className="btn">비밀번호 변경</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default PasswordChangeForm;
