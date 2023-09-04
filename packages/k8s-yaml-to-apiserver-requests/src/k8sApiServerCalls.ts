import * as jsYaml from "js-yaml";
import * as reactRouter from "react-router";
import { K8sApiServerEndpointByResourceKind, K8sResourceYaml } from "./types";

export async function callK8sApiServer(args: {
  k8sApiServerEndpointsByResourceKind: K8sApiServerEndpointByResourceKind;
  k8sResourceYamls: K8sResourceYaml[];
  k8sApiServerUrl: string;
  k8sNamespace: string;
  k8sServiceAccountToken: string;
}) {
  const apiCalls = args.k8sResourceYamls.map((yamlDocument) => {
    const rawEndpoints = args.k8sApiServerEndpointsByResourceKind
      .get(yamlDocument.kind)
      ?.get(yamlDocument.apiVersion ?? "v1");
    if (!rawEndpoints) {
      throw new Error(
        `Can't create '${yamlDocument.kind}' because there's no matching API for it registered on '${args.k8sApiServerUrl}'`
      );
    }

    const rawEndpoint = rawEndpoints?.url.namespaced ?? rawEndpoints?.url.global;

    return {
      kind: yamlDocument.kind,
      yaml: yamlDocument,
      rawEndpoint: rawEndpoint,
    };
  });
  console.info("Done.");
  console.info("");

  // Simulate actual API calls
  console.info("Start calling API endpoints for each parsed YAML...");

  const results = [];
  for (const apiCall of apiCalls) {
    // FIXME: Interpolate :namespace with actual namespace.
    const endpointUrl = new URL(apiCall.rawEndpoint);
    const interpolatedPathname = endpointUrl.pathname.replace(
      ":namespace",
      apiCall.yaml.metadata?.namespace ?? args.k8sNamespace
    );
    endpointUrl.pathname = interpolatedPathname;

    console.info(`Creating '${apiCall.kind}' with POST ${endpointUrl.toString()}`);
    results.push(
      await fetch(endpointUrl.toString(), {
        headers: {
          Authorization: `Bearer ${args.k8sServiceAccountToken}`,
          "Content-Type": "application/yaml",
        },
        method: "POST",
        body: jsYaml.dump(apiCall.yaml),
      }).then((response) => response.json())
    );
    // .then((res) => {
    //   console.info(`STATUS: ${res.status} - ${res.statusText}`);
    //   res.text();
    // })
    // .then((text) => {
    //   console.info(text);
    // });
  }
  console.info("Done.");

  return results;
}
