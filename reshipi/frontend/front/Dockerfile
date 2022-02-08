FROM ubuntu:20.04 AS build

RUN apt-get update && \
    apt-get install -y curl gnupg
RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list && \
    apt-get update && \
    apt-get install -y yarn

ADD . /build
WORKDIR /build
RUN yarn install

ARG VUE_APP_API_ENDPOINT_URL

RUN VUE_APP_API_ENDPOINT_URL=$VUE_APP_API_ENDPOINT_URL yarn build

FROM nginx

COPY --from=build /build/dist/ /usr/share/nginx/html
