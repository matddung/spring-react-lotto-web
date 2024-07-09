import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getAllUsers(page) {
    return request({
        url: `${API_BASE_URL}/admin/user-list?page=${page}`,
        method: 'GET'
    });
}

export function getUserHistory(id, page) {
    return request({
        url: `${API_BASE_URL}/admin/user-detail?id=${id}&?page=${page}`,
        method: 'GET'
    });
}

export function getUnansweredQuestions(page) {
    return request({
        url: `${API_BASE_URL}/admin/unanswered-questions?page=${page}`,
        method: 'GET'
    });
}

export function deleteUser(id) {
    return request({
        url: `${API_BASE_URL}/admin/user-delete?id=${id}`,
        method: 'DELETE'
    });
}