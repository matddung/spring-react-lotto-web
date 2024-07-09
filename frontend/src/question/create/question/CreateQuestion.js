import React, { useState, useRef } from 'react';

import { useOutsideClick } from '../../../common/UtilCollection';
import './CreateQuestion.css';

const CreateQuestion = ({ onCreate, onClose }) => {
    const [subject, setSubject] = useState('');
    const [content, setContent] = useState('');
    const [isPrivate, setIsPrivate] = useState(false);
    const modalRef = useRef();

    useOutsideClick(modalRef, onClose);

    const handleQuestionSubmit = () => {
        const questionData = { subject, content, isPrivate };
        onCreate(questionData);
        onClose();
    };

    return (
        <div className="create-question-overlay">
            <div className="create-question-container" ref={modalRef}>
                <h2>질문 작성</h2>
                <div className="form-check">
                    <label>
                        비밀 글
                        <input
                            type="checkbox"
                            checked={isPrivate}
                            onChange={(e) => setIsPrivate(e.target.checked)}
                        />
                    </label>
                </div>
                <div className="form-group">
                    <label>제목</label>
                    <input
                        type="text"
                        value={subject}
                        onChange={(e) => setSubject(e.target.value)}
                        placeholder="제목을 입력하세요"
                    />
                </div>
                <div className="form-group">
                    <label>내용</label>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        placeholder="내용을 입력하세요"
                    ></textarea>
                </div>
                <div className="form-buttons">
                    <button className="btn btn-question-cancle" onClick={onClose}>닫기</button>
                    <button className="btn btn-question-submit" onClick={handleQuestionSubmit}>제출</button>
                </div>
            </div>
        </div>
    );
};

export default CreateQuestion;