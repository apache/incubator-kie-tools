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
import { Label, Tooltip, TooltipPosition } from "@patternfly/react-core";

import "./CharacteristicLabel.scss";

export const CharacteristicLabel = (name: string, value: any, tooltip?: string) => {
  return (
    <>
      {!tooltip && (
        <Label color="orange" className="characteristic-list__item__label">
          <strong>{name}:</strong>
          &nbsp;
          <span>{value}</span>
        </Label>
      )}
      {tooltip && (
        <Tooltip
          position={TooltipPosition.top}
          isContentLeftAligned={true}
          maxWidth={"100em"}
          content={<pre>{tooltip}</pre>}
        >
          <Label tabIndex={0} color="orange" className="characteristic-list__item__label">
            <strong>{name}:</strong>
            &nbsp;
            <span>{value}</span>
          </Label>
        </Tooltip>
      )}
    </>
  );
};

export const CharacteristicLabelAttribute = (name: string, value: any, tooltip: string) => {
  return (
    <Tooltip
      position={TooltipPosition.top}
      isContentLeftAligned={true}
      maxWidth={"100em"}
      content={<pre>{tooltip}</pre>}
    >
      <Label tabIndex={0} color="blue" className="characteristic-list__item__label">
        <strong>{name}:</strong>
        &nbsp;
        <pre>{value}</pre>
      </Label>
    </Tooltip>
  );
};
