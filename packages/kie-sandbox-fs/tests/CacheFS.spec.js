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

import CacheFS from "../src/CacheFS";

const treeText = require("./__fixtures__/tree.txt.js");

describe("CacheFS module", () => {
  it("print ∘ parse == id", () => {
    const fs = new CacheFS();
    let parsed = fs.parse(treeText);
    let text = fs.print(parsed);
    expect(text).toEqual(treeText);
  });
  it("size()", () => {
    const fs = new CacheFS();
    fs.activate();
    expect(fs.size()).toEqual(0);
    fs.activate(treeText);
    let inodeCount = treeText.trim().split("\n").length;
    expect(fs.size()).toEqual(inodeCount);
  });
  it("autoinc()", () => {
    const fs = new CacheFS();
    fs.activate();
    expect(fs.autoinc()).toEqual(1);
    fs.writeStat("/foo", 3, {});
    expect(fs.autoinc()).toEqual(2);
    fs.mkdir("/bar", {});
    expect(fs.autoinc()).toEqual(3);
    fs.unlink("/foo");
    expect(fs.autoinc()).toEqual(3);
    fs.mkdir("/bar/baz", {});
    expect(fs.autoinc()).toEqual(4);
    fs.rmdir("/bar/baz");
    expect(fs.autoinc()).toEqual(3);
    fs.mkdir("/bar/bar", {});
    expect(fs.autoinc()).toEqual(4);
    fs.writeStat("/bar/bar/boo", 3, {});
    expect(fs.autoinc()).toEqual(5);
    fs.unlink("/bar/bar/boo");
    expect(fs.autoinc()).toEqual(4);
  });
});
