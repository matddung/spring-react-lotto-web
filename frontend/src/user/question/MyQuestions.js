import React, { useRef, useState, useEffect } from 'react';

import QuestionDetail from '../../question/detail/QuestionDetail';
import { getQuestionDetail, getMyQuestions } from '../../util/QuestionAPIUtils';
import { formatDate, useOutsideClick } from '../../common/UtilCollection';
import usePagination from '../../common/Pagination';
import './MyQuestions.css';

const MyQuestions = ({ onClose, currentUser }) => {
    const modalRef = useRef();
    const [selectedQuestion, setSelectedQuestion] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [totalElements, setTotalElements] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);

    useOutsideClick(modalRef, onClose);

    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                const questionsResponse = await getMyQuestions(currentPage - 1);
                if (questionsResponse && questionsResponse.content) {
                    setQuestions(questionsResponse.content);
                    setTotalElements(questionsResponse.totalElements);
                } else {
                    setQuestions([]);
                    setTotalElements(0);
                }
            } catch (error) {
                console.error('내 질문 목록을 불러오는데 실패했습니다.', error);
            }
        };

        fetchQuestions();
    }, [currentPage]);

    const {
        displayedPageNumbers,
        handlePageChange,
        handlePreviousPages,
        handleNextPages,
        handleFirstPage,
        handleLastPage
    } = usePagination(totalElements, 10, currentPage, setCurrentPage);

    const handleQuestionClick = async (questionId) => {
        try {
            const response = await getQuestionDetail(questionId);
            setSelectedQuestion(response);
        } catch (error) {
            console.error("Failed to fetch question detail:", error);
        }
    };

    const handleCloseDetail = () => {
        setSelectedQuestion(null);
    };

    return (
        <div className="my-questions-overlay">
            <div className="my-questions-container" ref={modalRef}>
                <h2>내 질문 목록</h2>
                <div className="question-list">
                    <ul>
                        {questions.map(question => (
                            <li key={question.id} className="question-item" onClick={() => handleQuestionClick(question.id)}>
                                <span>{question.id}</span>
                                <span>{question.subject}</span>
                                <span>{formatDate(question.createdDate)}</span>
                                <span className="question-answer">{question.answer ? '답변 완료' : '답변 대기'}</span>
                            </li>
                        ))}
                    </ul>
                    <div className="pagination">
                        <button onClick={handleFirstPage} disabled={currentPage === 1}>&lt;&lt;</button>
                        <button onClick={handlePreviousPages} disabled={currentPage === 1}>&lt;</button>
                        {displayedPageNumbers.map(number => (
                            <button key={number} onClick={() => handlePageChange(number)} className={currentPage === number ? 'active' : ''}>
                                {number}
                            </button>
                        ))}
                        <button onClick={handleNextPages} disabled={currentPage === totalElements}>&gt;</button>
                        <button onClick={handleLastPage} disabled={currentPage === totalElements}>&gt;&gt;</button>
                    </div>
                    <button className="btn btn-secondary" onClick={onClose}>닫기</button>
                </div>
                {selectedQuestion && (
                    <QuestionDetail
                        question={selectedQuestion}
                        currentUser={currentUser}
                        onClose={handleCloseDetail}
                    />
                )}
            </div>
        </div>
    );
};

export default MyQuestions;