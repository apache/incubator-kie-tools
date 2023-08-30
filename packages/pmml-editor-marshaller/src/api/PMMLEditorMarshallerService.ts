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

import { XML2PMML } from "../marshaller";
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
  TreeModel,
} from "../marshaller/model/pmml4_4";
import { PMMLDocumentData } from "./PMMLDocumentData";
import { PMMLModelData } from "./PMMLModelData";
import { PMMLFieldData } from "./PMMLFieldData";

export class PMMLEditorMarshallerService {
  public getPMMLDocumentData(xmlContent: string): PMMLDocumentData {
    const pmml = XML2PMML(xmlContent);
    const models: PMMLModelData[] = [];
    const document = new PMMLDocumentData(models);

    if (pmml.models) {
      pmml.models.forEach((model) => {
        const modelData = this.retrieveModelData(model);
        if (modelData) {
          models.push(modelData);
        }
      });
    }
    return document;
  }

  public retrieveModelData(model: Model): PMMLModelData | undefined {
    const modelsTypes = [
      AnomalyDetectionModel,
      AssociationModel,
      BayesianNetworkModel,
      BaselineModel,
      ClusteringModel,
      GaussianProcessModel,
      GeneralRegressionModel,
      MiningModel,
      NaiveBayesModel,
      NearestNeighborModel,
      NeuralNetwork,
      RegressionModel,
      RuleSetModel,
      SequenceModel,
      Scorecard,
      SupportVectorMachineModel,
      TextModel,
      TimeSeriesModel,
      TreeModel,
    ];
    let modelData;

    for (const type of modelsTypes) {
      if (model instanceof type) {
        const modelFields = model.MiningSchema.MiningField.map(
          (field) => new PMMLFieldData(field.name.toString(), field.usageType)
        );
        modelData = new PMMLModelData(model.modelName == null ? "" : model.modelName, modelFields);
      }
    }

    return modelData;
  }
}
