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

import * as React from "react";
import { useMemo } from "react";
import { useGlobals } from "../common/GlobalContext";
import { parsePrInfo } from "../pr/prEditors";
import { GitHubPageType } from "../../github/GitHubPageType";

export function OpenInExternalEditorButton(props: { className: string; pageType: GitHubPageType }) {
  const globals = useGlobals();
  const repoUrl = useMemo(() => {
    if (
      props.pageType === GitHubPageType.PR_HOME ||
      props.pageType === GitHubPageType.PR_FILES ||
      props.pageType === GitHubPageType.PR_COMMITS
    ) {
      const prInfo = parsePrInfo(globals.dependencies);
      return `${window.location.origin}/${prInfo.org}/${prInfo.repo}/tree/${prInfo.gitRef}`;
    }

    return window.location.href;
  }, [globals.dependencies, props.pageType]);

  return (
    <>
      {globals.externalEditorManager?.getImportRepoUrl && (
        <a
          className={props.className}
          href={globals.externalEditorManager?.getImportRepoUrl(repoUrl)}
          target={"_blank"}
        >
          <img alt="ext-logo" src={globals.extensionIconUrl} />
          Open in {globals.externalEditorManager.name}
        </a>
      )}
    </>
  );
}
