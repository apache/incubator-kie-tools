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

import { render } from "@testing-library/react";
import { flushPromises, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { HitPolicySelector } from "../../../components/DecisionTableExpression";
import { BuiltinAggregation, HitPolicy } from "../../../api";
import * as React from "react";
import * as _ from "lodash";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();

describe("HitPolicySelector tests", () => {
  test("should show passed hit policy without aggregation, when hit policy is collect, but aggregation is none", () => {
    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.Collect}
          selectedBuiltInAggregator={BuiltinAggregation["<None>"]}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );
    expect(baseElement.querySelector(".selected-hit-policy")).toBeTruthy();
    expect(baseElement.querySelector(".selected-hit-policy")).toHaveTextContent("C");
  });

  test("should show passed hit policy plus aggregation, when hit policy is collect and aggregation is not none", () => {
    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.Collect}
          selectedBuiltInAggregator={BuiltinAggregation.COUNT}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );
    expect(baseElement.querySelector(".selected-hit-policy")).toBeTruthy();
    expect(baseElement.querySelector(".selected-hit-policy")).toHaveTextContent("C#");
  });

  test("should show passed hit policy without aggregation, when aggregation is not collect", () => {
    const { baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.Any}
          selectedBuiltInAggregator={BuiltinAggregation.COUNT}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );
    expect(baseElement.querySelector(".selected-hit-policy")).toBeTruthy();
    expect(baseElement.querySelector(".selected-hit-policy")).toHaveTextContent("A");
  });

  test("should allow aggregator selection, when hit policy is collect", async () => {
    const { baseElement, container } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.Collect}
          selectedBuiltInAggregator={BuiltinAggregation.COUNT}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );

    await activateHitPolicyPopover(container);

    expect(baseElement.querySelector(".builtin-aggregator-selector button")).not.toHaveAttribute("disabled");
  });

  test("should avoid aggregator selection, when hit policy is not collect", async () => {
    const { baseElement, container } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.First}
          selectedBuiltInAggregator={BuiltinAggregation["<None>"]}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );

    await activateHitPolicyPopover(container);

    expect(baseElement.querySelector(".builtin-aggregator-selector button")).toHaveAttribute("disabled");
  });

  test("should trigger a callback, when hit policy is selected", async () => {
    const mockedHitPolicySelect = jest.fn();
    const changedHitPolicy = HitPolicy.Any;

    const { baseElement, container } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.First}
          selectedBuiltInAggregator={BuiltinAggregation["<None>"]}
          onHitPolicySelect={mockedHitPolicySelect}
          onBuiltInAggregatorSelect={_.identity}
        />
      ).wrapper
    );

    await activateHitPolicyPopover(container);
    await openDropdown(baseElement, "hit-policy-selector");
    await changeDropdownSelection(baseElement, changedHitPolicy);

    expect(mockedHitPolicySelect).toHaveBeenCalledWith(changedHitPolicy);
  });

  test("should trigger a callback, when aggregation is selected", async () => {
    const mockedAggregationSelect = jest.fn();
    const changedAggregation = "MAX" as BuiltinAggregation;

    const { baseElement, container } = render(
      usingTestingBoxedExpressionI18nContext(
        <HitPolicySelector
          selectedHitPolicy={HitPolicy.Collect}
          selectedBuiltInAggregator={BuiltinAggregation["<None>"]}
          onHitPolicySelect={_.identity}
          onBuiltInAggregatorSelect={mockedAggregationSelect}
        />
      ).wrapper
    );

    await activateHitPolicyPopover(container);
    await openDropdown(baseElement, "builtin-aggregator-selector");
    await changeDropdownSelection(baseElement, changedAggregation);

    expect(mockedAggregationSelect).toHaveBeenCalledWith(changedAggregation);
  });

  async function activateHitPolicyPopover(container: Element) {
    await act(async () => {
      (container.querySelector(".selected-hit-policy") as HTMLDivElement).click();
      await flushPromises();
      jest.runAllTimers();
    });
  }

  async function openDropdown(baseElement: Element, dropdownClass: string) {
    await act(async () => {
      (baseElement.querySelector(`.${dropdownClass} button`) as HTMLButtonElement).click();
      await flushPromises();
      jest.runAllTimers();
    });
  }

  async function changeDropdownSelection(baseElement: Element, selection: HitPolicy | BuiltinAggregation) {
    await act(async () => {
      (baseElement.querySelector(`[data-ouia-component-id='${selection}']`) as HTMLButtonElement).click();
      await flushPromises();
      jest.runAllTimers();
    });
  }
});
