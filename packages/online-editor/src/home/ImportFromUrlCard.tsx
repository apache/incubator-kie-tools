import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useGlobals } from "../common/GlobalContext";
import { useHistory } from "react-router";
import { ImportFromUrlForm } from "../workspace/components/ImportFromUrlForm";
import { UrlType, useImportableUrl } from "../workspace/hooks/ImportableUrlHooks";

export function ImportFromUrlCard() {
  const globals = useGlobals();
  const history = useHistory();
  const [url, setUrl] = useState("");

  const importFromUrl = useCallback(() => {
    history.push({
      pathname: globals.routes.importModel.path({}),
      search: globals.routes.importModel.queryString({ url: url }),
    });
  }, [history, globals, url]);

  const importableUrl = useImportableUrl(url);

  const buttonLabel = useMemo(() => {
    if (importableUrl.type === UrlType.GITHUB || importableUrl.type === UrlType.GIST) {
      return "Clone";
    }

    return "Import";
  }, [importableUrl]);

  return (
    <Card isFullHeight={true} isLarge={true} isPlain={true} isSelected={url.length > 0}>
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
        <ImportFromUrlForm url={url} onChange={setUrl} onSubmit={importFromUrl} />
      </CardBody>
      <CardFooter>
        <Button variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary} onClick={importFromUrl}>
          {buttonLabel}
        </Button>
      </CardFooter>
    </Card>
  );
}
