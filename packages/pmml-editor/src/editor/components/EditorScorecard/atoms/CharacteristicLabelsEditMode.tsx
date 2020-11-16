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
import { CSSProperties } from "react";
import { Label } from "@patternfly/react-core";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";

interface CharacteristicLabelsEditModeProps {
  viewAttributes: () => void;
}

const PADDING: CSSProperties = { marginRight: "4px" };

export const CharacteristicLabelsEditMode = (props: CharacteristicLabelsEditModeProps) => {
  const { viewAttributes } = props;

  return (
    <>
      <Label
        style={PADDING}
        variant="outline"
        color="orange"
        href="#outline"
        icon={<ArrowAltCircleRightIcon />}
        onClick={e => {
          e.preventDefault();
          viewAttributes();
        }}
      >
        Edit attributes...
      </Label>
    </>
  );
};
