import React, { useRef, useState } from 'react';

import QuestionDetail from '../../question/detail/QuestionDetail';
import { getQuestionDetail } from '../../util/QuestionAPIUtils';
import { formatDate, useOutsideClick } from '../../common/UtilCollection';
import './MyQuestions.css';

const MyQuestions = ({ questions = [], onClose, currentUser }) => {
    const modalRef = useRef();
    const [selectedQuestion, setSelectedQuestion] = useState(null);

    useOutsideClick(modalRef, onClose);

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
