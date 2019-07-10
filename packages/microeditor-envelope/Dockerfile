FROM alpine

RUN apk add yarn nodejs \
 && mkdir -p /usr/local/app

EXPOSE 9000

WORKDIR /usr/local/app/packages/microeditor-envelope

CMD ["yarn", "start"]