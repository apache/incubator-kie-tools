import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import * as React from "react";
import { useGlobals } from "../../common/GlobalContext";
import { useHistory } from "react-router";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../../settings/SettingsButton";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

export function OnlineEditorPage(props: { children?: React.ReactNode }) {
  const globals = useGlobals();
  const history = useHistory();

  return (
    <Page
      header={
        <Masthead aria-label={"Page header"}>
          <MastheadMain>
            <PageHeaderToolsItem>
              <MastheadBrand>
                <Brand
                  src={globals.routes.static.images.homeLogo.path({})}
                  onClick={() => history.push({ pathname: globals.routes.home.path({}) })}
                  alt={"Logo"}
                />
              </MastheadBrand>
            </PageHeaderToolsItem>
          </MastheadMain>
          <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
            <FlexItem>
              <PageHeaderToolsItem>
                <SettingsButton />
              </PageHeaderToolsItem>
            </FlexItem>
          </Flex>
        </Masthead>
      }
    >
      {props.children}
    </Page>
  );
}
