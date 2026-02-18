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

import * as http from "http";
import * as fs from "fs";
import * as path from "path";
import { startGitHttpServer } from "../src/server";

async function startServerAndWait(config: {
  port: number;
  contentRoot: string;
  logPrefix?: string;
}): Promise<http.Server> {
  const server = startGitHttpServer(config);
  return new Promise((resolve) => {
    server.on("listening", () => resolve(server));
  });
}

describe("Integration Tests", () => {
  let server: http.Server;
  let testPort = 9881;
  const testContentRoot = path.join(__dirname, "../dist-tests/test-integration-content");

  beforeEach(() => {
    if (!fs.existsSync(testContentRoot)) {
      fs.mkdirSync(testContentRoot, { recursive: true });
    }
  });

  afterEach(async () => {
    if (server) {
      await new Promise<void>((resolve, reject) => {
        server.close((err) => {
          if (err) reject(err);
          else resolve();
        });
      });
      server = null as any;
    }

    if (fs.existsSync(testContentRoot)) {
      fs.rmSync(testContentRoot, { recursive: true, force: true });
    }

    testPort++;
  });

  describe("Mixed content serving", () => {
    it("should serve both Git repos and static content simultaneously", async () => {
      // Setup
      fs.writeFileSync(path.join(testContentRoot, "index.html"), "<html><body>Test</body></html>");
      fs.mkdirSync(path.join(testContentRoot, "test.git"), { recursive: true });

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      // Test static content and Git repo endpoint concurrently
      const [staticRes, gitRes] = await Promise.all([
        fetch(`http://localhost:${testPort}/index.html`),
        fetch(`http://localhost:${testPort}/test.git/info/refs`),
      ]);

      expect(staticRes.status).toBe(200);
      expect(gitRes.status).toBeDefined();
    });

    it("should handle multiple concurrent requests", async () => {
      const files = ["file1.txt", "file2.txt", "file3.txt", "file4.txt"];
      files.forEach((file) => {
        fs.writeFileSync(path.join(testContentRoot, file), `Content of ${file}`);
      });

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      const responses = await Promise.all(files.map((file) => fetch(`http://localhost:${testPort}/${file}`)));

      responses.forEach((res) => {
        expect(res.status).toBe(200);
      });
    });
  });

  describe("Content type handling", () => {
    it("should serve HTML files with correct content type", async () => {
      fs.writeFileSync(path.join(testContentRoot, "test.html"), "<html></html>");

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      const res = await fetch(`http://localhost:${testPort}/test.html`);
      expect(res.headers.get("content-type")).toContain("text/html");
    });

    it("should serve JSON files with correct content type", async () => {
      fs.writeFileSync(path.join(testContentRoot, "data.json"), '{"key": "value"}');

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      const res = await fetch(`http://localhost:${testPort}/data.json`);
      expect(res.headers.get("content-type")).toContain("application/json");
    });

    it("should serve CSS files with correct content type", async () => {
      fs.writeFileSync(path.join(testContentRoot, "styles.css"), "body { margin: 0; }");

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      const res = await fetch(`http://localhost:${testPort}/styles.css`);
      expect(res.headers.get("content-type")).toContain("text/css");
    });
  });

  describe("Directory structure", () => {
    it("should handle nested directory structures", async () => {
      const nestedPath = path.join(testContentRoot, "level1", "level2", "level3");
      fs.mkdirSync(nestedPath, { recursive: true });
      fs.writeFileSync(path.join(nestedPath, "deep.txt"), "Deep content");

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      const res = await fetch(`http://localhost:${testPort}/level1/level2/level3/deep.txt`);
      const data = await res.text();

      expect(res.status).toBe(200);
      expect(data).toBe("Deep content");
    });

    it("should list multiple Git repositories and static directories", async () => {
      const dirs = ["repo1.git", "repo2.git", "static1", "static2"];
      dirs.forEach((dir) => {
        fs.mkdirSync(path.join(testContentRoot, dir), { recursive: true });
      });

      const consoleSpy = jest.spyOn(console, "log").mockImplementation();

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
      });

      expect(consoleSpy).toHaveBeenCalledWith(expect.stringContaining("Found 2 bare Git repo(s)"));
      expect(consoleSpy).toHaveBeenCalledWith(expect.stringContaining("Found 2 static content dir(s)"));
      consoleSpy.mockRestore();
    });
  });

  describe("Server configuration", () => {
    it("should respect custom log prefix in all operations", async () => {
      const customPrefix = "custom-test-prefix";
      const consoleSpy = jest.spyOn(console, "log").mockImplementation();

      server = await startServerAndWait({
        port: testPort,
        contentRoot: testContentRoot,
        logPrefix: customPrefix,
      });

      const allLogs = consoleSpy.mock.calls.map((call) => call[0]).join("\n");
      expect(allLogs).toContain(`[${customPrefix}]`);
      expect(allLogs).not.toContain("[git-repo-http-dev-server]");
      consoleSpy.mockRestore();
    });
  });
});
