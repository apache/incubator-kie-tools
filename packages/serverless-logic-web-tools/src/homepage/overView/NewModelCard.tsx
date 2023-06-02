/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import * as React from "react";
import { Link } from "react-router-dom";
import { SupportedFileExtensions } from "../../extension";
import { useRoutes } from "../../navigation/Hooks";
import { FileLabel } from "../../workspace/components/FileLabel";

export function NewModelCard(props: {
  title: string;
  jsonExtension?: SupportedFileExtensions;
  yamlExtension?: SupportedFileExtensions;
  description: string;
}) {
  const routes = useRoutes();
  const { jsonExtension, yamlExtension } = props;

  if (!jsonExtension && !yamlExtension) {
    throw new Error("At least one of jsonExtension or yamlExtension must be provided.");
  }

  return (
    <GridItem sm={12} md={4}>
      <Card isFullHeight={true} isPlain={true} isCompact={true} ouiaId={`${props.title}-card`}>
        <CardTitle>
          <FileLabel style={{ fontSize: "0.6em" }} extension={(jsonExtension || yamlExtension)!} />
        </CardTitle>
        <CardBody>
          <TextContent>
            <Text component={TextVariants.p}>{props.description}</Text>
          </TextContent>
        </CardBody>
        <CardFooter>
          <TextContent>
            <Text component={TextVariants.p}>
              <b>New {props.title}</b>
            </Text>
          </TextContent>
          <Flex>
            {jsonExtension && (
              <Link to={{ pathname: routes.newModel.path({ extension: jsonExtension }) }}>
                <Button variant={ButtonVariant.secondary} ouiaId={`new-${jsonExtension}-button`} isSmall>
                  JSON
                </Button>
              </Link>
            )}
            {yamlExtension && (
              <Link to={{ pathname: routes.newModel.path({ extension: yamlExtension }) }}>
                <Button variant={ButtonVariant.secondary} ouiaId={`new-${yamlExtension}-button`} isSmall>
                  YAML
                </Button>
              </Link>
            )}
          </Flex>
        </CardFooter>
      </Card>
    </GridItem>
  );
}
