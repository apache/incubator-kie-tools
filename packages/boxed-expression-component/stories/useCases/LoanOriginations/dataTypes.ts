import { dataTypes } from "../../boxedExpressionWrapper";

export const loanOriginationsDataTypes = [
  ...dataTypes,
  { typeRef: "tAdjudication", name: "tAdjudication", isCustom: true },
  { typeRef: "tApplicantData", name: "tApplicantData", isCustom: true },
  { typeRef: "tBureauCallType", name: "tBureauCallType", isCustom: true },
  { typeRef: "tBureauData", name: "tBureauData", isCustom: true },
  { typeRef: "tBureauRiskCategory", name: "tBureauRiskCategory", isCustom: true },
  { typeRef: "tEligibility", name: "tEligibility", isCustom: true },
  { typeRef: "tEmploymentStatus", name: "tEmploymentStatus", isCustom: true },
  { typeRef: "tMaritialStatus", name: "tMaritialStatus", isCustom: true },
  { typeRef: "tProductType", name: "tProductType", isCustom: true },
  { typeRef: "tRequestedProduc", name: "tRequestedProduc", isCustom: true },
  { typeRef: "tRouting", name: "tRouting", isCustom: true },
  { typeRef: "tStrategy", name: "tStrategy", isCustom: true },
];
