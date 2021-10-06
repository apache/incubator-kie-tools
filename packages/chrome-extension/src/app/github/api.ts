/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Octokit } from "@octokit/rest";
import { ContentType } from "@kie-tooling-core/workspace/dist/api";

export function fetchFile(
  octokit: Octokit,
  org: string,
  repo: string,
  ref: string,
  path: string,
  contentType?: ContentType
) {
  return octokit.repos
    .getContent({
      repo: repo,
      owner: org,
      ref: ref,
      path: path,
    })
    .then((res) => (contentType === ContentType.BINARY ? (res.data as any).content : atob((res.data as any).content)))
    .catch((e) => {
      console.debug(`Error fetching ${path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
      return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${ref}/${path}`).then((res) =>
        res.ok ? res.text() : Promise.resolve(undefined)
      );
    });
}
