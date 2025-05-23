# Step 1: Build stage
FROM node:18 AS build-stage

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# 권한 확인 및 부여
RUN chmod +x node_modules/.bin/vue-cli-service && \
    ls -l node_modules/.bin/vue-cli-service

RUN npm run build

# Step 2: Production stage
FROM nginx:stable-alpine as production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
