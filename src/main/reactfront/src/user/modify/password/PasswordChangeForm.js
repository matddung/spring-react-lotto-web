import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import { changePassword } from '../../../util/UserAPIUtils';
import './PasswordChangeForm.css';

const PasswordChangeForm = ({ onClose }) => {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [reNewPassword, setReNewPassword] = useState('');
    const [errors, setErrors] = useState({});
    const modalRef = useRef();

    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        return passwordRegex.test(password);
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const newErrors = {};

        if (!validatePassword(newPassword)) {
            newErrors.newPassword = '비밀번호는 8자 이상이어야 하며, 숫자와 특수 문자를 포함해야 합니다.';
        }

        if (newPassword !== reNewPassword) {
            newErrors.reNewPassword = '신규 등록 비밀번호 값이 일치하지 않습니다.';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const passwordChangeRequest = { oldPassword, newPassword, reNewPassword };

        changePassword(passwordChangeRequest)
            .then(response => {
                toast.success('비밀번호가 성공적으로 변경되었습니다.');
                onClose();
            }).catch(error => {
                setErrors({ submit: (error && error.message) || '비밀번호 변경에 실패하였습니다.' });
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
                        {errors.newPassword && (
                            <div className="error-message">
                                <span className="error-icon">⚠️</span>
                                {errors.newPassword}
                            </div>
                        )}
                    </div>
                    <div className="form-item">
                        <input type="password" name="reNewPassword"
                            className="form-control" placeholder="신규 비밀번호 확인"
                            value={reNewPassword} onChange={(e) => setReNewPassword(e.target.value)} required />
                        {errors.reNewPassword && (
                            <div className="error-message">
                                <span className="error-icon">⚠️</span>
                                {errors.reNewPassword}
                            </div>
                        )}
                    </div>
                    {errors.submit && (
                        <div className="error-message">
                            <span className="error-icon">⚠️</span>
                            {errors.submit}
                        </div>
                    )}
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