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

function normalizePath(path) {
  if (path.length === 0) {
    return ".";
  }
  let parts = splitPath(path);
  parts = parts.reduce(reducer, []);
  return joinPath(...parts);
}

function resolvePath(...paths) {
  let result = "";
  for (let path of paths) {
    if (path.startsWith("/")) {
      result = path;
    } else {
      result = normalizePath(joinPath(result, path));
    }
  }
  return result;
}

function joinPath(...parts) {
  if (parts.length === 0) return "";
  let path = parts.join("/");
  // Replace consecutive '/'
  path = path.replace(/\/{2,}/g, "/");
  return path;
}

function splitPath(path) {
  if (path.length === 0) return [];
  if (path === "/") return ["/"];
  let parts = path.split("/");
  if (parts[parts.length - 1] === "") {
    parts.pop();
  }
  if (path[0] === "/") {
    // assert(parts[0] === '')
    parts[0] = "/";
  } else {
    if (parts[0] !== ".") {
      parts.unshift(".");
    }
  }
  return parts;
}

function dirname(path) {
  const last = path.lastIndexOf("/");
  if (last === -1) throw new Error(`Cannot get dirname of "${path}"`);
  if (last === 0) return "/";
  return path.slice(0, last);
}

function basename(path) {
  if (path === "/") throw new Error(`Cannot get basename of "${path}"`);
  const last = path.lastIndexOf("/");
  if (last === -1) return path;
  return path.slice(last + 1);
}

function reducer(ancestors, current) {
  // Initial condition
  if (ancestors.length === 0) {
    ancestors.push(current);
    return ancestors;
  }
  // assert(ancestors.length > 0)
  // assert(ancestors[0] === '.' || ancestors[0] === '/')

  // Collapse '.' references
  if (current === ".") return ancestors;

  // Collapse '..' references
  if (current === "..") {
    if (ancestors.length === 1) {
      if (ancestors[0] === "/") {
        throw new Error("Unable to normalize path - traverses above root directory");
      }
      // assert(ancestors[0] === '.')
      if (ancestors[0] === ".") {
        ancestors.push(current);
        return ancestors;
      }
    }
    // assert(ancestors.length > 1)
    if (ancestors[ancestors.length - 1] === "..") {
      ancestors.push("..");
      return ancestors;
    } else {
      ancestors.pop();
      return ancestors;
    }
  }

  ancestors.push(current);
  return ancestors;
}

module.exports = {
  join: joinPath,
  normalize: normalizePath,
  split: splitPath,
  basename,
  dirname,
  resolve: resolvePath,
};
