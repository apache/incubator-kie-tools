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

import { ChromeExtensionI18n } from "..";

export const en: ChromeExtensionI18n = {
  openIn: (name: string) => `Open in ${name}`,
  seeAsDiagram: "See as diagram",
  fullScreen: "Full Screen",
  reset: "Reset",
  note: "Note",
  single: {
    exitFullScreen: `Exit Full Screen`,
    editorToolbar: {
      fixAndSeeAsDiagram: "Fix your file and try reopening it.",
      errorOpeningFile: `Can't open Editor for this file.`,
      seeAsSource: "See as source",
      copyLinkTo: (name: string) => `Copy link to ${name}`,
      linkCopied: "Link copied to clipboard",
      readOnly: "This is a read-only visualization.",
    },
  },
  pr: {
    isolated: {
      viewOriginal: "View original file",
    },
    toolbar: {
      closeDiagram: "Close diagram",
      original: "Original",
      changes: "Changes",
    },
  },
  common: {
    menu: {
      createToken: "Create token",
      placeYourToken: "Place your token here...",
      tokenInfo: {
        title: "Tokens are only stored locally as cookies.",
        disclaimer: "We never store or share your token with anyone.",
        explanation: `We use your GitHub OAuth Token to provide a better experience while using custom editors. The official GitHub API has a throttling mechanism with a fairly low threshold for unauthenticated requests.`,
        whichPermissionUserGive: `By authenticating with your OAuth Token we are able to avoid delays when fetching recently updated files and also provide features that need to read from your repositories, like Work Item Definitions on BPMN diagrams.`,
        permission: `${"For public repositories, no special permissions are required".bold()}. In fact, you can generate a Token without ticking any checkbox. For private repositories, however, you should provide a Token with the ${"'repo'".bold()} permission.`,
      },
    },
  },
};
