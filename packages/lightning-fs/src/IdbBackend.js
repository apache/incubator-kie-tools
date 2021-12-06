const idb = require("@wmhilton/idb-keyval");

module.exports = class IdbBackend {
  constructor(name) {
    this._database = name;
    this._store = new idb.Store(this._database, this._database + "_files");
  }
  saveSuperblock(superblock) {
    return idb.set("!root", superblock, this._store);
  }
  loadSuperblock() {
    return idb.get("!root", this._store);
  }
  readFile(inode) {
    return idb.get(inode, this._store)
  }
  writeFile(inode, data) {
    return idb.set(inode, data, this._store)
  }
  unlink(inode) {
    return idb.del(inode, this._store)
  }
  wipe() {
    return idb.clear(this._store)
  }
  close() {
    return idb.close(this._store)
  }
}
