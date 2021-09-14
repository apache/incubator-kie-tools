export function useQueryParams() {
  return new URLSearchParams(window.location.search);
}

export enum QueryParams {
  SETTINGS = "settings",
  GITHUB_OAUTH_CODE = "code",
  GITHUB_OAUTH_STATE = "state",
  EXT = "ext",
  FILE = "file",
  READONLY = "readonly",
}
