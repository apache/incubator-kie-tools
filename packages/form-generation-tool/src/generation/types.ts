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

export interface FormResources {
  styles: Record<string, string>;
  scripts: Record<string, string>;
}

export interface FormConfig {
  schema: string;
  resources: FormResources;
}

export interface FormAsset {
  id: string;
  sanitizedId: string;
  assetName: string;
  sanitizedAssetName: string;
  content: string;
  type: FormAssetType | string;
  config: FormConfig;
}

export interface FormGenerationTool {
  type: string;
  generate: (schema: FormSchema) => FormAsset;
}
