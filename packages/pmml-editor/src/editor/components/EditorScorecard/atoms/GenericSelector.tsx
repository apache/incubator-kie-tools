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
import { useState } from "react";
import {
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";

interface GenericSelectorProps {
  id: string;
  items: Array<string | GenericSelectorOption>;
  selection: string | undefined;
  onSelect: (_selection: string) => void;
  isDisabled?: boolean;
}

export const GenericSelector = (props: GenericSelectorProps) => {
  const [isOpen, setOpen] = useState(false);

  const onToggle = (_isOpen: boolean) => {
    setOpen(_isOpen);
  };
  const onSelect = (event: React.MouseEvent | React.ChangeEvent, value: string | SelectOptionObject) => {
    props.onSelect(value.toString());
    setOpen(!isOpen);
  };

  return (
    <Select
      id={props.id}
      className="generic-selector ignore-onclickoutside"
      variant={SelectVariant.single}
      aria-label="Select"
      onToggle={onToggle}
      onSelect={onSelect}
      selections={props.selection}
      isOpen={isOpen}
      menuAppendTo={() => document.body}
      isDisabled={props.isDisabled ?? false}
      ouiaId={props.id}
    >
      {props.items.map((item: string | GenericSelectorOption, index: number) => (
        <SelectOption
          key={index}
          value={typeof item === "string" ? item : item.value}
          isDisabled={typeof item === "string" ? false : item.isDisabled}
          data-ouia-component-type="select-option"
        />
      ))}
    </Select>
  );
};

export interface GenericSelectorOption {
  value: string;
  isDisabled?: boolean;
}
