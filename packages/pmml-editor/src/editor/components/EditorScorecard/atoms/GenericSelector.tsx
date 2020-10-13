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
import { useState } from "react";
import { Select, SelectOption, SelectOptionObject, SelectVariant } from "@patternfly/react-core";

interface GenericSelectorProps {
  id: string;
  items: string[];
  selection: string;
  onSelect: (_selection: string) => void;
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
      className="generic-selector"
      variant={SelectVariant.single}
      aria-label="Select"
      onToggle={onToggle}
      onSelect={onSelect}
      selections={props.selection}
      isOpen={isOpen}
    >
      {props.items.map((item, index) => (
        <SelectOption key={index} value={item} />
      ))}
    </Select>
  );
};
