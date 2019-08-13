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

import { GwtAppFormerApi } from "../GwtAppFormerApi";
import { GwtEditorWrapperFactory } from "../GwtEditorWrapperFactory";
import { GwtLanguageData, Resource } from "appformer-js-gwt-editors-common";

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

const gwtAppFormerApi: GwtAppFormerApi = {
  setErraiDomain: jest.fn(),
  onFinishedLoading: (callback: () => Promise<any>) => (window.appFormerGwtFinishedLoading = callback),
  getEditor: jest.fn(),
  setClientSideOnly: jest.fn()
};

const cssResource: Resource = {
  type: "css",
  paths: ["resource.css"]
};

const jsResource: Resource = {
  type: "js",
  paths: ["resource.js"]
};

const testLanguageData: GwtLanguageData = {
  type: "dummy",
  editorId: "editorID",
  gwtModuleName: "moduleName",
  erraiDomain: "erraiDomain",
  resources: [cssResource, jsResource]
};

const gwtEditorWrapperFactory: GwtEditorWrapperFactory = new GwtEditorWrapperFactory(gwtAppFormerApi);

describe("GwtEditorWrapperFactory", () => {
  test("create editor", async () => {
    const res = jest.fn();

    gwtEditorWrapperFactory.createEditor(testLanguageData, undefined as any).then(res);

    const links = document.body.getElementsByTagName("link");
    const scripts = document.getElementsByTagName("script");
    expect(links.length).toBe(1);
    expect(scripts.length).toBe(1);
    expect(links[0].href).toContain(cssResource.paths[0]);
    expect(scripts[0].src).toContain(jsResource.paths[0]);

    window.appFormerGwtFinishedLoading();

    await delay(100);
    expect(res).toBeCalled();
  });
});
