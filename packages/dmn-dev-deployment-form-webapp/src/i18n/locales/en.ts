/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { wrapped } from "@kie-tools-core/i18n/dist/core";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { DmnFormI18n } from "..";

export const en: DmnFormI18n = {
  ...en_common,
  formToolbar: {
    disclaimer: {
      title: "Development only",
      description: `Dev deployments are intended to be used for ${"development".bold()} purposes only, so users should not use the
        deployed services in production or for any type of business-critical workloads.`,
    },
  },
  page: {
    error: {
      title: `${en_common.terms.oops}!`,
      explanation: "The page couldn't be rendered due to an error.",
      dmnNotSupported: `This ${en_common.names.dmn} has a construct that is not supported. `,
      uploadFiles: "Don't forget to upload the current file, and the used inputs",
      referToJira: ["Please refer to ", wrapped("jira"), " and report an issue."],
    },
  },
  error: {
    title: "An unexpected error happened while trying to fetch your file",
    notFound: "A required file could be not be found",
  },
};
