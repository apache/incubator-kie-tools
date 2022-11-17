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

import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { ModalVariant } from "@patternfly/react-core/dist/esm/components/Modal";
import * as React from "react";
import { StartupBlockerTemplate } from "./StartupBlockerTemplate";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { KieIcon } from "./KieIcon";
import { LATEST_VERSION_COMPATIBLE_WITH_LFS } from "./LatestCompatibleVersion";
import { APP_NAME } from "../../AppConstants";
import { BROWSER_DETAILS, SUPPORTED_BROWSERS } from "./SupportedBrowsers";

export async function isTrue() {
  return !BROWSER_DETAILS.isCompatible;
}

export function Component() {
  return (
    <StartupBlockerTemplate>
      <Modal
        isOpen={true}
        showClose={false}
        onClose={() => {}}
        variant={ModalVariant.medium}
        title={"Oops!"}
        titleIconVariant={KieIcon}
      >
        <br />
        <TextContent>
          <Text component={TextVariants.h4}>
            {`${APP_NAME} is not compatible with this browser.`}
            <br />
            <small style={{ display: "inline" }}>
              {BROWSER_DETAILS.info.browser.name} {BROWSER_DETAILS.info.os.name} {BROWSER_DETAILS.info.browser.version}
            </small>
          </Text>
        </TextContent>
        <br />
        <hr />
        <br />
        <TextContent>
          <Text component={TextVariants.p}>Compatible desktop browsers are:</Text>
          <List>
            <ListItem>{`Chrome (${SUPPORTED_BROWSERS.chrome}) - recommended`}</ListItem>
            <ListItem>{`Firefox (${SUPPORTED_BROWSERS.firefox})`}</ListItem>
            <ListItem>{`Safari (${SUPPORTED_BROWSERS.safari})`}</ListItem>
            <ListItem>{`Opera (${SUPPORTED_BROWSERS.opera})`}</ListItem>
            <ListItem>{`Edge (${SUPPORTED_BROWSERS.edge})`}</ListItem>
          </List>
        </TextContent>
        <br />
        <hr />
        <br />
        <TextContent>
          <Text component={TextVariants.p}>
            {`If you have work that you'd like to download or push, please use the `}
            <a href={LATEST_VERSION_COMPATIBLE_WITH_LFS}>latest compatible version</a>
            {`.`}
          </Text>
        </TextContent>
      </Modal>
    </StartupBlockerTemplate>
  );
}
