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
import { useCallback, useMemo } from "react";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { MenuItemWithHelp } from "../../contextMenu/MenuWithHelp";
import { Menu } from "@patternfly/react-core/dist/js/components/Menu/Menu";
import { MenuGroup } from "@patternfly/react-core/dist/js/components/Menu/MenuGroup";
import { MenuList } from "@patternfly/react-core/dist/js/components/Menu/MenuList";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DMN15__tHitPolicy } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./HitPolicySelector.css";

export interface HitPolicySelectorProps {
  /** Pre-selected hit policy */
  selectedHitPolicy: DMN15__tHitPolicy;
  /** Pre-selected built-in aggregator */
  selectedBuiltInAggregator: string;
  /** Callback invoked when hit policy selection changes */
  onHitPolicySelected: (hitPolicy: string) => void;
  /** Callback invoked when built-in aggregator selection changes */
  onBuiltInAggregatorSelected: (builtInAggregator: string) => void;
  /** If the hit policy is readonly or not*/
  isReadOnly: boolean;
}

export const HIT_POLICIES_THAT_SUPPORT_AGGREGATION = ["COLLECT"];

export function HitPolicySelector({
  onBuiltInAggregatorSelected,
  onHitPolicySelected,
  selectedBuiltInAggregator,
  selectedHitPolicy,
  isReadOnly,
}: HitPolicySelectorProps) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { editorRef } = useBoxedExpressionEditor();

  const builtInAggregatorEnabled = useMemo(
    () => HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(selectedHitPolicy),
    [selectedHitPolicy]
  );

  const hitPolicyHelp = useCallback(
    (hitPolicyKey: string) => {
      switch (hitPolicyKey) {
        case "UNIQUE":
          return i18n.hitPolicyHelp.unique;
        case "FIRST":
          return i18n.hitPolicyHelp.first;
        case "PRIORITY":
          return i18n.hitPolicyHelp.priority;
        case "ANY":
          return i18n.hitPolicyHelp.any;
        case "COLLECT":
          return i18n.hitPolicyHelp.collect;
        case "RULE ORDER":
          return i18n.hitPolicyHelp.ruleOrder;
        case "OUTPUT ORDER":
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
    (aggregatorKey: string) => {
      switch (aggregatorKey) {
        case "+":
          return i18n.builtInAggregatorHelp.sum;
        case "#":
          return i18n.builtInAggregatorHelp.count;
        case "<":
          return i18n.builtInAggregatorHelp.min;
        case ">":
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
      onHitPolicySelected(itemId);
    },
    [onHitPolicySelected]
  );

  const builtInAggregatorSelectionCallback = useCallback(
    (event: React.MouseEvent, itemId: string | number) => {
      event.stopPropagation();
      onBuiltInAggregatorSelected(itemId as string);
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

  const hitPolicyCell = useMemo(() => {
    return (
      <div className="selected-hit-policy" data-testid="kie-tools--bee--selected-hit-policy">
        {!builtInAggregatorEnabled && `${_.first(selectedHitPolicy)}`}
        {builtInAggregatorEnabled && `${_.first(selectedHitPolicy)}${selectedBuiltInAggregator}`}
      </div>
    );
  }, [builtInAggregatorEnabled, selectedBuiltInAggregator, selectedHitPolicy]);

  return isReadOnly ? (
    hitPolicyCell
  ) : (
    <PopoverMenu
      onHidden={() => {
        setVisibleHelpAggregatorFunction("");
        setVisibleHelpHitPolicy("");
      }}
      appendTo={editorRef.current ?? undefined}
      className="hit-policy-popover"
      hasAutoWidth={true}
      position={PopoverPosition.left}
      distance={25}
      body={
        <div className="hit-policy-flex-container" data-testid={"kie-tools--bee--hit-policy-header"}>
          <div className="hit-policy-section">
            <Menu onSelect={hitPolicySelectionCallback} selected={selectedHitPolicy}>
              <MenuGroup className="menu-with-help" label="Hit policy">
                <MenuList>
                  <>
                    {["UNIQUE", "FIRST", "PRIORITY", "ANY", "COLLECT", "RULE ORDER", "OUTPUT ORDER"].map(
                      (hitPolicy) => {
                        return (
                          <MenuItemWithHelp
                            key={hitPolicy}
                            menuItemKey={hitPolicy}
                            menuItemHelp={hitPolicyHelp(hitPolicy)}
                            setVisibleHelp={toggleVisibleHelpHitPolicy}
                            visibleHelp={visibleHelpHitPolicy}
                          />
                        );
                      }
                    )}
                  </>
                </MenuList>
              </MenuGroup>
            </Menu>
          </div>

          {selectedHitPolicy === "COLLECT" && (
            <>
              <Divider orientation={{ default: "vertical" }} />
              <div className="hit-policy-aggregator-section">
                <Menu onSelect={builtInAggregatorSelectionCallback} selected={selectedBuiltInAggregator}>
                  <MenuGroup className="menu-with-help" label="Aggregator function">
                    <MenuList>
                      <>
                        {[
                          ["<None>", "?"],
                          ["SUM", "+"],
                          ["COUNT", "#"],
                          ["MIN", "<"],
                          ["MAX", ">"],
                        ].map((agg) => (
                          <MenuItemWithHelp
                            key={agg[0]}
                            menuItemKey={agg[1]}
                            menuItemCustomText={agg[0]}
                            menuItemHelp={aggregatorHelp(agg[1])}
                            setVisibleHelp={toggleVisibleHelpAggregatorFunction}
                            visibleHelp={visibleHelpAggregatorFunction}
                          />
                        ))}
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
      {hitPolicyCell}
    </PopoverMenu>
  );
}
