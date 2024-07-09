import React, { useEffect, useState, useRef } from 'react';

import { useOutsideClick } from '../../common/UtilCollection';
import { formatDate } from '../../common/UtilCollection';
import AnswerForm from '../create/answer/CreateAnswer';
import './QuestionDetail.css';

const QuestionDetail = ({ question, currentUser, onClose, onSubmitAnswer }) => {
    const [isAnswerFormVisible, setIsAnswerFormVisible] = useState(false);
    const modalRef = useRef();

    useOutsideClick(modalRef, onClose);

    useEffect(() => {}, [question, currentUser]);

    if (!question) {
        return null;
    }

    const isUserAdmin = currentUser && currentUser.information.role === 'ADMIN';

    return (
        <div className="question-detail-overlay">
            <div className="question-detail-container" ref={modalRef}>
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
                        {isUserAdmin && !question.answer && (
                            <button
                                className="btn btn-primary"
                                onClick={() => setIsAnswerFormVisible(!isAnswerFormVisible)}
                            >
                                답변하기
                            </button>
                        )}
                    </div>
                    {isAnswerFormVisible && (
                        <AnswerForm
                            onSubmitAnswer={onSubmitAnswer}
                            questionId={question.id}
                            onClose={() => {
                                setIsAnswerFormVisible(false);
                                onClose();
                            }}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default QuestionDetail;