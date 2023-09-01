/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Node } from "./Node";

export class TokenTree {
  private readonly root: Node;
  private currentNode?: Node;

  constructor() {
    this.root = new Node();
  }

  addName(tokens: string[]) {
    let current = this.root;

    for (const token of tokens) {
      let next = this.findToken(current, token);
      if (!next) {
        next = new Node(token, current);
        current.children.push(next);
      }
      current = next;
    }
  }

  private findToken(current: Node, t: string): Node | undefined {
    for (const n of current.children) {
      if (n.token === t) {
        return n;
      }
    }
    return undefined;
  }

  start(t: string) {
    this.currentNode = this.findToken(this.root, t);
  }

  followUp(t: string, commit: boolean) {
    if (this.currentNode == null) {
      // this happens when the start() call above does not
      // find a root token
      return false;
    }
    const node = this.findToken(this.currentNode, t);
    if (commit) {
      this.currentNode = node;
    }
    return node != null;
  }
}
