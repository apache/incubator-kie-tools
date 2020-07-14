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

import { ResourceContentApi, ResourceContentServiceCoordinator } from "../../api/resourceContent";
import { KogitoEnvelopeBus } from "../../KogitoEnvelopeBus";
import { ResourceContent, ResourcesList } from "@kogito-tooling/microeditor-envelope-protocol";

let coordinator: ResourceContentServiceCoordinator;
let resourceContentApi: ResourceContentApi;
let kogitoEnvelopeBus: KogitoEnvelopeBus;

beforeEach(() => {
  kogitoEnvelopeBus = new KogitoEnvelopeBus(
    { postMessage: _ => ({}) },
    {
      receive_initRequest: jest.fn(),
      receive_contentChanged: jest.fn(),
      receive_contentRequest: jest.fn(),
      receive_editorUndo: jest.fn(),
      receive_editorRedo: jest.fn(),
      receive_previewRequest: jest.fn(),
      receive_guidedTourElementPositionRequest: jest.fn(),
      receive_channelKeyboardEvent: jest.fn()
    }
  );
  coordinator = new ResourceContentServiceCoordinator();
  kogitoEnvelopeBus.startListening();
  resourceContentApi = coordinator.exposeApi(kogitoEnvelopeBus);
});

afterEach(() => {
  kogitoEnvelopeBus.stopListening();
});

describe("ResourceContentEditorCoordinator", () => {
  test("resource content", async () => {
    const resourceContent = new ResourceContent("/foo/bar/tar", "the content");
    jest.spyOn(kogitoEnvelopeBus, "request_resourceContent").mockReturnValueOnce(Promise.resolve(resourceContent));
    expect(await resourceContentApi.get("/foo/bar")).toStrictEqual(resourceContent.content);
  });

  test("resource list", async () => {
    const pattern = "*";
    const resources = new ResourcesList(pattern, ["/foo", "/foo/bar"]);
    jest.spyOn(kogitoEnvelopeBus, "request_resourceList").mockReturnValueOnce(Promise.resolve(resources));
    expect(await resourceContentApi.list("/foo/bar")).toStrictEqual(resources.paths);
  });
});
