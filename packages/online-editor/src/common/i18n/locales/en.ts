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
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: OnlineI18n = {
  ...en_common,
  downloadHubModal: {
    beforeDownload: {
      title: `The ${en_common.names.businessModeler.hub} allows you to access`,
      vscodeDescription: `Installs ${en_common.names.vscode} extension and gives you a convenient way to launch ${en_common.names.vscode} ready to work with ${en_common.names.kogito}.`,
      githubChromeExtension: {
        title: `${en_common.names.github}  ${en_common.names.chrome} Extension`,
        description: `Provides detailed instructions on how to install ${en_common.names.kogito} ${en_common.names.github} Extension for ${en_common.names.chrome}.`
      },
      desktop: {
        title: `${en_common.names.desktop} ${en_common.names.app}`,
        description: `Installs the ${en_common.names.businessModeler.desktop} ${en_common.names.app} for use locally and offline.`
      },
      businessModeler: {
        title: `${en_common.names.businessModeler.name} Preview`,
        description: "Provides a quick link to access the website in the same hub."
      }
    },
    afterDownload: {
      title: `Thank you for downloading ${en_common.names.businessModeler.hub}!`,
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
    sendChangesToGitHub: `Send changes to ${en_common.names.github}`,
    copySource: "Copy Source",
    downloadSVG: `${en_common.terms.download} ${en_common.names.svg}`,
    gistIt: "Gist it",
    fileActions: "File actions"
  },
  githubTokenModal: {
    header: {
      title: `${en_common.names.github} ${en_common.names.oauth} ${en_common.terms.token}`,
      subtitle: `Authentication required for exporting to ${en_common.names.github} gist.`
    },
    footer: {
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here"
    },
    body: {
      disclaimer: `By authenticating with your ${en_common.names.oauth} Token we are able to create gists so you can share your diagrams with your colleagues. The token you provide is locally stored as browser cookies and it is never shared with anyone.`,
      learnMore: `Learn more about ${en_common.names.github} tokens`,
      note: `You should provide a token with the ${"'gist'".bold()} permission.`
    }
  },
  homePage: {
    uploadFile: {
      dndZone: {
        invalidFile: "File extension is not supported",
        waitingFile: `Drop a ${en_common.names.bpmn} or ${en_common.names.dmn} file here`
      },
      or: "or",
      fileInput: "File extension is not supported"
    },
    openUrl: {
      validating: `Validating ${en_common.names.url}`,
      invalidGistExtension: "File type on the provided gist is not supported.",
      invalidExtension: `File type on the provided ${en_common.names.url} is not supported.`,
      invalidGist: `Enter a valid gist ${en_common.names.url}.`,
      invalidUrl: `This ${en_common.names.url} is not valid (don't forget "https://"!).`,
      notFoundUrl: `This ${en_common.names.url} does not exist.`,
      corsNotAvailable: `This ${en_common.names.url} cannot be opened because it doesn't allow other websites to access it.`,
      openFromSource: "Open from source",
      description: `Paste a ${en_common.names.url} to a source code link (${en_common.names.github}, ${en_common.names.dropbox}, etc.)`
    },
    dropdown: {
      getHub: `Get ${en_common.names.businessModeler.hub}`,
      onlineForum: "Online forum"
    },
    header: {
      title: `Asset Editor for ${en_common.names.kogito} and Process Automation`,
      welcomeText: `Welcome to ${en_common.names.businessModeler.name}! These simple ${en_common.names.bpmn} and ${en_common.names.dmn} editors are here to allow you to collaborate quickly\n and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch\n in the forum or review the documentation for more information.`
    },
    bpmnCard: {
      title: `Workflow (.${en_common.names.bpmn})`,
      explanation: `${en_common.names.bpmn} files are used to generate business processes.`,
      createNew: "Create new workflow"
    },
    dmnCard: {
      title: `Decision model (.${en_common.names.dmn})`,
      explanation: `${en_common.names.dmn} files are used to generate decision models`,
      createNew: "Create new decision model"
    },
    trySample: "Try Sample",
    editExistingFile: "Edit existing file",
    chooseLocalFile: "Choose a local file"
  },
  guidedTour: {
    init: {
      title: "Welcome to this 5 minutes tour",
      learnMore: `Learn more about the DMN online editor by taking this brief and interactive tour.`,
      letsGo: "Let's go"
    },
    end: {
      title: "Congratulations",
      motivational: `Now you know how each part of the ${en_common.names.dmn} editor works, and you're empowered to go ahead and explore`,
      nextSteps: {
        title: "As next steps, you can try to",
        firstStep: `Connect the ${"Age".bold()} input with the ${"Can drive?".bold()} decision`,
        secondStep: `Define the decision logic into the ${"Can drive?".bold()} node, to return ${"true".bold()} when ${"Age".bold()} is
              greater ${"21".bold()}, otherwise ${"false".bold()}`,
        thirdStep: "Execute the model."
      },
      findUsefulInfo: "You can find useful information at the",
      learnDMN: `Learn ${en_common.names.dmn} in 15 minutes`,
      courseOr: "course or at the",
      kogitoDoc: `${en_common.names.kogito} documentation`,
      finish: "Finish the Tour"
    }
  }
};
