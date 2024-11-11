import * as http from "http";
import * as __url from "url";
import * as path from "path";
import { spawn } from "child_process";

const p = path.resolve("./dist/git-repo-bare.git");
const port = 8787;
console.log(p);

const config = defaultConfig(p);

http.createServer(requestHandler(config)).listen(port, () => {
  console.log(`Git backend listening to port ${port}.`);
  console.log(`You should be able to clone the test repository with:`);
  console.log(`git clone http://localhost:${port}/test.git`);
});

//

export function requestHandler(config) {
  return function (req, res) {
    const { env } = config(req);
    const gitHttpBackend = spawn("git", ["http-backend"], { env });

    req.pipe(gitHttpBackend.stdin);

    const buffers = {
      header: [],
      body: [],
      completedHeader: false,
    };

    gitHttpBackend.stdout.on("data", (chunk) => writeData(chunk, buffers, res));
    gitHttpBackend.on("close", () => {
      res.end();
    });
  };
}

export function writeData(chunk, buffers, res) {
  if (buffers.completedHeader) {
    res.write(chunk);
  } else {
    buffers.completedHeader = readMaybeHeaderBuffer(chunk, buffers);
    if (buffers.completedHeader) {
      writeHeader(buffers.header, res);
      writeBody(buffers.body, res);
    }
  }
}

export function writeHeader(header, res) {
  const headerLines = Buffer.concat(header).toString().split("\r\n");
  for (let headerLine of headerLines) {
    const headerSplit = headerLine.split(":");
    const headerKey = headerSplit[0];
    const headerVal = headerSplit[1];
    res.setHeader(headerKey, headerVal);
  }
}

export function writeBody(body, res) {
  body.forEach((b) => res.write(b));
}

export function readMaybeHeaderBuffer(nextBuffer, buffers) {
  const completeHeader = false;
  const length = Buffer.from("\r\n\r\n", "utf-8").length;

  const offset = nextBuffer.indexOf("\r\n\r\n", 0, "utf-8");

  if (offset > 0) {
    const headerLines = nextBuffer.slice(0, offset);
    buffers.header.push(headerLines);
    buffers.body.push(nextBuffer.slice(offset + length));

    return true;
  }
  return completeHeader;
}

//

const gitHttpBackendVariableNames = [
  "QUERY_STRING",
  "REMOTE_USER",
  "CONTENT_LENGTH",
  "HTTP_CONTENT_ENCODING",
  "REMOTE_USER",
  "REMOTE_ADDR",
  "GIT_COMMITTER_NAME",
  "GIT_COMMITTER_EMAIL",
  "CONTENT_TYPE",
  "PATH_INFO",
  "GIT_PROJECT_ROOT",
  "PATH_TRANSLATED",
  "SERVER_PROTOCOL",
  "REQUEST_METHOD",
  "GIT_HTTP_EXPORT_ALL",
  "GIT_HTTP_MAX_REQUEST_BUFFER",
];

function setGitHttpBackendVariable(variables, name, value) {
  variables[name] = value;
}

function mapHeadersToEnv(headers, options = gitHttpBackendVariableNames) {
  const processEnv = {};
  for (let header in headers) {
    const name = header.toUpperCase().replace(/-/g, "_");
    if (options.includes(name)) {
      processEnv[name] = headers[header];
    }
  }
  return processEnv;
}

function defaultConfig(projectRoot) {
  return function (req) {
    const url = __url.parse(req.url);
    // const url = new URL(req.url);
    const env = mapHeadersToEnv(req.headers, gitHttpBackendVariableNames);
    setGitHttpBackendVariable(env, "GIT_PROJECT_ROOT", projectRoot);
    setGitHttpBackendVariable(env, "PATH_TRANSLATED", projectRoot + url.pathname);
    setGitHttpBackendVariable(env, "PATH_INFO", url.pathname);
    setGitHttpBackendVariable(env, "REQUEST_METHOD", req.method);
    setGitHttpBackendVariable(env, "GIT_HTTP_EXPORT_ALL", "1");
    setGitHttpBackendVariable(env, "QUERY_STRING", url.query);
    return { env };
  };
}
