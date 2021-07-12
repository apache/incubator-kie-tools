/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

export interface FormSchema {
  name: string;
  schema: any;
}

export enum FormStyle {
  PATTERNFLY = "patternfly",
  BOOTSTRAP = "bootstrap",
}

export enum FormAssetType {
  HTML = "html",
  TSX = "tsx",
}

export interface FormAsset {
  id: string;
  assetName: string;
  content: string;
  type: FormAssetType | string;
}

export interface FormGenerationTool {
  type: string;
  generate: (schema: FormSchema) => FormAsset;
}
