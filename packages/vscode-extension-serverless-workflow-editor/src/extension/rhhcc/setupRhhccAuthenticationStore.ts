import { RhhccAuthenticationStore } from "./RhhccAuthenticationStore";
import * as vscode from "vscode";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../configuration";
import { askForServiceRegistryUrl } from "../serviceCatalog/rhhccServiceRegistry";
import { RhhccServiceRegistryServiceCatalogStore } from "../serviceCatalog/rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";

export async function setupRhhccAuthenticationStore(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  rhhccAuthenticationStore: RhhccAuthenticationStore;
  rhhccServiceRegistryServiceCatalogStore: RhhccServiceRegistryServiceCatalogStore;
}) {
  args.context.subscriptions.push(
    vscode.authentication.onDidChangeSessions(async (e) => {
      if (e.provider.id === "redhat-mas-account-auth") {
        await args.rhhccAuthenticationStore.setSession(await getSession());
      }
    })
  );

  args.context.subscriptions.push(
    args.rhhccAuthenticationStore.subscribeToSessionChange(async (session) => {
      if (!session) {
        return args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const configuredServiceRegistryUrl = args.configuration.getConfiguredServiceRegistryUrl();
      if (configuredServiceRegistryUrl) {
        return args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const serviceRegistryUrl = await askForServiceRegistryUrl({ currentValue: configuredServiceRegistryUrl });
      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);

      return args.rhhccServiceRegistryServiceCatalogStore.refresh();
    })
  );

  await args.rhhccAuthenticationStore.setSession(await getSession());
}

async function getSession() {
  return vscode.authentication.getSession("redhat-mas-account-auth", ["openid"]);
}
