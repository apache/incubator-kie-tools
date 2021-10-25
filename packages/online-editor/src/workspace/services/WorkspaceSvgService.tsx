import { FsCache } from "./FsCache";
import { WorkspaceService } from "./WorkspaceService";
import { encoder, WorkspaceFile } from "../WorkspacesContext";

export class WorkspaceSvgService {
  constructor(private readonly workspaceService: WorkspaceService, private readonly fsCache = new FsCache()) {}

  public async getWorkspaceSvgsFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(`${workspaceId}__svgs`);
  }

  public async getSvg(workspaceFile: WorkspaceFile) {
    return this.workspaceService.getFile({
      fs: await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      workspaceId: workspaceFile.workspaceId,
      relativePath: `${workspaceFile.relativePath}.svg`,
    });
  }

  async createOrOverwriteSvg(workspaceFile: WorkspaceFile, svgString: any) {
    await this.workspaceService.createOrOverwriteFile(
      await this.getWorkspaceSvgsFs(workspaceFile.workspaceId),
      new WorkspaceFile({
        workspaceId: workspaceFile.workspaceId,
        getFileContents: () => Promise.resolve(encoder.encode(svgString)),
        relativePath: `${workspaceFile.relativePath}.svg`,
      }),
      { broadcast: false }
    );
  }
}
