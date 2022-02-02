import KieSandboxFs from "./index";

export default class DefaultBackend implements KieSandboxFs.FSBackend {
  constructor(args: { idbBackendDelegate: (fileDbName: string, fileStoreName: string) => any }) {}
}
