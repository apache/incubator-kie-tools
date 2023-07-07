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
import { Request, Response } from "express";
import { CorsConfig, CorsProxy, TARGET_URL_HEADER } from "./types";
import { GIT_CORS_CONFIG, isGitOperation } from "./git";
import fetch from "node-fetch";

const BANNED_PROXY_HEADERS = ["origin", "host"];

export class ExpressCorsProxy implements CorsProxy<Request, Response> {
  private readonly logger: Logger;

  constructor(private readonly origin: string, verbose: boolean = false) {
    this.logger = new Logger(verbose);
    this.logger.log("Starting in verbose mode...");
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

      this.logger.log("Proxying to: ", info.proxyUrl);
      this.logger.debug("Proxy Method: ", req.method);
      this.logger.debug("Proxy Headers: ", outHeaders);

      const proxyResponse = await fetch(info.proxyUrl, {
        method: req.method,
        headers: outHeaders,
        redirect: "manual",
        body: req.method !== "GET" && req.method !== "HEAD" ? req : undefined,
      });
      this.logger.debug("Proxy Response status: ", proxyResponse.status);

      // Setting up the headers to the original response...
      res.header("Access-Control-Allow-Origin", this.origin);

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
        res.setHeader("x-redirected-url", info.proxyUrl);
      }

      this.logger.debug("New Response Headers: ", res.getHeaders());

      res.status(proxyResponse.status);

      this.logger.debug("Writting Response...");
      if (proxyResponse.body) {
        const stream = proxyResponse.body.pipe(res);
        stream.on("end", () => {
          res.end();
          this.logger.log("Request succesfully proxied!");
        });
        stream.on("error", (e) => {
          this.logger.warn("Something went wrong when writting the new response... ", e);
          next();
        });
      } else {
        res.end();
        this.logger.log("Request succesfully proxied!");
      }
    } catch (err) {
      this.logger.warn("Couldn't handle request correctly due to: ", err);
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
    });
  }

  private resolveCorsConfig(targetUrl: string, request: Request): CorsConfig | undefined {
    return isGitOperation(targetUrl, {
      method: request.method,
      headers: request.headers as Record<string, string>,
    })
      ? GIT_CORS_CONFIG
      : undefined;
  }
}

class ProxyRequestInfo {
  constructor(
    private readonly args: {
      targetUrl: string;
      proxyUrl?: string;
      corsConfig?: CorsConfig;
    }
  ) {}

  get targetUrl(): string {
    return this.args.targetUrl;
  }

  get proxyUrl(): string {
    return this.args.proxyUrl ?? this.targetUrl;
  }

  get corsConfig(): CorsConfig | undefined {
    return this.args.corsConfig;
  }
}

class Logger {
  constructor(private readonly enabled: boolean) {}

  public log(message: string, arg?: any) {
    console.log(message, arg ?? "");
  }

  public debug(message: string, arg?: any) {
    if (!this.enabled) {
      return;
    }
    console.debug(message, arg ?? "");
  }

  public warn(message: string, arg?: any) {
    console.warn(message, arg ?? "");
  }
}
