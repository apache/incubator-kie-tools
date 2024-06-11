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

export enum UploadStatus {
  NOT_READY = "NOT_READY",
  READY = "READY",
  UPLOADING = "UPLOADING",
  UPLOADED = "UPLOADED",
  ERROR = "ERROR",
}

const UPLOAD_ENDPOINT = "/upload";
const UPLOAD_STATUS_ENDPOINT = `/upload-status`;
const DATA_PART_KEY = "myFile";

export async function getUploadStatus(args: { baseUrl: string }): Promise<UploadStatus> {
  try {
    const response = await fetch(`${args.baseUrl}${UPLOAD_STATUS_ENDPOINT}`);

    if (response.ok) {
      return (await response.text()) as UploadStatus;
    }
  } catch (e) {
    // ignore
  }
  return UploadStatus.NOT_READY;
}

export async function postUpload(args: { baseUrl: string; workspaceZipBlob: Blob; apiKey: string }): Promise<void> {
  const formData = new FormData();
  formData.append(DATA_PART_KEY, args.workspaceZipBlob);
  await fetch(`${args.baseUrl}${UPLOAD_ENDPOINT}?apiKey=${args.apiKey}`, {
    method: "POST",
    body: formData,
  });
}
