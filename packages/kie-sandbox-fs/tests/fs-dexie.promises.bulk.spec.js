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
import DexieBackend from "../src/DexieBackend";
import DefaultBackend from "../src/DefaultBackend";

const fs = new KieSandboxFs("testfs-dexie-promises-bulk", {
  wipe: true,
  backend: new DefaultBackend({
    idbBackendDelegate: (fileDbName, fileStoreName) => {
      return new DexieBackend(fileDbName, fileStoreName);
    },
  }),
}).promises;

const HELLO = new Uint8Array([72, 69, 76, 76, 79]);
const hello = new Uint8Array([104, 101, 108, 108, 111]);

describe("bulk::dexie::fs.promises module", () => {
  describe("bulk::dexie", () => {
    it("bulk", (done) => {
      fs.mkdir("/bulk").then(() => {
        fs.writeFileBulk([
          ["/bulk/a.txt", "hello"],
          ["/bulk/b.txt", HELLO],
        ]).then(() => {
          fs.readFileBulk(["/bulk/a.txt", "/bulk/b.txt"], { encoding: "utf8" }).then((files) => {
            expect(files.length).toBe(2);
            expect(files[0]).toEqual(["/bulk/a.txt", "hello"]);
            expect(files[1]).toEqual(["/bulk/b.txt", "HELLO"]);
            fs.readFileBulk(["/bulk/a.txt", "/bulk/b.txt"]).then((files) => {
              expect(files.length).toBe(2);
              expect(files[0]).toEqual(["/bulk/a.txt", hello]);
              expect(files[1]).toEqual(["/bulk/b.txt", HELLO]);
              fs.unlinkBulk(["/bulk/a.txt", "/bulk/b.txt"]).then(() => {
                fs.readdir("/bulk").then((files) => {
                  expect(files.length).toBe(0);
                  done();
                });
              });
            });
          });
        });
      });
    });
  });
});
