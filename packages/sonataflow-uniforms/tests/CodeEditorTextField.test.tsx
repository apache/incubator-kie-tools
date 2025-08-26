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
import CodeEditorTextField from "../src/CodeEditorTextField";
import { fireEvent, render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";
import { Language } from "@patternfly/react-code-editor/dist/js/components/CodeEditor";

const jsonMock = { first_name: "John", last_name: "Doe" };
const jsonMockStringified = JSON.stringify(jsonMock);
const htmlMock = "<html></html>";
jest.mock("@patternfly/react-code-editor", () => require("./__mocks__/react-code-editor"));

test("<CodeEditorTextField> - renders an editor", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
});

test("<CodeEditorTextField> - renders an editor with correct disabled state", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" disabled language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByTestId("code-editor-textarea").getAttribute("readOnly")).not.toBe(null);
});

test("<CodeEditorTextField> - renders an editor with correct value (default)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  const editor = screen.getByTestId("code-editor-textarea") as HTMLTextAreaElement;
  expect(editor.value).toBe("{}");
});

test("<CodeEditorTextField> - renders an editor with correct value (model)", () => {
  render(
    usingUniformsContext(
      <CodeEditorTextField name="x" language={Language.json} />,
      { x: { type: Object } },
      { model: { x: jsonMock } }
    )
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByText(jsonMockStringified)).toBeInTheDocument();
});

test("<CodeEditorTextField> - renders an editor with correct value (specified)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.json} value={jsonMock} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByText(jsonMockStringified)).toBeInTheDocument();
});

test("<CodeEditorTextField> - renders an html editor with correct value (specified)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.html} value={htmlMock} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByText(htmlMock)).toBeInTheDocument();
});

test("<CodeEditorTextField> - renders an editor which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(
    usingUniformsContext(
      <CodeEditorTextField name="x" language={Language.json} />,
      { x: { type: Object } },
      { onChange }
    )
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(onChange).toHaveBeenLastCalledWith("x", {});
  const editor = screen.getByTestId("code-editor-textarea");
  fireEvent.change(editor, { target: { value: jsonMockStringified } });
  expect(onChange).toHaveBeenLastCalledWith("x", jsonMock);
});

test("<CodeEditorTextField> - renders an html editor which correctly reacts on change", () => {
  const onChange = jest.fn();

  render(
    usingUniformsContext(
      <CodeEditorTextField name="x" language={Language.html} />,
      { x: { type: Object } },
      { onChange }
    )
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  const editor = screen.getByTestId("code-editor-textarea");
  fireEvent.change(editor, { target: { value: htmlMock } });
  expect(onChange).toHaveBeenLastCalledWith("x", htmlMock);
});

test("<CodeEditorTextField> - renders an editor which correctly reacts on change (same value)", () => {
  const onChange = jest.fn();

  render(
    usingUniformsContext(
      <CodeEditorTextField name="x" language={Language.json} />,
      { x: { type: Object } },
      { model: { x: jsonMock }, onChange }
    )
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  const editor = screen.getByTestId("code-editor-textarea");
  fireEvent.change(editor, { target: { value: jsonMock } });
  expect(screen.getByText(jsonMockStringified)).toBeInTheDocument();
  expect(onChange).not.toHaveBeenCalled();
});

test("<CodeEditorTextField> - renders a label", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" label="y" language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByText("y")).toBeInTheDocument();
});

test("<CodeEditorTextField> - renders an editor with correct height (default)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByTestId("code-editor-textarea").style.height).toBe("200px");
});

test("<CodeEditorTextField> - renders an editor with correct height (specified)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" height="300px" language={Language.json} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByTestId("code-editor-textarea").style.height).toBe("300px");
});

test("<CodeEditorTextField> - renders an editor with correct language (default)", () => {
  render(usingUniformsContext(<CodeEditorTextField name="x" />, { x: { type: Object } }));

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByTestId("code-editor-textarea").getAttribute("data-language")).toBe("json");
});

test("<CodeEditorTextField> - renders an editor with correct language (specified)", () => {
  render(
    usingUniformsContext(<CodeEditorTextField name="x" language={Language.html} />, {
      x: { type: Object },
    })
  );

  expect(screen.getByTestId("code-editor-textarea")).toBeInTheDocument();
  expect(screen.getByTestId("code-editor-textarea").getAttribute("data-language")).toBe("html");
});
