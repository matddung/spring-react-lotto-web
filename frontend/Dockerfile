FROM node:14

WORKDIR /app

# 필요한 파일들을 복사
COPY package.json /app/
RUN npm install
COPY . /app/

# 환경 변수 설정 및 빌드
ENV REACT_APP_API_BASE_URL=http://ec2-54-180-139-133.ap-northeast-2.compute.amazonaws.com:8080
ENV REACT_APP_OAUTH2_REDIRECT_URI=http://ec2-54-180-139-133.ap-northeast-2.compute.amazonaws.com:3000/oauth2/redirect

RUN echo $REACT_APP_API_BASE_URL
RUN echo $REACT_APP_OAUTH2_REDIRECT_URI

# 빌드
RUN REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL REACT_APP_OAUTH2_REDIRECT_URI=$REACT_APP_OAUTH2_REDIRECT_URI npm run build

# 필요한 포트 노출
EXPOSE 3000

# 빌드된 파일을 서빙
CMD ["npx", "serve", "-s", "build"]