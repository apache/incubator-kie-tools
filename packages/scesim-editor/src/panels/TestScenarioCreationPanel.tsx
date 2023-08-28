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

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

export function TestScenarioCreationPanel({
  onCreateScesimButtonClicked,
}: {
  onCreateScesimButtonClicked: (assetType: string, skipFile: boolean) => void;
}) {
  const assetsOption = [
    { value: "", label: "Select a type", disabled: true },
    { value: "DMN", label: "Decision (DMN)", disabled: false },
    { value: "RULE", label: "Rule (DRL)", disabled: true },
  ];

  const [assetType, setAssetType] = React.useState("");
  const [skipFile, setSkipFile] = React.useState(false);

  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel={"h6"} size={"md"}>
        Create a new Test Scenario
      </Title>
      <Form isHorizontal className="kie-scesim-editor--creation-form">
        <FormGroup label="Asset type" isRequired>
          <FormSelect
            value={assetType}
            id="asset-type-select"
            name="asset-type-select"
            onChange={(value: string) => {
              setAssetType(value);
            }}
          >
            {assetsOption.map((option, index) => (
              <FormSelectOption isDisabled={option.disabled} key={index} value={option.value} label={option.label} />
            ))}
          </FormSelect>
        </FormGroup>
        {assetType == "DMN" && (
          <FormGroup label="Select DMN" isRequired>
            <FormSelect id="dmn-select" name="dmn-select" value={"select one"} isDisabled>
              <FormSelectOption isDisabled={true} key={0} value={"select one"} label={"Select a DMN file"} />
            </FormSelect>
          </FormGroup>
        )}
        <FormGroup>
          <Checkbox
            id="skip-scesim-checkbox"
            isChecked={skipFile}
            label="Skip this file during the test"
            name="skip-scesim-checkbox"
            onChange={(value: boolean) => {
              setSkipFile(value);
            }}
          />
        </FormGroup>
      </Form>
      <Button
        variant="primary"
        icon={<AddIcon />}
        isDisabled={assetType == ""}
        onClick={() => onCreateScesimButtonClicked(assetType, skipFile)}
      >
        Create
      </Button>
    </EmptyState>
  );
}

export default TestScenarioCreationPanel;
