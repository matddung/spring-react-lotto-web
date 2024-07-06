import React, { useState } from 'react';
import { toast } from 'react-toastify';

import './CreateAnswer.css';

const AnswerForm = ({ onSubmitAnswer, questionId, onClose }) => {
    const [answerSubject, setAnswerSubject] = useState('');
    const [answerContent, setAnswerContent] = useState('');
    const [errors, setErrors] = useState({});

    const handleAnswerSubmit = async () => {
        if (answerSubject.trim() === '' || answerContent.trim() === '') {
            setErrors('답변 제목과 내용을 모두 입력해주세요.');
            toast.error(errors);
            return;
        }

        const answer = {
            subject: answerSubject,
            content: answerContent
        };
        await onSubmitAnswer(questionId, answer);
        onClose();
        setAnswerSubject('');
        setAnswerContent('');
    };

    return (
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
                <button className="btn btn-question-submit" onClick={handleAnswerSubmit}>
                    제출
                </button>
            </div>
        </div>
    );
};

export default AnswerForm;