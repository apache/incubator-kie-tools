import { FsCache } from "./FsCache";
import { encoder, WorkspaceFile } from "../WorkspacesContext";
import { StorageFile, StorageService } from "./StorageService";

export class WorkspaceSvgService {
  constructor(private readonly storageService: StorageService, private readonly fsCache = new FsCache()) {}

  public async getWorkspaceSvgsFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(`${workspaceId}__svgs`);
  }

  public async getSvg(workspaceFile: WorkspaceFile) {
    return this.storageService.getFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}.svg`
    );
  }

  public async deleteSvg(workspaceFile: WorkspaceFile) {
    const svgFile = await this.getSvg(workspaceFile);
    if (!svgFile) {
      console.debug(
        `Can't delete SVG, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    await this.storageService.deleteFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      `/${workspaceFile.relativePath}.svg`
    );
  }

  public async createOrOverwriteSvg(workspaceFile: WorkspaceFile, svgString: string) {
    await this.storageService.createOrOverwriteFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      new StorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(svgString)),
        path: `/${workspaceFile.relativePath}.svg`,
      })
    );
  }

  public async renameSvg(workspaceFile: WorkspaceFile, newFileNameWithoutExtension: string) {
    const svgFile = await this.getSvg(workspaceFile);
    if (!svgFile) {
      console.debug(
        `Can't rename SVG, because it doesn't exist for file '${workspaceFile.relativePath}' on Workspace '${workspaceFile.workspaceId}'`
      );
      return;
    }

    return this.storageService.renameFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      svgFile,
      `${newFileNameWithoutExtension}.${workspaceFile.extension}`
    );
  }

  public async delete(workspaceId: string) {
    indexedDB.deleteDatabase(WorkspaceSvgService.getSvgStoreName(workspaceId));
  }

  private static getSvgStoreName(workspaceId: string) {
    return `${workspaceId}__svgs`;
  }
}
