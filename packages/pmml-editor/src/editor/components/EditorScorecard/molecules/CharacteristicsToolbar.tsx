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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { SearchIcon } from "@patternfly/react-icons/dist/js/icons/search-icon";
import "./CharacteristicsToolbar.scss";

interface CharacteristicsToolbarProps {
  filter: string;
  setFilter: (filter: string) => void;
  onFilter: () => void;
  onAddCharacteristic: () => void;
}

export const CharacteristicsToolbar = (props: CharacteristicsToolbarProps) => {
  const { filter, setFilter, onFilter, onAddCharacteristic } = props;

  return (
    <Toolbar id="characteristics-toolbar" data-testid="characteristics-toolbar">
      <ToolbarContent>
        <Split hasGutter={true} style={{ width: "100%" }}>
          <SplitItem>
            <TextContent>
              <Title size="lg" headingLevel="h1">
                Characteristics
              </Title>
            </TextContent>
          </SplitItem>
          <SplitItem isFilled={true} data-ouia-component-type="filler" />
          <SplitItem>
            <ToolbarItem>
              <InputGroup>
                <form onSubmit={(e) => e.preventDefault()}>
                  <span style={{ display: "flex" }}>
                    <TextInput
                      id="characteristics-filter-input"
                      name="characteristics-filter-input"
                      data-testid="characteristics-toolbar__characteristics-filter"
                      type="search"
                      aria-label="filter characteristics"
                      placeholder="Filter by name"
                      value={filter}
                      onChange={(e) => setFilter(e)}
                    />
                    <Button
                      id="characteristics-filter"
                      type="submit"
                      data-testid="characteristics-toolbar__submit"
                      variant={ButtonVariant.control}
                      aria-label="filter button for filter input"
                      onClick={() => onFilter()}
                    >
                      <SearchIcon />
                    </Button>
                  </span>
                </form>
              </InputGroup>
            </ToolbarItem>
          </SplitItem>
          <SplitItem>
            <ToolbarItem>
              <Button
                id="add-characteristic-button"
                data-testid="characteristics-toolbar__add-characteristic"
                variant="primary"
                onClick={() => onAddCharacteristic()}
                ouiaId="add-characteristic"
              >
                Add Characteristic
              </Button>
            </ToolbarItem>
          </SplitItem>
        </Split>
      </ToolbarContent>
    </Toolbar>
  );
};
