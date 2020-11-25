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
import { GenericSelector } from "../atoms";
import { BaselineMethod, MiningFunction, ReasonCodeAlgorithm } from "@kogito-tooling/pmml-editor-marshaller";
import {
  Bullseye,
  Form,
  FormGroup,
  Level,
  LevelItem,
  PageSection,
  PageSectionVariants,
  Stack,
  StackItem,
  Switch,
  TextContent,
  TextInput,
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

const GenericSelectorEditor = (
  id: string,
  items: string[],
  selection: string | undefined,
  onSelect: (_selection: string) => void
) => {
  return <GenericSelector id={id} items={items} selection={selection} onSelect={onSelect} />;
};

export const CorePropertiesTable = (props: CorePropertiesTableProps) => {
  const { activeOperation, setActiveOperation } = props;

  const [isEditing, setEditing] = useState(false);
  const [isScorable, setScorable] = useState<boolean>();
  const [functionName, setFunctionName] = useState<MiningFunction>();
  const [algorithmName, setAlgorithmName] = useState<string | undefined>();
  const [baselineScore, setBaselineScore] = useState<number | undefined>();
  const [baselineMethod, setBaselineMethod] = useState<BaselineMethod | undefined>();
  const [initialScore, setInitialScore] = useState<number | undefined>(props.initialScore);
  const [useReasonCodes, setUseReasonCodes] = useState<boolean>(props.useReasonCodes);
  const [reasonCodeAlgorithm, setReasonCodeAlgorithm] = useState<ReasonCodeAlgorithm | undefined>();

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

  const ref = useOnclickOutside(event => onCommitAndClose(), {
    disabled: activeOperation !== Operation.UPDATE_CORE,
    eventTypes: ["click"]
  });

  const value = (_value: any) => {
    return _value ? <span>{_value}</span> : <span>&nbsp;</span>;
  };

  const toNumber = (value: string): number | undefined => {
    if (value === "") {
      return undefined;
    }
    const n = Number(value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
  };

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
        <Stack hasGutter={true}>
          <StackItem>
            <TextContent>
              <Title size="lg" headingLevel="h1">
                Model setup
              </Title>
            </TextContent>
          </StackItem>
          <StackItem>
            <Bullseye>
              <Form
                onSubmit={e => {
                  e.stopPropagation();
                  e.preventDefault();
                }}
                className="core-properties__container"
              >
                <Level hasGutter={true}>
                  <LevelItem>
                    <FormGroup label="Scorable?" fieldId="core-isScorable">
                      {!isEditModeEnabled && value(props.isScorable.toString())}
                      {isEditModeEnabled && (
                        <Switch
                          id="core-isScorable"
                          isChecked={isScorable}
                          onChange={checked => {
                            setScorable(checked);
                            onCommit({ isScorable: checked });
                          }}
                        />
                      )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Function" fieldId="core-functionName" required={true}>
                      {!isEditModeEnabled && value(props.functionName)}
                      {isEditModeEnabled &&
                        GenericSelectorEditor(
                          "core-functionName",
                          [
                            "associationRules",
                            "sequences",
                            "classification",
                            "regression",
                            "clustering",
                            "timeSeries",
                            "mixed"
                          ],
                          functionName,
                          _selection => {
                            setFunctionName(_selection as MiningFunction);
                            onCommit({ functionName: _selection as MiningFunction });
                          }
                        )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Algorithm" fieldId="core-algorithmName">
                      {!isEditModeEnabled && value(props.algorithmName)}
                      {isEditModeEnabled && (
                        <TextInput
                          type="text"
                          id="core-algorithmName"
                          name="core-algorithmName"
                          aria-describedby="core-algorithmName"
                          value={algorithmName}
                          onChange={e => setAlgorithmName(e)}
                          onBlur={e => {
                            onCommit({ algorithmName: algorithmName });
                          }}
                        />
                      )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Baseline score" fieldId="core-baselinScore">
                      {!isEditModeEnabled && value(props.baselineScore.toString())}
                      {isEditModeEnabled && (
                        <TextInput
                          id="core-baselineScore"
                          value={baselineScore}
                          onChange={e => setBaselineScore(toNumber(e))}
                          onBlur={() => {
                            onCommit({ baselineScore: baselineScore });
                          }}
                          type="number"
                        />
                      )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Baseline method" fieldId="core-baselineMethod">
                      {!isEditModeEnabled && value(props.baselineMethod)}
                      {isEditModeEnabled &&
                        GenericSelectorEditor(
                          "core-baselineMethod",
                          ["max", "min", "mean", "neutral", "other"],
                          baselineMethod,
                          _selection => {
                            setBaselineMethod(_selection as BaselineMethod);
                            onCommit({ baselineMethod: _selection as BaselineMethod });
                          }
                        )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Initial score" fieldId="core-initialScore">
                      {!isEditModeEnabled && value(props.initialScore.toString())}
                      {isEditModeEnabled && (
                        <TextInput
                          id="core-initialScore"
                          value={initialScore}
                          onChange={e => setInitialScore(toNumber(e))}
                          onBlur={() => {
                            onCommit({ initialScore: initialScore });
                          }}
                          type="number"
                        />
                      )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Use reason codes?" fieldId="core-useReasonCodes">
                      {!isEditModeEnabled && value(props.useReasonCodes.toString())}
                      {isEditModeEnabled && (
                        <Switch
                          id="core-useReasonCodes"
                          isChecked={useReasonCodes}
                          onChange={checked => {
                            setUseReasonCodes(checked);
                            onCommit({ useReasonCodes: checked });
                          }}
                        />
                      )}
                    </FormGroup>
                  </LevelItem>
                  <LevelItem>
                    <FormGroup label="Reason code algorithm" fieldId="core-reasonCodeAlgorithm">
                      {!isEditModeEnabled && value(props.reasonCodeAlgorithm.toString())}
                      {isEditModeEnabled &&
                        GenericSelectorEditor(
                          "core-reasonCodeAlgorithm",
                          ["pointsAbove", "pointsBelow"],
                          reasonCodeAlgorithm,
                          _selection => {
                            setReasonCodeAlgorithm(_selection as ReasonCodeAlgorithm);
                            onCommit({ reasonCodeAlgorithm: _selection as ReasonCodeAlgorithm });
                          }
                        )}
                    </FormGroup>
                  </LevelItem>
                </Level>
              </Form>
            </Bullseye>
          </StackItem>
        </Stack>
      </PageSection>
    </div>
  );
};
