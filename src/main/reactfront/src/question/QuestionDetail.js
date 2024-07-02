import React, { useEffect, useState } from 'react';
import './QuestionDetail.css';

const formatDate = (dateString) => {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('ko-KR', options).replace(',', '');
};

const QuestionDetail = ({ question, currentUser, onClose, onSubmitAnswer }) => {
    const [isAnswerFormVisible, setIsAnswerFormVisible] = useState(false);
    const [answerSubject, setAnswerSubject] = useState('');
    const [answerContent, setAnswerContent] = useState('');

    useEffect(() => {
        console.log("Question Detail:", question);
        console.log("Current User:", currentUser);
    }, [question, currentUser]);

    if (!question) {
        return null;
    }

    const handleAnswerSubmit = async () => {
        const answer = {
            subject: answerSubject,
            content: answerContent
        };
        console.log('Submitting answer:', answer);
        await onSubmitAnswer(question.id, answer);
        onClose(); // Close the modal after submitting the answer
        setIsAnswerFormVisible(false); // Hide the form after submitting
        setAnswerSubject('');
        setAnswerContent('');
    };

    return (
        <div className="question-detail-overlay">
            <div className="question-detail-container">
                <h2>질문 상세 정보</h2>
                <div className="modal-content">
                    <div className="question-section">
                        <p><strong>질문 번호:</strong> {question.id}</p>
                        <p><strong>제목:</strong> {question.subject}</p>
                        <p><strong>글쓴이:</strong> {question.author.nickname}</p>
                        <p><strong>질문 작성일:</strong> {formatDate(question.createdDate)}</p>
                        <p><strong>내용:</strong> {question.content}</p>
                    </div>
                    <div className="answer-section">
                        <p><strong>답변:</strong></p>
                        {question.answer ? (
                            <div>
                                <p><strong>답변 제목:</strong> {question.answer.subject}</p>
                                <p><strong>답변 내용:</strong> {question.answer.content}</p>
                                <p><strong>답변 작성일:</strong> {formatDate(question.answer.createdDate)}</p>
                            </div>
                        ) : (
                            <p>답변 대기 중</p>
                        )}
                    </div>
                    <div className="modal-actions">
                        <button className="btn btn-secondary" onClick={onClose}>닫기</button>
                        {currentUser && currentUser.information.role === 'ADMIN' && !question.answer && (
                            <button
                                className="btn btn-primary"
                                onClick={() => setIsAnswerFormVisible(!isAnswerFormVisible)}
                            >
                                답변하기
                            </button>
                        )}
                    </div>
                    {isAnswerFormVisible && (
                        <div className="answer-form">
                            <h3>답변 작성</h3>
                            <input
                                type="text"
                                placeholder="답변 제목"
                                value={answerSubject}
                                onChange={(e) => setAnswerSubject(e.target.value)}
                            />
                            <textarea
                                placeholder="답변 내용"
                                value={answerContent}
                                onChange={(e) => setAnswerContent(e.target.value)}
                            ></textarea>
                            <div className="submit-actions">
                                <button className="btn btn-secondary" onClick={handleAnswerSubmit}>
                                    제출
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default QuestionDetail;
