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

import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";
import { DmnFormI18n } from "..";

export const en: DmnFormI18n = {
  ...en_common,
  formToolbar: {
    disclaimer: {
      title: "Development only",
      description: `The ${en_common.names.dmn} ${
        en_common.names.shortDevSandbox
      } is intended to be used during ${"development".bold()}, so users should not use the
        deployed ${en_common.names.dmn} services in production or for any type of business-critical workloads.`,
    },
  },
  error: {
    title: "An unexpected error happened while trying to fetch your file",
    notFound: "A required file could be not be found",
  },
};
