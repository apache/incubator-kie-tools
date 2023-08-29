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
import { Label } from "@patternfly/react-core/dist/js/components/Label";

import "./OutputFieldLabel.scss";

export const OutputFieldLabel = (name: string, value: any, onClose?: () => void) => {
  return (
    <>
      {!onClose && (
        <Label color="cyan" className="output-fields-list__item__label">
          <strong>{name}:</strong>
          &nbsp;
          <span>{value}</span>
        </Label>
      )}
      {onClose && (
        <Label
          color="cyan"
          className="output-fields-list__item__label"
          onClose={(e) => {
            e.nativeEvent.stopImmediatePropagation();
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
