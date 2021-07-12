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

import { FormAsset } from "../types";
import fs from "fs";

export const FORM_STORAGE_FOLDER = "src/main/resources/forms";

export function getFormAssetStoragePath(sourcePath: string, formAsset: FormAsset): string {
  return `${sourcePath}/${FORM_STORAGE_FOLDER}/${formAsset.id}`;
}

export function getFormAssetPath(sourcePath: string, formAsset: FormAsset): string {
  return `${getFormAssetStoragePath(sourcePath, formAsset)}/${formAsset.assetName}`;
}

export function storeFormAsset(formAsset: FormAsset, source: string, overwriteExisting: boolean) {
  const storagePath = getFormAssetStoragePath(source, formAsset);

  if (fs.existsSync(storagePath)) {
    if (!overwriteExisting) {
      throw new Error(`Form already exists.`);
    }
    console.log(`Form "${formAsset.id}" already exists. Proceeding to overwrite it.`);
    fs.rmSync(storagePath, { recursive: true });
  }
  fs.mkdirSync(storagePath, { recursive: true });

  fs.writeFileSync(getFormAssetPath(source, formAsset), formAsset.content);
}
