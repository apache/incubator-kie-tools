import { extname } from "path";

// FIXME: Temporary
export function resolveExtension(path: string): string {
  const regex = /(\.sw\.json|\.sw\.yaml|\.sw\.yml)$/;
  const match = regex.exec(path.toLowerCase());
  const extension = match ? match[1] : extname(path);
  return extension.slice(1);
}
