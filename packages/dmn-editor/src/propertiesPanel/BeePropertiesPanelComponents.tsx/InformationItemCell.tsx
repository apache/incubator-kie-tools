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
import { DescriptionField, LabelField, NameField, TypeRefField } from "./Fields";
import { UniqueNameIndex } from "../../Dmn15Spec";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";

/**
 * This component implements a form to change an object with the DMN15__tInformationItem type
 * It's used for: ContextExpressionVariableCell, InvocationExpressionParametersCell and RelationExpressionHeaderCell
 */
export function InformationItemCell(props: {
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
}) {
  return (
    <>
      <NameField
        isReadonly={props.isReadonly}
        id={props.id}
        name={props.name}
        allUniqueNames={props.allUniqueNames}
        onChange={props.onChangeName}
      />
      <TypeRefField
        isReadonly={props.isReadonly}
        dmnEditorRootElementRef={props.dmnEditorRootElementRef}
        typeRef={props.typeRef}
        onChange={props.onChangeTypeRef}
      />
      <LabelField isReadonly={props.isReadonly} label={props.label} onChange={props.onChangeLabel} />
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={props.description}
        expressionPath={props.expressionPath}
        onChange={props.onChangeDescription}
      />
    </>
  );
}
