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
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintDate({ value, onChange, isValid }: ConstraintProps) {
  return (
    <>
      <DatePicker
        className={`kie-dmn-editor--constraint-date kie-dmn-editor--constraint-input ${
          isValid ? "" : "kie-dmn-editor--constraint-date-invalid"
        }`}
        inputProps={{ className: "kie-dmn-editor--constraint-input" }}
        value={value}
        onChange={(e, value) => onChange(value)}
      />
    </>
  );
}
