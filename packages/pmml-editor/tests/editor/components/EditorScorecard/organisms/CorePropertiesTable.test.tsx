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
import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { CoreProperties, CorePropertiesTable } from "../../../../../src/editor/components/EditorScorecard/organisms";
import { Operation, OperationContext } from "../../../../../src/editor/components/EditorScorecard";

const commit = jest.fn(() => {
  /*NOP*/
});

beforeEach(() => {
  commit.mockReset();
});

describe("CorePropertiesTable", () => {
  test("render::Editing", () => {
    const component = (
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {
            /*NOP*/
          },
        }}
      >
        <CorePropertiesTable
          modelIndex={0}
          isScorable={true}
          functionName={"regression"}
          algorithmName={"algorithmName"}
          baselineScore={1}
          isBaselineScoreDisabled={false}
          baselineMethod={"other"}
          initialScore={2}
          areReasonCodesUsed={true}
          reasonCodeAlgorithm={"pointsBelow"}
          commit={commit}
        />
      </OperationContext.Provider>
    );

    const { getByTestId, rerender } = render(component);
    const container = getByTestId("core-properties-table");
    expect(container.children[0].className.toString()).toContain("editable-item");
    expect(container).toMatchSnapshot();

    container.click();
    rerender(component);
    const containerEditing = getByTestId("core-properties-table");
    expect(containerEditing.children[0].className.toString()).toContain("editable-item--editing");
    expect(containerEditing).toMatchSnapshot();
  });

  test("render::Not editing", async () => {
    const component = (
      <CorePropertiesTable
        modelIndex={0}
        isScorable={true}
        functionName={"regression"}
        algorithmName={"algorithmName"}
        baselineScore={1}
        isBaselineScoreDisabled={false}
        baselineMethod={"other"}
        initialScore={2}
        areReasonCodesUsed={true}
        reasonCodeAlgorithm={"pointsBelow"}
        commit={commit}
      />
    );

    const container = render(component).getByTestId("core-properties-table");
    expect(container.children[0].className.toString()).toContain("editable-item");
    expect(container).toMatchSnapshot();
  });

  test("isScorable::clickable", () => {
    const component = (
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {
            /*NOP*/
          },
        }}
      >
        <CorePropertiesTable
          modelIndex={0}
          isScorable={true}
          functionName={"regression"}
          algorithmName={"algorithmName"}
          baselineScore={1}
          isBaselineScoreDisabled={false}
          baselineMethod={"other"}
          initialScore={2}
          areReasonCodesUsed={true}
          reasonCodeAlgorithm={"pointsBelow"}
          commit={commit}
        />
      </OperationContext.Provider>
    );

    const { getByTestId, rerender } = render(component);
    const container = getByTestId("core-properties-table");

    container.click();
    rerender(component);

    const isScorable = getByTestId("core-properties-table-isScorable");
    isScorable.click();

    expect(commit).toBeCalledTimes(1);
    const args: CoreProperties[] = commit.mock.calls[0];
    expect(args.length).toEqual(1);
    expect(args[0].isScorable).toBeFalsy();
  });

  test("useReasonCodes::clickable", () => {
    const component = (
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {
            /*NOP*/
          },
        }}
      >
        <CorePropertiesTable
          modelIndex={0}
          isScorable={true}
          functionName={"regression"}
          algorithmName={"algorithmName"}
          baselineScore={1}
          isBaselineScoreDisabled={false}
          baselineMethod={"other"}
          initialScore={2}
          areReasonCodesUsed={true}
          reasonCodeAlgorithm={"pointsBelow"}
          commit={commit}
        />
      </OperationContext.Provider>
    );

    const { getByTestId, rerender } = render(component);
    const container = getByTestId("core-properties-table");

    container.click();
    rerender(component);

    const useReasonCodes = getByTestId("core-properties-table-useReasonCodes");
    useReasonCodes.click();

    expect(commit).toBeCalledTimes(1);
    const args: CoreProperties[] = commit.mock.calls[0];
    expect(args.length).toEqual(1);
    expect(args[0].areReasonCodesUsed).toBeFalsy();
  });

  test("algorithmName::empty value", () => {
    const component = (
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {
            /*NOP*/
          },
        }}
      >
        <CorePropertiesTable
          modelIndex={0}
          isScorable={true}
          functionName={"regression"}
          algorithmName={"algorithmName"}
          baselineScore={1}
          isBaselineScoreDisabled={false}
          baselineMethod={"other"}
          initialScore={2}
          areReasonCodesUsed={true}
          reasonCodeAlgorithm={"pointsBelow"}
          commit={commit}
        />
      </OperationContext.Provider>
    );

    const { getByTestId, rerender } = render(component);
    const container = getByTestId("core-properties-table");

    container.click();
    rerender(component);

    const algorithmName = getByTestId("core-properties-table-algorithmName");
    fireEvent.change(algorithmName, { target: { value: "" } });
    fireEvent.blur(algorithmName);

    expect(commit).toBeCalledTimes(1);
    const args: CoreProperties[] = commit.mock.calls[0];
    expect(args.length).toEqual(1);
    expect(args[0].algorithmName).toBeUndefined();
  });
});
