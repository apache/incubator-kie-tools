import Dexie from "dexie";

export class IdbDexieBackend {
  private readonly _dexie: any;
  private readonly _storename: string;
  constructor(dbname: string, storename: string) {
    const stores = {} as any;
    stores[storename] = "";
    this._dexie = new Dexie(dbname);
    this._dexie.version(1).stores(stores);
    this._storename = storename;
  }
  async saveSuperblock(superblock: any) {
    await this._dexie.open();
    return await this._dexie[this._storename].put(superblock, "!root");
  }
  async loadSuperblock() {
    await this._dexie.open();
    return await this._dexie[this._storename].get("!root");
  }
  async readFile(inode: string) {
    await this._dexie.open();
    return await this._dexie[this._storename].get(inode);
  }
  async readFileBulk(inodeBulk: string[]) {
    await this._dexie.open();
    return await this._dexie[this._storename].bulkGet(inodeBulk);
  }
  async writeFile(inode: string, data: any) {
    await this._dexie.open();
    return await this._dexie[this._storename].put(data, inode);
  }
  async writeFileBulk(inodeBulk: string[], dataBulk: any[]) {
    await this._dexie.open();
    await this._dexie[this._storename].bulkPut(dataBulk, inodeBulk);
  }
  async unlink(inode: string) {
    await this._dexie.open();
    return await this._dexie[this._storename].delete(inode);
  }
  async wipe() {
    return this._dexie.clear();
  }
  async close() {
    return this._dexie.close();
  }
}
