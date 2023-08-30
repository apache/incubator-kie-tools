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
  masthead: {
    disclaimer: {
      title: "Development only",
      description: `This deployment is intended to be used during ${"development".bold()}, so users should not use the
        deployed services in production or for any type of business-critical workloads.`,
    },
  },
  page: {
    error: {
      title: `${en_common.terms.oops}!`,
      explanation: "The page couldn't be rendered due to an error.",
      referToJira: ["Please refer to ", wrapped("jira"), " and report an issue."],
    },
  },
};
