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
import "./CorePropertiesTable.scss";
import { Operation } from "../Operation";
import useOnclickOutside from "react-cool-onclickoutside";
import { isEqual } from "lodash";
import set = Reflect.set;
import get = Reflect.get;

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

const GenericNumericEditor = (
  id: string,
  value: number,
  valid: boolean,
  onChange: (_value: number) => void,
  onBlur: () => void
) => {
  return (
    <GenericNumericInput
      id={id}
      value={value}
      validated={valid ? "default" : "error"}
      onChange={onChange}
      onBlur={onBlur}
    />
  );
};

const GenericTextEditor = (
  id: string,
  value: string,
  valid: boolean,
  onChange: (_value: string) => void,
  onBlur: () => void
) => {
  return (
    <GenericTextInput
      id={id}
      value={value}
      validated={valid ? "default" : "error"}
      onChange={onChange}
      onBlur={onBlur}
    />
  );
};

export const CorePropertiesTable = (props: CorePropertiesTableProps) => {
  const { activeOperation, setActiveOperation } = props;

  const [isEditing, setEditing] = useState(false);
  const [isScorable, setScorable] = useState(props.isScorable);
  const [functionName, setFunctionName] = useState(props.functionName);
  const [algorithmName, setAlgorithmName] = useState(props.algorithmName);
  const [baselineScore, setBaselineScore] = useState<number>(props.baselineScore);
  const [baselineMethod, setBaselineMethod] = useState(props.baselineMethod);
  const [initialScore, setInitialScore] = useState<number>(props.initialScore);
  const [useReasonCodes, setUseReasonCodes] = useState(props.useReasonCodes);
  const [reasonCodeAlgorithm, setReasonCodeAlgorithm] = useState(props.reasonCodeAlgorithm);

  useEffect(() => {
    setScorable(props.isScorable);
    setFunctionName(props.functionName);
    setAlgorithmName(props.algorithmName);
    setBaselineScore(props.baselineScore);
    setBaselineMethod(props.baselineMethod);
    setInitialScore(props.initialScore);
    setUseReasonCodes(props.useReasonCodes);
    setReasonCodeAlgorithm(props.reasonCodeAlgorithm);
  }, [props]);

  const isScorableEditor = BooleanFieldEditor("is-scorable-id", isScorable, checked => {
    setScorable(checked);
    onCommit({ isScorable: checked });
  });
  const functionNameEditor = GenericSelectorEditor(
    "function-name-selector-id",
    ["associationRules", "sequences", "classification", "regression", "clustering", "timeSeries", "mixed"],
    functionName,
    _selection => {
      setFunctionName(_selection as MiningFunction);
      onCommit({ functionName: _selection as MiningFunction });
    }
  );
  const algorithmNameEditor = GenericTextEditor(
    "algorithm-name-id",
    algorithmName,
    true,
    _value => {
      setAlgorithmName(_value);
    },
    () => {
      onCommit({ algorithmName: algorithmName });
    }
  );
  const baselineScoreEditor = GenericNumericEditor(
    "baseline-score-id",
    baselineScore,
    true,
    _value => {
      setBaselineScore(_value);
    },
    () => {
      onCommit({ baselineScore: baselineScore });
    }
  );
  const baselineMethodEditor = GenericSelectorEditor(
    "baseline-method-selector-id",
    ["max", "min", "mean", "neutral", "other"],
    baselineMethod,
    _selection => {
      setBaselineMethod(_selection as BaselineMethod);
      onCommit({ baselineMethod: _selection as BaselineMethod });
    }
  );
  const initialScoreEditor = GenericNumericEditor(
    "initial-score-id",
    initialScore,
    true,
    _value => {
      setInitialScore(_value);
    },
    () => {
      onCommit({ initialScore: initialScore });
    }
  );
  const useReasonCodesEditor = BooleanFieldEditor("use-reason-codes-id", useReasonCodes, checked => {
    setUseReasonCodes(checked);
    onCommit({ useReasonCodes: checked });
  });
  const reasonCodeAlgorithmEditor = GenericSelectorEditor(
    "reason-code-algorithm-selector-id",
    ["pointsAbove", "pointsBelow"],
    reasonCodeAlgorithm,
    _selection => {
      setReasonCodeAlgorithm(_selection as ReasonCodeAlgorithm);
      onCommit({ reasonCodeAlgorithm: _selection as ReasonCodeAlgorithm });
    }
  );

  const ref = useOnclickOutside(event => onCommitAndClose(), {
    disabled: activeOperation !== Operation.UPDATE_CORE,
    eventTypes: ["click"]
  });

  const onEdit = () => {
    setEditing(true);
    setActiveOperation(Operation.UPDATE_CORE);
  };

  const onCommitAndClose = () => {
    onCommit({});
    onCancel();
  };

  const onCommit = (partial: Partial<CoreProperties>) => {
    const existingPartial: Partial<CoreProperties> = {};
    Object.keys(partial).forEach(key => set(existingPartial, key, get(props, key)));

    if (!isEqual(partial, existingPartial)) {
      props.commit({ ...props, ...partial });
    }
  };

  const onCancel = () => {
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
          e.preventDefault();
          e.stopPropagation();
          onEdit();
        } else if (e.key === "Escape") {
          onCancel();
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
            <Form
              onSubmit={e => {
                e.stopPropagation();
                e.preventDefault();
              }}
            >
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
                </tbody>
              </table>
            </Form>
          </StackItem>
        </Stack>
      </PageSection>
    </div>
  );
};
