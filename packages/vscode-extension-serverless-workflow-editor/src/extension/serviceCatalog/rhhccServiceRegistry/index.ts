import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { RhhccAuthenticationStore } from "../../rhhcc/RhhccAuthenticationStore";

export class RhhccServiceRegistryServiceCatalogStore {
  private onChangeCallback: (services: SwfServiceCatalogService[]) => Promise<any>;

  constructor(private readonly rhhccAuthenticationStore: RhhccAuthenticationStore) {}

  public async init(callback: (swfServices: SwfServiceCatalogService[]) => Promise<any>) {
    this.onChangeCallback = callback;
    return this.refresh();
  }

  public async refresh() {
    const session = this.rhhccAuthenticationStore.session;
    if (session) {
      // TODO: tiago Implement
      // List registries
      // List artifacts in each registry
      // Filter by type, must be OpenAPI
      // Parse to SwfServiceCatalogService and call the callback;
      // doSomething(session.accessToken);
      return this.onChangeCallback([]);
    }

    return this.onChangeCallback([]);
  }
}
