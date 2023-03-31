export type K8sResourceYaml = {
  apiVersion: string;
  kind: string;
  metadata?: {
    namespace?: string;
  };
};

export type K8sApiServerEndpointByResourceKind = Map<
  string,
  Map<string, { url: { namespaced?: string; global: string } }>
>;
