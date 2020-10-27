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
  hasUnsupportedModels: boolean;
  showUnsupportedModels: boolean;
  onShowUnsupportedModels: (showUnsupportedModels: boolean) => void;
}

export const LandingPageToolbar = (props: LandingPageToolbarProps) => {
  const [filter, setFilter] = useState("");
  const { onFilter, hasUnsupportedModels, showUnsupportedModels, onShowUnsupportedModels } = props;

  return (
    <Toolbar id="landing-page-toolbar" data-testid="landing-page-toolbar">
      <ToolbarContent>
        <ToolbarItem>
          <InputGroup>
            <form onSubmit={e => e.preventDefault()}>
              <span style={{ display: "flex" }}>
                <TextInput
                  id="model-filter-input"
                  name="model-filter-input"
                  data-testid="landing-page-toolbar__model-filter"
                  type="search"
                  aria-label="filter models"
                  placeholder="Filter models by name"
                  onChange={e => setFilter(e)}
                />
                <Button
                  id="models-filter"
                  type="submit"
                  data-testid="landing-page-toolbar__submit"
                  variant={ButtonVariant.control}
                  aria-label="filter button for filter input"
                  onClick={e => onFilter(filter)}
                >
                  <SearchIcon />
                </Button>
              </span>
            </form>
          </InputGroup>
        </ToolbarItem>
        {hasUnsupportedModels && (
          <ToolbarItem>
            <Switch
              id="only-supported-models-switch"
              data-testid="landing-page-toolbar__supported-models"
              label="Show unsupported models"
              isChecked={showUnsupportedModels}
              onChange={onShowUnsupportedModels}
            />
          </ToolbarItem>
        )}
      </ToolbarContent>
    </Toolbar>
  );
};
