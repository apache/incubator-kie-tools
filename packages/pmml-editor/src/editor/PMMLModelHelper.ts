/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  AnomalyDetectionModel,
  AssociationModel,
  BaselineModel,
  BayesianNetworkModel,
  Characteristics,
  ClusteringModel,
  DataDictionary,
  GaussianProcessModel,
  GeneralRegressionModel,
  MiningField,
  MiningModel,
  MiningSchema,
  Model,
  NaiveBayesModel,
  NearestNeighborModel,
  NeuralNetwork,
  Output,
  PMML,
  RegressionModel,
  RuleSetModel,
  Scorecard,
  SequenceModel,
  SupportVectorMachineModel,
  TextModel,
  TimeSeriesModel,
  TreeModel,
} from "@kie-tools/pmml-editor-marshaller";
import { Builder } from "./paths";
import { get } from "lodash";

const ICON_BASE: string = "images/";
const ICON_DEFAULT: string = "card-icon-default.svg";

export type ModelType =
  | "Anomaly Detection Model"
  | "card-icon-default.svg"
  | "Association Model"
  | "Bayesian Network Model"
  | "Baseline Model"
  | "Clustering Model"
  | "Gaussian Process Model"
  | "General Regression Model"
  | "Mining Model"
  | "Naive Bayes Model"
  | "Nearest Neighbor Model"
  | "Neural Network"
  | "Regression Model"
  | "RuleSet Model"
  | "Sequence Model"
  | "Scorecard"
  | "Support Vector Machine Model"
  | "Text Model"
  | "Time Series Model"
  | "Tree Model"
  | undefined;

export enum SupportedCapability {
  NONE,
  VIEWER,
  EDITOR,
}

export interface PMMLModelMapping<M> {
  model: M;
  type: ModelType;
  iconUrl: string;
  capability: SupportedCapability;
  factory: (() => M) | undefined;
}

export const PMMLModels: Array<PMMLModelMapping<any>> = new Array<PMMLModelMapping<any>>(
  {
    model: AnomalyDetectionModel,
    type: "Anomaly Detection Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: AssociationModel,
    type: "Association Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: BayesianNetworkModel,
    type: "Bayesian Network Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: BaselineModel,
    type: "Baseline Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: ClusteringModel,
    type: "Clustering Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: GaussianProcessModel,
    type: "Gaussian Process Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: GeneralRegressionModel,
    type: "General Regression Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: MiningModel,
    type: "Mining Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: NaiveBayesModel,
    type: "Naive Bayes Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: NearestNeighborModel,
    type: "Nearest Neighbor Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: NeuralNetwork,
    type: "Neural Network",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: RegressionModel,
    type: "Regression Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.VIEWER,
    factory: undefined,
  },
  {
    model: RuleSetModel,
    type: "RuleSet Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: SequenceModel,
    type: "Sequence Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: Scorecard,
    type: "Scorecard",
    iconUrl: "card-icon-scorecard.svg",
    capability: SupportedCapability.EDITOR,
    factory: () => {
      const model: Scorecard = new Scorecard({
        modelName: "Untitled model",
        MiningSchema: { MiningField: [] },
        Characteristics: { Characteristic: [] },
        Output: { OutputField: [] },
        functionName: "regression",
      });
      (model as any)._type = "Scorecard";
      return model;
    },
  },
  {
    model: SupportVectorMachineModel,
    type: "Support Vector Machine Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: TextModel,
    type: "Text Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: TimeSeriesModel,
    type: "Time Series Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  },
  {
    model: TreeModel,
    type: "Tree Model",
    iconUrl: "card-icon-default.svg",
    capability: SupportedCapability.NONE,
    factory: undefined,
  }
);

export const isCollection = <T>(collection: T[] | undefined): boolean => {
  if (collection === undefined) {
    return false;
  }
  return collection.length !== 0;
};

export const getModelName = (model: Model): string => {
  return get(model, "modelName") ?? "";
};

export const getModelType = (model: Model): ModelType | undefined => {
  for (const _mapping of PMMLModels) {
    if (model instanceof _mapping.model) {
      return _mapping.type;
    }
  }
};

export const getModelIconUrlByType = (type: ModelType): string => {
  for (const _mapping of PMMLModels) {
    if (type === _mapping.type) {
      return ICON_BASE + _mapping.iconUrl;
    }
  }
  return ICON_BASE + ICON_DEFAULT;
};

export const isSupportedModelType = (model: Model): boolean => {
  for (const _mapping of PMMLModels) {
    if (model instanceof _mapping.model) {
      return _mapping.capability === SupportedCapability.VIEWER || _mapping.capability === SupportedCapability.EDITOR;
    }
  }
  return false;
};

// TODO {kelvah} rough implementation for demoing purposes. to be done properly.
export const findIncrementalName = (name: string, existingNames: string[], startsFrom: number): string => {
  let newName = "";
  let counter = startsFrom;
  do {
    const potentialName = `${name}${counter !== 1 ? ` ${counter}` : ""}`;
    const found = existingNames.filter((existingName) => existingName === potentialName);
    if (found.length === 0) {
      newName = potentialName;
    }
    counter++;
  } while (newName.length === 0);
  return newName;
};

export const getDataDictionary = (pmml: PMML): DataDictionary | undefined => {
  return get(pmml, Builder().forDataDictionary().build().path);
};

export const getMiningSchema = (pmml: PMML, modelIndex: number): MiningSchema | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forMiningSchema().build().path);
};

export const getMiningField = (pmml: PMML, modelIndex: number, miningFieldIndex: number): MiningField | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).build().path);
};

export const getOutputs = (pmml: PMML, modelIndex: number): Output | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forOutput().build().path);
};

export const getCharacteristics = (pmml: PMML, modelIndex: number): Characteristics | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forCharacteristics().build().path);
};

export const getBaselineScore = (pmml: PMML, modelIndex: number): number | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forBaselineScore().build().path);
};

export const getUseReasonCodes = (pmml: PMML, modelIndex: number): boolean | undefined => {
  return get(pmml, Builder().forModel(modelIndex).forUseReasonCodes().build().path);
};
