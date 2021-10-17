import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";

export let isFsBatchInProgress = false;

export class InMemoryBackend {
  private fs = new Map<string, any>();
  private isFlushingSuperblock = false;

  constructor(private readonly dexieBackend: DexieBackend) {
    isFsBatchInProgress = true;
  }

  private async readEntireFs() {
    await this.dexieBackend._dexie.open();
    const keys = await this.dexieBackend._dexie.table(this.dexieBackend._storename).toCollection().keys();
    const data = await this.dexieBackend.readFileBulk(keys);
    const fs: any[] = [];
    for (let i = 0; i < data.length; i++) {
      fs[i] = [keys[i], data[i]];
    }
    return fs;
  }

  async saveSuperblock(superblock: any) {
    if (this.isFlushingSuperblock) {
      console.info("ABORTING BECAUSE FLUSHING IS ALREADY HAPPENING");
      return;
    }

    this.isFlushingSuperblock = true;

    console.info("MEM :: Saving superblock (prep)");
    this.fs.set("!root", superblock);
    const inodeBulk = Array.from(this.fs.keys());
    const dataBulk = Array.from(this.fs.values());
    console.info("MEM :: Saving superblock (flush)");
    console.time("MEM :: Saving superblock");
    await this.dexieBackend.writeFileBulk(inodeBulk, dataBulk);
    //TODO: delete what was deleted.
    console.timeEnd("MEM :: Saving superblock");

    isFsBatchInProgress = false;
    this.isFlushingSuperblock = false;
  }
  async loadSuperblock() {
    console.info("MEM :: Loading superblock");
    console.time("MEM :: Loading superblock");
    this.fs = new Map(await this.readEntireFs());
    const ret = this.fs.get("!root");
    console.timeEnd("MEM :: Loading superblock");
    return ret;
  }
  async readFile(inode: string) {
    return this.fs.get(inode);
  }
  async writeFile(inode: string, data: any) {
    this.fs.set(inode, data);
  }
  async unlink(inode: string) {
    this.fs.delete(inode);
  }
  async wipe() {
    this.fs = new Map();
    await this.dexieBackend.wipe();
  }
  async close() {
    await this.dexieBackend.close();
  }
  async readFileBulk(inodeBulk: string[]) {
    const ret = [];
    for (const ino of inodeBulk) {
      ret.push(this.fs.get(ino));
    }
    return ret;
  }
  async writeFileBulk(inodeBulk: string[], dataBulk: any[]) {
    for (let i = 0; i < inodeBulk.length; i++) {
      this.fs.set(inodeBulk[i], dataBulk[i]);
    }
  }
  async unlinkBulk(inodeBulk: string[]) {
    for (const ino of inodeBulk) {
      this.fs.delete(ino);
    }
  }
}
