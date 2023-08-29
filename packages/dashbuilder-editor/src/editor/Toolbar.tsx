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
import { useEffect, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/";

import "./Toolbar.scss";

import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";

interface ToolbarProps {
  onPreviewChange: (v: boolean) => void;
  preview: boolean;
}

export const Toolbar = (props: ToolbarProps) => {
  const [previewButtonSelected, setPreviewButtonSelected] = useState(props.preview);

  useEffect(() => props.onPreviewChange(previewButtonSelected), [props, previewButtonSelected]);
  return (
    <div className="toolbar ignore-onclickoutside">
      <Split hasGutter={true}>
        <SplitItem>
          <Button
            variant="plain"
            style={{ width: "auto", padding: "0px", textAlign: "left" }}
            onClick={(e) => {
              setPreviewButtonSelected(!previewButtonSelected);
            }}
            ouiaId="preview-button"
            className={previewButtonSelected ? "selected" : ""}
          >
            &#128065;
          </Button>
        </SplitItem>
      </Split>
    </div>
  );
};
