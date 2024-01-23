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
import { useCallback, useMemo, useRef, useState } from "react";
import { DescriptionField, LabelField } from "./Fields";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import {
  DMN15__tBuiltinAggregator,
  DMN15__tHitPolicy,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useInViewSelect } from "../../responsiveness/useInViewSelect";

/**
 * Pick<DMN15__tDecisionTable, "@_aggregation" | "@_hitPolicy" | "@_label" | "@_outputLabel" | "description">
 */
export function DecisionTableRootCell({
  onChangeAggregation,
  onChangeHitPolicy,
  ...props
}: {
  label: string;
  description: string;
  outputLabel: string;
  isReadonly: boolean;
  expressionPath: ExpressionPath[];
  dmnEditorRootElementRef: React.RefObject<HTMLElement>;
  onChangeAggregation: (newAggregation: DMN15__tBuiltinAggregator) => void;
  onChangeHitPolicy: (newHitPolicy: DMN15__tHitPolicy) => void;
  onChangeLabel: (newLabel: string) => void;
  onChangeDescription: (newDescription: string) => void;
  onChangeOutputLabel: (newLabel: string) => void;
}) {
  const aggregationRef = useRef<HTMLButtonElement>(null);
  const [aggregation, setAggregation] = useState<DMN15__tBuiltinAggregator | undefined>(undefined);
  const [isAggregationOpen, setAggregationOpen] = useState<boolean>(false);
  const aggregationInViewTimezoneSelect = useInViewSelect(props.dmnEditorRootElementRef, aggregationRef);
  const aggregationOptions = useMemo(() => ["COUNT", "MAX", "MIN", "SUM"] as Array<DMN15__tBuiltinAggregator>, []);
  const onInternalChangeAggregation = useCallback(
    (value: DMN15__tBuiltinAggregator) => {
      setAggregation(value);
      onChangeAggregation(value);
    },
    [onChangeAggregation]
  );

  const hitPolicyRef = useRef<HTMLButtonElement>(null);
  const [hitPolicy, setHitPolicy] = useState<DMN15__tHitPolicy | undefined>(undefined);
  const [isHitPolicyOpen, setHitPolicyOpen] = useState<boolean>(false);
  const hitPolicyInViewTimezoneSelect = useInViewSelect(props.dmnEditorRootElementRef, hitPolicyRef);
  const hitPolicyOptions = useMemo(
    () => ["ANY", "COLLECT", "FIRST", "OUTPUT ORDER", "PRIORITY", "RULE ORDER", "UNIQUE"] as Array<DMN15__tHitPolicy>,
    []
  );
  const onInternalChangeHitPolicy = useCallback(
    (value: DMN15__tHitPolicy) => {
      setHitPolicy(value);
      onChangeHitPolicy(value);
    },
    [onChangeHitPolicy]
  );

  return (
    <>
      <FormGroup label="Aggregation">
        <Select
          toggleRef={aggregationRef}
          variant={SelectVariant.single}
          placeholderText="Aggregation"
          aria-label="Select aggregation function"
          onToggle={(isExpanded) => setAggregationOpen(isExpanded)}
          onSelect={(e, value) => onInternalChangeAggregation(value.toString() as DMN15__tBuiltinAggregator)}
          selections={aggregation}
          isOpen={isAggregationOpen}
          isDisabled={false}
          isPlain={true}
          maxHeight={aggregationInViewTimezoneSelect.maxHeight}
          direction={aggregationInViewTimezoneSelect.direction}
        >
          {aggregationOptions.map((timezone) => (
            <SelectOption key={timezone} value={timezone} />
          ))}
        </Select>
      </FormGroup>
      <FormGroup label="Hit Policy">
        <Select
          toggleRef={hitPolicyRef}
          variant={SelectVariant.single}
          placeholderText="Hit Policy"
          aria-label="Select hit policy"
          onToggle={(isExpanded) => setHitPolicyOpen(isExpanded)}
          onSelect={(e, value) => onInternalChangeHitPolicy(value.toString() as DMN15__tHitPolicy)}
          selections={hitPolicy}
          isOpen={isHitPolicyOpen}
          isDisabled={false}
          isPlain={true}
          maxHeight={hitPolicyInViewTimezoneSelect.maxHeight}
          direction={hitPolicyInViewTimezoneSelect.direction}
        >
          {hitPolicyOptions.map((timezone) => (
            <SelectOption key={timezone} value={timezone} />
          ))}
        </Select>
      </FormGroup>
      <FormGroup label="Output Label">
        <TextInput value={props.outputLabel} onChange={props.onChangeOutputLabel}></TextInput>
      </FormGroup>
      <LabelField isReadonly={props.isReadonly} label={props.label} onChange={props.onChangeLabel} />
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={props.description}
        expressionPath={props.expressionPath}
        onChange={props.onChangeDescription}
      />
    </>
  );
}
