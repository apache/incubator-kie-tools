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
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import "./CharacteristicLabel.scss";
import { ValidationEntry } from "../../../validation";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import { toText } from "../organisms";
import { DataField, Predicate } from "@kie-tools/pmml-editor-marshaller";

interface CharacteristicLabelProps {
  name: string;
  value: string;
}

export const CharacteristicLabel = (props: CharacteristicLabelProps) => {
  const { name, value } = props;

  return (
    <Label color="cyan" className="characteristic-list__item__label">
      <strong>{name}:</strong>
      &nbsp;
      <span>{value}</span>
    </Label>
  );
};

export const CharacteristicPredicateLabel = (
  predicate: Predicate | undefined,
  dataFields: DataField[],
  validations: ValidationEntry[]
) => {
  const value = toText(predicate, dataFields);
  const truncatedText = value.length > 32 ? value.slice(0, 29) + "..." : value;

  return (
    <>
      {value.length > truncatedText.length && (
        <Tooltip
          position={TooltipPosition.top}
          isContentLeftAligned={true}
          maxWidth={"100em"}
          content={<pre>{value}</pre>}
        >
          <>
            {validations.length > 0 && (
              <ValidationIndicatorLabel validations={validations} cssClass="characteristic-list__item__label">
                <pre>{truncatedText}</pre>
              </ValidationIndicatorLabel>
            )}
            {validations.length === 0 && (
              <Label tabIndex={0} color="blue" className="characteristic-list__item__label">
                <pre>{truncatedText}</pre>
              </Label>
            )}
          </>
        </Tooltip>
      )}
      {value.length === truncatedText.length && (
        <>
          {validations.length > 0 && (
            <span className="characteristic-list__item__label">
              <ValidationIndicatorLabel validations={validations}>
                <>
                  {predicate && <pre>{value}</pre>}
                  {!predicate && (
                    <>
                      <strong>Predicate:</strong>&nbsp;
                      <em>Missing</em>
                    </>
                  )}
                </>
              </ValidationIndicatorLabel>
            </span>
          )}
          {validations.length === 0 && (
            <Label tabIndex={0} color="blue" className="characteristic-list__item__label">
              <pre>{value}</pre>
            </Label>
          )}
        </>
      )}
    </>
  );
};
