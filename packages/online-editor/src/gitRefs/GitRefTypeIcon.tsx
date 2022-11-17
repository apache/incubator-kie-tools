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

import * as React from "react";
import CodeBranchIcon from "@patternfly/react-icons/dist/js/icons/code-branch-icon";
import TagIcon from "@patternfly/react-icons/dist/js/icons/tag-icon";
import { GitRefType } from "./GitRefs";

export function GitRefTypeIcon(props: { type: GitRefType }) {
  if (props.type === GitRefType.BRANCH) {
    return <CodeBranchIcon />;
  }
  if (props.type === GitRefType.TAG) {
    return <TagIcon />;
  }

  return <CodeBranchIcon />;
}
