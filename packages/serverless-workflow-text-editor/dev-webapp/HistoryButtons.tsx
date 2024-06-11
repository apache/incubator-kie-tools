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

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import * as React from "react";
import { useState } from "react";
import { EditorTheme } from "../../editor/dist/api";
import "./HistoryButtons.scss";

interface HistoryButtonsProps {
  undo: () => Promise<void>;
  redo: () => Promise<void>;
  download: () => Promise<void>;
  setTheme: (theme: EditorTheme) => Promise<void>;
  validate: () => Promise<void>;
  isDirty: boolean;
}

export const HistoryButtons = (props: HistoryButtonsProps) => {
  const [theme, setTheme] = useState<EditorTheme>(EditorTheme.LIGHT);

  return (
    <div className="history-buttons ignore-onclickoutside">
      <Split hasGutter={true}>
        <SplitItem>
          <Button variant="primary" onClick={props.undo} ouiaId="undo-button">
            Undo
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.redo} ouiaId="redo-button">
            Redo
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.validate} ouiaId="validate-button">
            Validate
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={props.download} ouiaId="redo-button">
            Download
          </Button>
        </SplitItem>
        <SplitItem className="history-buttons__theme-switch">
          <Switch
            id="theme"
            label="Dark"
            labelOff="Light"
            checked={theme === EditorTheme.DARK}
            onChange={(checked) => {
              setTheme(checked ? EditorTheme.DARK : EditorTheme.LIGHT);
              props.setTheme(checked ? EditorTheme.DARK : EditorTheme.LIGHT);
            }}
          />
        </SplitItem>
        {props.isDirty && (
          <SplitItem className="history-buttons__edited-indicator">
            <TextContent>
              <Text component={"small"}>{"(Edited)"}</Text>
            </TextContent>
          </SplitItem>
        )}
      </Split>
      <hr className="history-buttons__divider" />
    </div>
  );
};
