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
import {BayesianNetworkModel, Scorecard} from "../marshaller/model/pmml4_4";

export class PMMLEditorMarshallerService {

    public getPMMLDocumentData(xmlContent: string) : PMMLDocumentData {
        const pmml = XML2PMML(xmlContent);

        const models : PMMLModelData[] = [];
        const document = new PMMLDocumentData(models);

        if (pmml.models) {
            pmml.models.forEach(model => {
                if (model instanceof Scorecard || model instanceof BayesianNetworkModel) {
                    const modelName = model.modelName!;
                    const fields = model.MiningSchema.MiningField.map(field => field.name.toString());
                    models.push(new PMMLModelData(modelName, fields));
                    return;
                }
            });
        }
        return document;
    }
}