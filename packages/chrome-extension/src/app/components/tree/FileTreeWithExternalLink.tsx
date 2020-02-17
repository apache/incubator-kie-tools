/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useState } from "react";
import { extractOpenFileExtension } from "../../utils";
import { ExternalEditorManager } from "../../../ExternalEditorManager";
import { Router } from "@kogito-tooling/core-api";
import { OpenExternalEditorButton } from "./OpenExternalEditorButton";
import { useGlobals } from "../common/GlobalContext";

export function FileTreeWithExternalLink(props: {
  router: Router;
  externalEditorManager?: ExternalEditorManager | undefined;
}) {
  const globals = useGlobals();
  const treeView = globals.dependencies.treeView;
  return (
    <>
      {treeView
        .filesLinksContainers()
        .filter(container => isBlob(treeView.fileLinkTarget(container)!))
        .filter(container => {
          const fileLink = treeView.fileLinkTarget(container)!;
          const ext = extractOpenFileExtension(fileLink.href);
          return props.router.getLanguageData(ext as any);
        })
        .map(container => {
          const id = "external_editor_" + treeView.fileLinkTarget(container)!.id;
          if (!document.getElementById(id)) {
            return (
              <OpenExternalEditorButton
                id={id}
                href={createTargetUrl(treeView.fileLinkTarget(container)!, props.externalEditorManager)}
                container={container}
              />
            );
          }
        })}
    </>
  );
}

function isBlob(fileLink: HTMLAnchorElement) {
  return fileLink && fileLink.pathname.split("/")[3] === "blob";
}
function createTargetUrl(fileLink: HTMLAnchorElement, externalEditorManager?: ExternalEditorManager): string {
  const split = fileLink.pathname.split("/");
  split.splice(0, 1);
  split.splice(2, 1);
  const linkToOpen = split.join("/");
  return externalEditorManager!.getLink(linkToOpen);
}
