import * as vscode from "vscode";
import {
  ContentType,
  ResourceContentOptions,
  ResourcesList,
  ResourceContent,
  ResourceContentService
} from "@kogito-tooling/core-api";

export class VsCodeResourceContentService implements ResourceContentService {
  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    const contentPath = this.resolvePath(path)!;
    if (!contentPath) {
      return new ResourceContent(path, undefined);
    }
    try {
      await vscode.workspace.fs.stat(vscode.Uri.parse(contentPath));
    } catch (e) {
      console.debug(`Error checking file ${path}: ${e}`);
      return new ResourceContent(path, undefined);
    }
    return this.retrieveContent(opts?.type, path, contentPath);
  }

  public async list(pattern: string): Promise<ResourcesList> {
    const files = await vscode.workspace.findFiles(pattern);
    const paths = files.map(f => vscode.workspace.asRelativePath(f.path));
    return new ResourcesList(pattern, paths);
  }

  private resolvePath(path: string) {
    const folders = vscode.workspace!.workspaceFolders!;
    if (!folders) {
      return null;
    }

    const rootPath = folders[0].uri.path;
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    return rootPath + path;
  }

  private retrieveContent(type: ContentType | undefined, path: string, contentPath: string): Thenable<ResourceContent> {
    if (type === ContentType.BINARY) {
      return vscode.workspace.fs
        .readFile(vscode.Uri.parse(contentPath))
        .then(content => new ResourceContent(path, Buffer.from(content).toString("base64"), ContentType.BINARY));
    } else {
      return vscode.workspace
        .openTextDocument(contentPath)
        .then(textDoc => new ResourceContent(path, textDoc.getText(), ContentType.TEXT));
    }
  }
}
