import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import * as React from "react";
import { useGlobals } from "../../common/GlobalContext";
import { useHistory } from "react-router";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../../settings/SettingsButton";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { KieToolingExtendedServicesIcon } from "../../editor/KieToolingExtendedServices/KieToolingExtendedServicesIcon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

export function BusinessAutomationStudioPage(props: { children?: React.ReactNode }) {
  const globals = useGlobals();
  const history = useHistory();

  return (
    <Page
      header={
        <Masthead aria-label={"Page header"}>
          <MastheadMain>
            <PageHeaderToolsItem>
              <MastheadBrand
                onClick={() => history.push({ pathname: globals.routes.home.path({}) })}
                style={{ textDecoration: "none" }}
              >
                <Flex alignItems={{ default: "alignItemsCenter" }}>
                  <Brand
                    src={globals.routes.static.images.homeLogo.path({})}
                    alt={"Logo"}
                    style={{ display: "inline" }}
                  />
                  <TextContent>
                    <Text
                      component={TextVariants.h1}
                      style={{ fontWeight: "lighter", fontSize: "2em", display: "inline" }}
                    >
                      Business Automation Studio
                    </Text>
                    &nbsp; &nbsp;
                    <Text
                      component={TextVariants.h3}
                      style={{ fontStyle: "italic", display: "inline", fontWeight: "lighter", color: "gray" }}
                    >
                      Online
                    </Text>
                  </TextContent>
                </Flex>
              </MastheadBrand>
            </PageHeaderToolsItem>
          </MastheadMain>
          <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
            <FlexItem>
              <PageHeaderToolsItem>
                <KieToolingExtendedServicesIcon />
              </PageHeaderToolsItem>
            </FlexItem>
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
