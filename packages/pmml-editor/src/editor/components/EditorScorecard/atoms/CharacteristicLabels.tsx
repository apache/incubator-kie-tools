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
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicLabel } from "./CharacteristicLabel";

interface CharacteristicLabelsProps {
  activeCharacteristic: Characteristic;
}

export const CharacteristicLabels = (props: CharacteristicLabelsProps) => {
  const { activeCharacteristic } = props;

  return (
    <>
      {activeCharacteristic.reasonCode && CharacteristicLabel("Reason code", activeCharacteristic.reasonCode)}
      {activeCharacteristic.baselineScore && CharacteristicLabel("Baseline score", activeCharacteristic.baselineScore)}
      {activeCharacteristic.Attribute.length > 0 &&
        CharacteristicLabel("Attribute count", activeCharacteristic.Attribute.length)}
    </>
  );
};
