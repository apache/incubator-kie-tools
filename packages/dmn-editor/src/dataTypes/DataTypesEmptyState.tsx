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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { ImportJavaClassesWrapper } from "./ImportJavaClasses";
import { useDmnEditorI18n } from "../i18n";

export function DataTypesEmptyState({ onAdd, onPaste }: { onAdd: () => void; onPaste: () => void }) {
  const { i18n } = useDmnEditorI18n();
  const { isReadOnly, isImportDataTypesFromJavaClassesSupported, javaCodeCompletionService } = useSettings();

  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateHeader
          titleText={<>{i18n.dataTypes.noCustomDataTypes}</>}
          icon={<EmptyStateIcon icon={CubesIcon} />}
          headingLevel={"h4"}
        />
        <EmptyStateBody>{i18n.dataTypes.dmnEmptyBody}</EmptyStateBody>
        <EmptyStateFooter>
          <br />
          {!isReadOnly && (
            <>
              <EmptyStateActions>
                <Button variant={ButtonVariant.primary} onClick={onAdd}>
                  {i18n.dataTypes.createCustomDataType}
                </Button>
              </EmptyStateActions>
              <br />
              {isImportDataTypesFromJavaClassesSupported && javaCodeCompletionService && (
                <>
                  {i18n.dataTypes.or}
                  <br />
                  <br />
                  <ImportJavaClassesWrapper javaCodeCompletionService={javaCodeCompletionService} />
                  <br />
                  <br />
                </>
              )}
              or
              <EmptyStateActions>
                <Button variant={ButtonVariant.link} onClick={onPaste} icon={<PasteIcon />}>
                  {i18n.dataTypes.pasteDataType}
                </Button>
              </EmptyStateActions>
            </>
          )}
        </EmptyStateFooter>
      </EmptyState>
    </Flex>
  );
}
