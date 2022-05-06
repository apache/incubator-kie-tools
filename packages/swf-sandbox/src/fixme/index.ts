import { basename, extname } from "path";

// FIXME: Temporary
export function resolveExtension(path: string): string {
  const fileName = basename(path);
  if (fileName.startsWith(".")) {
    return fileName.slice(1);
  }
  const regex = /(\.sw\.json|\.sw\.yaml|\.sw\.yml|\.sd\.yml|\.sd\.yaml|\.decision\.yml|\.decision\.yaml)$/;
  const match = regex.exec(path.toLowerCase());
  const extension = match ? match[1] : extname(path);
  return extension ? extension.slice(1) : "";
}

export function isServerlessWorkflow(path: string): boolean {
  return /^.*\.sw\.(json|yml|yaml)$/.test(path);
}

export function isServerlessDecision(path: string): boolean {
  return /^.*\.(sd|decision)\.(yml|yaml)$/.test(path);
}
