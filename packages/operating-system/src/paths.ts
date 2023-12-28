import * as __path from "path";

export function toFsPath(posixPath: string) {
  return posixPath.split("/").join(__path.sep);
}

export function toPosixPath(posixPath: string) {
  return posixPath.split(__path.sep).join("/");
}
