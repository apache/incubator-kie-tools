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

import { BaselineModel, MiningSchema, TestDistributions } from "../../model/pmml4_4";
import { LOCAL_TRANSFORMATIONS } from "./LocalTransformations";
import { MINING_SCHEMA } from "./MiningSchema";
import { MODEL_EXPLANATION } from "./ModelExplanation";
import { MODEL_STATS } from "./ModelStats";
import { MODEL_VERIFICATION } from "./ModelVerification";
import { OUTPUT } from "./Output";

/**
 * Basic unmarshalling to support https://issues.redhat.com/browse/FAI-235
 */
export const BASELINE_MODEL: string = `
elements.elements[(name = "BaselineModel")] ~> $map(function($v, $i) {
  $merge([
    $baselineModelFactory(),
    $v.attributes,
    {
      "_type": $v.name
    },
    {
      ${MINING_SCHEMA}, 
      ${OUTPUT},
      ${MODEL_STATS},
      ${MODEL_EXPLANATION},
      ${MODEL_VERIFICATION},
      ${LOCAL_TRANSFORMATIONS}
    }
  ])
})`;

//Construction of a BaselineModel data-structure can be peformed in the JSONata mapping however
//TypeScript's instanceof operator relies on the applicable constructor function having been
//called and therefore we must instantiate the object itself.
export function baselineModelFactory(): BaselineModel {
  return new BaselineModel({
    MiningSchema: new MiningSchema({ MiningField: [] }),
    TestDistributions: new TestDistributions({ Baseline: {}, field: "", testStatistic: "CUSUM" }),
    functionName: "regression",
  });
}
