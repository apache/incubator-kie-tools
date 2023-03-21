import { Resource } from "@kie-tools-core/openshift/dist/api/types";
import { KubernetesConnection } from "./KieSandboxKubernetesService";
import { ResourceFetch } from "@kie-tools-core/openshift/dist/fetch/ResourceFetch";
import { ContentTypes, HeaderKeys } from "@kie-tools-core/openshift/dist/fetch/FetchConstants";

export class KubernetesResourceFetcher {
  constructor(private readonly args: { connection: KubernetesConnection; proxyUrl?: string }) {}

  public async execute<T = Resource>(args: {
    target: ResourceFetch;
    rollbacks?: ResourceFetch[];
  }): Promise<Readonly<T>> {
    const targetUrl = `${this.args.connection.host}${args.target.endpoint()}`;
    const urlToFetch = this.args.proxyUrl ?? targetUrl;

    const headers: HeadersInit = {
      [HeaderKeys.AUTHORIZATION]: `Bearer ${this.args.connection.token}`,
      [HeaderKeys.ACCEPT]: ContentTypes.APPLICATION_JSON,
      [HeaderKeys.CONTENT_TYPE]: args.target.contentType(),
    };

    if (this.args.proxyUrl) {
      headers[HeaderKeys.TARGET_URL] = targetUrl;
    }

    try {
      const response = await fetch(urlToFetch, {
        method: args.target.method(),
        body: args.target.body(),
        headers,
      });

      if (response.ok) {
        return (await response.json()) as T;
      }
    } catch (e) {
      // No-op
    }

    if (args.rollbacks && args.rollbacks.length > 0) {
      for (const resource of args.rollbacks) {
        await this.execute({ target: resource });
      }
    }

    throw new Error(`Error fetching ${args.target.name()}`);
  }
}
