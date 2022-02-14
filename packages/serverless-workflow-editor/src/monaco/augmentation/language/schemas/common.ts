/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { JSONSchema7 } from "json-schema";

export const SW_SPEC_COMMON_SCHEMA: JSONSchema7 = {
  $id: "https://serverlessworkflow.io/schemas/0.8/common.json",
  $schema: "http://json-schema.org/draft-07/schema#",
  description: "Serverless Workflow specification - common schema",
  type: "object",
  definitions: {
    metadata: {
      type: "object",
      description: "Metadata information",
      additionalProperties: {
        type: "string",
      },
    },
  },
};
