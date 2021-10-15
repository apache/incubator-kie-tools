declare module "@isomorphic-git/lightning-fs/src/DefaultBackend" {
  import LightningFS from "@isomorphic-git/lightning-fs";

  export default class DefaultBackend implements LightningFS.FSBackend {
    constructor(args: { idbBackendDelegate: (fileDbName: string, fileStoreName: string) => any }) {}
  }
}

declare module "@isomorphic-git/lightning-fs/src/DexieBackend" {
  import LightningFS from "@isomorphic-git/lightning-fs";

  export default class DexieBackend implements LightningFS.FSBackend {
    constructor(fileDbName: string, fileStoreName: string) {}
  }
}
