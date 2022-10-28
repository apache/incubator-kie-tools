import { Card, CardBody, CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import * as React from "react";
import { AccountsModalDispatchAction, AccountsModalDispatchActionKind } from "../AccountsIcon";
import { AuthProviderIcon } from "./AuthProviderIcon";
import { useAuthProviders } from "./AuthProvidersContext";

export function AuthProvidersGallery(props: {
  dispatch: React.Dispatch<AccountsModalDispatchAction>;
  backActionKind: AccountsModalDispatchActionKind.GO_HOME | AccountsModalDispatchActionKind.SELECT_AUTH_PROVDER;
}) {
  const authProviders = useAuthProviders();

  return (
    <>
      <Gallery hasGutter={true} minWidths={{ default: "150px" }}>
        {authProviders
          .sort((a, b) => (a.name > b.name ? -1 : 1))
          .sort((a) => (a.enabled ? -1 : 1))
          .map((authProvider) => (
            <Card
              key={authProvider.id}
              isSelectable={authProvider.enabled}
              isRounded={true}
              style={{
                opacity: authProvider.enabled ? 1 : 0.5,
              }}
              onClick={() => {
                if (authProvider.enabled && authProvider.type === "github") {
                  props.dispatch({
                    kind: AccountsModalDispatchActionKind.SETUP_GITHUB_TOKEN,
                    selectedAuthProvider: authProvider,
                    backActionKind: props.backActionKind,
                  });
                }
              }}
            >
              <CardHeader>
                <CardHeaderMain>
                  <CardTitle>{authProvider.name}</CardTitle>
                  <TextContent>
                    {(!authProvider.enabled && (
                      <TextContent>
                        <Text component={TextVariants.small}>
                          <i>Available soon!</i>
                        </Text>
                      </TextContent>
                    )) || (
                      <Text component={TextVariants.small}>
                        <i>{authProvider.domain ?? <>&nbsp;</>}</i>
                      </Text>
                    )}
                  </TextContent>
                </CardHeaderMain>
              </CardHeader>
              <br />
              <CardBody>
                <AuthProviderIcon authProvider={authProvider} size={"xl"} />
              </CardBody>
            </Card>
          ))}
      </Gallery>
    </>
  );
}
