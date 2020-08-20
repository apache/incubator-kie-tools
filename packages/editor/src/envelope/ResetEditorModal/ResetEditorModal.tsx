/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { Button, List, ListItem, Modal, ModalVariant } from "@patternfly/react-core";
import { useEditorEnvelopeI18nContext } from "../i18n/setup";

interface Props {
  isOpen: boolean;
  close: () => void;
}

export function ResetEditorModal(props: Props) {
  const { i18n } = useEditorEnvelopeI18nContext();

  return (
    <Modal
      className={"kogito-tooling-reset-editor-modal"}
      variant={ModalVariant.small}
      title={i18n.resetEditorModal.title}
      isOpen={props.isOpen}
      onClose={props.close}
      actions={[
        <Button key={"close"} variant={"primary"} onClick={props.close}>
          {i18n.terms.close}
        </Button>
      ]}
    >
      <div>
        <h3>{i18n.resetEditorModal.subtitle}</h3>
        <p>{i18n.resetEditorModal.explanation}</p>
        <p>{i18n.resetEditorModal.options}</p>
        <List>
          <ListItem>{i18n.resetEditorModal.reOpen}</ListItem>
          <ListItem>{i18n.resetEditorModal.close}</ListItem>
        </List>
      </div>
    </Modal>
  );
}
