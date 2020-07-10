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

import { GwtEditorWrapperFactory } from "../GwtEditorWrapperFactory";
import { GwtLanguageData, Resource } from "../GwtLanguageData";

const cssResource: Resource = {
  type: "css",
  paths: ["resource1.css", "resource2.css"]
};

const jsResource: Resource = {
  type: "js",
  paths: [
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js",
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js",
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js"
  ]
};

const testLanguageData: GwtLanguageData = {
  type: "dummy",
  editorId: "editorID",
  gwtModuleName: "moduleName",
  resources: [cssResource, jsResource]
};

const gwtEditorWrapperFactory: GwtEditorWrapperFactory = new GwtEditorWrapperFactory(
  {
    format: (c: string) => c
  },
  {
    onFinishedLoading: (callback: () => Promise<any>) => {
      window.appFormerGwtFinishedLoading = callback;
    },
    getEditor: jest.fn(),
    setClientSideOnly: jest.fn()
  } as any
);

function waitForNScriptsToLoad(remaining: number) {
  if (remaining <= 0) {
    return Promise.resolve();
  }

  const script = Array.from(document.getElementsByTagName("script")).pop()!;
  return new Promise<void>(res => {
    script.addEventListener("load", () => {
      waitForNScriptsToLoad(remaining - 1).then(res);
    });
    script.dispatchEvent(new Event("load"));
  });
}

describe("GwtEditorWrapperFactory", () => {
  test("create editor", async () => {
    const editorCreation = gwtEditorWrapperFactory.createEditor(testLanguageData, {
      notify_ready: jest.fn()
    } as any);

    await waitForNScriptsToLoad(jsResource.paths.length);
    await window.appFormerGwtFinishedLoading();
    await editorCreation;

    const links = document.getElementsByTagName("link");
    expect(links.length).toBe(cssResource.paths.length);
    Array.from(links).forEach((l, i) => {
      expect(l.href).toContain(cssResource.paths[i]);
    });

    const scripts = document.getElementsByTagName("script");
    expect(scripts.length).toBe(jsResource.paths.length);
    Array.from(scripts).forEach((s, i) => {
      expect(s.src).toContain(jsResource.paths[i]);
    });
  });
});
