import React, { useRef } from 'react';
import { useOutsideClick } from '../../../common/UtilCollection';
import Pagination from '../../../common/Pagination';
import './UserDetail.css';

const UserDetail = ({ selectedUser, selectedUserQuestions, currentPageUserQuestions, setCurrentPageUserQuestions, onClose, onQuestionClick }) => {
    const itemsPerPage = 10;
    const modalRef = useRef();

    useOutsideClick(modalRef, onClose);

    const {
        startIndex: startIndexUserQuestions,
        endIndex: endIndexUserQuestions,
        displayedPageNumbers: displayedPageNumbersUserQuestions,
        totalPages: totalPagesUserQuestions,
        handlePageChange: handlePageChangeUserQuestions,
        handlePreviousPages: handlePreviousPagesUserQuestions,
        handleNextPages: handleNextPagesUserQuestions,
        handleFirstPage: handleFirstPageUserQuestions,
        handleLastPage: handleLastPageUserQuestions
    } = Pagination(selectedUserQuestions.length, itemsPerPage, currentPageUserQuestions, setCurrentPageUserQuestions);

    const currentUserQuestions = selectedUserQuestions.slice(startIndexUserQuestions, endIndexUserQuestions);

    return (
        <div className="modal-overlay">
            <div className="user-detail-modal" ref={modalRef}>
                <div className="user-detail-content">
                    <div className="user-detail">
                        <h2>유저 정보</h2>
                        <p>ID: {selectedUser.id}</p>
                        <p>Name: {selectedUser.nickname}</p>
                        <p>Email: {selectedUser.email}</p>
                        <p>Role: {selectedUser.role}</p>
                    </div>
                    <div className="user-questions">
                        <h2>질문 목록</h2>
                        <ul>
                            {currentUserQuestions.map(question => (
                                <li key={question.id} onClick={() => onQuestionClick(question.id)}>
                                    <span>{question.subject}</span>
                                    <span>{new Date(question.createdDate).toLocaleDateString()}</span>
                                    <span>{question.answer ? "답변 완료" : "답변 대기 중"}</span>
                                </li>
                            ))}
                        </ul>
                        <div className="pagination">
                            <button onClick={handleFirstPageUserQuestions} disabled={currentPageUserQuestions === 1}>&lt;&lt;</button>
                            <button onClick={handlePreviousPagesUserQuestions} disabled={currentPageUserQuestions === 1}>&lt;</button>
                            {displayedPageNumbersUserQuestions.map(number => (
                                <button key={number} onClick={() => handlePageChangeUserQuestions(number)} className={currentPageUserQuestions === number ? 'active' : ''}>
                                    {number}
                                </button>
                            ))}
                            <button onClick={handleNextPagesUserQuestions} disabled={currentPageUserQuestions === totalPagesUserQuestions}>&gt;</button>
                            <button onClick={handleLastPageUserQuestions} disabled={currentPageUserQuestions === totalPagesUserQuestions}>&gt;&gt;</button>
                        </div>
                    </div>
                </div>
                <button onClick={onClose} className="btn btn-secondary">Close</button>
            </div>
        </div>
    );
};

export default UserDetail;
