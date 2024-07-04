import React, { useEffect, useState } from 'react';
import { getAllQuestions, getQuestionDetail, createQuestion, createAnswer } from '../util/QuestionAPIUtils'; // createAnswer 추가
import { getCurrentUser } from '../util/UserAPIUtils';
import './QuestionService.css';
import QuestionDetail from './QuestionDetail';
import CreateQuestion from './CreateQuestion';
import LoadingIndicator from '../common/LoadingIndicator';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // toast 스타일 추가

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
            toast.success("질문이 작성되었습니다."); // toast 메시지 추가
        } catch (error) {
            setError(error);
            toast.error("질문 작성에 실패하였습니다."); // 실패 메시지 추가
        }
    };

    const handleAnswerSubmit = async (questionId, answerData) => {
        try {
            await createAnswer(questionId, answerData);
            const response = await getQuestionDetail(questionId);
            setSelectedQuestion(response); // Update the selected question with the new answer
            const questionsResponse = await getAllQuestions();
            setQuestions(questionsResponse); // Update the list of questions
            setSelectedQuestion(null); // Close the modal after submitting the answer
            toast.success("답변이 작성되었습니다."); // 답변 작성 성공 메시지 추가
        } catch (error) {
            setError(error);
            toast.error("답변 작성에 실패하였습니다."); // 실패 메시지 추가
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

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handlePreviousPages = () => {
        setCurrentPage(prevPage => Math.max(prevPage - 10, 1));
    };

    const handleNextPages = () => {
        setCurrentPage(prevPage => Math.min(prevPage + 10, Math.ceil(questions.length / itemsPerPage)));
    };

    const handleFirstPage = () => {
        setCurrentPage(1);
    };

    const handleLastPage = () => {
        setCurrentPage(Math.ceil(questions.length / itemsPerPage));
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

    const indexOfLastQuestion = currentPage * itemsPerPage;
    const indexOfFirstQuestion = indexOfLastQuestion - itemsPerPage;
    const currentQuestions = questions.slice(indexOfFirstQuestion, indexOfLastQuestion);

    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(questions.length / itemsPerPage); i++) {
        pageNumbers.push(i);
    }

    const displayedPageNumbers = pageNumbers.slice(
        Math.floor((currentPage - 1) / 10) * 10,
        Math.floor((currentPage - 1) / 10) * 10 + 10
    );

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
                <div className="question-list-header">
                    <button className="btn btn-primary" onClick={handleOpenCreateModal}>글쓰기</button>
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
                            <span className="question-author">{question.author.nickname}</span>
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
                    <button onClick={handleNextPages} disabled={currentPage === pageNumbers.length}>	&gt;</button>
                    <button onClick={handleLastPage} disabled={currentPage === pageNumbers.length}>	&gt;&gt;</button>
                </div>
            </div>
            {selectedQuestion && (
                <QuestionDetail
                    question={selectedQuestion}
                    currentUser={currentUser}
                    onClose={handleCloseModal}
                    onSubmitAnswer={handleAnswerSubmit} // onSubmitAnswer 전달
                />
            )}
            {isCreateModalOpen && (
                <CreateQuestion onCreate={handleCreateQuestion} onClose={handleCloseCreateModal} />
            )}
        </div>
    );
};

export default QuestionService;
