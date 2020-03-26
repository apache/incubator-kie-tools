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

import { ResourceContentApi, ResourceContentEditorCoordinator } from "../../api/resourceContent";
import { EnvelopeBusInnerMessageHandler } from "../../EnvelopeBusInnerMessageHandler";
import { EditorContent, KogitoEdit, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";

let coordinator: ResourceContentEditorCoordinator;
let resourceContentEditorService: ResourceContentApi;

const handler = new EnvelopeBusInnerMessageHandler(
  {
    postMessage: (message, targetOrigin) => {
      // do nothing
    }
  },
  self => ({
    receive_contentResponse: (content: EditorContent) => {
      // do nothing
    },
    receive_languageResponse: (languageData: LanguageData) => {
      // do nothing
    },
    receive_contentRequest: () => {
      // do nothing
    },
    receive_resourceContentResponse: (content: ResourceContent) => {
      // do nothing
    },
    receive_resourceContentList: (resourcesList: ResourcesList) => {
      // do nothing
    },
    receive_editorUndo(edits: KogitoEdit[]) {
      // do nothing
    },
    receive_editorRedo(edits: KogitoEdit[]) {
      // do nothing
    },
    receive_previewRequest() {
      // do nothing
    }
  })
);

beforeEach(() => {
  coordinator = new ResourceContentEditorCoordinator();
  handler.targetOrigin = "test";
  handler.startListening();
  resourceContentEditorService = coordinator.exposeApi(handler);
});

afterEach(() => {
  handler.stopListening();
});

describe("ResourceContentEditorCoordinator", () => {
  test("resource content", done => {
    const resourceURI = "/foo/bar";
    const resourceContent = "resource value";

    const mockCallback1 = jest.fn(v => {
      console.log(v);
    });
    const mockCallback2 = jest.fn(v => {
      console.log(v);
    });
    resourceContentEditorService.get(resourceURI).then(mockCallback1);
    resourceContentEditorService.get(resourceURI).then(mockCallback2);

    expect(coordinator.resolvePending.length).toBe(1);

    coordinator.resolvePending(new ResourceContent(resourceURI, resourceContent));

    setTimeout(() => {
      expect(mockCallback1).toHaveBeenCalledTimes(1);
      expect(mockCallback2).toHaveBeenCalledTimes(1);

      expect(mockCallback1).toHaveBeenCalledWith(resourceContent);
      expect(mockCallback2).toHaveBeenCalledWith(resourceContent);

      done();
    }, 500);
  });
  test("resource list", done => {
    const pattern = "*";
    const resources = ["/foo", "/foo/bar"];

    const mockCallback1 = jest.fn();
    const mockCallback2 = jest.fn();
    resourceContentEditorService.list(pattern).then(mockCallback1);
    resourceContentEditorService.list(pattern).then(mockCallback2);

    expect(coordinator.resolvePending.length).toBe(1);

    coordinator.resolvePendingList(new ResourcesList(pattern, resources));

    setTimeout(() => {
      expect(mockCallback1).toHaveBeenCalledTimes(1);
      expect(mockCallback2).toHaveBeenCalledTimes(1);

      expect(mockCallback1).toHaveBeenCalledWith(resources);
      expect(mockCallback2).toHaveBeenCalledWith(resources);

      done();
    }, 500);
  });
});
