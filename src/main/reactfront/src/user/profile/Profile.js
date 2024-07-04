import React, { useState } from 'react';
import './Profile.css';
import PasswordChangeForm from '../modify/password/PasswordChangeForm';
import NicknameChangeForm from '../modify/nickname/NicknameChangeForm';
import { deleteAccount } from '../../util/UserAPIUtils';
import { getMyQuestions } from '../../util/QuestionAPIUtils';
import { toast } from 'react-toastify';
import MyQuestions from '../question/MyQuestions';
import LoadingIndicator from '../../common/LoadingIndicator';
import { useNavigate } from 'react-router-dom';

const Profile = ({ currentUser, updateCurrentUser, onLogout }) => {
    const [showPasswordChangeForm, setShowPasswordChangeForm] = useState(false);
    const [showNicknameChangeForm, setShowNicknameChangeForm] = useState(false);
    const [showMyQuestionsModal, setShowMyQuestionsModal] = useState(false);
    const [myQuestions, setMyQuestions] = useState([]);
    const [isDeleting, setIsDeleting] = useState(false);
    const navigate = useNavigate();

    const togglePasswordChangeForm = () => {
        setShowPasswordChangeForm(prev => !prev);
    }

    const toggleNicknameChangeForm = () => {
        setShowNicknameChangeForm(prev => !prev);
    }

    const toggleMyQuestionsModal = async () => {
        if (!showMyQuestionsModal) {
            try {
                const questions = await getMyQuestions();
                setMyQuestions(questions);
            } catch (error) {
                toast.error('내 질문 목록을 불러오는데 실패했습니다.');
            }
        }
        setShowMyQuestionsModal(prev => !prev);
    }

    const handleNicknameChangeSuccess = (newNickname) => {
        const updatedUser = {
            ...currentUser,
            information: {
                ...currentUser.information,
                nickname: newNickname
            }
        };

        if (updateCurrentUser) {
            updateCurrentUser(updatedUser);
        }

        toast.success('닉네임이 성공적으로 변경되었습니다.');
        navigate('/profile');
    }

    const handleAccountDeletion = async () => {
        setIsDeleting(true);
        try {
            await deleteAccount();
            toast.success('회원 탈퇴가 성공적으로 완료되었습니다.');
            onLogout();
        } catch (error) {
            console.error('Error deleting account:', error);
            toast.error((error && error.message) || '회원 탈퇴에 실패하였습니다.');
            setIsDeleting(false);
        }
    }

    if (isDeleting) {
        return <LoadingIndicator />;
    }

    if (!currentUser) {
        return null; // LoadingIndicator 대신 null 반환
    }

    return (
        <div className="profile-container">
            <div className="container">
                <div className="profile-info">
                    <div className="profile-details">
                        <p><strong>닉네임:</strong> {currentUser.information.nickname}</p>
                        <p><strong>이메일:</strong> {currentUser.information.email}</p>
                    </div>
                    <div className="profile-actions">
                        {currentUser.information.provider === 'local' && (
                            <button className="btn btn-primary" onClick={togglePasswordChangeForm}>비밀번호 수정</button>
                        )}
                        <button className="btn btn-secondary" onClick={toggleNicknameChangeForm}>닉네임 수정</button>
                        <button className="btn btn-secondary" onClick={toggleMyQuestionsModal}>내 질문 목록</button>
                        <button className="btn btn-danger" onClick={handleAccountDeletion}>회원탈퇴</button>
                    </div>
                </div>
            </div>
            {showPasswordChangeForm && (
                <PasswordChangeForm onClose={togglePasswordChangeForm} />
            )}
            {showNicknameChangeForm && (
                <NicknameChangeForm onClose={toggleNicknameChangeForm} onNicknameChangeSuccess={handleNicknameChangeSuccess} />
            )}
            {showMyQuestionsModal && (
                <MyQuestions questions={myQuestions} onClose={toggleMyQuestionsModal} currentUser={currentUser} />
            )}
        </div>
    );
}

export default Profile;