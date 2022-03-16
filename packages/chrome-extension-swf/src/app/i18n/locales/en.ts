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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { ChromeExtensionI18n } from "..";

export const en: ChromeExtensionI18n = {
  ...en_common,
  editorPage: {
    textEditorModal: {
      title: (fileName: string) => `Editing ${fileName}`,
    },
    alerts: {
      setContentError: {
        title: "Error opening file. You can edit it as text and reopen the diagram after you've fixed it.",
        action: "Open as text",
      },
      copy: "Content copied to clipboard",
      updateGist: "Gist successfully updated.",
      createGist: "Gist successfully created.",
      errorPushingGist: "Failed to push an update to your current Gist. Attempt to force push?",
      forcePushWarning: "WARNING: This will overwrite your Gist with the local changes!",
      invalidCurrentGist: `Your current gist ${en_common.names.url} is invalid. If you've updated its filename, it's necessary to update your ${en_common.names.url} as well.`,
      invalidGistFilename: "Invalid filename. This gist already has a file with this name.",
      error: `An error occurred trying to perform the last operation. Check if your ${en_common.names.github} token is still valid and try again later.`,
      unsaved: {
        titleLocal: "You have new changes since your last download.",
        titleGit: "You have new changes since your last push.",
        proceedAnyway: "Proceed anyway",
        message: "Your files are temporarily persisted on your browser, but may be erased before you come back.",
      },
    },
  },
  editorToolbar: {
    closeAndReturnHome: "Close and return Home",
    saveAndDownload: "Save & Download",
    sendChangesToGitHub: `Send changes to ${en_common.names.github}`,
    copySource: "Copy Source",
    downloadSVG: `${en_common.terms.download} ${en_common.names.svg}`,
    setGitHubToken: `Setup`,
    createGist: "Create Gist",
    cantCreateGistTooltip: `You can't create a Gist because you're either not logged in, or your models are in nested directories.`,
    cantUpdateGistTooltip: `You can't update your Gist because you're either not logged in, not the owner, or your models are in nested directories.`,
    share: "Share",
    embed: "Embed",
  },
  openshift: {
    common: {
      deployYourModel: "Deploy",
      deployInstanceInfo: "Deploy instance information",
      disclaimer:
        "When you set up the required information, you are able to deploy decision models on your configured instance. All the information you provide is locally stored as browser cookies and they are never shared with anyone.",
      learnMore: "Learn more",
      requiredField: "This field cannot be empty.",
      deploying: "Deploying ...",
      saving: "Saving ...",
      setupFirst: `Set up your ${en_common.names.dmnDevSandbox} to be able to deploy your models`,
    },
    configWizard: {
      header: {
        provider: "Provider",
      },
      steps: {
        first: {
          name: "Create your instance",
          introduction: `In order to create your ${en_common.names.shortDevSandbox} instance:`,
          goToGetStartedPage: "Go to the Get Started page",
          followSteps: `Follow the steps to launch your instance. You will be asked to log in with your ${en_common.names.redHat} account.`,
          informNamespace: `Once your instance is up and running, inform the namespace (project) in your cluster you want to have the Decision Model deployed to.`,
          inputReason:
            "This information is necessary for deploying your Decision Model into the right project namespace.",
          namespacePlaceholder: `The namespace (project) you want to deploy the Decision Model.`,
        },
        second: {
          name: "Set credentials",
          introduction: `In your ${en_common.names.shortDevSandbox} instance:`,
          accessLoginCommand: `Click on your username on the top right corner and then ${"'Copy login command'".bold()}.`,
          accessDisplayToken: `If asked, log in with ${"'DevSandbox'".bold()}, and then access the ${"'Display Token'".bold()} link.`,
          copyInformation: `In ${"'Log in with this token'".bold()} section, copy your ${"'--server'".bold()} and ${"'--token'".bold()} values, and paste them below.`,
          inputReason: "This information is necessary for establishing a connection with your instance.",
          hostPlaceholder: "Paste the --server value here",
          tokenPlaceholder: "Paste the --token value here",
        },
        final: {
          name: "Connect",
          connectionSuccess: "Connection successfully established.",
          connectionError: "Connection refused.",
          introduction: "Now you are able to deploy DMN decisions to your OpenShift instance.",
          configNote: "Your configuration will be stored as browser cookies after the operations above.",
          connectionErrorLong: `A connection with your ${en_common.names.shortDevSandbox} instance could not be established.`,
          checkInfo: "Please check the information provided and try again.",
          possibleErrorReasons: {
            introduction: "Here are some possible reasons:",
            emptyField: "One or more required information are not filled.",
            instanceExpired:
              "Instances expire in 30 days. After this period, you will need to recreate it, thus receiving a new host.",
            tokenExpired: "Tokens expire on a daily basis.",
          },
        },
      },
    },
    configModal: {
      hostInfo: `The hostname associated with your instance.`,
      namespaceInfo: `The namespace (project) you want to deploy the Decision Model.`,
      tokenInfo: `The token associated with your instance.`,
      validationError: "You must fill out all required fields before you can proceed.",
      connectionError: "Connection refused. Please check the information provided.",
      configExpiredWarning: "Token or account expired. Please update your configuration.",
      useWizard: "Configure through the guided wizard instead",
    },
  },
};
