import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';

import { formatDate } from '../../../common/UtilCollection';
import { getQuestionDetail, createAnswer } from '../../../util/QuestionAPIUtils';
import { getCurrentUser } from '../../../util/UserAPIUtils';
import QuestionDetail from '../../../question/detail/QuestionDetail';
import { getAllUsers, getUserHistory, getUnansweredQuestions, deleteUser } from '../../../util/AdminAPIUtil';
import LoadingIndicator from '../../../common/LoadingIndicator';
import Pagination from '../../../common/Pagination';
import UserDetail from '../userDetail/UserDetail';
import './AdminPage.css';

const AdminPage = () => {
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [selectedUserQuestions, setSelectedUserQuestions] = useState([]);
    const [unansweredQuestions, setUnansweredQuestions] = useState([]);
    const [selectedQuestion, setSelectedQuestion] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPageUsers, setCurrentPageUsers] = useState(1);
    const [currentPageQuestions, setCurrentPageQuestions] = useState(1);
    const [currentPageUserQuestions, setCurrentPageUserQuestions] = useState(1);
    const itemsPerPage = 10;

    const fetchUsers = async () => {
        try {
            const usersResponse = await getAllUsers();
            setUsers(usersResponse);
        } catch (error) {
            setError(error);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const usersResponse = await getAllUsers();
                setUsers(usersResponse);

                console.log(usersResponse);

                const unansweredQuestionsResponse = await getUnansweredQuestions();
                setUnansweredQuestions(unansweredQuestionsResponse);

                const currentUserResponse = await getCurrentUser();
                setCurrentUser(currentUserResponse);

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const handleUserClick = async (userId) => {
        try {
            const userResponse = await getUserHistory(userId);
            setSelectedUser(userResponse.user);
            setSelectedUserQuestions(userResponse.question);
        } catch (error) {
            console.error('Error fetching user details or questions:', error);
            setError(error);
        }
    };

    const handleDeleteUser = async (userId) => {
        try {
            await deleteUser(userId);
            toast.success("사용자가 삭제되었습니다.");
            await fetchUsers();
        } catch (error) {
            toast.error("사용자 삭제에 실패하였습니다.");
        }
    };

    const handleQuestionClick = async (questionId) => {
        try {
            const questionResponse = await getQuestionDetail(questionId);
            setSelectedQuestion(questionResponse);
        } catch (error) {
            toast.error("질문 상세 정보를 가져오는데 실패했습니다.");
        }
    };

    const updateSelectedUserQuestions = async (userId) => {
        const userResponse = await getUserHistory(userId);
        setSelectedUserQuestions(userResponse.question);
    };

    const handleAnswerSubmit = async (questionId, answerData) => {
        try {
            await createAnswer(questionId, answerData);
            const questionResponse = await getQuestionDetail(questionId);
            setSelectedQuestion(questionResponse);

            if (selectedUser) {
                await updateSelectedUserQuestions(selectedUser.id);
            }

            const unansweredQuestionsResponse = await getUnansweredQuestions();
            setUnansweredQuestions(unansweredQuestionsResponse);
            toast.success("답변이 작성되었습니다.");
        } catch (error) {
            toast.error("답변 작성에 실패하였습니다.");
        }
    };

    const closeUserDetail = () => {
        setSelectedUser(null);
        setSelectedUserQuestions([]);
    };

    const closeQuestionDetail = () => {
        setSelectedQuestion(null);
    };

    const {
        startIndex: startIndexUsers,
        endIndex: endIndexUsers,
        displayedPageNumbers: displayedPageNumbersUsers,
        totalPages: totalPagesUsers,
        handlePageChange: handlePageChangeUsers,
        handlePreviousPages: handlePreviousPagesUsers,
        handleNextPages: handleNextPagesUsers,
        handleFirstPage: handleFirstPageUsers,
        handleLastPage: handleLastPageUsers
    } = Pagination(users.length, itemsPerPage, currentPageUsers, setCurrentPageUsers);

    const {
        startIndex: startIndexQuestions,
        endIndex: endIndexQuestions,
        displayedPageNumbers: displayedPageNumbersQuestions,
        totalPages: totalPagesQuestions,
        handlePageChange: handlePageChangeQuestions,
        handlePreviousPages: handlePreviousPagesQuestions,
        handleNextPages: handleNextPagesQuestions,
        handleFirstPage: handleFirstPageQuestions,
        handleLastPage: handleLastPageQuestions
    } = Pagination(unansweredQuestions.length, itemsPerPage, currentPageQuestions, setCurrentPageQuestions);

    const currentUsers = users.slice(startIndexUsers, endIndexUsers);
    const currentQuestions = unansweredQuestions.slice(startIndexQuestions, endIndexQuestions);

    if (loading) {
        return <LoadingIndicator />;
    }

    if (error) {
        return <div>Error: {error.message}</div>;
    }

    return (
        <div className="admin-page-container">
            <div className="user-list">
                <h2>전체 유저 목록</h2>
                <div className="user-list-header">
                    <span className="user-id">ID</span>
                    <span className="user-name">Nickname</span>
                    <span className="user-email">Email</span>
                    <span className="user-role">Role</span>
                    <span className="user-actions">Actions</span>
                </div>
                <ul>
                    {currentUsers.map(user => (
                        <li key={user.id} className="user-item" onClick={() => handleUserClick(user.id)}>
                            <span className="user-id">{user.id}</span>
                            <span className="user-name">{user.nickname}</span>
                            <span className="user-email">{user.email}</span>
                            <span className="user-role">{user.role}</span>
                            <span className="user-actions">
                                <button className="btn btn-danger" onClick={(e) => { e.stopPropagation(); handleDeleteUser(user.id); }}>Delete</button>
                            </span>
                        </li>
                    ))}
                </ul>
                <div className="pagination">
                    <button onClick={handleFirstPageUsers} disabled={currentPageUsers === 1}>&lt;&lt;</button>
                    <button onClick={handlePreviousPagesUsers} disabled={currentPageUsers === 1}>&lt;</button>
                    {displayedPageNumbersUsers.map(number => (
                        <button key={number} onClick={() => handlePageChangeUsers(number)} className={currentPageUsers === number ? 'active' : ''}>
                            {number}
                        </button>
                    ))}
                    <button onClick={handleNextPagesUsers} disabled={currentPageUsers === totalPagesUsers}>&gt;</button>
                    <button onClick={handleLastPageUsers} disabled={currentPageUsers === totalPagesUsers}>&gt;&gt;</button>
                </div>
            </div>
            {selectedUser && !selectedQuestion && (
                <UserDetail
                    selectedUser={selectedUser}
                    selectedUserQuestions={selectedUserQuestions}
                    currentPageUserQuestions={currentPageUserQuestions}
                    setCurrentPageUserQuestions={setCurrentPageUserQuestions}
                    onClose={closeUserDetail}
                    onQuestionClick={handleQuestionClick}
                    updateSelectedUserQuestions={updateSelectedUserQuestions}
                />
            )}
            {selectedQuestion && (
                <QuestionDetail
                    question={selectedQuestion}
                    currentUser={currentUser}
                    onClose={closeQuestionDetail}
                    onSubmitAnswer={handleAnswerSubmit}
                />
            )}<br />
            <h2>미답변 질문 목록</h2>
            <div className="unanswered-questions">
                <div className="question-list-header">
                    <span className="question-id">ID</span>
                    <span className="question-author">Author</span>
                    <span className="question-subject">Subject</span>
                    <span className="question-date">Create Date</span>
                </div>
                <ul>
                    {currentQuestions.map(question => (
                        <li key={question.id} className="question-item" onClick={() => handleQuestionClick(question.id)}>
                            <span className="question-id">{question.id}</span>
                            <span className="question-author">{question.author.nickname}</span>
                            <span className="question-subject">{question.subject}</span>
                            <span className="question-date">{formatDate(question.createdDate)}</span>
                        </li>
                    ))}
                </ul>
                <div className="pagination">
                    <button onClick={handleFirstPageQuestions} disabled={currentPageQuestions === 1}>&lt;&lt;</button>
                    <button onClick={handlePreviousPagesQuestions} disabled={currentPageQuestions === 1}>&lt;</button>
                    {displayedPageNumbersQuestions.map(number => (
                        <button key={number} onClick={() => handlePageChangeQuestions(number)} className={currentPageQuestions === number ? 'active' : ''}>
                            {number}
                        </button>
                    ))}
                    <button onClick={handleNextPagesQuestions} disabled={currentPageQuestions === totalPagesQuestions}>&gt;</button>
                    <button onClick={handleLastPageQuestions} disabled={currentPageQuestions === totalPagesQuestions}>&gt;&gt;</button>
                </div>
            </div>
        </div>
    );
};

export default AdminPage;