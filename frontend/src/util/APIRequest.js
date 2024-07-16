import { ACCESS_TOKEN, REFRESH_TOKEN, API_BASE_URL } from '../constants';

async function refreshAccessToken() {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN);

    const response = await fetch(`${API_BASE_URL}/user/refresh`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken: refreshToken }),
    });

    if (!response.ok) {
        throw new Error('Failed to refresh access token');
    }

    const data = await response.json();
    localStorage.setItem(ACCESS_TOKEN, data.accessToken);
    return data.accessToken;
}

async function fetchWithInterceptor(url, options = {}) {
    let accessToken = localStorage.getItem(ACCESS_TOKEN);

    if (!options.headers) {
        options.headers = new Headers({
            'Content-Type': 'application/json',
        });
    } else if (!(options.headers instanceof Headers)) {
        options.headers = new Headers(options.headers);
    }

    if (accessToken) {
        options.headers.set('Authorization', 'Bearer ' + accessToken);
    }

    try {
        let response = await fetch(`${url}`, options);

        if (response.status === 401) {
            accessToken = await refreshAccessToken();
            options.headers.set('Authorization', 'Bearer ' + accessToken);
            return await fetch(`${url}`, options);
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw errorData;
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export const request = (options) => {
    return fetchWithInterceptor(options.url, options);
};