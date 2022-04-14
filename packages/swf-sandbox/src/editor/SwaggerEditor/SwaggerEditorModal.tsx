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

import * as React from "react";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useEffect, useRef, useState } from "react";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";

export function SwaggerEditorModal(props: {
  isOpen: boolean;
  onClose: () => void;
  workspaceFile: WorkspaceFile;
  workspaceName: string;
}) {
  const [openApiSwaggerFile, setOpenApiSwaggerFile] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!props.workspaceFile) {
      setOpenApiSwaggerFile(undefined);
    }
    props.workspaceFile.getFileContentsAsString().then((content) => {
      setOpenApiSwaggerFile(content);
    });
  }, [props.workspaceFile]);

  return (
    <Modal
      showClose={true}
      title={`OpenAPI Specification for ${props.workspaceName}`}
      isOpen={props.isOpen}
      onClose={props.onClose}
    >
      <Flex style={{ backgroundColor: "#EEE" }}>
        <FlexItem grow={{ default: "grow" }}>{openApiSwaggerFile && <SwaggerUI spec={openApiSwaggerFile} />}</FlexItem>
      </Flex>
    </Modal>
  );
}
