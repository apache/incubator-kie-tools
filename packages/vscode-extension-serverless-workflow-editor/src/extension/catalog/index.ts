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

import * as __path from "path";
import { SwfServiceCatalogRegistry } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { FileSystemServiceCatalogRegistry } from "./fs";

const FILE_DIRNAME = "${fileDirname}";

export function lookupCatalogRegistry(args: { filePath: string; specsStoragePath: string }): SwfServiceCatalogRegistry {
  const parentDir = __path.parse(args.filePath).dir;

  const storagePath = args.specsStoragePath.replace(FILE_DIRNAME, parentDir);
  const baseSpecsFolder = args.specsStoragePath.includes(FILE_DIRNAME)
    ? storagePath.substring(storagePath.lastIndexOf("/") + 1)
    : storagePath;

  return new FileSystemServiceCatalogRegistry(baseSpecsFolder, storagePath);
}
