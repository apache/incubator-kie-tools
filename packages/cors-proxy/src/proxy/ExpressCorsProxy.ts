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

import * as https from "https";
import fetch from "node-fetch";
import { Request, Response } from "express";
import { INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION, CorsConfig, CorsProxy, TARGET_URL_HEADER } from "./types";
import { GIT_CORS_CONFIG, isGitOperation } from "./git";

const HTTPS_PROTOCOL = "https:";
const BANNED_PROXY_HEADERS = ["origin", "host", TARGET_URL_HEADER, INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION];

export class ExpressCorsProxy implements CorsProxy<Request, Response> {
  private readonly logger: Logger;

  constructor(
    private readonly args: {
      origin: string;
      verbose: boolean;
    }
  ) {
    this.logger = new Logger(args.verbose);

    this.logger.debug("");
    this.logger.debug("Proxy Configuration:");
    this.logger.debug("* Accept Origin Header: ", `"${args.origin}"`);
    this.logger.debug("* Verbose: ", args.verbose);
    this.logger.debug("");
  }

  async handle(req: Request, res: Response, next: Function): Promise<void> {
    try {
      const info = this.resolveRequestInfo(req);

      this.logger.log("New request: ", info.targetUrl);
      this.logger.debug("Request Method: ", req.method);
      this.logger.debug("Request Headers: ", req.headers);

      // Creating the headers for the new request
      const outHeaders: Record<string, string> = { ...info?.corsConfig?.customHeaders };

      Object.keys(req.headers).forEach((header) => {
        if (!BANNED_PROXY_HEADERS.includes(header) && !outHeaders[header]) {
          if (!info.corsConfig || info.corsConfig.allowHeaders.includes(header)) {
            outHeaders[header] = req.headers[header] as string;
          }
        }
      });

      this.logger.log("Proxying to: ", info.proxyUrl.toString());
      this.logger.debug("Proxy Method: ", req.method);
      this.logger.debug("Proxy Headers: ", outHeaders);

      const proxyResponse = await fetch(info.proxyUrl, {
        method: req.method,
        headers: outHeaders,
        redirect: "manual",
        body: req.method !== "GET" && req.method !== "HEAD" ? req : undefined,
        agent: this.getProxyAgent(info),
      });
      this.logger.debug("Proxy Response status: ", proxyResponse.status);

      // Setting up the headers to the original response...
      res.header("Access-Control-Allow-Origin", this.args.origin);

      if (req.method == "OPTIONS") {
        res.header("Access-Control-Allow-Methods", info.corsConfig?.allowMethods.join(", ") ?? "*");
        res.header("Access-Control-Allow-Headers", info.corsConfig?.allowHeaders.join(", ") ?? "*");
      }

      if (proxyResponse.headers.has("location")) {
        proxyResponse.headers.set("location", info.targetUrl);
      }

      proxyResponse.headers.forEach((value, header) => {
        if (!info.corsConfig || info.corsConfig.exposeHeaders.includes(header)) {
          res.setHeader(header, value);
        }
      });

      if (proxyResponse.redirected) {
        res.setHeader("x-redirected-url", info.proxyUrl.toString());
      }

      this.logger.debug("New Response Headers: ", res.getHeaders());

      res.status(proxyResponse.status);

      this.logger.debug("Writting Response...");
      if (proxyResponse.body) {
        const stream = proxyResponse.body.pipe(res);
        stream.on("close", () => {
          this.logger.log("Request succesfully proxied!");
          res.end();
        });
        stream.on("error", (e) => {
          this.logger.warn("Something went wrong when writting the new response... ", e);
          next();
        });
      } else {
        this.logger.log("Request succesfully proxied!");
        res.end();
      }
    } catch (err) {
      this.logger.warn("Couldn't handle request correctly due to: ", err.message);
      next();
    }
  }

  private resolveRequestInfo(request: Request): ProxyRequestInfo {
    const targetUrl: string = (request.headers[TARGET_URL_HEADER] as string) ?? request.url;

    if (!targetUrl || targetUrl == "/") {
      throw new Error("Couldn't resolve the target url...");
    }

    const proxyUrl = targetUrl.startsWith("/") ? `https:/${targetUrl}` : undefined;

    return new ProxyRequestInfo({
      targetUrl,
      proxyUrl,
      corsConfig: this.resolveCorsConfig(targetUrl, request),
      insecurelyDisableTLSCertificateValidation:
        request.headers[INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION] === "true",
    });
  }

  private getProxyAgent(info: ProxyRequestInfo): https.Agent | undefined {
    if (info.insecurelyDisableTLSCertificateValidation && info.proxyUrl.protocol === HTTPS_PROTOCOL) {
      return new https.Agent({
        rejectUnauthorized: false,
      });
    }
    return undefined;
  }

  private resolveCorsConfig(targetUrl: string, request: Request): CorsConfig | undefined {
    return isGitOperation(targetUrl, request.method, request.headers as Record<string, string>)
      ? GIT_CORS_CONFIG
      : undefined;
  }
}

class ProxyRequestInfo {
  private readonly _proxyUrl: URL;

  constructor(
    private readonly args: {
      targetUrl: string;
      proxyUrl?: string;
      corsConfig?: CorsConfig;
      insecurelyDisableTLSCertificateValidation?: boolean;
    }
  ) {
    this._proxyUrl = new URL(args.proxyUrl ?? args.targetUrl);
  }

  get targetUrl(): string {
    return this.args.targetUrl;
  }

  get proxyUrl(): URL {
    return this._proxyUrl;
  }

  get corsConfig(): CorsConfig | undefined {
    return this.args.corsConfig;
  }

  get insecurelyDisableTLSCertificateValidation() {
    return this.args.insecurelyDisableTLSCertificateValidation;
  }
}

class Logger {
  constructor(private readonly verbose: boolean) {}

  public log(message: string, arg?: any) {
    console.log(message, arg ?? "");
  }

  public debug(message: string, arg?: any) {
    if (!this.verbose) {
      return;
    }
    console.debug(message, arg ?? "");
  }

  public warn(message: string, arg?: any) {
    console.warn(message, arg ?? "");
  }
}
