export type K8sResourceYamlMetadata = {
  namespace?: string;
};

export type K8sResourceYaml = {
  apiVersion: string;
  kind: string;
  metadata?: K8sResourceYamlMetadata;
};

export type K8sApiServerEndpointByResourceKind = Map<
  string,
  Map<string, { url: { namespaced?: string; global: string } }>
>;

export function isValidK8sResource(content: any): content is K8sResourceYaml {
  return (
    "apiVersion" in content &&
    typeof content.apiVersion == "string" &&
    "kind" in content &&
    typeof content.kind == "string" &&
    (!("metadata" in content) || ("metadata" in content && typeof content === "object"))
  );
}
