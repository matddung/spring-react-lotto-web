import React, { useState, useRef } from 'react';
import { toast } from 'react-toastify';

import { changeNickname } from '../../../util/UserAPIUtils';
import { validateNickname, useOutsideClick } from '../../../common/UtilCollection';
import './NicknameChangeForm.css';

const NicknameChangeForm = ({ onClose, onNicknameChangeSuccess }) => {
    const [newNickname, setNewNickname] = useState('');
    const [errors, setErrors] = useState({});
    const modalRef = useRef();

    const handleSubmit = (event) => {
        event.preventDefault();

        const newErrors = {};

        if (!validateNickname(newNickname)) {
            newErrors.newNickname = '닉네임은 2자 이상 8자 이하로 입력하고, 특수 문자를 포함할 수 없습니다.';
            setErrors(newErrors);
            return;
        }

        const nicknameChangeRequest = { newNickname };

        changeNickname(nicknameChangeRequest)
            .then(response => {
                onNicknameChangeSuccess(newNickname);
                onClose();
            }).catch(error => {
                toast.error((error && error.message) || '중복된 닉네임이거나 사용 불가능한 닉네임입니다.');
            });
    };

    useOutsideClick(modalRef, onClose);

    return (
        <div className="nickname-change-form-overlay">
            <div className="nickname-change-form-container" ref={modalRef}>
                <form onSubmit={handleSubmit} className="nickname-change-form">
                    <h2>닉네임 수정</h2>
                    <div className="form-item">
                        <input type="text" name="newNickname"
                            className="form-control" placeholder="신규 닉네임"
                            value={newNickname} onChange={(e) => setNewNickname(e.target.value)} required />
                        {errors.newNickname && (
                            <div className="error-message">
                                <span className="error-icon">⚠️</span>
                                {errors.newNickname}
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
                        <button type="submit" className="btn">닉네임 변경</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default NicknameChangeForm;