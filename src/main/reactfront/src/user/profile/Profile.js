import React, { Component } from 'react';
import './Profile.css';
import PasswordChangeForm from '../modify/password/PasswordChangeForm';
import NicknameChangeForm from '../modify/nickname/NicknameChangeForm';
import { deleteAccount } from '../../util/UserAPIUtils';
import { getMyQuestions } from '../../util/QuestionAPIUtils';
import { toast } from 'react-toastify';
import MyQuestions from '../question/MyQuestions';
import LoadingIndicator from '../../common/LoadingIndicator';

class Profile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            showPasswordChangeForm: false,
            showNicknameChangeForm: false,
            showMyQuestionsModal: false,
            myQuestions: [],
            isDeleting: false,
        };
    }

    togglePasswordChangeForm = () => {
        this.setState(prevState => ({ showPasswordChangeForm: !prevState.showPasswordChangeForm }));
    }

    toggleNicknameChangeForm = () => {
        this.setState(prevState => ({ showNicknameChangeForm: !prevState.showNicknameChangeForm }));
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
        this.setState(prevState => ({ showMyQuestionsModal: !prevState.showMyQuestionsModal }));
    }

    handleNicknameChangeSuccess = (newNickname) => {
        const { updateCurrentUser } = this.props;
        const updatedUser = {
            ...this.props.currentUser,
            information: {
                ...this.props.currentUser.information,
                nickname: newNickname
            }
        };

        this.setState({
            currentUser: updatedUser
        }, () => {
            if (updateCurrentUser) {
                updateCurrentUser(updatedUser);
            }

            this.setState({ showNicknameChangeForm: false }, () => {
                toast.success('닉네임이 성공적으로 변경되었습니다.');
            });
        });
    }

    handleAccountDeletion = async () => {
        this.setState({ isDeleting: true });
        try {
            await deleteAccount();
            toast.success('회원 탈퇴가 성공적으로 완료되었습니다.');
            this.props.onLogout();
        } catch (error) {
            console.error('Error deleting account:', error);
            toast.error((error && error.message) || '회원 탈퇴에 실패하였습니다.');
            this.setState({ isDeleting: false });
        }
    }

    render() {
        const { myQuestions, showMyQuestionsModal, isDeleting } = this.state;
        const { currentUser } = this.props;

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