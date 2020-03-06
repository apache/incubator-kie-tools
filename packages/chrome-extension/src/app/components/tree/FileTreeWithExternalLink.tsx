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
import { useMemo, useEffect, useState, useCallback } from "react";
import { extractOpenFileExtension } from "../../utils";
import { ExternalEditorManager } from "../../../ExternalEditorManager";
import { OpenExternalEditorButton } from "./OpenExternalEditorButton";
import { useGlobals } from "../common/GlobalContext";
import { Router } from "@kogito-tooling/core-api";

interface ExternalLinkInfo {
  id: string;
  url: string;
  container: HTMLElement;
}

export function FileTreeWithExternalLink() {
  const { externalEditorManager, router, dependencies } = useGlobals();

  const filteredLinks = useCallback(() => filterLinks(dependencies.treeView.linksToFiles(), router), []);
  const [linksToFiles, setLinksToFiles] = useState(filteredLinks());

  useHtmlElementChangeListener(dependencies.treeView.repositoryContainer()!, () => {
    const linksToAdd = filteredLinks();
    if (linksToAdd.length > 0) {
      setLinksToFiles(linksToAdd);
    }
  });

  const externalLinksInfo = useMemo(
    () =>
      filteredLinks().map(fileLink => ({
        id: externalLinkId(fileLink),
        container: fileLink.parentElement!.parentElement!,
        url: createTargetUrl(fileLink.pathname, externalEditorManager)
      })),
    [linksToFiles]
  );

  return (
    <>
      {externalLinksInfo.map(linkInfo =>
        ReactDOM.createPortal(<OpenExternalEditorButton id={linkInfo.id} href={linkInfo.url} />, linkInfo.container)
      )}
    </>
  );
}

function filterLinks(links: HTMLAnchorElement[], router: Router): HTMLAnchorElement[] {
  return links
    .filter(fileLink => router.getLanguageData(extractOpenFileExtension(fileLink.href) as any))
    .filter(fileLink => !document.getElementById(externalLinkId(fileLink)));
}

function useHtmlElementChangeListener(target: HTMLElement, action: () => void) {
  useEffect(() => {
    const elementObserver = new MutationObserver(e => action());
    elementObserver.observe(target, {
      childList: true,
      subtree: true
    });
    return () => elementObserver.disconnect();
  }, []);
}

function externalLinkId(fileLink: HTMLAnchorElement): string {
  return "external_editor_" + fileLink.id;
}

export function createTargetUrl(pathname: string, externalEditorManager?: ExternalEditorManager): string {
  const split = pathname.split("/");
  split.splice(0, 1);
  split.splice(2, 1);
  const linkToOpen = split.join("/");
  return externalEditorManager!.getLink(linkToOpen);
}
