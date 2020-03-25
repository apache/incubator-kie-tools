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
import { PageHeader, Brand, Toolbar, ToolbarGroup, ToolbarItem, Button, Title } from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip/Tooltip";
import { removeDirectories } from "../../common/utils";

interface Props {
  onSave: () => void;
  onClose: () => void;
}

export function EditorToolbar(props: Props) {
  const context = useContext(GlobalContext);

  const editorType = useMemo(() => {
    return context.file!.fileType;
  }, [location]);

  const tooltipContent = <div>{context.file?.filePath!}</div>;

  const fileNameTitle = (
    <Tooltip content={tooltipContent} position={TooltipPosition.bottom} maxWidth={"50em"}>
      <Title headingLevel={"h3"} size={"xl"}>
        {removeDirectories(context.file!.filePath)}
      </Title>
    </Tooltip>
  );

  const headerToolbar = (
    // TODO: The toolbar should be switched out for DataToolbar and possibly the Overflow menu
    <Toolbar>
      <ToolbarGroup className={"kogito--right"}>
        <ToolbarItem>
          <Button
            variant={"secondary"}
            onClick={props.onSave}
            className={"pf-u-display-flex-on-lg"}
            aria-label={"Save file"}
          >
            Save
          </Button>
        </ToolbarItem>
        <ToolbarItem>
          <Button variant={"plain"} onClick={props.onClose} aria-label={"Go to homepage"}>
            <CloseIcon />
          </Button>
        </ToolbarItem>
      </ToolbarGroup>
    </Toolbar>
  );

  return (
    <PageHeader
      logo={<Brand src={`images/${editorType}_kogito_logo.svg`} alt={`${editorType} kogito logo`} onClick={props.onClose} />}
      toolbar={headerToolbar}
      topNav={fileNameTitle}
      className={"kogito--editor__toolbar"}
    />
  );
}
