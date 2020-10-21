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
import { GenericNumericInput, GenericSelector, GenericTextInput } from "../atoms";
import { BaselineMethod, MiningFunction, ReasonCodeAlgorithm } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Button,
  Flex,
  FlexItem,
  Form,
  FormGroup,
  PageSection,
  PageSectionVariants,
  Stack,
  StackItem,
  Switch,
  TextContent,
  Title
} from "@patternfly/react-core";
import { ExclamationCircleIcon, PencilAltIcon } from "@patternfly/react-icons";
import "./CorePropertiesTable.scss";
import { ValidatedType } from "../../../types";

interface CoreProperties {
  isScorable: boolean;
  functionName: MiningFunction;
  algorithmName: string;
  baselineScore: number;
  baselineMethod: BaselineMethod;
  initialScore: number;
  useReasonCodes: boolean;
  reasonCodeAlgorithm: ReasonCodeAlgorithm;
}

interface CorePropertiesTableProps extends CoreProperties {
  commit: (props: CoreProperties) => void;
}

const BooleanFieldEditor = (
  id: string,
  isChecked: boolean,
  onChange: (checked: boolean, event: React.FormEvent<HTMLInputElement>) => void
) => {
  return <Switch id={id} isChecked={isChecked} onChange={onChange} />;
};

const GenericSelectorEditor = (
  id: string,
  items: string[],
  selection: string,
  onSelect: (_selection: string) => void
) => {
  return <GenericSelector id={id} items={items} selection={selection} onSelect={onSelect} />;
};

const GenericNumericEditor = (id: string, value: number, valid: boolean, onChange: (_value: number) => void) => {
  return <GenericNumericInput id={id} value={value} validated={valid ? "default" : "error"} onChange={onChange} />;
};

const GenericTextEditor = (id: string, value: string, valid: boolean, onChange: (_value: string) => void) => {
  return <GenericTextInput id={id} value={value} validated={valid ? "default" : "error"} onChange={onChange} />;
};

const ValidateBaselineScore = (value: number) => {
  return value > 0;
};

const ValidateInitialScore = (value: number) => {
  return value > 0;
};

export const CorePropertiesTable = (props: CorePropertiesTableProps) => {
  const [isEditing, setEditing] = useState(false);
  const [isScorable, setScorable] = useState(props.isScorable);
  const [functionName, setFunctionName] = useState(props.functionName);
  const [algorithmName, setAlgorithmName] = useState(props.algorithmName);
  const [baselineScore, setBaselineScore] = useState<ValidatedType<number>>({
    value: props.baselineScore,
    valid: ValidateBaselineScore(props.baselineScore)
  });
  const [baselineMethod, setBaselineMethod] = useState(props.baselineMethod);
  const [initialScore, setInitialScore] = useState<ValidatedType<number>>({
    value: props.initialScore,
    valid: ValidateInitialScore(props.initialScore)
  });
  const [useReasonCodes, setUseReasonCodes] = useState(props.useReasonCodes);
  const [reasonCodeAlgorithm, setReasonCodeAlgorithm] = useState(props.reasonCodeAlgorithm);

  const isScorableEditor = BooleanFieldEditor("is-scorable-id", isScorable, checked => setScorable(checked));
  const functionNameEditor = GenericSelectorEditor(
    "function-name-selector-id",
    ["associationRules", "sequences", "classification", "regression", "clustering", "timeSeries", "mixed"],
    functionName,
    _selection => setFunctionName(_selection as MiningFunction)
  );
  const algorithmNameEditor = GenericTextEditor("algorithm-name-id", algorithmName, true, _value => {
    setAlgorithmName(_value);
  });
  const baselineScoreEditor = GenericNumericEditor(
    "baseline-score-id",
    baselineScore.value,
    baselineScore.valid,
    _value => {
      setBaselineScore({ value: _value, valid: ValidateBaselineScore(_value) });
    }
  );
  const baselineMethodEditor = GenericSelectorEditor(
    "baseline-method-selector-id",
    ["max", "min", "mean", "neutral", "other"],
    baselineMethod,
    _selection => setBaselineMethod(_selection as BaselineMethod)
  );
  const initialScoreEditor = GenericNumericEditor(
    "initial-score-id",
    initialScore.value,
    initialScore.valid,
    _value => {
      setInitialScore({ value: _value, valid: ValidateInitialScore(_value) });
    }
  );
  const useReasonCodesEditor = BooleanFieldEditor("use-reason-codes-id", useReasonCodes, checked =>
    setUseReasonCodes(checked)
  );
  const reasonCodeAlgorithmEditor = GenericSelectorEditor(
    "reason-code-algorithm-selector-id",
    ["pointsAbove", "pointsBelow"],
    reasonCodeAlgorithm,
    _selection => setReasonCodeAlgorithm(_selection as ReasonCodeAlgorithm)
  );

  const onEdit = () => {
    setEditing(true);
  };

  const commitEdit = () => {
    props.commit({
      isScorable: isScorable,
      functionName: functionName,
      algorithmName: algorithmName,
      baselineScore: baselineScore.value,
      baselineMethod: baselineMethod,
      initialScore: initialScore.value,
      useReasonCodes: useReasonCodes,
      reasonCodeAlgorithm: reasonCodeAlgorithm
    });
    setEditing(false);
  };
  const cancelEdit = () => {
    setEditing(false);
  };

  return (
    <PageSection variant={PageSectionVariants.light}>
      <Stack>
        <StackItem>
          <Flex>
            <FlexItem>
              <TextContent>
                <Title size="lg" headingLevel="h1">
                  Model setup
                </Title>
              </TextContent>
            </FlexItem>
            <FlexItem>
              {!isEditing && (
                <Button variant="link" icon={<PencilAltIcon />} onClick={onEdit}>
                  Edit
                </Button>
              )}
              {/*This is a hack to ensure the layout remains unchanged when the Edit button is hidden*/}
              {isEditing && (
                <Button variant="tertiary" isDisabled={true} style={{ visibility: "hidden" }}>
                  Temp
                </Button>
              )}
            </FlexItem>
          </Flex>
        </StackItem>
        <StackItem>
          <Form>
            <table className="core-properties__table">
              <thead>
                <tr>
                  <th>Is Scorable</th>
                  <th>Function Name</th>
                  <th>Algorithm Name</th>
                  <th>Baseline Score</th>
                  <th>Baseline Method</th>
                  <th>Initial Score</th>
                  <th>Use Reason Codes</th>
                  <th>Reason Code Algorithm</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    {!isEditing && props.isScorable.toString()}
                    {isEditing && isScorableEditor}
                  </td>
                  <td>
                    {!isEditing && props.functionName}
                    {isEditing && functionNameEditor}
                  </td>
                  <td>
                    {!isEditing && props.algorithmName}
                    {isEditing && algorithmNameEditor}
                  </td>
                  <td>
                    {!isEditing && props.baselineScore.toString()}
                    {isEditing && baselineScoreEditor}
                  </td>
                  <td>
                    {!isEditing && props.baselineMethod}
                    {isEditing && baselineMethodEditor}
                  </td>
                  <td>
                    {!isEditing && props.initialScore.toString()}
                    {isEditing && initialScoreEditor}
                  </td>
                  <td>
                    {!isEditing && props.useReasonCodes.toString()}
                    {isEditing && useReasonCodesEditor}
                  </td>
                  <td>
                    {!isEditing && props.reasonCodeAlgorithm.toString()}
                    {isEditing && reasonCodeAlgorithmEditor}
                  </td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>
                    {isEditing && !baselineScore.valid && (
                      <FormGroup
                        helperTextInvalid="Must be greater than zero"
                        helperTextInvalidIcon={<ExclamationCircleIcon />}
                        fieldId="characteristic-baselineScore-validation"
                        validated="error"
                      />
                    )}
                  </td>
                  <td>&nbsp;</td>
                  <td>
                    {isEditing && !initialScore.valid && (
                      <FormGroup
                        helperTextInvalid="Must be greater than zero"
                        helperTextInvalidIcon={<ExclamationCircleIcon />}
                        fieldId="characteristic-initialScore-validation"
                        validated="error"
                      />
                    )}
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
              </tbody>
            </table>
          </Form>
          {isEditing && (
            <Flex className="core-properties__edit_buttons">
              <FlexItem>
                <Button variant="primary" onClick={commitEdit}>
                  Save
                </Button>
              </FlexItem>
              <FlexItem>
                <Button variant="secondary" onClick={cancelEdit}>
                  Cancel
                </Button>
              </FlexItem>
            </Flex>
          )}
        </StackItem>
      </Stack>
    </PageSection>
  );
};
