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

import * as React from "react";
import { useRoutes } from "../../navigation/Hooks";
import { useHistory } from "react-router";
import { useCallback, useMemo, useState } from "react";
import { UrlType, useImportableUrl } from "../../workspace/hooks/ImportableUrlHooks";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { ImportFromUrlForm } from "../../workspace/components/ImportFromUrlForm";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useEditorEnvelopeLocator } from "../../envelopeLocator/EditorEnvelopeLocatorContext";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";

export function ImportFromUrlCard() {
  const routes = useRoutes();
  const history = useHistory();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const [url, setUrl] = useState("");
  const [isUrlValid, setIsUrlValid] = useState(ValidatedOptions.default);

  const importFromUrl = useCallback(() => {
    history.push({
      pathname: routes.importModel.path({}),
      search: routes.importModel.queryString({ url: url }),
    });
  }, [history, routes, url]);

  const importableUrl = useImportableUrl({
    isFileSupported: (path: string) => editorEnvelopeLocator.hasMappingFor(path),
    urlString: url,
  });

  const buttonLabel = useMemo(() => {
    if (importableUrl.type === UrlType.GITHUB || importableUrl.type === UrlType.GIST) {
      return "Clone";
    }

    return "Import";
  }, [importableUrl]);

  return (
    <Card isFullHeight={true} isPlain={true} isSelected={url.length > 0} isCompact>
      <CardTitle>
        <TextContent>
          <Text component={TextVariants.h2}>
            <CodeIcon />
            &nbsp;&nbsp;From URL
          </Text>
        </TextContent>
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>Import a GitHub Repository, a GitHub Gist, or any other file URL.</Text>
        </TextContent>
        <br />
        <ImportFromUrlForm url={url} onChange={setUrl} onSubmit={importFromUrl} onValidate={setIsUrlValid} />
      </CardBody>
      <CardFooter>
        <Button
          variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
          onClick={importFromUrl}
          isDisabled={isUrlValid !== ValidatedOptions.success}
          ouiaId="import-from-url-button"
        >
          {buttonLabel}
        </Button>
      </CardFooter>
    </Card>
  );
}
