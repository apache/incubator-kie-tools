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
import { useMemo, useState } from "react";
import { GenericSelector } from "../atoms";
import { BaselineMethod, MiningFunction, ReasonCodeAlgorithm } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Button,
  Flex,
  FlexItem,
  PageSection,
  PageSectionVariants,
  Stack,
  StackItem,
  Switch,
  TextContent,
  Title
} from "@patternfly/react-core";
import { PencilAltIcon } from "@patternfly/react-icons";
import { useDispatch } from "react-redux";
import "./CorePropertiesTable.scss";
import { NumericInput } from "../atoms/NumericInput";
import { Actions } from "../../../reducers";

interface CorePropertiesTableProps {
  isScorable: boolean;
  functionName: MiningFunction;
  baselineScore: number;
  baselineMethod: BaselineMethod;
  initialScore: number;
  useReasonCodes: boolean;
  reasonCodeAlgorithm: ReasonCodeAlgorithm;
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

const NumericEditor = (id: string, value: number, valid: boolean, onChange: (_value: number) => void) => {
  return <NumericInput id={id} value={value} valid={valid} onChange={onChange} />;
};

const ValidateBaselineScore = (value: number) => {
  return value > 0;
};

const ValidateInitialScore = (value: number) => {
  return value > 0;
};

export const CorePropertiesTable = (props: CorePropertiesTableProps) => {
  const dispatch = useDispatch();
  const [isEditing, setEditing] = useState(false);
  const [isScorable, setScorable] = useState(props.isScorable);
  const [functionName, setFunctionName] = useState(props.functionName);
  const [baselineScore, setBaselineScore] = useState({
    valid: ValidateBaselineScore(props.baselineScore),
    value: props.baselineScore
  });
  const [baselineMethod, setBaselineMethod] = useState(props.baselineMethod);
  const [initialScore, setInitialScore] = useState({
    valid: ValidateInitialScore(props.initialScore),
    value: props.initialScore
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
  const baselineScoreEditor = useMemo(
    () =>
      NumericEditor("baseline-score-id", baselineScore.value, baselineScore.valid, _value => {
        setBaselineScore({ ...baselineScore, value: _value, valid: ValidateBaselineScore(_value) });
      }),
    [baselineScore.value]
  );
  const baselineMethodEditor = GenericSelectorEditor(
    "baseline-method-selector-id",
    ["max", "min", "mean", "neutral", "other"],
    baselineMethod,
    _selection => setBaselineMethod(_selection as BaselineMethod)
  );
  const initialScoreEditor = useMemo(
    () =>
      NumericEditor("initial-score-id", initialScore.value, initialScore.valid, _value => {
        setInitialScore({ ...initialScore, value: _value, valid: ValidateInitialScore(_value) });
      }),
    [initialScore.value]
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
    dispatch({
      type: Actions.Scorecard_SetCoreProperties,
      payload: {
        index: 0,
        isScorable: isScorable,
        functionName: functionName,
        baselineScore: baselineScore,
        baselineMethod: baselineMethod,
        initialScore: initialScore,
        useReasonCodes: useReasonCodes,
        reasonCodeAlgorithm: reasonCodeAlgorithm
      }
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
          <table className="core-properties__table">
            <thead>
              <tr>
                <th>Is Scorable</th>
                <th>Function Name</th>
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
            </tbody>
          </table>
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
