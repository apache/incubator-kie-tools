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
import MonacoTextField from "../src/MonacoTextField";
import { render, screen } from "@testing-library/react";
import { usingUniformsContext } from "./test-utils";
import { Language } from "@patternfly/react-code-editor";

describe("<MonacoTextField>", () => {
  test("<MonacoTextField> - renders correctly", () => {
    render(
      usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, {
        x: { type: String, uniforms: { language: "json" } },
      })
    );

    expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  });

  test("<MonacoTextField> - renders an editor with correct disabled state", () => {
    render(
      usingUniformsContext(<MonacoTextField name="x" disabled language={Language.json} />, {
        x: { type: String, uniforms: { language: "json" } },
      })
    );

    expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
    expect(screen.getByTestId("code-editor-textarea").getAttribute("readOnly")).toBe("");
  });

  test("<MonacoTextField> - renders with label", () => {
    render(
      usingUniformsContext(<MonacoTextField name="x" label="Test Label" language={Language.json} />, {
        x: { type: String, uniforms: { language: "json" } },
      })
    );

    expect(screen.getByText("Test Label")).toBeInTheDocument();
  });

  test("<MonacoTextField> - renders a editor with correct value (default)", () => {
    render(
      usingUniformsContext(<MonacoTextField name="x" language={Language.json} value="y" />, {
        x: { type: String, uniforms: { language: "json" } },
      })
    );

    expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
    expect(screen.getByText("y")).toBeInTheDocument();
  });

  // test("<MonacoTextField> - renders a editor with correct value (model)", () => {
  //   render(usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { model: { x: "y" } }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   expect(screen.getByText("y")).toBeInTheDocument();
  // });
  //
  // test("<MonacoTextField> - renders a editor with correct value (specified)", () => {
  //   render(usingUniformsContext(<MonacoTextField name="x" value="y" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   expect(screen.getByText("y")).toBeInTheDocument();
  // });
  //
  // test("<MonacoTextField> - renders a editor which correctly reacts on change", () => {
  //   const onChange = jest.fn();
  //
  //   render(usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { onChange }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   const editor = screen.getByTestId("monaco-field").getElementsByTagName("editor")[0];
  //   fireEvent.change(editor, { target: { value: "y" } });
  //   expect(onChange).toHaveBeenLastCalledWith("x", "y");
  // });
  //
  // test("<MonacoTextField> - renders a editor which correctly reacts on change (empty)", () => {
  //   const onChange = jest.fn();
  //
  //   render(usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { onChange }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   const editor = screen.getByTestId("monaco-field").getElementsByTagName("editor")[0];
  //   fireEvent.change(editor, { target: { value: "" } });
  //   expect(onChange).not.toHaveBeenCalled();
  // });
  //
  // test("<MonacoTextField> - renders a editor which correctly reacts on change (same value)", () => {
  //   const onChange = jest.fn();
  //
  //   render(usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { model: { x: "y" }, onChange }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   const editor = screen.getByTestId("monaco-field").getElementsByTagName("editor")[0];
  //   fireEvent.change(editor, { target: { value: "y" } });
  //   expect(screen.getByText("y")).toBeInTheDocument();
  //   expect(onChange).not.toHaveBeenCalled();
  // });
  //
  // test("<MonacoTextField> - renders a label", () => {
  //   render(usingUniformsContext(<MonacoTextField name="x" label="y" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }));
  //
  //   expect(screen.getByTestId("monaco-field")).toBeInTheDocument();
  //   expect(screen.getByText("y")).toBeInTheDocument();
  // });

  // ----------------------------------------------------------------------------------------------------
  //
  // test("<MonacoTextField> - renders with initial value", () => {
  //   render(
  //     usingUniformsContext(<MonacoTextField name="x" value="y" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { model: { x: '{"foo":"bar"}' } })
  //   );
  //
  //   const editor = screen.getByTestId("monaco-editor");
  //   expect(editor.getAttribute("value")).toBe(null);
  // });
  //
  // test("<MonacoTextField> - calls onChange on value change", () => {
  //   const onChange = jest.fn();
  //
  //   render(
  //     usingUniformsContext(<MonacoTextField name="x" language={Language.json} />, { x: { type: String, uniforms:{language: "json"} } }, { onChange })
  //   );
  //
  //   const editor = screen.getByTestId("monaco-editor");
  //   expect(editor).toBeInTheDocument();
  //
  //   const editorInstance = editor.create.mock.results[0].value;
  //   editorInstance.setValue('{"updated":true}');
  //
  //   expect(onChange).toHaveBeenCalled();
  //   expect(onChange).toHaveBeenCalledWith("x", '{"updated":true}');
  // });
});
