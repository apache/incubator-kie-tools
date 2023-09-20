import { DmnModel, getMarshaller } from "@kie-tools/dmn-marshaller";
import { sumBkm, sumDiffDs } from "./OtherDmns";
import * as DmnEditor from "../../src/DmnEditor";

export const sumBkmModel = getMarshaller(sumBkm).parser.parse();
export const sumDiffDsModel = getMarshaller(sumDiffDs).parser.parse();

export const avaiableModelsByPath: Record<string, DmnModel> = {
  "dev-webapp/available-models-to-include/sumBkm.dmn": sumBkmModel,
  "dev-webapp/available-models-to-include/sumDiffDs.dmn": sumDiffDsModel,
};

export const modelsByNamespace = Object.entries(avaiableModelsByPath).reduce((acc, [k, v]) => {
  acc[v.definitions["@_namespace"]] = { model: v, svg: "", path: k };
  return acc;
}, {} as DmnEditor.DependenciesByNamespace);
