/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as url from "url";

import { CorsConfig } from "@kie-tools/cors-proxy-api/dist";

export enum GIT_HTTP_METHODS {
  GET = "GET",
  OPTIONS = "OPTIONS",
  POST = "POST",
}

export const GIT_CONSTS = {
  INFO_REFS: "/info/refs",

  GIT_UPLOAD_PACK: "git-upload-pack",
  GIT_RECEIVE_PACK: "git-receive-pack",

  ACCESS_CONTROL_HEADERS: "access-control-request-headers",
  CONTENT_TYPE: "content-type",

  X_GIT_UPLOAD_PACK_REQUEST: "application/x-git-upload-pack-request",
  X_GIT_RECEIVE_PACK_REQUEST: "application/x-git-receive-pack-request",
};

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
  allowMethods: Object.values(GIT_HTTP_METHODS),
};

export const isGitOperation = (targetUrl: string, method: string, headers: Record<string, string> = {}) => {
  const parsedUrl = url.parse(targetUrl, true);

  return (
    isPreflightInfoRefs(parsedUrl, method) ||
    isInfoRefs(parsedUrl, method) ||
    isPreflightPull(parsedUrl, method, headers) ||
    isPull(parsedUrl, method, headers) ||
    isPreflightPush(parsedUrl, method, headers) ||
    isPush(parsedUrl, method, headers)
  );
};

function isPreflightInfoRefs(url: url.UrlWithParsedQuery, method: string) {
  return (
    method === GIT_HTTP_METHODS.OPTIONS &&
    url.pathname!.endsWith(GIT_CONSTS.INFO_REFS) &&
    [GIT_CONSTS.GIT_UPLOAD_PACK, GIT_CONSTS.GIT_RECEIVE_PACK].includes(url.query.service as string)
  );
}

function isInfoRefs(url: url.UrlWithParsedQuery, method: string) {
  return (
    method === GIT_HTTP_METHODS.GET &&
    url.pathname!.endsWith(GIT_CONSTS.INFO_REFS) &&
    [GIT_CONSTS.GIT_UPLOAD_PACK, GIT_CONSTS.GIT_RECEIVE_PACK].includes(url.query.service as string)
  );
}

function isPreflightPull(url: url.UrlWithParsedQuery, method: string, headers: Record<string, string>) {
  return (
    method === GIT_HTTP_METHODS.OPTIONS &&
    headers[GIT_CONSTS.ACCESS_CONTROL_HEADERS] === GIT_CONSTS.CONTENT_TYPE &&
    url.pathname!.endsWith(GIT_CONSTS.GIT_UPLOAD_PACK)
  );
}

function isPull(url: url.UrlWithParsedQuery, method: string, headers: Record<string, string>) {
  return (
    method === GIT_HTTP_METHODS.POST &&
    headers[GIT_CONSTS.CONTENT_TYPE] === GIT_CONSTS.X_GIT_UPLOAD_PACK_REQUEST &&
    url.pathname!.endsWith(GIT_CONSTS.GIT_UPLOAD_PACK)
  );
}

function isPreflightPush(url: url.UrlWithParsedQuery, method: string, headers: Record<string, string>) {
  return (
    method === GIT_HTTP_METHODS.OPTIONS &&
    headers[GIT_CONSTS.ACCESS_CONTROL_HEADERS] === GIT_CONSTS.CONTENT_TYPE &&
    url.pathname!.endsWith(GIT_CONSTS.GIT_RECEIVE_PACK)
  );
}

function isPush(u: url.UrlWithParsedQuery, method: string, headers: Record<string, string>) {
  return (
    method === GIT_HTTP_METHODS.POST &&
    headers[GIT_CONSTS.CONTENT_TYPE] === GIT_CONSTS.X_GIT_RECEIVE_PACK_REQUEST &&
    u.pathname!.endsWith(GIT_CONSTS.GIT_RECEIVE_PACK)
  );
}
