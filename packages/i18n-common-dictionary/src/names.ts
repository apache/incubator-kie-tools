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

// tslint:disable-next-line:interface-over-type-literal
export type NamesDictionary = {
  app: string;
  bpmn: string;
  businessModeler: {
    name: string;
    desktop: string;
    hub: string;
  };
  chrome: string;
  desktop: string;
  dmn: string;
  dropbox: string;
  gist: string;
  github: string;
  kogito: string;
  linux: string;
  macos: string;
  oauth: string;
  svg: string;
  url: string;
  vscode: string;
  windows: string;
};

export const names: NamesDictionary = {
  app: "App",
  bpmn: "BPMN",
  businessModeler: {
    name: "Business Modeler",
    desktop: "Business Modeler Desktop Preview",
    hub: "Business Modeler Hub Preview"
  },
  chrome: "Chrome",
  desktop: "Desktop",
  dmn: "DMN",
  dropbox: "Dropbox",
  gist: "gist",
  github: "GitHub",
  kogito: "Kogito",
  linux: "Linux",
  macos: "macOS",
  oauth: "OAuth",
  svg: "SVG",
  url: "URL",
  vscode: "VS Code",
  windows: "Windows"
};
