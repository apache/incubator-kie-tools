import {
  Page,
  PageHeader,
  PageHeaderTools,
  PageHeaderToolsGroup,
  PageHeaderToolsItem,
} from "@patternfly/react-core/dist/js/components/Page";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import * as React from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useGlobals } from "../../common/GlobalContext";
import { useHistory } from "react-router";
import { SettingsButton } from "../../settings/SettingsButton";

export function OnlineEditorPage(props: { children: React.ReactNode }) {
  const { i18n } = useOnlineI18n();
  const globals = useGlobals();
  const history = useHistory();

  return (
    <Page
      className="kogito--editor-landing"
      header={
        <PageHeader
          logo={<Brand src={globals.routes.static.images.homeLogo.path({})} alt="Logo" />}
          logoProps={{ onClick: () => history.push({ pathname: globals.routes.home.path({}) }) }}
          headerTools={
            <PageHeaderTools>
              <PageHeaderToolsGroup>
                <PageHeaderToolsItem>
                  <SettingsButton />
                </PageHeaderToolsItem>
              </PageHeaderToolsGroup>
            </PageHeaderTools>
          }
        />
      }
    >
      {/*<PageSection variant="dark" className="kogito--editor-landing__title-section">*/}
      {/*  <TextContent>*/}
      {/*    <Title size="3xl" headingLevel="h1">*/}
      {/*      {i18n.homePage.header.title}*/}
      {/*    </Title>*/}
      {/*    <Text>{i18n.homePage.header.welcomeText}</Text>*/}
      {/*    <Text component={TextVariants.small} className="pf-u-text-align-right">*/}
      {/*      {`${i18n.terms.poweredBy} `}*/}
      {/*      <Brand*/}
      {/*        src={globals.routes.static.images.kogitoLogoWhite.path({})}*/}
      {/*        alt="Kogito Logo"*/}
      {/*        style={{ height: "1em", verticalAlign: "text-bottom" }}*/}
      {/*      />*/}
      {/*    </Text>*/}
      {/*  </TextContent>*/}
      {/*</PageSection>*/}
      {props.children}
    </Page>
  );
}
