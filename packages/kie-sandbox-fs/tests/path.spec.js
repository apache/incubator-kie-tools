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

const path = require("../src/path.js");

describe("path module", () => {
  describe("path.split", () => {
    it("should handle absolute paths", () => {
      expect(path.split("/hello/world.txt")).toEqual(["/", "hello", "world.txt"]);
    });
    it("should handle relative paths", () => {
      expect(path.split("./hello/world.txt")).toEqual([".", "hello", "world.txt"]);
    });
  });
  describe("path.join", () => {
    it("should join two partial paths", () => {
      expect(path.join("hello/world", "test.txt")).toEqual("hello/world/test.txt");
      expect(path.join("hello", "world", "test.txt")).toEqual("hello/world/test.txt");
    });
    it("should join a relative path and a partial path", () => {
      expect(path.join("./hello/world", "test.txt")).toEqual("./hello/world/test.txt");
      expect(path.join("./hello", "world", "test.txt")).toEqual("./hello/world/test.txt");
    });
  });
  describe("path.normalize", () => {
    it("should return normal paths unchanged", () => {
      expect(path.normalize("./hello/world.txt")).toEqual("./hello/world.txt");
      expect(path.normalize("/hello/world.txt")).toEqual("/hello/world.txt");
    });
    it("should normalize relative paths to start with ./", () => {
      expect(path.normalize("hello/world.txt")).toEqual("./hello/world.txt");
    });
    it("should normalize paths with multiple '.' to a single '.'", () => {
      expect(path.normalize("./././hello/./world.txt")).toEqual("./hello/world.txt");
    });
    it("should normalize paths with '..'", () => {
      expect(path.normalize("./hello/../world.txt")).toEqual("./world.txt");
      expect(path.normalize("/hello/../world.txt")).toEqual("/world.txt");
    });
    it("should normalize relative paths above '.'", () => {
      expect(path.normalize("./hello/../../world.txt")).toEqual("./../world.txt");
    });
    it("should refust to normalize absolute paths above '/'", () => {
      expect(() => path.normalize("/hello/../../world.txt")).toThrow();
    });
  });

  describe("path.dirname", () => {
    it("should return parent directory of file", () => {
      expect(path.dirname("./hello/world.txt")).toEqual("./hello");
      expect(path.dirname("/hello/world.txt")).toEqual("/hello");
      expect(path.dirname("/hello.txt")).toEqual("/");
    });

    it("should return parent directory of directories", () => {
      expect(path.dirname("./hello/world")).toEqual("./hello");
      expect(path.dirname("/hello/world")).toEqual("/hello");
      expect(path.dirname("/hello")).toEqual("/");
    });

    it("should throw for ambiguous cases", () => {
      expect(() => path.dirname("world.txt")).toThrow();
    });
  });

  describe("path.basename", () => {
    it("should return basename of file", () => {
      expect(path.basename("./hello/world.txt")).toEqual("world.txt");
      expect(path.basename("/hello/world.txt")).toEqual("world.txt");
      expect(path.basename("/hello.txt")).toEqual("hello.txt");
    });

    it("should return basename of directories", () => {
      expect(path.basename("./hello/world")).toEqual("world");
      expect(path.basename("/hello/world")).toEqual("world");
      expect(path.basename("/hello")).toEqual("hello");
    });

    it("should throw for ambiguous cases", () => {
      expect(() => path.basename("/")).toThrow();
    });
  });
});
