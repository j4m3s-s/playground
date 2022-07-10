FROM node:14 AS builder
WORKDIR /app
COPY package.json /app
COPY yarn.lock /app
RUN chown -R 1000:1000 /app
RUN yarn install

COPY . /app
RUN NODE_ENV=production VUE_APP_API_ENDPOINT_URL=https://api.flashcard.j4m3s.eu yarn run build
#TODO: remove *.map, tree-shaking and merge js files?

FROM nginx:1.23
COPY --from=builder /app/dist/ /usr/share/nginx/html/
