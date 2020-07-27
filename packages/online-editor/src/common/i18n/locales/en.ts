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

import { OnlineI18n } from "..";
import { en as en_terms } from "@kogito-tooling/i18n-terms";

export const en: OnlineI18n = {
  ...en_terms,
  downloadHubModal: {
    beforeDownload: {
      title: "The Business Modeler Hub Preview allows you to access",
      vscodeDescription:
        "Installs VS Code extension and gives you a convenient way to launch VS Code ready to work with Kogito.",
      githubChromeExtensionDescription:
        "Provides detailed instructions on how to install Kogito GitHub Extension for Chrome.",
      desktop: {
        title: "Desktop App",
        description: "Installs the Business Modeler desktop app for use locally and offline."
      },
      businessModelerDescription: "Provides a quick link to access the website in the same hub."
    },
    afterDownload: {
      title: "Thank you for downloading Business Modeler Hub Preview",
      message: "If the download does not begin automatically,",
      link: "click here"
    }
  },
  editorFullScreenToolbar: "Exit full screen",
  editorPage: {
    alerts: {
      copy: "Content copied to clipboard",
      unsaved: {
        title: "Unsaved changes will be lost",
        message: "Click Save to download your progress before closing.",
        closeWithoutSaving: "Close without saving"
      }
    }
  },
  editorToolbar: {
    sendChangesToGitHub: "Send changes to GitHub",
    copySource: "Copy Source",
    gistIt: "Gist it",
    fileActions: "File actions"
  },
  githubTokenModal: {
    header: "Authentication required for exporting to GitHub gist.",
    footer: {
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here"
    },
    body: {
      disclaimer:
        "By authenticating with your OAuth Token we are able to create gists so you can share your diagrams with your colleagues. The token you provide is locally stored as browser cookies and it is never shared with anyone.",
      learnMore: "Learn more about GitHub tokens",
      note: "You should provide a token with the 'gist' permission."
    }
  },
  homePage: {
    uploadFile: {
      dndZone: {
        invalidFile: "File extension is not supported",
        waitingFile: "Drop a BPMN or DMN file here"
      },
      fileInput: "File extension is not supported"
    },
    openUrl: {
      validating: "Validating URL",
      invalidGistExtension: "File type on the provided gist is not supported.",
      invalidExtension: "File type on the provided URL is not supported.",
      invalidGist: "Enter a valid Gist URL.",
      invalidUrl: 'This URL is not valid (don\'t forget "https://"!).',
      notFoundUrl: "This URL does not exist.",
      corsNotAvailable: "This URL cannot be opened because it doesn't allow other websites to access it.",
      openFromSource: "Open from source",
      description: "Paste a URL to a source code link (GitHub, Dropbox, etc.)"
    },
    dropdown: {
      getHub: "Get Business Modeler Hub Preview",
      onlineForum: "Online forum"
    },
    header: {
      title: "Asset Editor for Kogito and Process Automation",
      welcomeText:
        "Welcome to Business Modeler! These simple BPMN and DMN editors are here to allow you to collaborate quickly\n and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch\n in the forum or review the documentation for more information."
    },
    bpmnCard: {
      title: "Workflow (.BPMN)",
      explanation: "BPMN files are used to generate business processes.",
      createNew: "Create new workflow"
    },
    dmnCard: {
      title: "Decision model (.DMN)",
      explanation: "DMN files are used to generate decision models",
      createNew: "Create new decision model"
    },
    trySample: "Try Sample",
    editExistingFile: "Edit existing file",
    chooseLocalFile: "Choose a local file"
  }
};
