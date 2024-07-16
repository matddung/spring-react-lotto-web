FROM node:20

WORKDIR /app

# 필요한 파일들을 복사
COPY package.json /app/
COPY package-lock.json /app/

RUN npm cache clean --force
RUN npm install --save 'react-scripts'

COPY . /app/

RUN npm run build

# 필요한 포트 노출
EXPOSE 3000

# 빌드된 파일을 서빙
CMD ["npx", "serve", "-s", "build"]