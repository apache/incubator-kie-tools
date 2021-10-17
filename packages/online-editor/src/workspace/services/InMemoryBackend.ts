import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";

export class InMemoryBackend {
  constructor(public readonly dexieBackend: DexieBackend, public fs = new Map<string, any>()) {}

  async saveSuperblock(superblock: any) {
    console.info("Saving superblock (in memory)...");
    this.fs.set("!root", superblock);
  }
  async loadSuperblock() {
    console.info("Reading superblock (in memory)...");
    return this.fs.get("!root");
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
