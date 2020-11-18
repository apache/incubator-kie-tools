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
import { useEffect, useMemo, useState } from "react";
import { GenericNumericInput, GenericSelector, GenericTextInput } from "../atoms";
import { BaselineMethod, MiningFunction, ReasonCodeAlgorithm } from "@kogito-tooling/pmml-editor-marshaller";
import {
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
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import "./CorePropertiesTable.scss";
import { ValidatedType } from "../../../types";
import { Operation } from "../Operation";
import useOnclickOutside from "react-cool-onclickoutside";

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
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  commit: (props: CoreProperties) => void;
}

const BooleanFieldEditor = (
  id: string,
  isChecked: boolean,
  onChange: (checked: boolean, event: React.FormEvent<HTMLInputElement>) => void,
  autoFocus?: boolean
) => {
  return <Switch id={id} isChecked={isChecked} onChange={onChange} autoFocus={autoFocus === true} />;
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
  const { activeOperation, setActiveOperation } = props;

  const [isEditing, setEditing] = useState(false);
  const [isScorable, setScorable] = useState(props.isScorable);
  const [functionName, setFunctionName] = useState(props.functionName);
  const [algorithmName, setAlgorithmName] = useState(props.algorithmName);
  const [baselineScore, setBaselineScore] = useState<ValidatedType<number>>({
    value: props.baselineScore,
    valid: true
  });
  const [baselineMethod, setBaselineMethod] = useState(props.baselineMethod);
  const [initialScore, setInitialScore] = useState<ValidatedType<number>>({
    value: props.initialScore,
    valid: true
  });
  const [useReasonCodes, setUseReasonCodes] = useState(props.useReasonCodes);
  const [reasonCodeAlgorithm, setReasonCodeAlgorithm] = useState(props.reasonCodeAlgorithm);

  useEffect(() => {
    setScorable(props.isScorable);
    setFunctionName(props.functionName);
    setAlgorithmName(props.algorithmName);
    setBaselineScore({
      value: props.baselineScore,
      valid: ValidateBaselineScore(props.baselineScore)
    });
    setBaselineMethod(props.baselineMethod);
    setInitialScore({
      value: props.initialScore,
      valid: ValidateInitialScore(props.initialScore)
    });
    setUseReasonCodes(props.useReasonCodes);
    setReasonCodeAlgorithm(props.reasonCodeAlgorithm);
  }, [props]);

  const isScorableEditor = BooleanFieldEditor("is-scorable-id", isScorable, checked => setScorable(checked), true);
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

  const ref = useOnclickOutside(event => commitEdit(), { disabled: activeOperation !== Operation.UPDATE_CORE });

  const onEdit = () => {
    setEditing(true);
    setActiveOperation(Operation.UPDATE_CORE);
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

    cancelEdit();
  };
  const cancelEdit = () => {
    setEditing(false);
    setActiveOperation(Operation.NONE);
  };

  const isEditModeEnabled = useMemo(() => isEditing && activeOperation === Operation.UPDATE_CORE, [
    isEditing,
    activeOperation
  ]);

  return (
    <div
      ref={ref}
      className={`editable${isEditModeEnabled ? " editing" : ""}`}
      onClick={onEdit}
      tabIndex={0}
      onKeyDown={e => {
        if (e.key === "Enter") {
          onEdit();
        } else if (e.key === "Escape") {
          cancelEdit();
        }
      }}
    >
      <PageSection variant={PageSectionVariants.light}>
        <Stack>
          <StackItem>
            <TextContent>
              <Title size="lg" headingLevel="h1">
                Model setup
              </Title>
            </TextContent>
          </StackItem>
          <StackItem>
            <Form>
              <table className="core-properties__table">
                <thead>
                  <tr>
                    <th>
                      <FormGroup fieldId="IsScorable" label="Is Scorable" />
                    </th>
                    <th>
                      <FormGroup fieldId="FunctionName" label="Function Name" />
                    </th>
                    <th>
                      <FormGroup fieldId="AlgorithmName" label="Algorithm Name" />
                    </th>
                    <th>
                      <FormGroup fieldId="BaselineScore" label="Baseline Score" />
                    </th>
                    <th>
                      <FormGroup fieldId="BaselineMethod" label="Baseline Method" />
                    </th>
                    <th>
                      <FormGroup fieldId="InitialScore" label="Initial Score" />
                    </th>
                    <th>
                      <FormGroup fieldId="UseReasonCodes" label="Use Reason Codes" />
                    </th>
                    <th>
                      <FormGroup fieldId="ReasonCodeAlgorithm" label="Reason Code Algorithm" />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>
                      {!isEditModeEnabled && props.isScorable.toString()}
                      {isEditModeEnabled && isScorableEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.functionName}
                      {isEditModeEnabled && functionNameEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.algorithmName}
                      {isEditModeEnabled && algorithmNameEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.baselineScore.toString()}
                      {isEditModeEnabled && baselineScoreEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.baselineMethod}
                      {isEditModeEnabled && baselineMethodEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.initialScore.toString()}
                      {isEditModeEnabled && initialScoreEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.useReasonCodes.toString()}
                      {isEditModeEnabled && useReasonCodesEditor}
                    </td>
                    <td>
                      {!isEditModeEnabled && props.reasonCodeAlgorithm.toString()}
                      {isEditModeEnabled && reasonCodeAlgorithmEditor}
                    </td>
                  </tr>
                  <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>
                      {isEditModeEnabled && !baselineScore.valid && (
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
                      {isEditModeEnabled && !initialScore.valid && (
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
          </StackItem>
        </Stack>
      </PageSection>
    </div>
  );
};
