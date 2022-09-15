export interface LfsStat {
  mode: number;
  size: number;
  ino: number;
  mtimeMs: number;
  ctimeMs: number;
  uid: 1;
  gid: 1;
  dev: 1;
  isDirectory: () => boolean;
  isFile: () => boolean;
  isSymbolicLink: () => boolean;
}

export interface KieSandboxWorkspacesFs {
  promises: {
    rename(path: string, newPath: string): Promise<void>;
    readFile(path: string, options?: any): Promise<Uint8Array>;
    writeFile(path: any, data: any, options?: any): Promise<void>;
    unlink(path: any): Promise<void>;
    readdir(path: any, options?: any): Promise<string[]>;
    mkdir(path: any, mode?: any): Promise<void>;
    rmdir(path: any): Promise<void>;
    stat(path: any, options?: any): Promise<LfsStat>;
    lstat(path: any, options?: any): Promise<LfsStat>;
    readlink(path: any, options?: any): Promise<string>;
    symlink(target: any, path: any, type: any): Promise<void>;
    chmod(path: any, mode: any): Promise<void>;
  };
}
