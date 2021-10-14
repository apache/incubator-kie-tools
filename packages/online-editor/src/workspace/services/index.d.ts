declare module "@isomorphic-git/lightning-fs/src/DefaultBackend" {
  import LightningFS from "@isomorphic-git/lightning-fs";

  export default class DefaultBackend implements LightningFS.FSBackend {
    constructor(args: { idbBackendDelegate: (fileDbName: string, fileStoreName: string) => any }) {}
  }
}
