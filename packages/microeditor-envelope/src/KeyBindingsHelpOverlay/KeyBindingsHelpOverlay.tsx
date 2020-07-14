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
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Modal,
  Text,
  TextContent,
  TextList,
  TextListItem,
  TextListItemVariants,
  TextListVariants,
  TextVariants
} from "@patternfly/react-core";
import { KeyboardIcon } from "@patternfly/react-icons";
import { EditorContext, OperatingSystem } from "@kogito-tooling/microeditor-envelope-protocol";
import "./styles.scss";
import { useEnvelopeApi } from "@kogito-tooling/editor-api";

export function KeyBindingsHelpOverlay() {
  const [showing, setShowing] = useState(false);
  const envelopeApi = useEnvelopeApi();

  const toggle = useCallback(() => {
    setShowing(!showing);
  }, [showing]);

  const keyBindings = useMemo(() => {
    return removeDuplicatesByAttr(envelopeApi.services.keyboardShortcuts.registered(), "combination")
      .filter(k => !k.opts?.hidden)
      .map(k => {
        return {
          combination: handleMacOsCombination(k.combination, envelopeApi.context),
          category: k.label.split("|")[0]?.trim(),
          label: k.label.split("|")[1]?.trim()
        };
      })
      .reduce((lhs, rhs) => {
        if (!lhs.has(rhs.category)) {
          lhs.set(rhs.category, new Set([{ label: rhs.label, combination: rhs.combination }]));
        } else {
          lhs.get(rhs.category)!.add({ label: rhs.label, combination: rhs.combination });
        }
        return lhs;
      }, new Map<string, Set<{ label: string; combination: string }>>());
  }, [envelopeApi.services.keyboardShortcuts.registered()]);

  useEffect(() => {
    const id = envelopeApi.services.keyboardShortcuts.registerKeyPress(
      "shift+/",
      "Help | Show keyboard shortcuts",
      async () => setShowing(true),
      { element: window }
    );
    return () => envelopeApi.services.keyboardShortcuts.deregister(id);
  }, []);

  useEffect(() => {
    if (showing) {
      const id = envelopeApi.services.keyboardShortcuts.registerKeyPressOnce("esc", async () => setShowing(false), {
        element: window
      });
      return () => envelopeApi.services.keyboardShortcuts.deregister(id);
    }
  }, [showing]);

  return (
    <>
      <div
        onClick={() => setShowing(!showing)}
        className={"kogito-tooling--keyboard-shortcuts-icon"}
        data-testid={"keyboard-shortcuts-help-overlay-icon"}
      >
        <KeyboardIcon />
      </div>

      <Modal
        title={"Keyboard shortcuts"}
        isOpen={showing}
        width={"60%"}
        onClose={toggle}
        data-testid={"keyboard-shortcuts-help-overlay"}
      >
        <TextContent>
          <TextList component={TextListVariants.dl}>
            {Array.from(keyBindings.keys()).map(category => (
              <React.Fragment key={category}>
                <Text component={TextVariants.h2}>{category}</Text>
                {Array.from(keyBindings.get(category)!).map(keyBinding => (
                  <React.Fragment key={keyBinding.combination}>
                    <TextListItem component={TextListItemVariants.dt}>
                      {formatKeyBindingCombination(keyBinding.combination)}
                    </TextListItem>
                    <TextListItem component={TextListItemVariants.dd}>{keyBinding.label}</TextListItem>
                  </React.Fragment>
                ))}
              </React.Fragment>
            ))}
          </TextList>
        </TextContent>
      </Modal>
    </>
  );
}

function handleMacOsCombination(combination: string, context: EditorContext) {
  if (context.operatingSystem === OperatingSystem.MACOS) {
    return combination.replace("ctrl", "cmd");
  }

  return combination;
}

function removeDuplicatesByAttr<T>(myArr: T[], prop: keyof T) {
  return myArr.filter((obj, pos, arr) => {
    return arr.map(mapObj => mapObj[prop]).indexOf(obj[prop]) === pos;
  });
}

function formatKeyBindingCombination(combination: string) {
  return combination
    .split("+")
    .map(w => w.replace(/^\w/, c => c.toUpperCase()))
    .join(" + ");
}
