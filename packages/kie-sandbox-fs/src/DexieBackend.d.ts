export default class DexieBackend {
  _dexie: any;
  _storename: any;
  constructor(dbName: string, storeName: string) {}
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
