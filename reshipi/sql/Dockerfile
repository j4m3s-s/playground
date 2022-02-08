FROM alpine:3.15 AS build

RUN apk add wget
RUN wget https://github.com/PostgREST/postgrest/releases/download/v9.0.0.20220204/postgrest-v9.0.0.20220204-linux-static-x64.tar.xz && tar xvf postgrest-v9.0.0.20220204-linux-static-x64.tar.xz

FROM scratch

COPY --from=build /postgrest .
ENTRYPOINT [ "/postgrest" ]
