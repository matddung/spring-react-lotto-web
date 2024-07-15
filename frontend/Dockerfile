FROM node:14

WORKDIR /app

# 필요한 파일들을 복사
COPY package.json /app/
RUN npm install
COPY . /app/

# 환경 변수 설정
ENV REACT_APP_API_BASE_URL=http://ec2-43-201-150-170.ap-northeast-2.compute.amazonaws.com:8080
ENV REACT_APP_OAUTH2_REDIRECT_URI=http://ec2-43-201-150-170.ap-northeast-2.compute.amazonaws.com:3000/oauth2/redirect

# 빌드
RUN npm run build

# 필요한 포트 노출
EXPOSE 3000

# 빌드된 파일을 서빙
CMD ["npx", "serve", "-s", "build"]