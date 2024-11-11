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

import * as express from "express";
import * as cors from "cors";

import { ExpressCorsProxy } from "./ExpressCorsProxy";

export type ServerArgs = {
  port: number;
  origin: string;
  verbose: boolean;
  domainsToUseHttp: string[];
};

export const startServer = (args: ServerArgs): void => {
  console.log("Starting CORS proxy...");
  console.log("=================================");
  console.log(`Origin:                     ${args.origin}`);
  console.log(`Port:                       ${args.port}`);
  console.log(`Verbose:                    ${args.verbose}`);
  console.log(`Domains to proxy with HTTP: ${args.domainsToUseHttp}`);
  console.log("=================================");

  const app: express.Express = express();

  app.disable("x-powered-by");

  const proxy = new ExpressCorsProxy(args);

  const corsHandler = cors();

  app.use(corsHandler);
  app.options("/", corsHandler); // enable pre-flight requests

  // Just to avoid proxying the favicon if requested from browser
  app.use("/favicon.ico", (_req: express.Request, res: express.Response) => {
    res.status(200).send();
  });

  // Ping handler
  app.use("/ping", (_req: express.Request, res: express.Response) => {
    res.status(200).send("pong");
  });

  // Default handler
  app.use("/", (req: express.Request, res: express.Response, next: express.NextFunction) => {
    proxy.handle(req, res, next);
  });

  // Fallback that will be executed it the Proxy cannot handle the request!
  app.use("/", (_req: express.Request, res: express.Response) => {
    res.setHeader("content-type", "text/html");
    res.status(403).send(`<!DOCTYPE html>
    <html>
      <title>@kie-tools/cors-proxy</title>
      <h1>@kie-tools/cors-proxy</h1>
      <p>This is a CORS proxy software intended to be used to proxy requests.</p>
      <p>The source code is hosted on GitHub at <a href="https://github.com/apache/incubator-kie-tools/tree/main/packages/cors-proxy">@kie-tools/cors-proxy</a></p>
      <p>It can also be installed from NPM with <code>npm install <a href="https://npmjs.org/package/@kie-tools/cors-proxy">@kie-tools/cors-proxy</a></code></p>
    </html>`);
  });

  app.listen(args.port, () => console.log(`CORS proxy listening at port ${args.port}`));
};
