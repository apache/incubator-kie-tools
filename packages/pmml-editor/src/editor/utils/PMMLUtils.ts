/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import {
  AnomalyDetectionModel,
  AssociationModel,
  BaselineModel,
  BayesianNetworkModel,
  ClusteringModel,
  GaussianProcessModel,
  GeneralRegressionModel,
  MiningModel,
  Model,
  NaiveBayesModel,
  NearestNeighborModel,
  NeuralNetwork,
  RegressionModel,
  RuleSetModel,
  Scorecard,
  SequenceModel,
  SupportVectorMachineModel,
  TextModel,
  TimeSeriesModel,
  TreeModel
} from "@kogito-tooling/pmml-editor-marshaller";
import get = Reflect.get;

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
  | "<Unknown>";

interface PMMLModelMapping {
  model: any;
  type: ModelType;
  iconUrl: string;
  isSupported: boolean;
}

export const PMMLModels: PMMLModelMapping[] = [
  {
    model: AnomalyDetectionModel,
    type: "Anomaly Detection Model",
    iconUrl: "card-icon-default.svg",
    isSupported: false
  },
  { model: AssociationModel, type: "Association Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: BayesianNetworkModel, type: "Bayesian Network Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: BaselineModel, type: "Baseline Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: ClusteringModel, type: "Clustering Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: GaussianProcessModel, type: "Gaussian Process Model", iconUrl: "card-icon-default.svg", isSupported: false },
  {
    model: GeneralRegressionModel,
    type: "General Regression Model",
    iconUrl: "card-icon-default.svg",
    isSupported: false
  },
  { model: MiningModel, type: "Mining Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: NaiveBayesModel, type: "Naive Bayes Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: NearestNeighborModel, type: "Nearest Neighbor Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: NeuralNetwork, type: "Neural Network", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: RegressionModel, type: "Regression Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: RuleSetModel, type: "RuleSet Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: SequenceModel, type: "Sequence Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: Scorecard, type: "Scorecard", iconUrl: "card-icon-scorecard.svg", isSupported: true },
  {
    model: SupportVectorMachineModel,
    type: "Support Vector Machine Model",
    iconUrl: "card-icon-default.svg",
    isSupported: false
  },
  { model: TextModel, type: "Text Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: TimeSeriesModel, type: "Time Series Model", iconUrl: "card-icon-default.svg", isSupported: false },
  { model: TreeModel, type: "Tree Model", iconUrl: "card-icon-default.svg", isSupported: false }
];

export const isCollection = <T>(collection: T[] | undefined): boolean => {
  if (collection === undefined) {
    return false;
  }
  return collection.length !== 0;
};

export const getModelName = (model: Model): string => {
  return get(model, "modelName") ?? "<Undefined>";
};

export const getModelType = (model: Model): ModelType => {
  for (const _mapping of PMMLModels) {
    if (model instanceof _mapping.model) {
      return _mapping.type;
    }
  }
  return "<Unknown>";
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
      return _mapping.isSupported;
    }
  }
  return false;
};
