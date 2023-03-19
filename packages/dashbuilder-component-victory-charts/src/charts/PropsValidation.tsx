import { DataSet } from "@kie-tools/dashbuilder-component-api";
import { ChartType } from "./BaseChart";

export interface ValidationResult {
  isValid: boolean;
  message?: string;
}

const notEnoughColumns = (chartType: string, n: number, required: number): ValidationResult => {
  return {
    isValid: false,
    message: `Not enough columns to build chart of type ${chartType}. The dataset has ${n}, but the chart requires at least ${required}`,
  };
};

export function validateDataSetForChart(chartType: ChartType, dataSet: DataSet): ValidationResult {
  const columns = dataSet.columns;

  if (columns.length < 2 && chartType != "utilization-donut") {
    return notEnoughColumns(chartType, columns.length, 2);
  }

  return { isValid: true };
}
