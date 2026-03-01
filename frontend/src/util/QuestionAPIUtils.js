import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

const extractPayload = (response) => response?.data ?? response;

export async function getAllQuestions(page) {
    const response = await request({
        url: `${API_BASE_URL}/api/question/list?page=${page}`,
        method: 'GET',
    });

    return extractPayload(response);
}

export async function getQuestionDetail(id) {
    const response = await request({
        url: `${API_BASE_URL}/api/question/detail?id=${id}`,
        method: 'GET',
    });

    return extractPayload(response);
}

export async function createAnswer(id, createAnswerRequest) {
    const response = await request({
        url: `${API_BASE_URL}/api/question/answer?id=${id}`,
        method: 'POST',
        body: JSON.stringify(createAnswerRequest)
    });

    return extractPayload(response);
}

export async function createQuestion(createQuestionRequest) {
    const response = await request({
        url: `${API_BASE_URL}/api/question/create`,
        method: 'POST',
        body: JSON.stringify(createQuestionRequest)
    });

    return extractPayload(response);
}

export async function getMyQuestions(page) {
    const response = await request({
        url: `${API_BASE_URL}/api/question/my-list?page=${page}`,
        method: 'GET',
    });

    return extractPayload(response);
}