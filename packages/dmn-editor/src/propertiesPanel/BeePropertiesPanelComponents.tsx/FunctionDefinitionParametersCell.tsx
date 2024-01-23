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
import { NameField, TypeRefField } from "./Fields";
import { UniqueNameIndex } from "../../Dmn15Spec";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";

/**
 * This component implements a form to change an object with the DMN15__tInformationItem type
 * It's used for: ContextExpressionVariableCell, InvocationExpressionParametersCell and RelationExpressionHeaderCell
 */
export function FunctionDefinitionParameterCell(props: {
  formalParameter?: Array<{
    id: string;
    name: string;
    typeRef: string;
    label: string;
    description: string;
    isReadonly: boolean;
    allUniqueNames: UniqueNameIndex;
    dmnEditorRootElementRef: React.RefObject<HTMLElement>;
    expressionPath: ExpressionPath[];
    onChangeName: (newName: string) => void;
    onChangeTypeRef: (newTypeRef: string) => void;
    onChangeLabel: (newLabel: string) => void;
    onChangeDescription: (newDescription: string) => void;
  }>;
}) {
  return (
    <>
      {/* {props.formalParameter?.map((parameter, i) => (
        <FormGroup label={`Parameter ${parameter.name}`} key={i}>
          <NameField
            isReadonly={parameter.isReadonly}
            id={parameter.id}
            name={parameter.name}
            allUniqueNames={parameter.allUniqueNames}
            onChange={parameter.onChangeName}
          />
          <TypeRefField
            isReadonly={parameter.isReadonly}
            dmnEditorRootElementRef={parameter.dmnEditorRootElementRef}
            typeRef={parameter.typeRef}
            onChange={parameter.onChangeTypeRef}
          />
          <LabelField isReadonly={parameter.isReadonly} label={parameter.label} onChange={parameter.onChangeLabel} />
          <DescriptionField
            isReadonly={parameter.isReadonly}
            initialValue={parameter.description}
            expressionPath={parameter.expressionPath}
            onChange={parameter.onChangeDescription}
          />
        </FormGroup>
      ))} */}
    </>
  );
}
