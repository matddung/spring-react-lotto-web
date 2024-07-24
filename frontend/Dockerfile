FROM node:20

WORKDIR /app

COPY package.json /app/
COPY package-lock.json /app/

RUN npm cache clean --force
RUN npm install

COPY . /app/

EXPOSE 3000

CMD ["npx", "serve", "-s", "build"]