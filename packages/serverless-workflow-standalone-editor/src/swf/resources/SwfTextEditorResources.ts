/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as fs from "fs";
import * as path from "path";

import { BaseEditorResources, EditorResources, JsResource } from "../../common/EditorResources";

export interface WorkerJSResource extends JsResource {
  workerName: string;
}

interface ServerlessWorkflowResources extends EditorResources {
  workersJSResources: WorkerJSResource[];
}

export class ServerlessWorkflowTextEditorResources extends BaseEditorResources {
  private readonly JS_RESOURCES_EXPR = "(\\wmonaco-editor.*)(\\wjso|\\wyaml)";
  private readonly JS_WORKER_EXPR = ".worker.js";

  public get(args: { resourcesPathPrefix: string }) {
    const swfTextEditorResources: ServerlessWorkflowResources = {
      baseCssResources: [],
      baseJsResources: [],
      fontResources: [],
      referencedCssResources: [],
      referencedJsResources: this.getReferencedJSPaths(args.resourcesPathPrefix),
      envelopeJsResource: this.createResource({ path: `dist/envelope/swf-text-editor-envelope.js` }),
      workersJSResources: this.getWorkersJSResources(args.resourcesPathPrefix),
    };
    return swfTextEditorResources;
  }

  public getReferencedJSPaths(resourcesPathPrefix: string) {
    return this.getJSResources(resourcesPathPrefix, this.JS_RESOURCES_EXPR);
  }

  public getWorkersJSResources(resourcesPathPrefix: string) {
    return this.getJSResources(resourcesPathPrefix, this.JS_WORKER_EXPR, ["\\", "`", "$"]).map((jsResource) => {
      return {
        workerName: path.parse(jsResource.path).name.split(".")[0],
        ...jsResource,
      };
    });
  }

  private getJSResources(resourcesPathPrefix: string, matchExpr: string, escapeCharacters?: string[]): JsResource[] {
    return fs
      .readdirSync(resourcesPathPrefix)
      .filter((file) => file.match(matchExpr))
      .map((file) => ({ path: `${resourcesPathPrefix}/${file?.split("/").pop()}` }))
      .map((ref) => this.createResource(ref, escapeCharacters));
  }

  public getReferencedCSSPaths(resourcesPathPrefix: string, gwtModuleName: string) {
    return [];
  }

  public getFontResources(resourcesPathPrefix: string, gwtModuleName: string) {
    return [];
  }

  public getEditorResourcesPath() {
    return "dist/resources/swf/js";
  }

  public getTemplatePath() {
    return "dist/resources/swf/swfTextEditorEnvelopeIndex.template";
  }

  public getHtmlOutputPath() {
    return "dist/resources/swf/swfTextEditorEnvelopeIndex.html";
  }
}
