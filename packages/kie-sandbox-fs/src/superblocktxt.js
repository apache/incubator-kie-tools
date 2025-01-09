#! /usr/bin/env node

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

var fs = require("fs");
var symLinks = {};

module.exports = (dirpath) => {
  let str = "";
  const printTree = (root, indent) => {
    let files = fs.readdirSync(root);
    for (let file of files) {
      // Ignore itself
      if (file === ".superblock.txt") continue;

      let fpath = `${root}/${file}`;
      let lstat = fs.lstatSync(fpath);
      // Avoid infinite loops.
      if (lstat.isSymbolicLink()) {
        if (!symLinks[lstat.dev]) {
          symLinks[lstat.dev] = {};
        }
        // Skip this entry if we've seen it before
        if (symLinks[lstat.dev][lstat.ino]) {
          continue;
        }
        symLinks[lstat.dev][lstat.ino] = true;
      }
      let mode = lstat.mode.toString(8);
      str += `${"\t".repeat(indent)}`;
      if (lstat.isDirectory()) {
        str += `${file}\t${mode}\n`;
        printTree(fpath, indent + 1);
      } else {
        str += `${file}\t${mode}\t${lstat.size}\t${lstat.mtimeMs}\n`;
      }
    }
  };
  printTree(dirpath, 0);
  return str;
};

if (!module.parent) {
  let filepath = process.cwd() + "/.superblock.txt";
  let contents = module.exports(process.cwd());
  fs.writeFileSync(filepath, contents);
}
