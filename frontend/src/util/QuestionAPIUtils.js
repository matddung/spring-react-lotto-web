import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getAllQuestions(page) {
    return request({
        url: `${API_BASE_URL}/api/question/list?page=${page}`,
        method: 'GET',
    });
}

export function getQuestionDetail(id) {
    return request({
        url: `${API_BASE_URL}/api/question/detail?id=${id}`,
        method: 'GET',
    });
}

export function createAnswer(id, createAnswerRequest) {
    return request({
        url: `${API_BASE_URL}/api/question/answer?id=${id}`,
        method: 'POST',
        body: JSON.stringify(createAnswerRequest)
    });
}

export function createQuestion(createQuestionRequest) {
    return request({
        url: `${API_BASE_URL}/api/question/create`,
        method: 'POST',
        body: JSON.stringify(createQuestionRequest)
    });
}

export function getMyQuestions(page) {
    return request({
        url: `${API_BASE_URL}/api/question/my-list?page=${page}`,
        method: 'GET',
    });
}