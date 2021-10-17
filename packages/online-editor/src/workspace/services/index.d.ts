declare module "@isomorphic-git/lightning-fs/src/DefaultBackend" {
  import LightningFS from "@isomorphic-git/lightning-fs";

  export default class DefaultBackend implements LightningFS.FSBackend {
    constructor(args: { idbBackendDelegate: (fileDbName: string, fileStoreName: string) => any }) {}
  }
}

declare module "@isomorphic-git/lightning-fs/src/DexieBackend" {
  export default class DexieBackend {
    _dexie: any;
    _storename: any;
    constructor(fileDbName: string, fileStoreName: string) {}
    async saveSuperblock(superblock: any);
    async loadSuperblock();
    async readFile(inode: string);
    async readFileBulk(inodeBulk: string[]);
    async writeFile(inode: string, data: any);
    async writeFileBulk(inodeBulk: string[], dataBulk: any[]);
    async unlink(inode: string);
    async unlinkBulk(inodeBulk: string[]);
    async wipe();
    async close();
  }
}
