FROM alpine:3.5
LABEL maintainer "Tiago Alves <tiagoalves83@gmail.com>"

LABEL caddy_version="0.10.3" architecture="amd64"

ENV CADDYPATH=/srv/ssl

ARG plugins=http.git

RUN apk add --no-cache openssh-client git tar curl

# install caddy
RUN curl --silent --show-error --fail --location \
      --header "Accept: application/tar+gzip, application/x-gzip, application/octet-stream" -o - \
      "https://caddyserver.com/download/linux/amd64?plugins=${plugins}" \
    | tar --no-same-owner -C /usr/bin/ -xz caddy \
 && chmod 0755 /usr/bin/caddy \
 && /usr/bin/caddy -version

EXPOSE 80 443 2015
WORKDIR /srv

VOLUME /srv/ssl

COPY Caddyfile /srv/Caddyfile

ENTRYPOINT ["/usr/bin/caddy"]
CMD ["--conf", "/srv/Caddyfile", "--log", "stdout"]
