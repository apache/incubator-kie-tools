/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { fireEvent, render, waitForElementToBeRemoved } from "@testing-library/react";
import { SingleEditorToolbar } from "../../../../app/components/single/SingleEditorToolbar";
import { usingTestingGlobalContext } from "../../../testing_utils";

beforeEach(() => {
  document.execCommand = () => true;
});

describe("SingleEditorToolbar", () => {
  test("see as diagram", () => {
    const onSeeAsSource = jest.fn();
    const onSeeAsDiagram = jest.fn();

    const component = render(
      <SingleEditorToolbar
        readonly={true}
        textMode={true}
        textModeEnabled={true}
        onFullScreen={undefined as any}
        onSeeAsSource={onSeeAsSource}
        onSeeAsDiagram={onSeeAsDiagram}
        onOpenInExternalEditor={undefined as any}
        linkToExternalEditor={"test.com/editor"}
      />
    );

    expect(component.asFragment()).toMatchSnapshot();

    fireEvent.click(component.getByTestId("see-as-diagram-button"));
    expect(onSeeAsDiagram).toHaveBeenCalled();
  });

  test("readonly false | textMode true | textModeEnabled true", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={false}
          textMode={true}
          textModeEnabled={true}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("readonly false | textMode false | textModeEnabled true", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={true}
          textMode={false}
          textModeEnabled={true}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("see as source", () => {
    const onSeeAsSource = jest.fn();
    const onSeeAsDiagram = jest.fn();

    const component = render(
      <SingleEditorToolbar
        readonly={false}
        textMode={false}
        textModeEnabled={true}
        onFullScreen={undefined as any}
        onSeeAsSource={onSeeAsSource}
        onSeeAsDiagram={onSeeAsDiagram}
        onOpenInExternalEditor={undefined as any}
        linkToExternalEditor={"test.com/editor"}
      />
    );

    fireEvent.click(component.getByTestId("see-as-source-button"));
    expect(onSeeAsSource).toHaveBeenCalled();

    expect(component.asFragment()).toMatchSnapshot();
  });

  test("readonly true | textMode true | textModeEnabled false", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={true}
          textMode={true}
          textModeEnabled={false}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("readonly false | textMode true | textModeEnabled false", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={false}
          textMode={true}
          textModeEnabled={false}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("readonly true | textMode false | textModeEnabled false", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={true}
          textMode={false}
          textModeEnabled={false}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("readonly false | textMode false | textModeEnabled false", () => {
    expect(
      render(
        <SingleEditorToolbar
          readonly={false}
          textMode={false}
          textModeEnabled={false}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).asFragment()
    ).toMatchSnapshot();
  });

  test("button actions", () => {
    const onFullScreen = jest.fn();
    const onSeeAsSource = jest.fn();
    const onSeeAsDiagram = jest.fn();
    const onOpenInExternalEditor = jest.fn();

    const component = render(
      usingTestingGlobalContext(
        <SingleEditorToolbar
          readonly={false}
          textMode={false}
          textModeEnabled={false}
          onFullScreen={onFullScreen}
          onSeeAsSource={onSeeAsSource}
          onSeeAsDiagram={onSeeAsDiagram}
          onOpenInExternalEditor={onOpenInExternalEditor}
          linkToExternalEditor={"test.com/editor"}
        />
      ).wrapper
    );

    fireEvent.click(component.getByTestId("go-fullscreen-button"));
    expect(onFullScreen).toHaveBeenCalled();

    fireEvent.click(component.getByTestId("open-ext-editor-button"));
    expect(onOpenInExternalEditor).toHaveBeenCalled();

    fireEvent.click(component.getByTestId("copy-link-button"));
    expect(component.asFragment()).toMatchSnapshot();
  });

  test("copied to clipboard message", async () => {
    const component = render(
      usingTestingGlobalContext(
        <SingleEditorToolbar
          readonly={false}
          textMode={false}
          textModeEnabled={false}
          onFullScreen={undefined as any}
          onSeeAsSource={undefined as any}
          onSeeAsDiagram={undefined as any}
          onOpenInExternalEditor={undefined as any}
          linkToExternalEditor={"test.com/editor"}
        />
      ).wrapper
    );

    fireEvent.click(component.getByTestId("copy-link-button"));
    await waitForElementToBeRemoved(() => component.queryByTestId("link-copied-alert"));
    expect(component.asFragment()).toMatchSnapshot();
  });
});
