/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./HitPolicySelector.css";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
} from "../../api";
import * as React from "react";
import { useCallback, useState } from "react";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export interface HitPolicySelectorProps {
  /** Pre-selected hit policy */
  selectedHitPolicy: DecisionTableExpressionDefinitionHitPolicy;
  /** Pre-selected built-in aggregator */
  selectedBuiltInAggregator: DecisionTableExpressionDefinitionBuiltInAggregation;
  /** Callback invoked when hit policy selection changes */
  onHitPolicySelect: (hitPolicy: DecisionTableExpressionDefinitionHitPolicy) => void;
  /** Callback invoked when built-in aggregator selection changes */
  onBuiltInAggregatorSelect: (builtInAggregator: DecisionTableExpressionDefinitionBuiltInAggregation) => void;
}

const BUILT_IN_AGGREGATION_AVAILABILITY = [DecisionTableExpressionDefinitionHitPolicy.Collect];
export const HitPolicySelector: React.FunctionComponent<HitPolicySelectorProps> = ({
  onBuiltInAggregatorSelect,
  onHitPolicySelect,
  selectedBuiltInAggregator,
  selectedHitPolicy,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const [hitPolicySelectOpen, setHitPolicySelectOpen] = useState(false);
  const [builtInAggregatorSelectOpen, setBuiltInAggregatorSelectOpen] = useState(false);
  const [builtInAggregatorSelectDisabled, setBuiltInAggregatorSelectDisabled] = useState(
    !_.includes(BUILT_IN_AGGREGATION_AVAILABILITY, selectedHitPolicy)
  );

  const onHitPolicySelectToggle = useCallback((isOpen) => setHitPolicySelectOpen(isOpen), []);
  const onBuiltInAggregatorSelectToggle = useCallback((isOpen) => setBuiltInAggregatorSelectOpen(isOpen), []);

  const hitPolicySelectionCallback = useCallback(
    (event: React.MouseEvent<Element, MouseEvent>, itemId: string) => {
      const updatedHitPolicy = itemId as DecisionTableExpressionDefinitionHitPolicy;
      const hitPolicySupportsAggregation = _.includes(BUILT_IN_AGGREGATION_AVAILABILITY, updatedHitPolicy);
      boxedExpressionEditor.beeGwtService?.notifyUserAction();
      onHitPolicySelect(updatedHitPolicy);
      if (hitPolicySupportsAggregation) {
        setBuiltInAggregatorSelectDisabled(false);
      } else {
        setBuiltInAggregatorSelectDisabled(true);
        onBuiltInAggregatorSelect("<None>" as DecisionTableExpressionDefinitionBuiltInAggregation);
      }
      setHitPolicySelectOpen(false);
    },
    [boxedExpressionEditor.beeGwtService, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  const renderHitPolicyItems = useCallback(
    () =>
      _.map(Object.values(DecisionTableExpressionDefinitionHitPolicy), (key) => (
        <SelectOption key={key} value={key} data-ouia-component-id={key}>
          {key}
        </SelectOption>
      )),
    []
  );

  const builtInAggregatorSelectionCallback = useCallback(
    (event: React.MouseEvent<Element, MouseEvent>, itemId: string) => {
      boxedExpressionEditor.beeGwtService?.notifyUserAction();
      onBuiltInAggregatorSelect(itemId as DecisionTableExpressionDefinitionBuiltInAggregation);
      setBuiltInAggregatorSelectOpen(false);
    },
    [boxedExpressionEditor.beeGwtService, onBuiltInAggregatorSelect]
  );

  const renderBuiltInAggregationItems = useCallback(
    () =>
      _.map(Object.keys(DecisionTableExpressionDefinitionBuiltInAggregation), (key) => (
        <SelectOption key={key} value={key} data-ouia-component-id={key}>
          {key}
        </SelectOption>
      )),
    []
  );

  const renderAggregator = useCallback(
    (
      builtInAggregator: DecisionTableExpressionDefinitionBuiltInAggregation,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy
    ) => {
      if (_.includes(BUILT_IN_AGGREGATION_AVAILABILITY, hitPolicy)) {
        return builtInAggregator;
      }
      return "";
    },
    []
  );

  return (
    <PopoverMenu
      title={i18n.editHitPolicy}
      appendTo={boxedExpressionEditor.editorRef?.current ?? undefined}
      className="hit-policy-popover"
      hasAutoWidth
      body={
        <div className="hit-policy-container">
          <div className="hit-policy-section">
            <label>{i18n.hitPolicy}</label>
            <Select
              className="hit-policy-selector"
              menuAppendTo={boxedExpressionEditor.editorRef?.current ?? "inline"}
              ouiaId="hit-policy-selector"
              variant={SelectVariant.single}
              onToggle={onHitPolicySelectToggle}
              onSelect={hitPolicySelectionCallback}
              isOpen={hitPolicySelectOpen}
              selections={selectedHitPolicy}
            >
              {renderHitPolicyItems()}
            </Select>
          </div>
          <div className="builtin-aggregator-section">
            <label>{i18n.builtInAggregator}</label>
            <Select
              className="builtin-aggregator-selector"
              menuAppendTo={boxedExpressionEditor.editorRef?.current ?? "inline"}
              ouiaId="builtin-aggregator-selector"
              isDisabled={builtInAggregatorSelectDisabled}
              variant={SelectVariant.single}
              onToggle={onBuiltInAggregatorSelectToggle}
              onSelect={builtInAggregatorSelectionCallback}
              isOpen={builtInAggregatorSelectOpen}
              selections={
                getEnumKeyByEnumValue(DecisionTableExpressionDefinitionBuiltInAggregation, selectedBuiltInAggregator)!
              }
            >
              {renderBuiltInAggregationItems()}
            </Select>
          </div>
        </div>
      }
    >
      <div className="selected-hit-policy">{`${_.first(selectedHitPolicy)}${renderAggregator(
        selectedBuiltInAggregator,
        selectedHitPolicy
      )}`}</div>
    </PopoverMenu>
  );
};

export function getEnumKeyByEnumValue<T extends { [index: string]: string }>(
  myEnum: T,
  enumValue: string
): keyof T | null {
  const keys = Object.keys(myEnum).filter((x) => myEnum[x] == enumValue);
  return keys.length > 0 ? keys[0] : null;
}
