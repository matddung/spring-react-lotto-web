import React, { Component } from 'react';
import './Profile.css';
import PasswordChangeForm from '../modify/password/PasswordChangeForm';
import NicknameChangeForm from '../modify/nickname/NicknameChangeForm';
import { deleteAccount } from '../../util/UserAPIUtils';
import { getMyQuestions } from '../../util/QuestionAPIUtils';
import { toast } from 'react-toastify';
import MyQuestions from '../question/MyQuestions';

class Profile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            showPasswordChangeForm: false,
            showNicknameChangeForm: false,
            showMyQuestionsModal: false,
            myQuestions: [],
            currentUser: props.currentUser
        };
    }

    togglePasswordChangeForm = () => {
        this.setState({ showPasswordChangeForm: !this.state.showPasswordChangeForm });
    }

    toggleNicknameChangeForm = () => {
        this.setState({ showNicknameChangeForm: !this.state.showNicknameChangeForm });
    }

    toggleMyQuestionsModal = async () => {
        if (!this.state.showMyQuestionsModal) {
            try {
                const questions = await getMyQuestions();
                this.setState({ myQuestions: questions });
            } catch (error) {
                toast.error('내 질문 목록을 불러오는데 실패했습니다.');
            }
        }
        this.setState({ showMyQuestionsModal: !this.state.showMyQuestionsModal });
    }

    handleNicknameChangeSuccess = (newNickname) => {
        this.setState(prevState => ({
            showNicknameChangeForm: false,
            currentUser: {
                ...prevState.currentUser,
                information: {
                    ...prevState.currentUser.information,
                    nickname: newNickname
                }
            }
        }));
    }

    handleAccountDeletion = () => {
        deleteAccount()
            .then(response => {
                toast.success('회원 탈퇴가 성공적으로 완료되었습니다.');
                this.props.onLogout();
            }).catch(error => {
                toast.error((error && error.message) || '회원 탈퇴에 실패하였습니다.');
            });
    }

    render() {
        const { currentUser, myQuestions, showMyQuestionsModal } = this.state;

        if (!currentUser) {
            return <div className="profile-container">Loading...</div>;
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
                            <button className="btn btn-primary" onClick={this.togglePasswordChangeForm}>비밀번호 수정</button>
                            <button className="btn btn-secondary" onClick={this.toggleNicknameChangeForm}>닉네임 수정</button>
                            <button className="btn btn-secondary" onClick={this.toggleMyQuestionsModal}>내 질문 목록</button>
                            <button className="btn btn-danger" onClick={this.handleAccountDeletion}>회원탈퇴</button>
                        </div>
                    </div>
                </div>
                {this.state.showPasswordChangeForm && (
                    <PasswordChangeForm onClose={this.togglePasswordChangeForm} />
                )}
                {this.state.showNicknameChangeForm && (
                    <NicknameChangeForm onClose={this.toggleNicknameChangeForm} onNicknameChangeSuccess={this.handleNicknameChangeSuccess} />
                )}
                {showMyQuestionsModal && (
                    <MyQuestions questions={myQuestions} onClose={this.toggleMyQuestionsModal} currentUser={currentUser} />
                )}
            </div>
        );
    }
}

export default Profile;
