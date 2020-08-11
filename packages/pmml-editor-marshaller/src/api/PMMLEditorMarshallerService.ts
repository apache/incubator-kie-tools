/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { PMMLDocumentData, PMMLModelData } from "@kogito-tooling/microeditor-envelope-protocol";
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
} from "../marshaller/model/pmml4_4";

export class PMMLEditorMarshallerService {

    public getPMMLDocumentData(xmlContent: string) : PMMLDocumentData {
        const pmml = XML2PMML(xmlContent);
        const models : PMMLModelData[] = [];
        const document = new PMMLDocumentData(models);

        if (pmml.models) {
            pmml.models.forEach(model => {
                if (model instanceof AnomalyDetectionModel || model instanceof AssociationModel
                    || model instanceof BayesianNetworkModel || model instanceof BaselineModel
                    || model instanceof ClusteringModel || model instanceof GaussianProcessModel
                    || model instanceof GeneralRegressionModel || model instanceof MiningModel
                    || model instanceof NaiveBayesModel || model instanceof NearestNeighborModel
                    || model instanceof NeuralNetwork || model instanceof RegressionModel
                    || model instanceof RuleSetModel || model instanceof SequenceModel
                    || model instanceof Scorecard || model instanceof SupportVectorMachineModel
                    || model instanceof TextModel || model instanceof TimeSeriesModel || model instanceof TreeModel) {
                    const modelName = model.modelName;
                    if (modelName == null) {
                        return;
                    }
                    const fields = model.MiningSchema.MiningField.map(field => field.name.toString());
                    models.push(new PMMLModelData(modelName, fields));
                    return;
                }
            });
        }
        return document;
    }
}