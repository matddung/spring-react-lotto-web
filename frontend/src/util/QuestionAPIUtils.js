import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getAllQuestions() {
    return request({
        url: `${API_BASE_URL}/question/list`,
        method: 'GET',
    });
}

export function getQuestionDetail(id) {
    return request({
        url: `${API_BASE_URL}/question/detail?id=${id}`,
        method: 'GET',
    });
}

export function createAnswer(id, createAnswerRequest) {
    return request({
        url: `${API_BASE_URL}/question/answer?id=${id}`,
        method: 'POST',
        body: JSON.stringify(createAnswerRequest)
    });
}

export function createQuestion(createQuestionRequest) {
    return request({
        url: `${API_BASE_URL}/question/create`,
        method: 'POST',
        body: JSON.stringify(createQuestionRequest)
    });
}

export function getMyQuestions() {
    return request({
        url: `${API_BASE_URL}/question/my-list`,
        method: 'GET',
    });
}