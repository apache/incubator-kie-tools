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

import * as ReactDOM from "react-dom";
import * as React from "react";
import { Router } from "@kogito-tooling/core-api";
import { ExternalEditorManager } from "../../../ExternalEditorManager";
import { extractOpenFileExtension } from "../../utils";
import { OpenExternalEditorButton } from "./OpenExternalEditorButton";

const FILES_CONTAINER_SELECTOR = "div.file-wrap"
const LINK_CONTAINER_SELECTOR = "table.files > tbody > tr > td.content";
const LINK_SELECTOR = "span > a";

export function addExternalEditorLinks(args: {
    router: Router,
    externalEditorManager?: ExternalEditorManager
}) {
    addLinksToExternalEditor(args.router, args.externalEditorManager);
    const observer = new MutationObserver(() => addLinksToExternalEditor(args.router, args.externalEditorManager));
    observer.observe(document.querySelector(FILES_CONTAINER_SELECTOR)!, { childList: true });
}

function addLinksToExternalEditor(router: Router, externalEditorManager?: ExternalEditorManager | undefined) {
    getLinkContainers()
        .filter(container => container.childElementCount > 0)
        .filter(container => getLinkToAsset(container)!.pathname.split('/')[3] === 'blob')
        .filter(container => {
            const fileLink = getLinkToAsset(container)!;
            const ext = extractOpenFileExtension(fileLink.href);
            return router.getLanguageData(ext as any);
        })
        .forEach(container => {
            const fileLink = getLinkToAsset(container)!;
            if (!fileLink) {
                return;
            }
            const parentId = getLinkParentId(fileLink.id);
            let parentDiv = document.getElementById(parentId);
            if (!parentDiv) {
                parentDiv = createLinkParentDiv(parentId);
                container.append(parentDiv);
                ReactDOM.render(<OpenExternalEditorButton href={createTargetUrl(fileLink, externalEditorManager)} />, parentDiv);
            }
        });
}

function createLinkParentDiv(id: string) {
    const parentDiv = document.createElement('div');
    parentDiv.id = id;
    parentDiv.className = "float-right";
    return parentDiv;
}

function createTargetUrl(
    fileLink: HTMLAnchorElement,
    externalEditorManager?: ExternalEditorManager): string {
    const split = fileLink.pathname.split("/")
    split.splice(0, 1);
    split.splice(2, 1);
    const linkToOpen = split.join("/");
    return externalEditorManager!.getLink(linkToOpen);
}

function getLinkContainers(): HTMLElement[] {
    return Array.from(document.querySelectorAll(LINK_CONTAINER_SELECTOR));
}
function getLinkToAsset(parent: HTMLElement): (HTMLAnchorElement | null) {
    return parent.querySelector(LINK_SELECTOR);
}

function getLinkParentId(linkId: string) {
    return "external_link_" + linkId;
}