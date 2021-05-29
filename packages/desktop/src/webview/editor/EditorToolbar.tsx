/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useContext, useMemo } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
} from "@patternfly/react-core/dist/js/components/Page";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { CloseIcon } from "@patternfly/react-icons/dist/js/icons/close-icon";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip/Tooltip";
import { removeDirectories } from "../../common/utils";
import { useDesktopI18n } from "../common/i18n";

interface Props {
  onSave: () => void;
  onClose: () => void;
  isEdited: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);
  const { i18n } = useDesktopI18n();

  const fileExtension = useMemo(() => {
    return context.file.fileExtension;
  }, [context]);

  const fileName = useMemo(() => {
    return context.file.fileName;
  }, [context]);

  const title = useMemo(() => {
    return removeDirectories(fileName);
  }, [fileName]);

  const fileNameTitle = (
    <div data-testid="toolbar-title" className={"kogito--editor__toolbar-title"}>
      <Tooltip content={<div>{fileName}</div>} position={TooltipPosition.bottom} maxWidth={"50em"}>
        <Title headingLevel={"h3"} size={"xl"}>
          {title}
        </Title>
      </Tooltip>
      {props.isEdited && (
        <span className={"kogito--editor__toolbar-edited"} data-testid="is-dirty-indicator">
          {` - ${i18n.terms.edited}`}
        </span>
      )}
    </div>
  );

  const headerToolbar = (
    <PageHeaderTools>
      <PageHeaderToolsGroup>
        <PageHeaderToolsItem>
          <Button
            data-testid="save-button"
            variant={"tertiary"}
            onClick={props.onSave}
            className={"pf-u-display-flex-on-lg"}
            aria-label={"Save file"}
            ouiaId="save-button"
          >
            {i18n.terms.save}
          </Button>
        </PageHeaderToolsItem>
        <PageHeaderToolsItem>
          <Button
            variant={"plain"}
            onClick={props.onClose}
            aria-label={"Go to homepage"}
            data-testid="close-editor-button"
            ouiaId="close-button"
          >
            <CloseIcon />
          </Button>
        </PageHeaderToolsItem>
      </PageHeaderToolsGroup>
    </PageHeaderTools>
  );

  return (
    <PageHeader
      logo={
        <Brand
          src={`images/${fileExtension}_kogito_logo.svg`}
          alt={`${fileExtension} kogito logo`}
          onClick={props.onClose}
        />
      }
      headerTools={headerToolbar}
      topNav={fileNameTitle}
      className={"kogito--editor__toolbar"}
    />
  );
}
