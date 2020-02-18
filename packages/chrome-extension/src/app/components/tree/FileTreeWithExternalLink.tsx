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
import * as ReactDOM from "react-dom";
import { useMemo } from "react";
import { extractOpenFileExtension } from "../../utils";
import { ExternalEditorManager } from "../../../ExternalEditorManager";
import { OpenExternalEditorButton } from "./OpenExternalEditorButton";
import { useGlobals } from "../common/GlobalContext";

export function FileTreeWithExternalLink() {
  const { externalEditorManager, router, dependencies } = useGlobals();

  const externalLinksInfo = useMemo(
    () =>
      dependencies.treeView
        .linksToFiles()
        .filter(fileLink => router.getLanguageData(extractOpenFileExtension(fileLink.href) as any))
        .map(fileLink => ({
          id: idForLink(fileLink),
          container: fileLink.parentElement!.parentElement!,
          url: createTargetUrl(fileLink.pathname, externalEditorManager)
        }))
        .filter(externalLinkInfo => !document.getElementById(externalLinkInfo.id)),
    []
  );

  return (
    <>
      {externalLinksInfo.map(linkInfo =>
        ReactDOM.createPortal(<OpenExternalEditorButton id={linkInfo.id} href={linkInfo.url} />, linkInfo.container)
      )}
    </>
  );
}

function idForLink(fileLink: HTMLAnchorElement): string {
  return "external_editor_" + fileLink.id;
}

function createTargetUrl(pathname: string, externalEditorManager?: ExternalEditorManager): string {
  const split = pathname.split("/");
  split.splice(0, 1);
  split.splice(2, 1);
  const linkToOpen = split.join("/");
  return externalEditorManager!.getLink(linkToOpen);
}
