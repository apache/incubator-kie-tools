import { useGlobals } from "../../common/GlobalContext";
import { matchPath } from "react-router";
import { extname } from "path";

export enum UrlType {
  GITHUB,
  GIST,
  GIST_FILE,
  GITHUB_FILE,
  FILE,
  ZIP,
  INVALID,
}

export type ImportableUrl =
  | {
      type: UrlType.ZIP;
      errors?: undefined;
      url: URL;
    }
  | {
      type: UrlType.FILE;
      errors?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GIST;
      errors?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GIST_FILE;
      errors?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GITHUB_FILE;
      errors?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GITHUB;
      errors?: undefined;
      url: URL;
      branch?: string;
    }
  | {
      type: UrlType.INVALID;
      errors: string[];
      url: string;
    };

export function useImportableUrl(urlString?: string, allowedUrlTypes?: UrlType[]): ImportableUrl {
  const globals = useGlobals();

  const ifAllowed = (url: ImportableUrl): ImportableUrl => {
    if (allowedUrlTypes && !allowedUrlTypes.includes(url.type)) {
      return { type: UrlType.INVALID, errors: ["URL not allowed"], url: url.url.toString() };
    }

    return url;
  };

  if (!urlString) {
    return { type: UrlType.INVALID, errors: ["Empty URL"], url: "" };
  }

  let url: URL;
  try {
    url = new URL(urlString);
  } catch (e) {
    return { type: UrlType.INVALID, errors: ["Invalid URL"], url: urlString };
  }

  if (url.host === "github.com") {
    const defaultBranchMatch = matchPath<{ org: string; repo: string }>(url.pathname, {
      path: "/:org/:repo",
      exact: true,
      strict: true,
      sensitive: false,
    });

    const customBranchMatch = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
      path: "/:org/:repo/tree/:tree",
      exact: true,
      strict: true,
      sensitive: false,
    });

    if (defaultBranchMatch) {
      return ifAllowed({ type: UrlType.GITHUB, url });
    }

    if (customBranchMatch) {
      const branch = customBranchMatch.params.tree;
      const customBranchUrl = new URL(urlString);
      customBranchUrl.pathname = customBranchUrl.pathname.replace(`/tree/${branch}`, "");
      return ifAllowed({ type: UrlType.GITHUB, url: customBranchUrl, branch });
    }

    return { type: UrlType.INVALID, errors: ["Unsupported GitHub URL"], url: urlString };
  }

  if (url.host === "gist.github.com") {
    const match = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
      path: "/:user/:gistId",
      exact: true,
      strict: true,
    });

    if (!match) {
      return { type: UrlType.INVALID, errors: ["Unsupported Gist URL"], url: urlString };
    }

    return ifAllowed({ type: UrlType.GIST, url });
  }

  const extension = extname(url.pathname).replace(".", "");
  if (!extension) {
    return { type: UrlType.INVALID, errors: [`Can't determine file extension from URL`], url: urlString };
  }

  // if (extension === "zip") {
  //   return ifAllowed({ type: UrlType.ZIP, url });
  // }

  if (![...globals.editorEnvelopeLocator.mapping.keys()].includes(extension)) {
    return { type: UrlType.INVALID, errors: [`Unsupported extension '${extension}'`], url: urlString };
  }

  return ifAllowed({ type: UrlType.FILE, url });
}
