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

import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import * as React from "react";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";

export function RefactorConfirmationDialog({
  onConfirmExpressionRefactor,
  onConfirmRenameOnly,
  isRefactorModalOpen,
  fromName,
  toName,
}: {
  onConfirmExpressionRefactor: () => void;
  onConfirmRenameOnly: () => void;
  isRefactorModalOpen: boolean;
  fromName: string | undefined;
  toName: string | undefined;
}) {
  return (
    <Modal
      aria-labelledby={"identifier-renamed"}
      variant={ModalVariant.small}
      isOpen={isRefactorModalOpen}
      showClose={false}
      actions={[
        <Button key="confirm" variant={ButtonVariant.primary} onClick={onConfirmExpressionRefactor}>
          Yes, rename and update the expressions
        </Button>,
        <Button key="cancel" variant="link" onClick={onConfirmRenameOnly}>
          No, just rename
        </Button>,
      ]}
    >
      The identifier `{fromName ?? "<undefined>"}` was renamed to `{toName ?? "<undefined>"}`.
      <br />
      <br />
      This identifier is used in one or more expressions.
      <br />
      <br />
      Do you want also automatically update the expressions to the new name?
    </Modal>
  );
}

export function isIdentifierReferencedInSomeExpression(args: {
  identifierUuid: string;
  dmnDefinitions: Normalized<DMN15__tDefinitions>;
  externalDmnModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
}) {
  const identifiersRefactor = new IdentifiersRefactor({
    writeableDmnDefinitions: args.dmnDefinitions,
    _readonly_externalDmnModelsByNamespaceMap: args.externalDmnModelsByNamespaceMap,
  });

  return Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier(args.identifierUuid)).length > 0;
}
