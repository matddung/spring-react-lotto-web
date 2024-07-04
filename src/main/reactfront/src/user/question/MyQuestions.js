import React, { useEffect, useRef, useState } from 'react';
import './MyQuestions.css';
import QuestionDetail from '../../question/QuestionDetail'; // Import QuestionDetail
import { getQuestionDetail } from '../../util/QuestionAPIUtils'; // Import getQuestionDetail

const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' };
    return new Date(dateString).toLocaleDateString('ko-KR', options).replace(',', '');
};

const MyQuestions = ({ questions = [], onClose, currentUser }) => {
    const modalRef = useRef();
    const [selectedQuestion, setSelectedQuestion] = useState(null);

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
