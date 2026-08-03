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

import git from "isomorphic-git";
import { KieSandboxWorkspacesFs } from "../services/KieSandboxWorkspaceFs";
import { SigningArgs, buildSignOptions } from "./SigningArgs";

type ReadCommitResult = Awaited<ReturnType<typeof git.readCommit>>;

/**
 * Signs existing commits by recreating them with a signature attached. Recreating a commit changes
 * its id, so every commit after it is recreated too.
 */
export class CommitHistorySigner {
  public constructor(private readonly args: { fs: KieSandboxWorkspacesFs; dir: string }) {}

  /**
   * Signs the commits on `ref` that are not reachable from `until`, and moves `ref` to the new tip.
   * It makes no changes when every one of them is already signed, or when `ref` moved while the
   * signatures were being produced.
   */
  public async signUnpushed(args: {
    ref: string;
    author: { name: string; email: string };
    signing: SigningArgs;
    until?: string;
  }): Promise<void> {
    const { fs, dir } = this.args;
    const tipAtStart = await git.resolveRef({ fs, dir, ref: args.ref });

    const unpushed = await this.listUnpushedCommits({ ref: args.ref, until: args.until });
    if (unpushed === undefined) {
      return;
    }

    let oldestUnsignedIndex = -1;
    for (let i = 0; i < unpushed.length; i++) {
      if (!unpushed[i].commit.gpgsig) {
        oldestUnsignedIndex = i;
      }
    }
    if (oldestUnsignedIndex === -1) {
      return;
    }

    // `unpushed` is newest first, `recreateCommits` takes oldest first.
    const newHeadOid = await this.recreateCommits({
      commits: unpushed.slice(0, oldestUnsignedIndex + 1).reverse(),
      author: args.author,
      signing: args.signing,
    });

    // Stop if `ref` moved while the signatures were being produced.
    if ((await git.resolveRef({ fs, dir, ref: args.ref })) !== tipAtStart) {
      return;
    }

    // `ref` can expand to a ref other than `refs/heads/<branch>`.
    const pushRef = await git.expandRef({ fs, dir, ref: args.ref });
    await git.writeRef({ fs, dir, ref: pushRef, value: newHeadOid, force: true });

    // `depth: 2` yields the branch a symbolic HEAD points at, or an oid when HEAD is detached.
    const headRef = await git.resolveRef({ fs, dir, ref: "HEAD", depth: 2 });
    if (headRef !== pushRef) {
      const ref = headRef.startsWith("refs/") ? headRef : "HEAD";
      await git.writeRef({ fs, dir, ref, value: newHeadOid, force: true });
    }
  }

  /**
   * The commits on `ref` that `until` does not already reach, newest first. Undefined when the point
   * where the two histories meet cannot be found.
   */
  private async listUnpushedCommits(args: { ref: string; until?: string }): Promise<ReadCommitResult[] | undefined> {
    const chain = await this.walkFirstParents({ ref: args.ref, stopAt: args.until });
    if (args.until === undefined || chain.stoppedAtTarget) {
      return chain.commits;
    }

    // The chain missed `until`. The commits it reaches are a suffix of the chain, so the first one
    // found searching back from `until` is where the two meet.
    const boundary = await this.findFirstReachable({
      from: args.until,
      targets: new Set(chain.commits.map((entry) => entry.oid)),
    });
    if (boundary === undefined) {
      return undefined;
    }
    return chain.commits.slice(
      0,
      chain.commits.findIndex((entry) => entry.oid === boundary)
    );
  }

  /**
   * Walks first parents from `ref`, newest first. `stoppedAtTarget` is true only when `stopAt` ended
   * the walk, not when the history ran out.
   */
  private async walkFirstParents(args: {
    ref: string;
    stopAt?: string;
  }): Promise<{ commits: ReadCommitResult[]; stoppedAtTarget: boolean }> {
    const { fs, dir } = this.args;
    const commits: ReadCommitResult[] = [];
    let oid: string | undefined = await git.resolveRef({ fs, dir, ref: args.ref });
    while (oid !== undefined && oid !== args.stopAt) {
      let entry: ReadCommitResult;
      try {
        entry = await git.readCommit({ fs, dir, oid });
      } catch {
        // Not in the local object store, as at the boundary of a shallow clone.
        return { commits, stoppedAtTarget: false };
      }
      commits.push(entry);
      oid = entry.commit.parent[0];
    }
    return { commits, stoppedAtTarget: oid !== undefined };
  }

  /**
   * The first commit reachable from `from` that is in `targets`, searched breadth first. Undefined
   * when `from` is missing locally, or when it reaches nothing in `targets`.
   */
  private async findFirstReachable(args: { from: string; targets: Set<string> }): Promise<string | undefined> {
    const { fs, dir } = this.args;
    const visited = new Set<string>();
    const queue: string[] = [args.from];
    for (let i = 0; i < queue.length; i++) {
      const oid = queue[i];
      if (visited.has(oid)) {
        continue;
      }
      visited.add(oid);
      if (args.targets.has(oid)) {
        return oid;
      }
      try {
        const entry = await git.readCommit({ fs, dir, oid });
        queue.push(...entry.commit.parent);
      } catch {
        // Not in the local object store, so its parents are unreachable.
      }
    }
    return undefined;
  }

  /**
   * Recreates `commits`, ordered oldest first, each keeping its tree, message and author. A commit
   * whose author has no email is attributed to `author`. Returns the oid of the last one.
   */
  private async recreateCommits(args: {
    commits: ReadCommitResult[];
    author: { name: string; email: string };
    signing: SigningArgs;
  }): Promise<string> {
    const { fs, dir } = this.args;
    const newOidByOldOid = new Map<string, string>();
    let newHeadOid = "";
    for (const entry of args.commits) {
      const commit = entry.commit;
      newHeadOid = await git.commit({
        fs,
        dir,
        message: commit.message,
        tree: commit.tree,
        parent: commit.parent.map((oid) => newOidByOldOid.get(oid) ?? oid),
        author: commit.author.email ? commit.author : { ...commit.author, ...args.author },
        committer: { ...commit.committer, ...args.author },
        noUpdateBranch: true,
        ...buildSignOptions(args.signing),
      });
      newOidByOldOid.set(entry.oid, newHeadOid);
    }
    return newHeadOid;
  }
}
