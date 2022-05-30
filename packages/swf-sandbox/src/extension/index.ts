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

import { basename, extname } from "path";

export function resolveExtension(path: string): string {
  const fileName = basename(path);
  if (fileName.startsWith(".")) {
    return fileName.slice(1);
  }
  const regex = /(\.sw\.json|\.sw\.yaml|\.sw\.yml|\.yard\.json|\.yard\.yaml|\.yard\.yml|\.dash\.yml|\.dash\.yaml)$/;
  const match = regex.exec(path.toLowerCase());
  const extension = match ? match[1] : extname(path);
  return extension ? extension.slice(1) : "";
}

export function isServerlessWorkflow(path: string): boolean {
  return /^.*\.sw\.(json|yml|yaml)$/.test(path.toLowerCase());
}

export function isServerlessDecision(path: string): boolean {
  return /^.*\.yard\.(json|yml|yaml)$/.test(path.toLowerCase());
}

export function isDashbuilder(path: string): boolean {
  return /^.*\.dash\.(yml|yaml)$/.test(path.toLowerCase());
}

export function isSandboxAsset(path: string): boolean {
  return isServerlessWorkflow(path) || isServerlessDecision(path) || isDashbuilder(path);
}
