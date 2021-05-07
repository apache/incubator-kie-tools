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

import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";
import { ReferenceDictionary } from "@kogito-tooling/i18n/dist/core";

interface VsCodeDictionary extends ReferenceDictionary {
  savedSvg: (fileName: string) => string;
  openSvg: string;
  savedSuccessfully: string;
  errorOpeningFileText: (fileName: string) => string;
  openAsTextButton: string;
  reopenAsDiagramText: string;
  reopenAsDiagramButton: string;
}

export interface VsCodeI18n extends VsCodeDictionary, CommonI18n {}
