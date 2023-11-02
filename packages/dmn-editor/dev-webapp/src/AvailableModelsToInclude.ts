import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { sumBkm, sumDiffDs, testTreePmml } from "./ExternalModels";
import * as DmnEditor from "../../src/DmnEditor";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { buildXmlHref } from "../../src/xml/xmlHrefs";
import { PMML_NAMESPACE } from "../../src/store/useDiagramData";

export const sumBkmModel = getMarshaller(sumBkm, { upgradeTo: "latest" }).parser.parse();
export const sumDiffDsModel = getMarshaller(sumDiffDs, { upgradeTo: "latest" }).parser.parse();
export const testTreePmmlModel = XML2PMML(testTreePmml);

export const avaiableModels: DmnEditor.ExternalModel[] = [
  {
    type: "dmn",
    model: sumBkmModel,
    svg: "",
    path: "dev-webapp/available-models-to-include/sumBkm.dmn",
  },
  {
    type: "dmn",
    model: sumDiffDsModel,
    svg: "",
    path: "dev-webapp/available-models-to-include/sumDiffDs.dmn",
  },
  {
    type: "pmml",
    model: testTreePmmlModel,
    path: "dev-webapp/available-models-to-include/testTree.pmml",
  },
];

export const availableModelsByPath: Record<string, DmnEditor.ExternalModel> = Object.values(avaiableModels).reduce(
  (acc, v) => {
    acc[v.path] = v;
    return acc;
  },
  {} as Record<string, DmnEditor.ExternalModel>
);

export const modelsByNamespace = Object.values(avaiableModels).reduce((acc, v) => {
  if (v.type === "dmn") {
    acc[v.model.definitions["@_namespace"]] = v;
  } else if (v.type === "pmml") {
    acc[buildXmlHref({ namespace: PMML_NAMESPACE, id: v.path })] = v;
  }
  return acc;
}, {} as DmnEditor.ExternalModelsIndex);
