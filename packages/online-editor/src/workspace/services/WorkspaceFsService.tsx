import LightningFS from "@isomorphic-git/lightning-fs";
import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";
import { InMemoryBackend } from "./InMemoryBackend";
import DefaultBackend from "@isomorphic-git/lightning-fs/src/DefaultBackend";

export class WorkspaceFsService {
  private fsCache = new Map<string, LightningFS>();

  public getWorkspaceFs(workspaceId: string) {
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

  public async withInMemoryFs<T>(workspaceId: string, callback: (fs: LightningFS) => Promise<T>) {
    const { fs, flush } = await this.createInMemoryWorkspaceFs(workspaceId);
    const ret = await callback(fs);
    await flush();
    return ret;
  }

  private async createInMemoryWorkspaceFs(workspaceId: string) {
    const readEntireFs = async (dexieBackend: DexieBackend) => {
      console.info("MEM :: Reading FS to memory");
      console.time("MEM :: Reading FS to memory");
      await dexieBackend._dexie.open();
      const keys = await dexieBackend._dexie.table(dexieBackend._storename).toCollection().keys();
      const data = await dexieBackend.readFileBulk(keys);
      const fsAsMapConstructorParameter: any[] = [];
      for (let i = 0; i < data.length; i++) {
        fsAsMapConstructorParameter[i] = [keys[i], data[i]];
      }
      console.timeEnd("MEM :: Reading FS to memory");
      return fsAsMapConstructorParameter;
    };

    const dbName = workspaceId; // don't change. (This is hardcoded on LightningFS).
    const storeName = workspaceId + "_files"; // don't change (This is hardcoded on LightningFS).
    const dexieBackend = new DexieBackend(dbName, storeName);
    const inMemoryBackend = new InMemoryBackend(dexieBackend, new Map(await readEntireFs(dexieBackend)));

    const flush = async () => {
      // TODO: Mutate `inMemoryBackend` to not allow further use after flush.
      // TODO: Make a lock mechanism to not allow interactions with this FS while the in-memory operation is in progress
      // TODO: Improve `autoinc` performance. Right now it's iterating over the superblock on each write.
      return new Promise<void>((res) => {
        setTimeout(async () => {
          const inodeBulk = Array.from(inMemoryBackend.fs.keys());
          const dataBulk = Array.from(inMemoryBackend.fs.values());
          console.info("MEM :: Flushing in memory FS");
          console.time("MEM :: Flushing in memory FS");
          await dexieBackend.writeFileBulk(inodeBulk, dataBulk);
          console.timeEnd("MEM :: Flushing in memory FS");
          res();
        }, 500); // necessary to wait for debounce of 500ms (This is hardcoded on LightningFS).
      });
    };

    const fs = new LightningFS(dbName, {
      backend: new DefaultBackend({ idbBackendDelegate: () => inMemoryBackend }) as any,
    });

    return { fs, flush };
  }
}
