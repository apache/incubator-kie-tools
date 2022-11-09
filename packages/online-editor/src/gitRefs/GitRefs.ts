/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

export enum GitRefType {
  BRANCH = "branch",
  GITHUB_PULL_REQUEST = "github_pull_request",
  GITLAB_MERGE_REQUEST = "gitlab_merge_request",
  TAG = "tag",
  OTHER = "other",
}

export function getGitRefTypeLabel(type: GitRefType) {
  switch (type) {
    case GitRefType.BRANCH:
      return "Branches";
    case GitRefType.GITHUB_PULL_REQUEST:
      return "Pull requests";
    case GitRefType.GITLAB_MERGE_REQUEST:
      return "Merge requests";
    case GitRefType.TAG:
      return "Tags";
    case GitRefType.OTHER:
      return "Other";
  }
}

export function getGitRefName(ref: string | undefined) {
  if (!ref) {
    return "";
  }
  return ref
    .replace("refs/heads/", "")
    .replace("refs/merge-requests/", "")
    .replace("refs/tags/", "")
    .replace("refs/pull/", "");
}

export function getGitRefType(ref: string | undefined) {
  if (ref?.startsWith("refs/heads")) {
    return GitRefType.BRANCH;
  } else if (ref?.startsWith("refs/pull")) {
    return GitRefType.GITHUB_PULL_REQUEST;
  } else if (ref?.startsWith("refs/tags")) {
    return GitRefType.TAG;
  } else if (ref?.startsWith("refs/merge-requests")) {
    return GitRefType.GITLAB_MERGE_REQUEST;
  } else {
    return GitRefType.OTHER;
  }
}
