#!/usr/bin/env bash

function prepareEnv() {
    unset HTTPS_PROXY
    unset HTTP_PROXY_HOST
    unset HTTP_PROXY_PORT
    unset HTTP_PROXY_PASSWORD
    unset HTTP_PROXY_USERNAME
    unset HTTP_PROXY_NONPROXYHOSTS
    unset MAVEN_MIRROR_URL
}

function configure() {
    configure_proxy
    configure_mirrors
}

# insert settings for HTTP proxy into maven settings.xml if supplied
function configure_proxy() {

  # prefer old http_proxy_ format for username/password, but
  # also allow proxy_ format.
  HTTP_PROXY_USERNAME=${HTTP_PROXY_USERNAME:-$PROXY_USERNAME}
  HTTP_PROXY_PASSWORD=${HTTP_PROXY_PASSWORD:-$PROXY_PASSWORD}

  proxy=${HTTPS_PROXY:-${https_proxy:-${HTTP_PROXY:-$http_proxy}}}
  # if http_proxy_host/port is set, prefer that (oldest mechanism)
  # before looking at HTTP(S)_PROXY
  proxyhost=${HTTP_PROXY_HOST:-$(echo $proxy | cut -d : -f 1,2)}
  proxyport=${HTTP_PROXY_PORT:-$(echo $proxy | cut -d : -f 3)}

   if [ -n "$proxyhost" ]; then
    if [[ `echo $proxyhost | grep -i https://` ]]; then
      proxyport=${proxyport:-443}
      proxyprotocol="https"
    else
      proxyport=${proxyport:-80}
      proxyprotocol="http"
    fi

     xml="<proxy>\
         <id>genproxy</id>\
         <active>true</active>\
         <protocol>$proxyprotocol</protocol>\
         <host>$proxyhost</host>\
         <port>$proxyport</port>"
    if [ -n "$HTTP_PROXY_USERNAME" -a -n "$HTTP_PROXY_PASSWORD" ]; then
      xml="$xml\
         <username>$HTTP_PROXY_USERNAME</username>\
         <password>$HTTP_PROXY_PASSWORD</password>"
    fi
    if [ -n "$HTTP_PROXY_NONPROXYHOSTS" ]; then
      xml="$xml\
         <nonProxyHosts>$HTTP_PROXY_NONPROXYHOSTS</nonProxyHosts>"
    fi
  xml="$xml\
       </proxy>"
    sed -i "s|<!-- ### configured http proxy ### -->|$xml|" $HOME/.m2/settings.xml
  fi
}

 # insert settings for mirrors/repository managers into settings.xml if supplied
function configure_mirrors() {
  if [ -n "$MAVEN_MIRROR_URL" ]; then
    xml="    <mirror>\
      <id>mirror.default</id>\
      <url>$MAVEN_MIRROR_URL</url>\
      <mirrorOf>external:*</mirrorOf>\
    </mirror>"
    sed -i "s|<!-- ### configured mirrors ### -->|$xml|" $HOME/.m2/settings.xml
  fi
}

function manage_incremental_build() {
    if [ -d /tmp/artifacts ]; then
        echo "Expanding artifacts from incremental build..."
        ( cd /tmp/artifacts && tar cf - . ) | ( cd ${HOME} && tar xvf - )
        rm -rf /tmp/artifacts
    fi
}

function s2i_save_build_artifacts() {
    cd ${HOME}
    tar cf - .m2
}