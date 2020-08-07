/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"),
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

import { DesktopI18n } from "..";
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: DesktopI18n = {
  ...en_common,
  app: {
    title: "This file extension is not supported."
  },
  editorPage: {
    alerts: {
      copy: "Content copied to clipboard",
      unsaved: {
        title: "Unsaved changes will be lost",
        message: "Click Save to download your progress before closing.",
        closeWithoutSaving: "Close without saving"
      },
      saved: "File saved successfully!",
      previewSaved: "Preview saved successfully!"
    }
  },
  filesPage: {
    alerts: {
      errorFetchingFile: "An error happened while fetching your file",
      unexpectedErrorFetchingFile: "An unexpected error happened while trying to fetch your file"
    },
    errorDetails: "Error details",
    files: {
      title: "Create new file",
      bpmn: {
        blank: `Blank Workflow (.${en_common.names.bpmn})`,
        sample: `Sample Workflow (.${en_common.names.bpmn})`
      },
      dmn: {
        blank: `Blank Decision Model (.${en_common.names.dmn})`,
        sample: `Sample Decision Model (.${en_common.names.dmn})`
      }
    },
    openUrl: {
      initial: "http://",
      invalidExtension: "File type is not supported",
      invalidUrl: `Enter a valid ${en_common.names.url}`,
      notFoundUrl: `File ${en_common.names.url} is not valid`,
      openFromSource: "Open from source",
      description: `Paste a ${en_common.names.url} to a source code link (${en_common.names.github}, ${en_common.names.dropbox}, etc.)`
    },
    recent: {
      title: `Recent ${en_common.terms.files}`,
      noFilesYet: "No files were opened yet."
    }
  },
  homePage: {
    learnMore: "Learn more"
  },
  learnMorePage: {
    readMore: "Read more",
    bpmn: {
      title: `Why ${en_common.names.bpmn}?`,
      explanation: `${en_common.names.bpmn} files are used to generate business processes.`,
      create: `Create ${en_common.names.bpmn}`
    },
    dmn: {
      title: `Why ${en_common.names.dmn}?`,
      explanation: `${en_common.names.dmn} files are used to generate decision models.`,
      learn: `Learn ${en_common.names.dmn} in 15 minutes`,
      create: `Create ${en_common.names.dmn}`
    },
    about: `About ${en_common.names.businessModeler.name} Preview`,
    editorsExplanation: `These simple ${en_common.names.bpmn} and ${en_common.names.dmn} editors are here to allow you to collaborate quickly and to help introduce you to the new tools and capabilities of Process Automation. Feel free to get in touch in the`,
    getChromeExtension: `Get ${en_common.names.github} ${en_common.names.chrome} extension`,
    getVsCodeExtension: `Get ${en_common.names.vscode} extension`,
    redHatOpenSource: `${en_common.names.redHat} and open source`,
    kogitoWebsite: `${en_common.names.kogito} website`
  }
};
