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

import { getPortPromise } from "portfinder";
import * as http from "http";
import * as fs from "fs";
import * as path from "path";

export class ComponentServer {
  private server: http.Server;
  public port: number | undefined;

  requestListener = (request: any, response: any) => {
    if (["/", "", "index.html"].includes(request.url)) {
      response.writeHead(200);
      response.end("Components server is alive! Base path is " + this.componentsPath);
      return;
    }

    const userInput = path.normalize(request.url).replace(/^(\.\.(\/|\\|$))+/, "");
    const filePath = path.join(this.componentsPath, userInput);
    if (filePath.indexOf(this.componentsPath) !== 0) {
      console.debug("Denying access to file " + filePath);
      response.writeHead(403);
      return;
    }
    console.debug("Requesting file: " + filePath);

    fs.readFile(filePath, function (error, content) {
      if (error) {
        if (error.code == "ENOENT") {
          response.writeHead(404);
          response.end();
        } else {
          response.writeHead(500);
          response.end();
        }
      } else {
        response.writeHead(200);
        response.end(content, "utf-8");
      }
    });
  };

  constructor(private readonly componentsPath: string) {}

  identify(): string {
    return "Components HTTP Server";
  }
  public async start(): Promise<void> {
    console.debug("Attempt to start component server!");
    this.server = http.createServer(this.requestListener);
    this.server.listen(this.port, "localhost", () => {
      console.debug("Components Server is running");
    });
  }

  stop(): void {
    if (this.server) {
      this.server.close();
    }
  }

  public async satisfyRequirements(): Promise<boolean> {
    try {
      this.port = await getPortPromise({ port: 8001 });
      return true;
    } catch (e) {
      console.error(e);
      return false;
    }
  }
}
