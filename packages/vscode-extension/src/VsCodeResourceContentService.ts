import * as vscode from "vscode";
import {
  ContentType,
  ResourceContentOptions,
  ResourcesList,
  ResourceContent,
  ResourceContentService
} from "@kogito-tooling/core-api";

export class VsCodeResourceContentService implements ResourceContentService {
  public get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    const contentPath = this.resolvePath(path)!;
    const type = opts?.type;
    if (contentPath) {
      return new Promise(resolve => {
        vscode.workspace.fs.stat(vscode.Uri.parse(contentPath)).then(
          fileStat => {
            if (type === ContentType.BINARY) {
              this.retrieveBinaryContent(contentPath, resolve, path);
            } else {
              this.retrieveTextContent(contentPath, resolve, path);
            }
          },
          e => this.errorRetrievingFile(e, path, resolve)
        );
      });
    }
    return Promise.resolve(new ResourceContent(path, undefined));
  }

  public list(pattern: string): Promise<ResourcesList> {
    return new Promise((resolve, error) => {
      vscode.workspace.findFiles(pattern).then(files => {
        const paths: string[] = files.map(f => f.path).map(f => vscode.workspace.asRelativePath(f));
        resolve(new ResourcesList(pattern, paths));
      });
    });
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

  private errorRetrievingFile(errorMsg: string, uri: string, resolve: (value: any) => void): void {
    console.error(`Error retrieving file ${uri}: ${errorMsg}`);
    resolve(new ResourceContent(uri, undefined));
  }

  private retrieveTextContent(contentPath: string, resolve: (value: ResourceContent) => void, path: string) {
    vscode.workspace.openTextDocument(contentPath).then(
      textDoc => {
        const textContent = textDoc.getText();
        resolve(new ResourceContent(path, textContent, ContentType.TEXT));
      },
      e => this.errorRetrievingFile(e, contentPath, resolve)
    );
  }

  private retrieveBinaryContent(contentPath: string, resolve: (value: ResourceContent) => void, path: string) {
    vscode.workspace.fs.readFile(vscode.Uri.parse(contentPath)).then(
      content => {
        const base64Content = new Buffer(content).toString("base64");
        resolve(new ResourceContent(path, base64Content, ContentType.BINARY));
      },
      e => this.errorRetrievingFile(e, contentPath, resolve)
    );
  }
}
