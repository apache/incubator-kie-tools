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
import * as React from "react";
import { Attribute } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicLabel } from "./CharacteristicLabel";

interface AttributeLabelsProps {
  activeAttribute: Attribute;
}

export const AttributeLabels = (props: AttributeLabelsProps) => {
  const { activeAttribute } = props;

  return (
    <>
      {activeAttribute.reasonCode !== undefined && CharacteristicLabel("Reason code", activeAttribute.reasonCode)}
      {activeAttribute.partialScore !== undefined && CharacteristicLabel("Partial score", activeAttribute.partialScore)}
    </>
  );
};
