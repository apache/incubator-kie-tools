import { DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api/DmnBuiltInDataType";

export const builtInDataTypes: DmnDataType[] = Object.values(DmnBuiltInDataType).map((feelType) => ({
  isCustom: false,
  typeRef: feelType,
  name: feelType,
}));
