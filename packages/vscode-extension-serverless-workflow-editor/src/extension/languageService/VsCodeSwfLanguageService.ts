import { TextDocument } from "vscode-languageserver-textdocument";
import * as vscode from "vscode";
import { RhhccAuthenticationStore } from "../rhhcc/RhhccAuthenticationStore";
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogStore } from "../serviceCatalog/SwfServiceCatalogStore";
import { posix as posixPath } from "path";
import { SwfLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { FsWatchingServiceCatalogRelativeStore } from "../serviceCatalog/fs";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "../serviceCatalog/rhhccServiceRegistry";

export class VsCodeSwfLanguageService {
  public readonly ls: SwfLanguageService;
  private readonly fsWatchingSwfServiceCatalogStore: Map<string, FsWatchingServiceCatalogRelativeStore> = new Map();
  constructor(
    private readonly args: {
      rhhccAuthenticationStore: RhhccAuthenticationStore;
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {
    this.ls = new SwfLanguageService({
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => {
            return args.swfServiceCatalogGlobalStore.storedServices;
          },
        },
        relative: {
          getServices: async (textDocument) => {
            const specsDirAbsolutePosixPath = this.getSpecsDirPosixPaths(textDocument).specsDirAbsolutePosixPath;
            let swfServiceCatalogRelativeStore = this.fsWatchingSwfServiceCatalogStore.get(specsDirAbsolutePosixPath);
            if (swfServiceCatalogRelativeStore) {
              return swfServiceCatalogRelativeStore.getServices();
            }

            swfServiceCatalogRelativeStore = new FsWatchingServiceCatalogRelativeStore({
              baseFileAbsolutePosixPath: vscode.Uri.parse(textDocument.uri).path,
              configuration: this.args.configuration,
            });

            await swfServiceCatalogRelativeStore.init();
            this.fsWatchingSwfServiceCatalogStore.set(specsDirAbsolutePosixPath, swfServiceCatalogRelativeStore);
            return swfServiceCatalogRelativeStore.getServices();
          },
        },
        getServiceFileNameFromSwfServiceCatalogServiceId: async (s) => {
          return getServiceFileNameFromSwfServiceCatalogServiceId(s);
        },
      },
      config: {
        shouldDisplayRhhccIntegration: async () => {
          // FIXME: Tiago: This should take the OS into account as well. RHHCC integration only works on macOS.
          return vscode.env.uiKind === vscode.UIKind.Desktop;
        },
        getServiceRegistryUrl: () => {
          return args.configuration.getConfiguredServiceRegistryUrl();
        },
        getServiceRegistryAuthInfo: () => {
          const session = args.rhhccAuthenticationStore.session;
          return !session ? undefined : { username: session.account.label, token: session.accessToken };
        },
        getSpecsDirPosixPaths: async (textDocument) => {
          return this.getSpecsDirPosixPaths(textDocument);
        },
      },
    });
  }

  private getSpecsDirPosixPaths(document: TextDocument) {
    const baseFileAbsolutePosixPath = vscode.Uri.parse(document.uri).path;

    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
      baseFileAbsolutePosixPath,
    });

    const specsDirRelativePosixPath = posixPath.relative(
      posixPath.dirname(baseFileAbsolutePosixPath),
      specsDirAbsolutePosixPath
    );

    return { specsDirRelativePosixPath, specsDirAbsolutePosixPath };
  }

  public dispose() {
    this.ls.dispose();
    return Array.from(this.fsWatchingSwfServiceCatalogStore.values()).forEach((f) => f.dispose());
  }
}
