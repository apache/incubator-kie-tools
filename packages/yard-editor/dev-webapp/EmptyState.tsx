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
import { ChangeEvent } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
  EmptyStateVariant,
} from "@patternfly/react-core";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";

interface NewProps {
  newContent: (type: string) => void;
}

interface UploadProps {
  setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) => void;
}

interface AllProps extends NewProps, UploadProps {}

export const YardEmptyState = (props: AllProps) => (
  <EmptyState variant={EmptyStateVariant.sm}>
    <EmptyStateIcon icon={CubesIcon} />
    <Title headingLevel="h4" size="lg">
      No YARD document
    </Title>
    <EmptyStateBody>
      A YARD Document is required to enable the editor. Please either upload an existing document or create a new one or
      select a sample.
    </EmptyStateBody>
    <FileChooser setContent={props.setContent} />
    <EmptyStateActions>
      <Button variant="link" onClick={(e) => props.newContent("yaml")} ouiaId="new-button">
        New YAML
      </Button>
      <Button variant="link" ouiaId="new-button" isDisabled={true}>
        Try YAML sample (not yet available)
      </Button>
    </EmptyStateActions>
  </EmptyState>
);

const FileChooser = (props: UploadProps) => {
  const showFile = async (e1: ChangeEvent<HTMLInputElement>) => {
    e1.preventDefault();
    const reader = new FileReader();
    const files: FileList | null = e1.target.files;
    if (files !== null) {
      const file: File = files[0];

      reader.onload = async (e2: ProgressEvent<FileReader>) => {
        const text: string | ArrayBuffer | null | undefined = e2.target?.result;
        if (text) {
          props.setContent(file.name, text?.toString() as string);
        }
      };

      reader.readAsText(file);
    }
  };

  return (
    <div style={{ marginTop: "var(--pf-c-empty-state__primary--MarginTop)" }}>
      <label htmlFor="file-upload" className="pf-c-button pf-m-primary" data-ouia-component-id="upload-button">
        <i className="fa fa-cloud-upload" />
        Upload
      </label>
      <input
        id="file-upload"
        style={{ display: "none" }}
        type="file"
        accept={"yaml,yml"}
        onChange={(e) => showFile(e)}
      />
    </div>
  );
};
