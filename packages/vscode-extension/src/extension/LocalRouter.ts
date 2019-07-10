import { LanguageData } from "appformer-js-microeditor-router";
import * as vscode from "vscode";
import * as __path from "path";

const dmnGwtModuleName = "org.kie.workbench.common.dmn.showcase.DMNShowcase";
const dmnDistPath = `dist/webview/editors/dmn/`;
const bpmnGwtModuleName = "org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase";
const bpmnDistPath = `dist/webview/editors/bpmn/`;

export class LocalRouter {
  private readonly context: vscode.ExtensionContext;
  private readonly languageDataByFileExtension: Map<string, LanguageData>;

  constructor(context: vscode.ExtensionContext) {
    this.context = context;
    this.languageDataByFileExtension = new Map<string, LanguageData>([
      [
        "dmn",
        {
          editorId: "DMNDiagramEditor",
          gwtModuleName: dmnGwtModuleName,
          erraiDomain: "",
          resources: [
            {
              type: "css",
              paths: [this.getRelativePathTo(`${dmnDistPath}${dmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                this.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/ace/ace.js`),
                this.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/ace/theme-chrome.js`),
                this.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/${dmnGwtModuleName}.nocache.js`)
              ]
            }
          ]
        }
      ],
      [
        //FIXME: BPMN doesn't have a client-side only editor yet.
        "bpmn",
        {
          editorId: "BPMNStandaloneDiagramEditor",
          gwtModuleName: bpmnGwtModuleName,
          erraiDomain: "",
          resources: [
            {
              type: "css",
              paths: [this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/ace.js`),
                this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/theme-chrome.js`),
                this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/${bpmnGwtModuleName}.nocache.js`)
              ]
            }
          ]
        }
      ]
    ]);
  }

  public getRelativePathTo(uri: string) {
    return vscode.Uri.file(__path.join(this.context.extensionPath, ...uri.split("/")))
      .with({
        scheme: "vscode-resource"
      })
      .toString();
  }

  public getLanguageData(fileExtension: string) {
    return this.languageDataByFileExtension.get(fileExtension);
  }
}
