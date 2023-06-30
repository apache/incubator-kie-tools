/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as url from "url";

import { CorsConfig } from "./types";

export const GIT_CORS_CONFIG: CorsConfig = {
  allowHeaders: [
    "accept-encoding",
    "accept-language",
    "accept",
    "access-control-allow-origin",
    "authorization",
    "cache-control",
    "connection",
    "content-length",
    "content-type",
    "dnt",
    "git-protocol",
    "pragma",
    "range",
    "referer",
    "user-agent",
    "x-authorization",
    "x-http-method-override",
    "x-requested-with",
  ],
  exposeHeaders: [
    "accept-ranges",
    "age",
    "cache-control",
    "content-length",
    "content-language",
    "content-type",
    "date",
    "etag",
    "expires",
    "last-modified",
    "location",
    "pragma",
    "server",
    "transfer-encoding",
    "vary",
    "x-github-request-id",
    "x-redirected-url",
  ],
  customHeaders: {
    "user-agent": "git/@kie-tools/cors-proxy",
  },
  allowMethods: ["POST", "GET", "OPTIONS"],
};

type IncommingRequestInfo = {
  method: string;
  headers: Record<string, string>;
};

export const isGitOperation = (targetUrl: string, request: IncommingRequestInfo) => {
  const parsedUrl = url.parse(targetUrl, true);

  return (
    isPreflightInfoRefs(parsedUrl, request) ||
    isInfoRefs(parsedUrl, request) ||
    isPreflightPull(parsedUrl, request) ||
    isPull(parsedUrl, request) ||
    isPreflightPush(parsedUrl, request) ||
    isPush(parsedUrl, request)
  );
};

function isPreflightInfoRefs(url: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "OPTIONS" &&
    url.pathname!.endsWith("/info/refs") &&
    (url.query.service === "git-upload-pack" || url.query.service === "git-receive-pack")
  );
}
function isInfoRefs(url: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "GET" &&
    url.pathname!.endsWith("/info/refs") &&
    (url.query.service === "git-upload-pack" || url.query.service === "git-receive-pack")
  );
}

function isPreflightPull(url: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "OPTIONS" &&
    request.headers["access-control-request-headers"] === "content-type" &&
    url.pathname!.endsWith("git-upload-pack")
  );
}

function isPull(url: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "POST" &&
    request.headers["content-type"] === "application/x-git-upload-pack-request" &&
    url.pathname!.endsWith("git-upload-pack")
  );
}

function isPreflightPush(url: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "OPTIONS" &&
    request.headers["access-control-request-headers"] === "content-type" &&
    url.pathname!.endsWith("git-receive-pack")
  );
}

function isPush(u: url.UrlWithParsedQuery, request: IncommingRequestInfo) {
  return (
    request.method === "POST" &&
    request.headers["content-type"] === "application/x-git-receive-pack-request" &&
    u.pathname!.endsWith("git-receive-pack")
  );
}
