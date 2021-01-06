/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import {
  CategoricalPredictor,
  DataDictionary,
  DataField,
  FieldName,
  Interval,
  MiningField,
  MiningSchema,
  NumericPredictor,
  PMML,
  RegressionModel,
  RegressionTable
} from "@kogito-tooling/pmml-editor-marshaller";
import { Line, LinearRegressionView, Range } from "./LinearRegressionView";
import { useSelector } from "react-redux";

interface LinearRegressionViewAdaptorProps {
  model: RegressionModel;
}

export const LinearRegressionViewAdaptor = (props: LinearRegressionViewAdaptorProps) => {
  const { model } = props;

  const dataDictionary = useSelector<PMML, DataDictionary>((state: PMML) => state.DataDictionary);

  const table: RegressionTable | undefined = getRegressionTable(model);
  if (table === undefined) {
    return <div>Unsupported</div>;
  }

  const numericPredictor: NumericPredictor | undefined = getNumericPredictorType(table);
  if (numericPredictor === undefined) {
    return <div>Unsupported</div>;
  }

  const modelName: string | undefined = model.modelName;
  const miningSchema: MiningSchema = model.MiningSchema;
  const dependentAxisTitle = miningSchema.MiningField.filter(mf => mf.usageType === "target")[0].name;
  const lines: Line[] = getLines(table, numericPredictor);

  //Get Ranges from DataDictionary or use reasonable defaults
  let rangeY: Range | undefined = getYRange(dataDictionary, model);
  if (rangeY === undefined) {
    rangeY = getDefaultYRange(lines);
  }
  let rangeX: Range | undefined = getXRange(dataDictionary, numericPredictor);
  if (rangeX === undefined) {
    rangeX = getDefaultXRange(lines, rangeY);
  }

  return (
    <div>
      <LinearRegressionView
        modelName={modelName ?? "<Undefined>"}
        independentAxisTitle={numericPredictor.name as string}
        dependentAxisTitle={dependentAxisTitle as string}
        lines={lines}
        rangeX={rangeX}
        rangeY={rangeY}
      />
    </div>
  );
};

const getRegressionTable = (model: RegressionModel): RegressionTable | undefined => {
  const tables: RegressionTable[] = model.RegressionTable;
  if (tables === undefined || tables.length > 1) {
    return undefined;
  }
  return tables[0];
};

const getNumericPredictorType = (table: RegressionTable): NumericPredictor | undefined => {
  const predicates: NumericPredictor[] | undefined = table.NumericPredictor;
  if (predicates === undefined || predicates.length > 1) {
    return undefined;
  }
  return predicates[0];
};

const getLines = (table: RegressionTable, numericPredictor: NumericPredictor): Line[] => {
  const c: number = table.intercept;
  const line: Line = { m: numericPredictor.coefficient, c: c, title: "base" };
  const lines: Line[] = new Array<Line>(line);

  //We need to duplicate the line for each CategoricalPredictor
  const categoricalPredictors: CategoricalPredictor[] | undefined = table.CategoricalPredictor;
  if (categoricalPredictors === undefined) {
    return lines;
  }

  categoricalPredictors.forEach(cp => {
    lines.push({ m: line.m, c: line.c + cp.coefficient, title: `${line.title} (${cp.value})` });
  });

  return lines;
};

const getYRange = (dataDictionary: DataDictionary, model: RegressionModel): Range | undefined => {
  const targetField: MiningField | undefined = getTargetMiningField(model);
  if (targetField === undefined) {
    return undefined;
  }
  const targetFieldIntervals: Interval[] | undefined = getMiningFieldIntervals(dataDictionary, targetField.name);
  return getIntervalsMaximumRange(targetFieldIntervals);
};

const getTargetMiningField = (model: RegressionModel): MiningField | undefined => {
  const targetFields: MiningField[] = model.MiningSchema.MiningField.filter(mf => mf.usageType === "target");
  if (targetFields === undefined || targetFields.length !== 1) {
    return undefined;
  }
  return targetFields[0];
};

const getXRange = (dataDictionary: DataDictionary, numericPredictor: NumericPredictor): Range | undefined => {
  const predictorFieldIntervals: Interval[] | undefined = getMiningFieldIntervals(
    dataDictionary,
    numericPredictor.name
  );
  return getIntervalsMaximumRange(predictorFieldIntervals);
};

const getMiningFieldIntervals = (dataDictionary: DataDictionary, fieldName: FieldName): Interval[] => {
  const dataFields: DataField[] = dataDictionary.DataField.filter(
    df => df.name === fieldName && df.optype === "continuous"
  );
  if (dataFields === undefined || dataFields.length !== 1) {
    return [];
  }
  const intervals: Interval[] | undefined = dataFields[0].Interval;
  if (intervals === undefined) {
    return [];
  }

  return intervals;
};

const getIntervalsMaximumRange = (intervals: Interval[]): Range | undefined => {
  if (intervals.length === 0) {
    return undefined;
  }
  const min: number = intervals.map(interval => interval.leftMargin ?? 0).reduce((pv, cv) => Math.min(pv, cv));
  const max: number = intervals.map(interval => interval.rightMargin ?? 0).reduce((pv, cv) => Math.max(pv, cv));
  return new Range(min, max);
};

const getDefaultYRange = (lines: Line[]): Range => {
  const maxIntersect: number = Math.max(...lines.map(line => line.c));
  const defaultMaxY: number = maxIntersect * 2;
  const defaultMinY: number = -defaultMaxY;
  return new Range(defaultMinY, defaultMaxY);
};

const getDefaultXRange = (lines: Line[], rangeY: Range): Range => {
  const minGradient: number = Math.min(...lines.map(line => line.m));
  const maxIntersect: number = Math.max(...lines.map(line => line.c));
  const defaultMaxX: number = (rangeY.max - maxIntersect) / minGradient;
  const defaultMinX: number = -defaultMaxX;
  return new Range(defaultMinX, defaultMaxX);
};
