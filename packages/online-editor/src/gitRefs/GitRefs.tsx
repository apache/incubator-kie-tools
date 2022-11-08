import * as React from "react";
import CodeBranchIcon from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import TagIcon from "@patternfly/react-icons/dist/js/icons/tag-icon";

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

export function GitRefTypeIcon(props: { type: GitRefType }) {
  if (props.type === GitRefType.BRANCH) {
    return <CodeBranchIcon />;
  }
  if (props.type === GitRefType.TAG) {
    return <TagIcon />;
  }

  return <CodeBranchIcon />;
}
