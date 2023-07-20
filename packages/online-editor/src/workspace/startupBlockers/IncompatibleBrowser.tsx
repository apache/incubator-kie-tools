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
import * as Bowser from "bowser";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { KieIcon } from "./KieIcon";
import { useEnv } from "../../env/hooks/EnvContext";

export const LATEST_VERSION_COMPATIBLE_WITH_LFS = "0.23.0";

const hasNecessaryApis = window["SharedWorker"] && window["BroadcastChannel"];
const isCompatibleBrowser = Bowser.getParser(window.navigator.userAgent).satisfies({
  chrome: ">4",
  edge: ">=79",
  safari: ">=16",
  mobile: {
    safari: ">=16",
  },
});
const browserInfo = Bowser.parse(window.navigator.userAgent);

export async function isTrue() {
  return !hasNecessaryApis || !isCompatibleBrowser;
}

export function Component() {
  const { env } = useEnv();
  return (
    <StartupBlockerTemplate>
      <Modal isOpen={true} onClose={() => {}} variant={ModalVariant.small} title={"Oops!"} titleIconVariant={KieIcon}>
        <br />
        <TextContent>
          <Text component={TextVariants.h4}>
            ${env.KIE_SANDBOX_APP_NAME} is not compatible with this browser.{" "}
            <small style={{ display: "inline" }}>
              ({browserInfo.browser.name} {browserInfo.browser.version})
            </small>
          </Text>
        </TextContent>
        <br />
        <hr />
        <br />
        <TextContent>
          <Text component={TextVariants.p}>Compatible browsers are:</Text>
          <List>
            <ListItem>Chrome</ListItem>
            <ListItem>Edge</ListItem>
            <ListItem>Safari 16 or newer</ListItem>
          </List>
        </TextContent>
        <br />
        <hr />
        <br />
        <TextContent>
          <Text component={TextVariants.p}>
            {`If you have work that you'd like to download or Push, please use the `}
            <a href={LATEST_VERSION_COMPATIBLE_WITH_LFS}>latest compatible version</a>
            {`.`}
          </Text>
        </TextContent>
      </Modal>
    </StartupBlockerTemplate>
  );
}
