import { HttpMethod } from "@kie-tools-core/openshift/dist/fetch/FetchConstants";
import { ResourceFetch } from "@kie-tools-core/openshift/dist/fetch/ResourceFetch";

export class PingCluster extends ResourceFetch {
  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/apis/apps/v1/namespaces/${this.args.namespace}/deployments`;
  }
}
