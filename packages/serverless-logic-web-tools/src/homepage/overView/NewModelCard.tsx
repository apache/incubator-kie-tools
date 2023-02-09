/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import * as React from "react";
import { Link } from "react-router-dom";
import { SupportedFileExtensions } from "../../extension";
import { useRoutes } from "../../navigation/Hooks";
import { FileLabel } from "../../workspace/components/FileLabel";

export function NewModelCard(props: { title: string; extension: SupportedFileExtensions; description: string }) {
  const routes = useRoutes();

  return (
    <GridItem sm={12} md={4}>
      <Card isFullHeight={true} isPlain={true} isCompact>
        <CardTitle>
          <FileLabel style={{ fontSize: "0.6em" }} extension={props.extension} />
        </CardTitle>
        <CardBody>
          <TextContent>
            <Text component={TextVariants.p}>{props.description}</Text>
          </TextContent>
        </CardBody>
        <CardFooter>
          <Link to={{ pathname: routes.newModel.path({ extension: props.extension }) }}>
            <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.extension}-button`}>
              New {props.title}
            </Button>
          </Link>
        </CardFooter>
      </Card>
    </GridItem>
  );
}
