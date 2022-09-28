import { LocalHttpServer } from "@kie-tools-core/backend/dist/api";
import { getPortPromise } from "portfinder";
import * as http from "http";
import * as fs from "fs";

export class ComponentServer extends LocalHttpServer {
  private componentsPath: string;
  private server: http.Server;

  requestListener = (request: any, response: any) => {
    if (request.url == "/" || request.url == "" || request.url == "index.html") {
      response.writeHead(200);
      response.end("Components server is alive! Base path is " + this.componentsPath);
      return;
    }

    const filePath = this.componentsPath + request.url;
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

  constructor(componentsPath: string) {
    super();
    this.componentsPath = componentsPath;
  }

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
