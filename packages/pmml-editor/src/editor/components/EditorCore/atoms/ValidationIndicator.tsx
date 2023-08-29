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
import { ReactElement, useMemo } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import "./ModelTitle.scss";
import { ValidationEntry, ValidationLevel } from "../../../validation";

interface ValidationIndicatorProps {
  validations: ValidationEntry[];
}

export const ValidationIndicator = (props: ValidationIndicatorProps) => {
  const { validations } = props;

  const maxLevel = useMemo(() => getMaxLevel(validations), [validations]);

  return (
    <>
      {maxLevel !== undefined && (
        <ValidationIndicatorTooltip validations={validations}>
          <>
            {maxLevel === ValidationLevel.ERROR && <ExclamationCircleIcon size={"sm"} color={"red"} />}
            {maxLevel === ValidationLevel.WARNING && <ExclamationTriangleIcon size={"sm"} color={"orange"} />}
          </>
        </ValidationIndicatorTooltip>
      )}
    </>
  );
};

interface ValidationIndicatorTooltipProps extends ValidationIndicatorProps {
  children: ReactElement<any>;
  customTooltipContent?: string;
}

export const ValidationIndicatorTooltip = (props: ValidationIndicatorTooltipProps) => {
  const { validations, children, customTooltipContent } = props;

  return (
    <>
      {validations.length > 0 && (
        <Tooltip
          maxWidth={"100%"}
          isContentLeftAligned={true}
          content={customTooltipContent ? customTooltipContent : list(validations)}
        >
          {children}
        </Tooltip>
      )}
    </>
  );
};

interface ValidationIndicatorLabelProps extends ValidationIndicatorProps {
  children: React.ReactNode;
  customTooltipContent?: string;
  onClose?: (event: React.MouseEvent) => void;
  cssClass?: string;
}

export const ValidationIndicatorLabel = (props: ValidationIndicatorLabelProps) => {
  const { validations, children, customTooltipContent, onClose, cssClass } = props;

  const maxLevel = useMemo(() => getMaxLevel(validations), [validations]);
  const labelColor = useMemo(() => {
    switch (maxLevel) {
      case ValidationLevel.ERROR:
        return "red";
      case ValidationLevel.WARNING:
        return "orange";
      default:
        return "orange";
    }
  }, [maxLevel]);

  const labelIcon = useMemo(() => {
    switch (maxLevel) {
      case ValidationLevel.ERROR:
        return <ExclamationCircleIcon size={"sm"} color={"red"} />;
      case ValidationLevel.WARNING:
        return <ExclamationTriangleIcon size={"sm"} color={"orange"} />;
      default:
        return undefined;
    }
  }, [maxLevel]);

  return (
    <>
      {maxLevel !== undefined && (
        <ValidationIndicatorTooltip validations={validations} customTooltipContent={customTooltipContent}>
          <Label
            onClose={onClose}
            className={cssClass}
            color={labelColor}
            icon={labelIcon}
            data-ouia-component-type="invalid-label"
          >
            {children}
          </Label>
        </ValidationIndicatorTooltip>
      )}
    </>
  );
};

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
