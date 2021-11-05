import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { v4 as uuid } from "uuid";
import LightningFS from "@isomorphic-git/lightning-fs";
import DefaultBackend from "@isomorphic-git/lightning-fs/src/DefaultBackend";
import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";
import { StorageFile, StorageService } from "./StorageService";
import { decoder, encoder } from "../WorkspacesContext";
import { WorkspaceKind, WorkspaceOrigin } from "../model/WorkspaceOrigin";
import { GIST_DEFAULT_BRANCH } from "./GitService";

const WORKSPACE_DESCRIPTORS_FS_NAME = "workspaces";
const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

export class WorkspaceDescriptorService {
  constructor(
    private readonly storageService: StorageService,
    private readonly descriptorsFs = new LightningFS(WORKSPACE_DESCRIPTORS_FS_NAME, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => {
          return new DexieBackend(fileDbName, fileStoreName);
        },
      }) as any,
    })
  ) {}

  public async listAll(): Promise<WorkspaceDescriptor[]> {
    const workspaceDescriptorsFilePaths = await this.storageService.walk({
      fs: this.descriptorsFs,
      startFromDirPath: "/",
      shouldExcludeDir: () => false,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const workspaceDescriptorFiles = await this.storageService.getFiles(
      this.descriptorsFs,
      workspaceDescriptorsFilePaths
    );

    return workspaceDescriptorFiles.map((workspaceDescriptorFile) =>
      this.getWorkspaceDescriptorFromFileContent(workspaceDescriptorFile.content)
    );
  }

  public async bumpLastUpdatedDate(workspaceId: string): Promise<void> {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        lastUpdatedDateISO: new Date().toISOString(),
      })
    );
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor> {
    const workspaceDescriptorFile = await this.storageService.getFile(this.descriptorsFs, `/${workspaceId}`);
    if (!workspaceDescriptorFile) {
      throw new Error(`Workspace not found (${workspaceId})`);
    }
    return this.getWorkspaceDescriptorFromFileContent(await workspaceDescriptorFile.getFileContents());
  }

  public async create(args: { origin: WorkspaceOrigin; preferredName?: string }) {
    const workspace: WorkspaceDescriptor = {
      workspaceId: this.newWorkspaceId(),
      name: args.preferredName?.trim() || NEW_WORKSPACE_DEFAULT_NAME,
      origin: args.origin,
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
    };
    await this.storageService.createOrOverwriteFile(this.descriptorsFs, this.toStorageFile(workspace));
    return workspace;
  }

  public async delete(workspaceId: string) {
    await this.storageService.deleteFile(this.descriptorsFs, `/${workspaceId}`);
  }

  public async rename(workspaceId: string, newName: string) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        name: newName,
      })
    );
  }

  public async turnIntoGist(workspaceId: string, gistUrl: URL) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        origin: {
          kind: WorkspaceKind.GITHUB_GIST,
          url: gistUrl,
          branch: GIST_DEFAULT_BRANCH,
        },
      })
    );
  }

  private getWorkspaceDescriptorFromFileContent(workspaceDescriptorFileContent: Uint8Array) {
    return JSON.parse(decoder.decode(workspaceDescriptorFileContent), (_key: string, value: any) => {
      //FIXME: OMG this is ugly
      if (typeof value === "string") {
        try {
          return new URL(value);
        } catch (e) {
          return value;
        }
      }

      return value;
    }) as WorkspaceDescriptor;
  }

  private toStorageFile(descriptor: WorkspaceDescriptor) {
    return new StorageFile({
      path: `/${descriptor.workspaceId}`,
      getFileContents: () => Promise.resolve(encoder.encode(JSON.stringify(descriptor))),
    });
  }

  public newWorkspaceId(): string {
    return uuid();
  }
}
