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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";

const fs = new KieSandboxFs();
const pfs = fs.promises;

describe("hotswap backends", () => {
  xit("re-init with new backend", async () => {
    // write a file
    fs.init("testfs-1", { wipe: true });
    await pfs.writeFile("/a.txt", "HELLO");
    expect(await pfs.readFile("/a.txt", "utf8")).toBe("HELLO");

    // we swap backends. file is gone
    fs.init("testfs-2", { wipe: true });
    let err = null;
    try {
      await pfs.readFile("/a.txt", "utf8");
    } catch (e) {
      err = e;
    }
    expect(err).not.toBeNull();
    expect(err.code).toBe("ENOENT");
  });
});
