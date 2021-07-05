/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { I18nHtml } from "@kogito-tooling/i18n/dist/react-components";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import * as React from "react";
import { useCallback } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { useDeploy } from "./DeployContext";
import { DEVELOPER_SANDBOX_URL } from "./devsandbox/DeveloperSandboxService";

export function DeployIntroductionModal() {
  const deployContext = useDeploy();
  const { i18n } = useOnlineI18n();

  const onConfigure = useCallback(() => {
    deployContext.setDeployIntroductionModalOpen(false);
    deployContext.setConfigWizardOpen(true);
  }, [deployContext]);

  const onClose = useCallback(() => {
    deployContext.setDeployIntroductionModalOpen(false);
  }, [deployContext]);

  return (
    <Modal
      data-testid={"deploy-introduction-modal"}
      variant={ModalVariant.medium}
      isOpen={deployContext.isDeployIntroductionModalOpen}
      aria-label={"Deploy introduction modal"}
      onClose={onClose}
    >
      <Stack>
        <StackItem>
          <TextContent>
            <Text className="pf-u-text-align-center" component={TextVariants.h1}>
              {i18n.deploy.introduction.header}
            </Text>
          </TextContent>
        </StackItem>
        <StackItem>
          <TextContent>
            <Text className="pf-u-mt-md pf-u-text-align-center" component={TextVariants.h3}>
              {i18n.deploy.introduction.subHeader}
            </Text>
          </TextContent>
        </StackItem>
        <StackItem>
          <TextContent>
            <Text className="pf-u-mt-md" component={TextVariants.p}>
              <I18nHtml>{i18n.deploy.introduction.disclaimer}</I18nHtml>
            </Text>
          </TextContent>
          <TextContent>
            <Text className="pf-u-mt-md" component={TextVariants.p}>
              {i18n.deploy.introduction.getStarted}
            </Text>
          </TextContent>
        </StackItem>
        <StackItem className="pf-u-py-lg">
          <Card isHoverable>
            <CardTitle>{i18n.names.devSandbox}</CardTitle>
            <CardBody>
              <TextContent>
                <Text component={TextVariants.p}>
                  {i18n.deploy.introduction.sandboxShortDescription}
                  <br />
                  <Text component={TextVariants.a} href={DEVELOPER_SANDBOX_URL} target={"_blank"}>
                    {i18n.deploy.common.learnMore}
                    <ExternalLinkAltIcon className="pf-u-mx-sm" />
                  </Text>
                </Text>
              </TextContent>
            </CardBody>
            <CardFooter>
              <Button key="use-wizard" variant="primary" onClick={onConfigure}>
                {i18n.terms.configure}
                <ArrowRightIcon className="pf-u-ml-sm" />
              </Button>
            </CardFooter>
          </Card>
        </StackItem>
      </Stack>
    </Modal>
  );
}
