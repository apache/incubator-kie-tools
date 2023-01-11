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
import { useCallback, useMemo, useState } from "react";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export interface HitPolicySelectorProps {
  /** Pre-selected hit policy */
  selectedHitPolicy: DecisionTableExpressionDefinitionHitPolicy;
  /** Pre-selected built-in aggregator */
  selectedBuiltInAggregator: DecisionTableExpressionDefinitionBuiltInAggregation;
  /** Callback invoked when hit policy selection changes */
  onHitPolicySelected: (hitPolicy: DecisionTableExpressionDefinitionHitPolicy) => void;
  /** Callback invoked when built-in aggregator selection changes */
  onBuiltInAggregatorSelected: (builtInAggregator: DecisionTableExpressionDefinitionBuiltInAggregation) => void;
}

export const HIT_POLICIES_THAT_SUPPORT_AGGREGATION = [DecisionTableExpressionDefinitionHitPolicy.Collect];

export function HitPolicySelector({
  onBuiltInAggregatorSelected,
  onHitPolicySelected,
  selectedBuiltInAggregator,
  selectedHitPolicy,
}: HitPolicySelectorProps) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { editorRef } = useBoxedExpressionEditor();

  const [hitPolicySelectOpen, setHitPolicySelectOpen] = useState(false);
  const [builtInAggregatorSelectOpen, setBuiltInAggregatorSelectOpen] = useState(false);

  const builtInAggregatorEnabled = useMemo(
    () => HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(selectedHitPolicy),
    [selectedHitPolicy]
  );

  const onHitPolicySelectToggle = useCallback((isOpen) => {
    return setHitPolicySelectOpen(isOpen);
  }, []);

  const onBuiltInAggregatorSelectToggle = useCallback((isOpen) => {
    return setBuiltInAggregatorSelectOpen(isOpen);
  }, []);

  const hitPolicySelectionCallback = useCallback(
    (event: React.MouseEvent, itemId: string) => {
      const updatedHitPolicy = itemId as DecisionTableExpressionDefinitionHitPolicy;
      onHitPolicySelected(updatedHitPolicy);
      setHitPolicySelectOpen(false);
    },
    [onHitPolicySelected]
  );

  const builtInAggregatorSelectionCallback = useCallback(
    (event: React.MouseEvent, itemId: string) => {
      onBuiltInAggregatorSelected(itemId as DecisionTableExpressionDefinitionBuiltInAggregation);
      setBuiltInAggregatorSelectOpen(false);
    },
    [onBuiltInAggregatorSelected]
  );

  return (
    <PopoverMenu
      appendTo={editorRef.current ?? undefined}
      className="hit-policy-popover"
      hasAutoWidth={true}
      position={PopoverPosition.left}
      distance={25}
      body={
        <div className="hit-policy-container">
          <div className="hit-policy-section">
            <label>{i18n.hitPolicy}</label>
            <Select
              className="hit-policy-selector"
              menuAppendTo={editorRef.current ?? "inline"}
              ouiaId="hit-policy-selector"
              variant={SelectVariant.single}
              onToggle={onHitPolicySelectToggle}
              onSelect={hitPolicySelectionCallback}
              isOpen={hitPolicySelectOpen}
              selections={selectedHitPolicy}
            >
              {_.map(Object.values(DecisionTableExpressionDefinitionHitPolicy), (key) => (
                <SelectOption key={key} value={key} data-ouia-component-id={key}>
                  {key}
                </SelectOption>
              ))}
            </Select>
          </div>
          <div className="builtin-aggregator-section">
            <label>{i18n.builtInAggregator}</label>
            <Select
              className="builtin-aggregator-selector"
              menuAppendTo={editorRef.current ?? "inline"}
              ouiaId="builtin-aggregator-selector"
              isDisabled={!builtInAggregatorEnabled}
              variant={SelectVariant.single}
              onToggle={onBuiltInAggregatorSelectToggle}
              onSelect={builtInAggregatorSelectionCallback}
              isOpen={builtInAggregatorSelectOpen}
              selections={selectedBuiltInAggregator}
            >
              {_.map(Object.keys(DecisionTableExpressionDefinitionBuiltInAggregation), (key) => (
                <SelectOption
                  key={key}
                  value={(DecisionTableExpressionDefinitionBuiltInAggregation as any)[key]}
                  data-ouia-component-id={key}
                >
                  {key}
                </SelectOption>
              ))}
            </Select>
          </div>
        </div>
      }
    >
      <div className="selected-hit-policy">
        {!builtInAggregatorEnabled && `${_.first(selectedHitPolicy)}`}
        {builtInAggregatorEnabled && `${_.first(selectedHitPolicy)}${selectedBuiltInAggregator}`}
      </div>
    </PopoverMenu>
  );
}
