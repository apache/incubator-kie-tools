import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { v4 as uuid } from "uuid";
import LightningFS from "@isomorphic-git/lightning-fs";
import DefaultBackend from "@isomorphic-git/lightning-fs/src/DefaultBackend";
import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";
import { StorageFile, StorageService } from "./StorageService";
import { decoder, encoder } from "../WorkspacesContext";
import { WorkspaceKind } from "../model/WorkspaceOrigin";

const WORKSPACE_CONFIG_FS_NAME = "workspaces";
const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

export class WorkspaceDescriptorService {
  constructor(
    private readonly storageService: StorageService,
    private readonly workspacesFs = new LightningFS(WORKSPACE_CONFIG_FS_NAME, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => {
          return new DexieBackend(fileDbName, fileStoreName);
        },
      }) as any,
    })
  ) {}

  public async listAll(): Promise<WorkspaceDescriptor[]> {
    const workspaceDescriptorsFilePaths = await this.storageService.walk({
      fs: this.workspacesFs,
      startFromDirPath: "/",
      shouldExcludeDir: () => false,
      onVisit: (path) => path,
    });

    const workspaceDescriptorFiles = await this.storageService.getFiles(
      this.workspacesFs,
      workspaceDescriptorsFilePaths
    );

    return workspaceDescriptorFiles.map((workspaceDescriptorFile) =>
      this.getWorkspaceDescriptorFromFileContent(workspaceDescriptorFile.content)
    );
  }

  public async bumpLastUpdatedDate(workspaceId: string): Promise<void> {
    await this.storageService.updateFile(
      this.workspacesFs,
      this.workspaceDescriptorFile({
        ...(await this.get(workspaceId)),
        lastUpdatedDateISO: new Date().toISOString(),
      })
    );
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor> {
    const workspaceDescriptorFile = await this.storageService.getFile(this.workspacesFs, `/${workspaceId}`);
    if (!workspaceDescriptorFile) {
      throw new Error(`Workspace not found (${workspaceId})`);
    }
    return this.getWorkspaceDescriptorFromFileContent(await workspaceDescriptorFile.getFileContents());
  }

  async create() {
    const workspace: WorkspaceDescriptor = {
      workspaceId: this.newWorkspaceId(),
      name: NEW_WORKSPACE_DEFAULT_NAME,
      origin: { kind: WorkspaceKind.LOCAL },
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
    };
    await this.storageService.createFile(this.workspacesFs, this.workspaceDescriptorFile(workspace));
    return workspace;
  }

  public async delete(workspaceId: string) {
    await this.storageService.deleteFile(this.workspacesFs, `/${workspaceId}`);
  }

  public async rename(workspaceId: string, newName: string) {
    await this.storageService.updateFile(
      this.workspacesFs,
      this.workspaceDescriptorFile({
        ...(await this.get(workspaceId)),
        name: newName,
      })
    );
  }

  private getWorkspaceDescriptorFromFileContent(workspaceDescriptorFileContent: Uint8Array) {
    return JSON.parse(decoder.decode(workspaceDescriptorFileContent)) as WorkspaceDescriptor;
  }

  private workspaceDescriptorFile(descriptor: WorkspaceDescriptor) {
    return new StorageFile({
      path: `/${descriptor.workspaceId}`,
      getFileContents: () => Promise.resolve(encoder.encode(JSON.stringify(descriptor))),
    });
  }

  public newWorkspaceId(): string {
    return uuid();
  }
}
