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
import { useRef } from "react";

export function ColorPicker(props: {
  color: string;
  onChange: (newColor: string) => void;
  colorPickerRef?: React.MutableRefObject<HTMLInputElement>;
  icon?: React.ReactNode;
  colorDisplay?: React.ReactNode;
  name: string;
}) {
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-evenly",
          alignItems: "center",
        }}
        onClick={() => props.colorPickerRef?.current?.click()}
      >
        {props.icon}
        {props.colorDisplay ? (
          props.colorDisplay
        ) : (
          <div style={{ height: "4px", width: "18px", backgroundColor: props.color }} />
        )}

        <input
          ref={(ref) => {
            if (ref !== null) {
              (inputRef as React.MutableRefObject<HTMLInputElement>).current = ref;
              if (props.colorPickerRef) {
                props.colorPickerRef.current = ref;
              }
            }
          }}
          name={props.name}
          data-testid={`kie-tools--dmn-editor--color-picker-${props.name}`}
          aria-label={"Color picker"}
          type={"color"}
          disabled={false}
          value={props.color}
          style={{ opacity: 0, width: 0, height: 0 }}
          onChange={(e) => props.onChange(e.currentTarget.value)}
        />
      </div>
    </>
  );
}
