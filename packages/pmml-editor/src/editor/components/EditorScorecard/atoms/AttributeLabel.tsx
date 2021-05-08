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
import { Label } from "@patternfly/react-core/dist/js/components/Label";

import "./AttributeLabel.scss";

interface AttributeLabelProps {
  name: string;
  value: any;
  onClose?: () => void;
}

export const AttributeLabel = (props: AttributeLabelProps) => {
  const { name, value, onClose } = props;

  return (
    <>
      {!onClose && (
        <Label color="cyan" className="attribute-list__item__label">
          <strong>{name}:</strong>
          &nbsp;
          <span>{value}</span>
        </Label>
      )}
      {onClose && (
        <Label
          color="cyan"
          className="attribute-list__item__label"
          onClose={(e) => {
            e.nativeEvent.stopImmediatePropagation();
            e.stopPropagation();
            onClose();
          }}
        >
          <strong>{name}:</strong>
          &nbsp;
          <span>{value}</span>
        </Label>
      )}
    </>
  );
};
