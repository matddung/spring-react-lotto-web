import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

const extractPayload = (response) => response?.data ?? response;

export async function getAllUsers(page) {
    const response = await request({
        url: `${API_BASE_URL}/api/admin/user-list?page=${page}`,
        method: 'GET'
    });

    return extractPayload(response);
}

export async function getUserHistory(id, page) {
    const response = await request({
        url: `${API_BASE_URL}/api/admin/user-detail?id=${id}&page=${page}`,
        method: 'GET'
    });

    return extractPayload(response);
}

export async function getUnansweredQuestions(page) {
    const response = await request({
        url: `${API_BASE_URL}/api/admin/unanswered-questions?page=${page}`,
        method: 'GET'
    });

    return extractPayload(response);
}

export async function deleteUser(id) {
    const response = await request({
        url: `${API_BASE_URL}/api/admin/user-delete?id=${id}`,
        method: 'DELETE'
    });

    return extractPayload(response);
}