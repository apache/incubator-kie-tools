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
import { Specification } from "@severlessworkflow/sdk-typescript";

export const eventCompletion: Omit<Specification.Eventdef, "normalize"> = {
  name: "${1:Unique event name}",
  source: "${2:CloudEvent source}",
  type: "${3:CloudEvent type}",
  // @ts-expect-error not using the original type to use CodeCompletions placeholder
  kind: "${4:Eventdef kind}",
  // @ts-expect-error not using the original type to use CodeCompletions placeholder
  metadata: "${5:Eventdef metdata}",
};
