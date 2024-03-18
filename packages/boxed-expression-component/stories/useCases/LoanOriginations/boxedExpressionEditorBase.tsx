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

import * as React from "react";
import { ExpressionDefinition } from "../../../src/api";
import { BoxedExpressionComponentWrapper } from "../../boxedExpressionComponentWrapper";
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
  { typeRef: "t.MaritalStatus", name: "t.MaritalStatus", isCustom: true },
  { typeRef: "t.ProductType", name: "t.ProductType", isCustom: true },
  { typeRef: "t.RequestedProduc", name: "t.RequestedProduc", isCustom: true },
  { typeRef: "t.Routing", name: "t.Routing", isCustom: true },
  { typeRef: "t.Strategy", name: "t.Strategy", isCustom: true },
];

export function BoxedExpressionEditorBase(props: { expression: ExpressionDefinition }) {
  const emptyRef = React.useRef<HTMLDivElement>(null);

  return (
    <div ref={emptyRef}>
      <BoxedExpressionComponentWrapper
        expressionDefinition={props.expressionDefinition}
        dataTypes={loanOriginationsDataTypes}
        isResetSupportedOnRootExpression={false}
      />
    </div>
  );
}
