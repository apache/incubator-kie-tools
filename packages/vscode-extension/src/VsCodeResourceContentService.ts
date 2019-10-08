import * as vscode from "vscode";
import { ResourcesList, ResourceContent, ResourceContentService } from "@kogito-tooling/core-api";

export class VsCodeResourceContentService implements ResourceContentService {

  public read(uri: string): Promise<ResourceContent | undefined> {
    const contentPath: string = this.resolvePath(uri)!;
    if (contentPath) {
      const content = vscode.workspace.openTextDocument(contentPath);
      return new Promise((resolve, error) => {
        content.then(textDoc => {
          resolve(new ResourceContent(uri, textDoc.getText()));
        }, errorMsg => {
          console.log(`Error retrieving file ${uri}: ${errorMsg}`);
          resolve(new ResourceContent(uri, undefined));
        });
      });
    }
    return Promise.resolve(new ResourceContent(uri, undefined));
  }

  public list(pattern: string): Promise<ResourcesList> {
    return new Promise((resolve, error) => {
      vscode.workspace.findFiles(pattern).then(files => {
        const paths: string[] = files.map(f => f.path)
          .map(f => vscode.workspace.asRelativePath(f));
        resolve(new ResourcesList(pattern, paths));
      });
    })
  }

  private resolvePath(uri: string) {
    const folders: vscode.WorkspaceFolder[] = vscode.workspace!.workspaceFolders!;
    if (folders) {
      const rootPath = folders[0].uri.path;
      if (!uri.startsWith("/")) {
        uri = "/" + uri;
      }
      return rootPath + uri;
    }
    return null;
  }

}