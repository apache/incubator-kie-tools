/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

export interface Space {
  name: string;
  deleted: boolean;
  contributors: any[];
  repositories: any[];
}

export function fetchIsValidGroupIdName(groupId: string): Promise<boolean> {
  return fetch(`rest/spacesScreen/spaces/validGroupId?groupId=${groupId}`, {
    credentials: "same-origin"
  }).then(r => r.json());
}

export function fetchIsDuplicatedSpaceName(name: string): Promise<boolean> {
  return fetch(`rest/spacesScreen/spaces/${name}`, {
    credentials: "same-origin"
  }).then(response => response.status === 200);
}

export function fetchSpaces(): Promise<Space[]> {
  return fetch("rest/spacesScreen/spaces", { credentials: "same-origin" }).then(
    response => response.json()
  );
}

export function createSpace(newSpace: {
  name: string;
  groupId: string;
}): Promise<Response> {
  return fetch("rest/spacesScreen/spaces", {
    credentials: "same-origin",
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(newSpace)
  });
}

export function updateLibraryPreference(preference: {
  projectExplorerExpanded: boolean;
  lastOpenedOrganizationalUnit: string;
}): Promise<Response> {
  return fetch("rest/spacesScreen/libraryPreference", {
    credentials: "same-origin",
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(preference)
  });
}
