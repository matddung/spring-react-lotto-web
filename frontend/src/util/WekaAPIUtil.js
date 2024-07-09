import { API_BASE_URL } from '../constants';
import { request } from './APIRequest';

export function getStatistics() {
    return request({
        url: `${API_BASE_URL}/statistics`,
        method: 'GET'
    });
}

export function getStatisticsTop6(date) {
    return request({
        url: `${API_BASE_URL}/top6?date=${date}`,
        method: 'GET'
    });
}

export function getPatternRecognition(date) {
    return request({
        url: `${API_BASE_URL}/pattern-recognition?date=${date}`,
        method: 'GET'
    });
}

export function generateRandomNumbers() {
    return request({
        url: `${API_BASE_URL}/random`,
        method: 'GET'
    });
}

export function getEnsemblePrediction(date) {
    return request({
        url: `${API_BASE_URL}/ensemble?date=${date}`,
        method: 'GET'
    });
}

export function runMonteCarloSimulation() {
    return request({
        url: `${API_BASE_URL}/monte-carlo`,
        method: 'GET'
    });
}

export function getCurrentUserLottoInfo() {
    return request({
        url: `${API_BASE_URL}/user-lotto-info`,
        method: 'GET'
    })
}