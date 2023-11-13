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

import * as fs from "fs";
import { BaseEditorResources } from "../../common/EditorResources";
import { getServerlessWorkflowLanguageData } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import * as swfEditorAssets from "@kie-tools/serverless-workflow-diagram-editor-assets";

export class ServerlessWorkflowDiagramEditorResources extends BaseEditorResources {
  public get(args: { resourcesPathPrefix: string }) {
    const swfLanguageData = getServerlessWorkflowLanguageData(args.resourcesPathPrefix);
    const swfEditorResources: any = {
      envelopeJsResource: this.createResource({ path: `dist/envelope/swf-diagram-editor-envelope.js` }),
      baseJsResources: swfLanguageData.resources
        .filter((r) => r.type === "js")
        .flatMap((r) => r.paths)
        .map((p) => this.createResource({ path: p }, ["\\", "`", "$"]))!,
      referencedJsResources: this.getReferencedJSPaths(args.resourcesPathPrefix, swfLanguageData.gwtModuleName).map(
        (rp) => this.createResource(rp, ["\\", "`", "$"])
      ),
      baseCssResources: swfLanguageData.resources
        .filter((r) => r.type === "css")
        .flatMap((r) => r.paths)
        .map((p) => this.createResource({ path: p }))!,
      referencedCssResources: this.getReferencedCSSPaths(args.resourcesPathPrefix, swfLanguageData.gwtModuleName).map(
        (rp) => this.createResource(rp)
      ),
      fontResources: this.getFontResources(args.resourcesPathPrefix, swfLanguageData.gwtModuleName),
    };

    return swfEditorResources;
  }

  public getReferencedJSPaths(resourcesPathPrefix: string, gwtModuleName: string) {
    const editorDirPath = `${resourcesPathPrefix}/${gwtModuleName}`;
    const editorDir = fs.readdirSync(editorDirPath);
    const gwtJsFiles = editorDir.filter((file) => file.indexOf(".cache.js") >= 0);

    return gwtJsFiles.map((file) => {
      const gwtJsFileName = `${file?.split("/").pop()}`;
      const gwtJsFilePath = `${editorDirPath}/${gwtJsFileName}`;
      return { path: gwtJsFilePath, userAgentCondition: this.getUserAgentCondition(editorDirPath, gwtJsFileName) };
    });
  }

  public getReferencedCSSPaths(resourcesPathPrefix: string, gwtModuleName: string) {
    return [];
  }

  public getFontResources(resourcesPathPrefix: string, gwtModuleName: string) {
    return [
      {
        family: "FontAwesome",
        additionalStyle: "font-weight:normal;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/fontawesome-webfont.ttf`)],
      },
      {
        family: "PatternFlyIcons-webfont",
        additionalStyle: "font-weight:normal;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/PatternFlyIcons-webfont.ttf`)],
      },
      {
        family: "Glyphicons Halflings",
        sources: [
          this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/glyphicons-halflings-regular.ttf`),
        ],
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:300;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Light-webfont.ttf`)],
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:400;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Regular-webfont.ttf`)],
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:600;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Semibold-webfont.ttf`)],
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:700;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Bold-webfont.ttf`)],
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:800;font-style:normal;",
        sources: [
          this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-ExtraBold-webfont.ttf`),
        ],
      },
      {
        family: "Font Awesome 5 Free",
        additionalStyle: "font-weight:900;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/fontawesome-webfont.ttf`)],
      },
    ];
  }

  public getEditorResourcesPath() {
    return swfEditorAssets.swEditorPath();
  }

  public getTemplatePath() {
    return "dist/resources/swf/swfDiagramEditorEnvelopeIndex.template";
  }

  public getHtmlOutputPath() {
    return "dist/resources/swf/swfDiagramEditorEnvelopeIndex.html";
  }
}
