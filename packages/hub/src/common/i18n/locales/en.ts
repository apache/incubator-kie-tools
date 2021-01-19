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

import { HubI18n } from "..";
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: HubI18n = {
  ...en_common,
  alert: {
    launching: {
      title: `Error while launching ${en_common.names.businessModeler.hub}. This is a known issue on ${en_common.names.macos}.`,
      try: "Try again after executing the command below on a Terminal window.",
      directory: `You have to be at the same directory as '${en_common.names.businessModeler.hub}.app'.`
    }
  },
  vscode: {
    title: `${en_common.names.kogito} Bundle ${en_common.names.vscode} extension`,
    description: `Launches ${en_common.names.vscode} ready to use with ${en_common.names.kogito} ${en_common.names.bpmn}, ${en_common.names.dmn} and ${en_common.names.testScenario} Editors`,
    installed: "Installed",
    uninstalling: "Uninstalling"
  },
  chromeExtension: {
    title: `${en_common.names.bpmn}, ${en_common.names.dmn} and ${en_common.names.testScenario} Editors for ${en_common.names.github}`,
    description: `Install the ${en_common.names.chromeExtensionName} on ${en_common.names.chrome} browser`,
    modal: {
      title: `Install ${en_common.names.chromeExtensionName}`,
      chromeRequirement: `To be able to install and use the ${en_common.names.chromeExtensionName} it's necessary to have the ${en_common.names.chrome} browser installed on your computer.`,
      chromeDownload: "In case you don't have it, you can download it",
      here: "here",
      alreadyHaveChrome: `If you already have the ${en_common.names.chrome} browser or have just downloaded it, follow this steps`,
      firstStep: {
        firstPart: `Open the ${en_common.names.chromeExtensionName} on the`,
        secondPart: `using your ${en_common.names.chrome} browser`
      },
      secondStep: `Click on "Add to ${en_common.names.chrome}".`,
      thirdStep: "Read the permissions and in case you agree, click on “Add Extension”",
      done: {
        firstPart: "Done! You can go to",
        secondPart: "now and start using it."
      }
    }
  },
  desktop: {
    title: `${en_common.names.businessModeler.desktop}`,
    description: `Launches the desktop version of ${en_common.names.businessModeler.online}`
  },
  online: {
    title: `${en_common.names.businessModeler.online}`,
    description: `Navigates to the Online Modeler Preview site`
  },
  noUpdates: "No updates available"
};
