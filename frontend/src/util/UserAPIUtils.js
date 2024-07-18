import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getCurrentUser() {
    return request({
        url: `${API_BASE_URL}/api/user`,
        method: 'GET'
    });
}

export function login(loginRequest) {
    return request({
        url: `${API_BASE_URL}/api/user/signIn`,
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signUpRequest) {
    return request({
        url: `${API_BASE_URL}/api/user/signUp`,
        method: 'POST',
        body: JSON.stringify(signUpRequest)
    });
}

export function changePassword(passwordChangeRequest) {
    return request({
        url: `${API_BASE_URL}/api/user/password`,
        method: 'PUT',
        body: JSON.stringify(passwordChangeRequest)
    });
}

export function changeNickname(nicknameChangeRequest) {
    return request({
        url: `${API_BASE_URL}/api/user/nickname`,
        method: 'PUT',
        body: JSON.stringify(nicknameChangeRequest)
    });
}

export function deleteAccount() {
    return request({
        url: `${API_BASE_URL}/api/user`,
        method: 'DELETE'
    });
}

export function findPassword(findPasswordRequest) {
    return request({
        url: `${API_BASE_URL}/api/user/find-password`,
        method: 'POST',
        body: JSON.stringify(findPasswordRequest)
    });
}