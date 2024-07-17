export const API_BASE_URL = 'http://ec2-3-39-227-55.ap-northeast-2.compute.amazonaws.com/api';
export const ACCESS_TOKEN = 'accessToken';
export const REFRESH_TOKEN = 'refreshToken';

export const OAUTH2_REDIRECT_URI = 'http://ec2-3-39-227-55.ap-northeast-2.compute.amazonaws.com/oauth2/redirect';

export const GOOGLE_AUTH_URL = `${API_BASE_URL}/oauth2/authorize/google?redirect_uri=${OAUTH2_REDIRECT_URI}`;
export const KAKAO_AUTH_URL = `${API_BASE_URL}/oauth2/authorize/kakao?redirect_uri=${OAUTH2_REDIRECT_URI}`;
export const NAVER_AUTH_URL = `${API_BASE_URL}/oauth2/authorize/naver?redirect_uri=${OAUTH2_REDIRECT_URI}`;