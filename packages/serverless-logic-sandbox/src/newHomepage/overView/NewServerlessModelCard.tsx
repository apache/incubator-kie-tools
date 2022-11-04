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

import * as React from "react";
import { SupportedFileExtensions } from "../../extension";
import { useRoutes } from "../../navigation/Hooks";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { FileLabel } from "../../workspace/components/FileLabel";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Link } from "react-router-dom";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

interface IOwnProps {
  title: string;
  jsonExtension: SupportedFileExtensions;
  yamlExtension: SupportedFileExtensions;
  description: string;
}

const NewServerlessModelCard: React.FC<IOwnProps> = (props) => {
  const routes = useRoutes();

  return (
    <Card isFullHeight={true} isPlain={true} isLarge={true}>
      <CardTitle>
        <FileLabel style={{ fontSize: "0.6em" }} extension={props.jsonExtension} />
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>{props.description}</Text>
        </TextContent>
      </CardBody>
      <CardFooter>
        <Grid hasGutter>
          <GridItem span={12}>New {props.title}</GridItem>
          <GridItem span={6}>
            <Link to={{ pathname: routes.newModel.path({ extension: props.jsonExtension }) }}>
              <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.jsonExtension}-button`}>
                JSON
              </Button>
            </Link>
          </GridItem>
          <GridItem span={6}>
            <Link to={{ pathname: routes.newModel.path({ extension: props.yamlExtension }) }}>
              <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.yamlExtension}-button`}>
                YAML
              </Button>
            </Link>
          </GridItem>
        </Grid>
      </CardFooter>
    </Card>
  );
};

export default NewServerlessModelCard;
