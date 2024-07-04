import React, { useState, useEffect, useRef } from 'react';
import { toast } from 'react-toastify';
import { changeNickname } from '../../../util/UserAPIUtils';
import './NicknameChangeForm.css';

const NicknameChangeForm = ({ onClose, onNicknameChangeSuccess }) => {
    const [newNickname, setNewNickname] = useState('');
    const modalRef = useRef();

    const handleSubmit = (event) => {
        event.preventDefault();

        const nicknameChangeRequest = { newNickname };

        changeNickname(nicknameChangeRequest)
            .then(response => {
                onNicknameChangeSuccess(newNickname);
                onClose();
            }).catch(error => {
                toast.error((error && error.message) || '중복된 닉네임이거나 사용 불가능한 닉네임입니다.');
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
        <div className="nickname-change-form-overlay">
            <div className="nickname-change-form-container" ref={modalRef}>
                <form onSubmit={handleSubmit} className="nickname-change-form">
                    <h2>닉네임 수정</h2>
                    <div className="form-item">
                        <input type="text" name="newNickname"
                            className="form-control" placeholder="신규 닉네임"
                            value={newNickname} onChange={(e) => setNewNickname(e.target.value)} required />
                    </div>
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
