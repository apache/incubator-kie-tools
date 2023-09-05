import { dataTypes } from "../../boxedExpressionStoriesWrapper";

export const loanOriginationsDataTypes = [
  ...dataTypes,
  { typeRef: "t.Adjudication", name: "t.Adjudication", isCustom: true },
  { typeRef: "t.ApplicantData", name: "t.ApplicantData", isCustom: true },
  { typeRef: "t.BureauCallType", name: "t.BureauCallType", isCustom: true },
  { typeRef: "t.BureauData", name: "t.BureauData", isCustom: true },
  { typeRef: "t.BureauRiskCategory", name: "t.BureauRiskCategory", isCustom: true },
  { typeRef: "t.Eligibility", name: "t.Eligibility", isCustom: true },
  { typeRef: "t.EmploymentStatus", name: "t.EmploymentStatus", isCustom: true },
  { typeRef: "t.MaritialStatus", name: "t.MaritialStatus", isCustom: true },
  { typeRef: "t.ProductType", name: "t.ProductType", isCustom: true },
  { typeRef: "t.RequestedProduc", name: "t.RequestedProduc", isCustom: true },
  { typeRef: "t.Routing", name: "t.Routing", isCustom: true },
  { typeRef: "t.Strategy", name: "t.Strategy", isCustom: true },
];
