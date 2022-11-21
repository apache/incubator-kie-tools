/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

export type UploadStatus = "NOT_READY" | "WAITING" | "UPLOADING" | "UPLOADED" | "ERROR";

const UPLOAD_ENDPOINT = "/upload";
const UPLOAD_STATUS_ENDPOINT = `/upload/status`;
const DATA_PART_KEY = "zipFile";

export async function getUploadStatus(args: { baseUrl: string }): Promise<UploadStatus> {
  try {
    const response = await fetch(`${args.baseUrl}${UPLOAD_STATUS_ENDPOINT}`);

    if (response.ok) {
      return (await response.text()) as UploadStatus;
    }
  } catch (e) {
    // ignore
  }
  return "NOT_READY";
}

export async function postUpload(args: { baseUrl: string; workspaceZipBlob: Blob }): Promise<void> {
  const formData = new FormData();
  formData.append(DATA_PART_KEY, args.workspaceZipBlob);
  await fetch(`${args.baseUrl}${UPLOAD_ENDPOINT}`, {
    method: "POST",
    body: formData,
  });
}
