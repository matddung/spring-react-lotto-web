import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getAllUsers() {
    return request({
        url: `${API_BASE_URL}/admin/user-list`,
        method: 'GET'
    });
}

export function getUserHistory(id) {
    return request({
        url: `${API_BASE_URL}/admin/user-detail?id=${id}`,
        method: 'GET'
    });
}

export function getUnansweredQuestions() {
    return request({
        url: `${API_BASE_URL}/admin/unanswered-questions`,
        method: 'GET'
    });
}

export function deleteUser(id) {
    return request({
        url: `${API_BASE_URL}/admin/user-delete?id=${id}`,
        method: 'DELETE'
    });
}