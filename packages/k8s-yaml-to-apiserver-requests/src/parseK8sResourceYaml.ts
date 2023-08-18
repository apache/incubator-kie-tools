import * as jsYaml from "js-yaml";
import { K8sResourceYaml, isValidK8sResource } from "./types";

export function parseK8sResourceYaml(yamls: string[]): K8sResourceYaml[] {
  const parsedResources: K8sResourceYaml[] = [];
  yamls.forEach((yamlContent) => {
    const parsedContent = jsYaml.loadAll(yamlContent);
    parsedContent.forEach((parsedYaml) => {
      if (isValidK8sResource(parsedYaml)) {
        parsedResources.push(parsedYaml);
      }
    });
  });

  return parsedResources;
}
