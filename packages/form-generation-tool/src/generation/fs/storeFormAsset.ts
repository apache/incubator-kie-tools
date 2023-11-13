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

import { FormAsset } from "../types";
import fs from "fs";
import path from "path";

export const FORM_STORAGE_FOLDER = "src/main/resources/forms";
export const FORM_CONFIG_EXT = ".config";

function getFormsFolder(sourcePath: string): string {
  return `${sourcePath}/${FORM_STORAGE_FOLDER}`;
}

export function getFormAssetPath(sourcePath: string, formAsset: string): string {
  return `${getFormsFolder(sourcePath)}/${formAsset}`;
}

export function getFormConfigAssetPath(source: string, formAsset: FormAsset): string {
  return getFormAssetPath(source, `${formAsset.sanitizedId}${FORM_CONFIG_EXT}`);
}

export function storeFormAsset(formAsset: FormAsset, source: string, overwriteExisting: boolean) {
  const storagePath = getFormsFolder(source);

  if (!fs.existsSync(storagePath)) {
    fs.mkdirSync(storagePath);
  }

  const existingFormAssets = fs.readdirSync(storagePath).filter((file) => {
    const extension = path.extname(file);
    return path.basename(file, extension) === formAsset.sanitizedId;
  });

  if (existingFormAssets.length > 0) {
    if (!overwriteExisting) {
      throw new Error(`Form already exists.`);
    }

    console.log(`Form "${formAsset.sanitizedId}" already exists. Proceeding to overwrite it.`);

    existingFormAssets.forEach((file) => {
      fs.rmSync(getFormAssetPath(source, file));
    });
  }
  fs.writeFileSync(getFormAssetPath(source, formAsset.sanitizedAssetName), formAsset.content);
  fs.writeFileSync(getFormConfigAssetPath(source, formAsset), JSON.stringify(formAsset.config, null, 4));
}
