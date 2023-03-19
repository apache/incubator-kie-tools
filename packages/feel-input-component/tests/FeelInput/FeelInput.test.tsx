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
import * as React from "react";
import { RefObject } from "react";
import { FeelInput, FeelInputRef, FeelEditorService } from "@kie-tools/feel-input-component";

describe("FeelInput", () => {
  describe("when it's not enabled", () => {
    it("should render an empty component", () => {
      const { container } = render(<FeelInput enabled={false} />);
      expect(container).toMatchSnapshot();
    });
  });

  describe("when it's enabled", () => {
    it("should render the FEEL input component", () => {
      const { container } = render(<FeelInput enabled={true} />);
      expect(container).toMatchSnapshot();
    });

    it("should insert a newline", () => {
      const feelInputRef = { current: {} } as RefObject<FeelInputRef>;
      const editorTrigger = jest.fn();
      FeelEditorService.getStandaloneEditor = jest.fn().mockReturnValue({ trigger: editorTrigger });
      render(<FeelInput ref={feelInputRef} enabled={true} />);
      feelInputRef.current?.insertNewLineToMonaco();
      expect(editorTrigger).toHaveBeenCalledWith("keyboard", "type", { text: "\n" });
    });
  });
});
