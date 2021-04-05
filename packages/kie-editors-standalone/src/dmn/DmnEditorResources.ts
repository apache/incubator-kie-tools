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

import * as fs from "fs";
import { BaseEditorResources, EditorResources } from "../common/EditorResources";
import * as externalAssets from "@kogito-tooling/external-assets-base";
import { getDmnLanguageData } from "@kogito-tooling/kie-bc-editors/dist/dmn/api";

export class DmnEditorResources extends BaseEditorResources {
  public get(args: { resourcesPathPrefix: string }) {
    const dmnLanguageData = getDmnLanguageData(args.resourcesPathPrefix);

    const dmnEditorResources: EditorResources = {
      envelopeJsResource: this.createResource({ path: `dist/envelope/dmn-envelope.js` }),
      baseJsResources: dmnLanguageData?.resources
        .filter(r => r.type === "js")
        .pop()
        ?.paths.map(p => this.createResource({ path: p }, ["\\", "`", "$"]))!,
      referencedJsResources: this.getReferencedJSPaths(
        args.resourcesPathPrefix,
        dmnLanguageData.gwtModuleName
      ).map(rp => this.createResource(rp, ["\\", "`", "$"])),
      baseCssResources: dmnLanguageData?.resources
        .filter(r => r.type === "css")
        .pop()
        ?.paths.map(p => this.createResource({ path: p }))!,
      referencedCssResources: this.getReferencedCSSPaths(
        args.resourcesPathPrefix,
        dmnLanguageData.gwtModuleName
      ).map(rp => this.createResource(rp)),
      fontResources: this.getFontResources(args.resourcesPathPrefix, dmnLanguageData.gwtModuleName)
    };

    return dmnEditorResources;
  }

  public getReferencedJSPaths(resourcesPathPrefix: string, gwtModuleName: string) {
    const editorDir = fs.readdirSync(`${resourcesPathPrefix}/${gwtModuleName}`);
    const gwtJsFiles = editorDir.filter(file => file.indexOf(".cache.js") >= 0);
    return gwtJsFiles.map(file => ({ path: `${resourcesPathPrefix}/${gwtModuleName}/${file?.split("/").pop()}` }));
  }

  public getReferencedCSSPaths(resourcesPathPrefix: string, gwtModuleName: string) {
    return [
      { path: `${resourcesPathPrefix}/${gwtModuleName}/jquery-ui/jquery-ui.min.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/bootstrap-daterangepicker/daterangepicker.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/bootstrap-select/css/bootstrap-select.min.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/uberfire-patternfly.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/patternfly-additions.min.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/bootstrap-datepicker3-1.6.4.min.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/animate-3.5.2.min.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/bootstrap-notify-custom.min.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/card-1.0.1.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/bootstrap-slider-9.2.0.min.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/bootstrap-datetimepicker-2.4.4.min.cache.css` },
      { path: `${resourcesPathPrefix}/${gwtModuleName}/css/typeahead-0.10.5.min.cache.css` }
    ];
  }

  public getFontResources(resourcesPathPrefix: string, gwtModuleName: string) {
    return [
      {
        family: "FontAwesome",
        additionalStyle: "font-weight:normal;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/fontawesome-webfont.ttf`)]
      },
      {
        family: "PatternFlyIcons-webfont",
        additionalStyle: "font-weight:normal;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/PatternFlyIcons-webfont.ttf`)]
      },
      {
        family: "Glyphicons Halflings",
        sources: [
          this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/glyphicons-halflings-regular.ttf`)
        ]
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:300;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Light-webfont.ttf`)]
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:400;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Regular-webfont.ttf`)]
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:600;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Semibold-webfont.ttf`)]
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:700;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-Bold-webfont.ttf`)]
      },
      {
        family: "Open Sans",
        additionalStyle: "font-weight:800;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/OpenSans-ExtraBold-webfont.ttf`)]
      },
      {
        family: "Font Awesome 5 Free",
        additionalStyle: "font-weight:900;font-style:normal;",
        sources: [this.createFontSource(`${resourcesPathPrefix}/${gwtModuleName}/fonts/fontawesome-webfont.ttf`)]
      }
    ];
  }

  public getEditorResourcesPath() {
    return externalAssets.dmnEditorPath();
  }

  public getTemplatePath() {
    return "dist/resources/dmn/dmnEnvelopeIndex.template";
  }

  public getHtmlOutputPath() {
    return "dist/resources/dmn/dmnEnvelopeIndex.html";
  }
}
