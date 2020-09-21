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
import { useRef } from "react";
import {
  Button,
  ButtonVariant,
  InputGroup,
  Switch,
  TextInput,
  Toolbar,
  ToolbarContent,
  ToolbarItem
} from "@patternfly/react-core";
import { SearchIcon } from "@patternfly/react-icons";

interface LandingPageToolbarProps {
  setFilter: (filter: string) => void;
  showUnsupportedModels: boolean;
  setShowUnsupportedModels: (showUnsupportedModels: boolean) => void;
}

export const LandingPageToolbar = (props: LandingPageToolbarProps) => {
  const { setFilter, showUnsupportedModels, setShowUnsupportedModels } = props;

  const filterField = useRef<HTMLInputElement>(null);
  const onFilterSubmit = (): void => {
    if (filterField && filterField.current) {
      setFilter(filterField.current.value);
    }
  };
  const onFilterEnter = (event: React.KeyboardEvent): void => {
    if (filterField && filterField.current && event.key === "Enter") {
      setFilter(filterField.current.value);
    }
  };

  return (
    <Toolbar id="landing-page-toolbar" data-testid="landing-page-toolbar">
      <ToolbarContent>
        <ToolbarItem>
          <InputGroup>
            <TextInput
              id="model-filter-input"
              name="model-filter-input"
              data-testid="landing-page-toolbar__model-filter"
              ref={filterField}
              type="search"
              aria-label="filter models"
              onKeyDown={onFilterEnter}
              placeholder="Filter models by name"
            />
            <Button
              id="models-filter"
              data-testid="landing-page-toolbar__submit"
              variant={ButtonVariant.control}
              aria-label="filter button for filter input"
              onClick={onFilterSubmit}
            >
              <SearchIcon />
            </Button>
          </InputGroup>
        </ToolbarItem>
        <ToolbarItem>
          <Switch
            id="only-supported-models-switch"
            data-testid="landing-page-toolbar__supported-models"
            label="Show unsupported models"
            labelOff="Hide unsupported models"
            isChecked={showUnsupportedModels}
            onChange={setShowUnsupportedModels}
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );
};
