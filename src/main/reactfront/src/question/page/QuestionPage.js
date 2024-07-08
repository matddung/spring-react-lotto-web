import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';

import { getAllQuestions, getQuestionDetail, createQuestion, createAnswer } from '../../util/QuestionAPIUtils';
import { getCurrentUser } from '../../util/UserAPIUtils';
import QuestionDetail from '../detail/QuestionDetail';
import CreateQuestion from '../create/question/CreateQuestion';
import LoadingIndicator from '../../common/LoadingIndicator';
import Pagination from '../../common/Pagination';
import 'react-toastify/dist/ReactToastify.css';
import './QuestionPage.css';

const QuestionService = () => {
    const [questions, setQuestions] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedQuestion, setSelectedQuestion] = useState(null);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;

    useEffect(() => {
        const fetchData = async () => {
            try {
                const questionsResponse = await getAllQuestions();
                console.log("Fetched questions:", questionsResponse);
                setQuestions(questionsResponse);

                const userResponse = await getCurrentUser();
                setCurrentUser(userResponse);

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const {
        startIndex,
        endIndex,
        displayedPageNumbers,
        totalPages,
        handlePageChange,
        handlePreviousPages,
        handleNextPages,
        handleFirstPage,
        handleLastPage
    } = Pagination(questions.length, itemsPerPage, currentPage, setCurrentPage);

    const handleQuestionClick = async (questionId) => {
        try {
            const response = await getQuestionDetail(questionId);
            setSelectedQuestion(response);
        } catch (error) {
            setError(error);
        }
    };

    const handleCreateQuestion = async (questionData) => {
        try {
            await createQuestion(questionData);
            const questionsResponse = await getAllQuestions();
            setQuestions(questionsResponse);
            setIsCreateModalOpen(false);
            toast.success("질문이 작성되었습니다.");
        } catch (error) {
            setError(error);
            toast.error("질문 작성에 실패하였습니다.");
        }
    };

    const handleAnswerSubmit = async (questionId, answerData) => {
        try {
            await createAnswer(questionId, answerData);
            const response = await getQuestionDetail(questionId);
            setSelectedQuestion(response);
            const questionsResponse = await getAllQuestions();
            setQuestions(questionsResponse);
            setSelectedQuestion(null);
            toast.success("답변이 작성되었습니다.");
        } catch (error) {
            setError(error);
            toast.error("답변 작성에 실패하였습니다.");
        }
    };

    const handleCloseModal = () => {
        setSelectedQuestion(null);
    };

    const handleOpenCreateModal = () => {
        setIsCreateModalOpen(true);
    };

    const handleCloseCreateModal = () => {
        setIsCreateModalOpen(false);
    };

    const isPrivateVisible = (question) => {
        if (!question.private) {
            return true;
        }
        if (currentUser && (currentUser.information.role === 'ADMIN' || currentUser.information.id === question.author.id)) {
            return true;
        }
        return false;
    };

    const currentQuestions = questions.slice(startIndex, endIndex);

    if (loading) {
        return <LoadingIndicator />;
    }

    if (error) {
        return <div>Error: {error.message}</div>;
    }

    return (
        <div className="question-service-container">
            <h1>고객센터</h1>
            <div className="question-list">
                <div className="question-create-header">
                    <button className="btn btn-creat-question" onClick={handleOpenCreateModal}>글쓰기</button>
                </div>
                <div className="question-header">
                    <span className="question-id">질문 번호</span>
                    <span className="question-subject">제목</span>
                    <span className="question-author">글쓴이</span>
                    <span className="question-answer">답변 여부</span>
                </div>
                <ul>
                    {currentQuestions.map(question => (
                        <li
                            key={question.id}
                            className={`question-item ${question.private ? 'private' : ''} ${isPrivateVisible(question) ? 'admin' : ''}`}
                            onClick={() => isPrivateVisible(question) && handleQuestionClick(question.id)}
                        >
                            <span className="question-id">{question.id}</span>
                            <span className="question-subject">
                                {isPrivateVisible(question) ? question.subject : "🔒 비밀글 입니다."}
                            </span>
                            <span className="question-author">{question.author ? question.author.nickname : "알 수 없음"}</span>
                            <span className="question-answer">{question.answer ? "답변 완료" : "답변 대기"}</span>
                        </li>
                    ))}
                </ul>
                <div className="pagination">
                    <button onClick={handleFirstPage} disabled={currentPage === 1}>	&lt;&lt;</button>
                    <button onClick={handlePreviousPages} disabled={currentPage === 1}>	&lt;</button>
                    {displayedPageNumbers.map(number => (
                        <button key={number} onClick={() => handlePageChange(number)} className={currentPage === number ? 'active' : ''}>
                            {number}
                        </button>
                    ))}
                    <button onClick={handleNextPages} disabled={currentPage === totalPages}>	&gt;</button>
                    <button onClick={handleLastPage} disabled={currentPage === totalPages}>	&gt;&gt;</button>
                </div>
            </div>
            {selectedQuestion && (
                <QuestionDetail
                    question={selectedQuestion}
                    currentUser={currentUser}
                    onClose={handleCloseModal}
                    onSubmitAnswer={handleAnswerSubmit}
                />
            )}
            {isCreateModalOpen && (
                <CreateQuestion onCreate={handleCreateQuestion} onClose={handleCloseCreateModal} />
            )}
        </div>
    );
};

export default QuestionService;
