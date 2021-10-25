import LightningFS from "@isomorphic-git/lightning-fs";
import DefaultBackend from "@isomorphic-git/lightning-fs/src/DefaultBackend";
import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";

export class FsCache {
  private fsCache = new Map<string, LightningFS>();
  public getOrCreateFs(workspaceId: string) {
    const fs = this.fsCache.get(workspaceId);
    if (fs) {
      return fs;
    }

    const newFs = new LightningFS(workspaceId, {
      backend: new DefaultBackend({
        idbBackendDelegate: (dbName, storeName) => new DexieBackend(dbName, storeName),
      }) as any,
    });

    this.fsCache.set(workspaceId, newFs);
    return newFs;
  }
}
