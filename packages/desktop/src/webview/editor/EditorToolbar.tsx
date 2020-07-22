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
import {
  PageHeader,
  Brand,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
  Button,
  Title
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip/Tooltip";
import { removeDirectories } from "../../common/utils";

interface Props {
  onSave: () => void;
  onClose: () => void;
  isEdited: boolean;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);

  const fileExtension = useMemo(() => {
    return context.file!.fileType;
  }, [location]);

  const tooltipContent = <div>{context.file?.filePath!}</div>;

  const fileNameTitle = (
    <div data-testid="toolbar-title" className={"kogito--editor__toolbar-title"}>
      <Tooltip content={tooltipContent} position={TooltipPosition.bottom} maxWidth={"50em"}>
        <Title headingLevel={"h3"} size={"xl"}>
          {removeDirectories(context.file!.filePath)}
        </Title>
      </Tooltip>
      {props.isEdited && (
        <span className={"kogito--editor__toolbar-edited"} data-testid="is-dirty-indicator">
          {" - Edited"}
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
          >
            Save
          </Button>
        </PageHeaderToolsItem>
        <PageHeaderToolsItem>
          <Button
            variant={"plain"}
            onClick={props.onClose}
            aria-label={"Go to homepage"}
            data-testid="close-editor-button"
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
        <Brand src={`images/${fileExtension}_kogito_logo.svg`} alt={`${fileExtension} kogito logo`} onClick={props.onClose} />
      }
      headerTools={headerToolbar}
      topNav={fileNameTitle}
      className={"kogito--editor__toolbar"}
    />
  );
}
