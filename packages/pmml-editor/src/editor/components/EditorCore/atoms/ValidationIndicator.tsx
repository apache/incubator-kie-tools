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
import { ReactElement, useMemo } from "react";
import { Tooltip } from "@patternfly/react-core";
import { ExclamationCircleIcon, WarningTriangleIcon } from "@patternfly/react-icons";
import "./ModelTitle.scss";
import { ValidationEntry, ValidationLevel } from "../../../validation";

interface ValidationIndicatorProps {
  validations: ValidationEntry[];
}

interface ValidationIndicatorTooltipProps extends ValidationIndicatorProps {
  children: ReactElement<any>;
}

const getMaxLevel = (validations: ValidationEntry[]): ValidationLevel | undefined => {
  if (validations.length === 0) {
    return undefined;
  }
  return validations.reduce((pv, cv) => {
    if (pv.level < cv.level) {
      return cv;
    }
    if (pv.level > cv.level) {
      return pv;
    }
    return cv;
  }).level;
};

const list = (validations: ValidationEntry[]) => {
  return (
    <ol>
      {validations.map((validation, index) => (
        <li key={index}>{validation.message}</li>
      ))}
    </ol>
  );
};

export const ValidationIndicator = (props: ValidationIndicatorProps) => {
  const { validations } = props;

  const maxLevel = useMemo(() => getMaxLevel(validations), [validations]);

  return (
    <>
      {maxLevel !== undefined && (
        <ValidationIndicatorTooltip validations={validations}>
          <>
            {maxLevel === ValidationLevel.ERROR && <ExclamationCircleIcon size={"sm"} color={"red"} />}
            {maxLevel === ValidationLevel.WARNING && <WarningTriangleIcon size={"sm"} color={"orange"} />}
          </>
        </ValidationIndicatorTooltip>
      )}
    </>
  );
};

export const ValidationIndicatorTooltip = (props: ValidationIndicatorTooltipProps) => {
  const { validations, children } = props;

  return (
    <>
      {validations.length > 0 && (
        <Tooltip maxWidth={"100%"} isContentLeftAligned={true} content={list(validations)}>
          {children}
        </Tooltip>
      )}
    </>
  );
};
