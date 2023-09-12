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

import { wrapped } from "@kie-tools-core/i18n/dist/core";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { AppI18n } from "..";

export const en: AppI18n = {
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
  },
  openshift: {
    configModal: {
      hostInfo: `The hostname associated with your instance.`,
      namespaceInfo: `The namespace (project) you want to deploy the model.`,
      tokenInfo: `The token associated with your instance.`,
      insecurelyDisableTlsCertificateValidation: "Insecurely disable TLS certificate validation",
      insecurelyDisableTlsCertificateValidationInfo:
        "Checking this option will insecurely disable TLS certificate verification for this account. This is an alternative to not having to deal with the browser's restrictions when your cluster is behind an HTTPS endpoint with a self-signed certificate. Please be advised that the use of self-signed certificates is a weaker form of security, so consider contacting your cluster admins to use a trusted certificate. For more information, refer to <a href='https://cwe.mitre.org/data/definitions/295.html' target='_blank'>https://cwe.mitre.org/data/definitions/295.html</a>.",
      validationError: "You must fill out all required fields before you can proceed.",
      connectionError: "Connection refused. Please check the information provided.",
      configExpiredWarning: "Token or account expired. Please update your configuration.",
    },
    confirmModal: {
      title: "Deploy",
      body: "Are you sure you want to deploy models from this workspace to your instance? This action will take a few minutes to be completed and you will need to create a new deployment if you update your models.",
    },
    introduction: {
      explanation: `Deploy your models to a cloud instance on OpenShift and share with others. This tool is intended for ${"development".bold()} and should not be used for business-critical workloads.`,
    },
  },
  githubTokenModal: {
    header: {
      title: `${en_common.names.github} ${en_common.names.oauth} ${en_common.terms.token}`,
      subtitle: `Set up your ${en_common.names.github} token so you can create and update gist.`,
    },
    footer: {
      createNewToken: "Create a new token",
      placeHolder: "Paste your token here",
    },
    body: {
      disclaimer: `The token you provide is locally stored as a browser cookie and is never shared with anyone.`,
      learnMore: `Learn more about ${en_common.names.github} tokens`,
      note: `You should provide a token with the ${"'gist'".bold()} permission.`,
    },
  },
  notificationsPanel: {
    name: "Notifications Panel",
    tooltip: {
      retractAll: "Retract All",
      expandAll: "Expand All",
    },
  },
  deployments: {
    virtualServiceRegistry: {
      dependencyWarning: "Has foreign workspace dependencies!",
      dependencyWarningTooltip: "Models in this workspace may depend on deployments from other workspaces.",
    },
  },
};
