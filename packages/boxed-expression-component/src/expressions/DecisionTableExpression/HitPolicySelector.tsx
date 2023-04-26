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
import { useCallback, useMemo } from "react";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { MenuItemWithHelp } from "../../contextMenu/MenuWithHelp/MenuItemWithHelp";
import { Menu } from "@patternfly/react-core/dist/js/components/Menu/Menu";
import { MenuGroup } from "@patternfly/react-core/dist/js/components/Menu/MenuGroup";
import { MenuList } from "@patternfly/react-core/dist/js/components/Menu/MenuList";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

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

  const builtInAggregatorEnabled = useMemo(
    () => HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(selectedHitPolicy),
    [selectedHitPolicy]
  );

  const hitPolicyHelp = useCallback(
    (hitPolicyKey: DecisionTableExpressionDefinitionHitPolicy) => {
      switch (hitPolicyKey) {
        case DecisionTableExpressionDefinitionHitPolicy.Unique:
          return i18n.hitPolicyHelp.unique;
        case DecisionTableExpressionDefinitionHitPolicy.First:
          return i18n.hitPolicyHelp.first;
        case DecisionTableExpressionDefinitionHitPolicy.Priority:
          return i18n.hitPolicyHelp.priority;
        case DecisionTableExpressionDefinitionHitPolicy.Any:
          return i18n.hitPolicyHelp.any;
        case DecisionTableExpressionDefinitionHitPolicy.Collect:
          return i18n.hitPolicyHelp.collect;
        case DecisionTableExpressionDefinitionHitPolicy.RuleOrder:
          return i18n.hitPolicyHelp.ruleOrder;
        case DecisionTableExpressionDefinitionHitPolicy.OutputOrder:
          return i18n.hitPolicyHelp.outputOrder;
        default:
          return i18n.hitPolicyHelp.unique;
      }
    },
    [
      i18n.hitPolicyHelp.unique,
      i18n.hitPolicyHelp.first,
      i18n.hitPolicyHelp.priority,
      i18n.hitPolicyHelp.any,
      i18n.hitPolicyHelp.collect,
      i18n.hitPolicyHelp.ruleOrder,
      i18n.hitPolicyHelp.outputOrder,
    ]
  );

  const aggregatorHelp = useCallback(
    (aggregatorKey: DecisionTableExpressionDefinitionBuiltInAggregation) => {
      switch (aggregatorKey) {
        case DecisionTableExpressionDefinitionBuiltInAggregation.SUM:
          return i18n.builtInAggregatorHelp.sum;
        case DecisionTableExpressionDefinitionBuiltInAggregation.COUNT:
          return i18n.builtInAggregatorHelp.count;
        case DecisionTableExpressionDefinitionBuiltInAggregation.MIN:
          return i18n.builtInAggregatorHelp.min;
        case DecisionTableExpressionDefinitionBuiltInAggregation.MAX:
          return i18n.builtInAggregatorHelp.max;
        default:
          return i18n.builtInAggregatorHelp.none;
      }
    },
    [
      i18n.builtInAggregatorHelp.sum,
      i18n.builtInAggregatorHelp.count,
      i18n.builtInAggregatorHelp.min,
      i18n.builtInAggregatorHelp.max,
      i18n.builtInAggregatorHelp.none,
    ]
  );

  const hitPolicySelectionCallback = useCallback(
    (event: React.MouseEvent, itemId: string) => {
      event.stopPropagation();
      const updatedHitPolicy = itemId as DecisionTableExpressionDefinitionHitPolicy;
      onHitPolicySelected(updatedHitPolicy);
    },
    [onHitPolicySelected]
  );

  const builtInAggregatorSelectionCallback = useCallback(
    (event: React.MouseEvent, itemId: string | number) => {
      event.stopPropagation();
      onBuiltInAggregatorSelected(itemId as DecisionTableExpressionDefinitionBuiltInAggregation);
    },
    [onBuiltInAggregatorSelected]
  );

  const [visibleHelpHitPolicy, setVisibleHelpHitPolicy] = React.useState<string>("");
  const toggleVisibleHelpHitPolicy = useCallback((help: string) => {
    setVisibleHelpHitPolicy((previousHelp) => (previousHelp !== help ? help : ""));
  }, []);
  const [visibleHelpAggregatorFunction, setVisibleHelpAggregatorFunction] = React.useState<string>("");
  const toggleVisibleHelpAggregatorFunction = useCallback((help: string) => {
    setVisibleHelpAggregatorFunction((previousHelp) => (previousHelp !== help ? help : ""));
  }, []);

  return (
    <PopoverMenu
      onHide={() => {
        setVisibleHelpAggregatorFunction("");
        setVisibleHelpHitPolicy("");
      }}
      appendTo={editorRef.current ?? undefined}
      className="hit-policy-popover"
      hasAutoWidth={true}
      position={PopoverPosition.left}
      distance={25}
      body={
        <div className="hit-policy-flex-container">
          <div className="hit-policy-section">
            <Menu onSelect={hitPolicySelectionCallback} selected={selectedHitPolicy}>
              <MenuGroup className="menu-with-help" label="Hit policy">
                <MenuList>
                  <>
                    {_.map(Object.entries(DecisionTableExpressionDefinitionHitPolicy), ([hitPolicyKey, hitPolicy]) => (
                      <MenuItemWithHelp
                        key={hitPolicyKey}
                        menuItemKey={hitPolicy}
                        menuItemHelp={hitPolicyHelp(hitPolicy)}
                        setVisibleHelp={toggleVisibleHelpHitPolicy}
                        visibleHelp={visibleHelpHitPolicy}
                      />
                    ))}
                  </>
                </MenuList>
              </MenuGroup>
            </Menu>
          </div>

          {selectedHitPolicy === DecisionTableExpressionDefinitionHitPolicy.Collect && (
            <>
              <Divider isVertical={true} />
              <div className="hit-policy-aggregator-section">
                <Menu onSelect={builtInAggregatorSelectionCallback} selected={selectedBuiltInAggregator}>
                  <MenuGroup className="menu-with-help" label="Aggregator function">
                    <MenuList>
                      <>
                        {_.map(
                          Object.entries(DecisionTableExpressionDefinitionBuiltInAggregation),
                          ([aggregatorKey, aggregator]) => (
                            <MenuItemWithHelp
                              key={aggregatorKey}
                              menuItemKey={aggregator}
                              menuItemCustomText={aggregatorKey}
                              menuItemHelp={aggregatorHelp(aggregator)}
                              setVisibleHelp={toggleVisibleHelpAggregatorFunction}
                              visibleHelp={visibleHelpAggregatorFunction}
                            />
                          )
                        )}
                      </>
                    </MenuList>
                  </MenuGroup>
                </Menu>
              </div>
            </>
          )}
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
