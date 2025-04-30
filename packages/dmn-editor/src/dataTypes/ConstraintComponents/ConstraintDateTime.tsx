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

import React, { useState } from "react";
import { useCallback, useMemo } from "react";
import "./ConstraintDateTime.css";
import { ConstraintProps } from "./Constraint";
import { ConstraintTime } from "./ConstraintTime";
import { ConstraintDate } from "./ConstraintDate";

export function ConstraintDateTime({ value, onChange, isValid, ...props }: ConstraintProps) {
  const date = useMemo(() => value.split("T")?.[0] ?? "", [value]);
  const time = useMemo(() => value.split("T")?.[1] ?? "", [value]);
  const [internalTime, setInternalTime] = useState<string>(time);

  const onInternalChange = useCallback(
    (args: { date?: string; time?: string }) => {
      const newDate = args.date ?? date;
      const newTime = args.time ?? internalTime ?? time;
      if (newDate !== "" && newTime === "") {
        onChange(`${newDate}`);
      }
      if (newDate !== "" && newTime !== "") {
        onChange(`${newDate}T${newTime}`);
      }
    },
    [date, onChange, time, internalTime]
  );

  const onChangeDate = useCallback(
    (value: string) => {
      onInternalChange({ date: value });
    },
    [onInternalChange]
  );

  const onChangeTime = useCallback(
    (value: string) => {
      onInternalChange({ time: value });
      setInternalTime(value);
    },
    [onInternalChange]
  );

  return (
    <>
      <div className={"kie-dmn-editor--constraint-date-time"}>
        <ConstraintDate {...props} value={date} onChange={onChangeDate} isValid={isValid} />
        <ConstraintTime {...props} value={time} onChange={onChangeTime} isValid={isValid} />
      </div>
    </>
  );
}
