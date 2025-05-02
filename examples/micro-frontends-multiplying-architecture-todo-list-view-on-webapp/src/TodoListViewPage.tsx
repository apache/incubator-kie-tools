/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useMemo, useRef, useState } from "react";
import {
  EmbeddedTodoList,
  EmbeddedTodoListRef,
} from "@kie-tools-examples/micro-frontends-multiplying-architecture-todo-list-view/dist/embedded";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeader } from "@patternfly/react-core/deprecated";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";

import { useSharedValue, useStateAsSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";

export function TodoListViewPage() {
  const embeddedTodoListRef = useRef<EmbeddedTodoListRef>(null);

  const [newItem, setNewItem] = useState("");
  const addItem = useCallback(
    (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      if (newItem.length > 0) {
        embeddedTodoListRef.current?.addItem(newItem);
        setNewItem("");
      }
    },
    [newItem]
  );

  const handleItemRemoved = useCallback((item: string) => {
    window.alert(`Item '${item}' removed successfully!`);
  }, []);

  useStateAsSharedValue(
    newItem,
    setNewItem,
    embeddedTodoListRef.current?.envelopeServer.shared.todoList__potentialNewItem
  );

  const [itemsCount, _] = useSharedValue(
    embeddedTodoListRef.current?.envelopeServer.envelopeApi.shared.todoList__itemsCount
  );

  const apiImpl = useMemo(() => {
    return {
      todoList__itemRemoved: handleItemRemoved,
      todoList__potentialNewItem: () => ({ defaultValue: "" }),
    };
  }, [handleItemRemoved]);

  return (
    <Page
      header={<PageHeader logo={<Brand src={"logo.png"} alt="Logo" />} />}
      sidebar={
        <Stack hasGutter={true} style={{ padding: "16px" }}>
          <StackItem>Actions</StackItem>
          <StackItem>
            <form onSubmit={addItem}>
              <input
                type={"text"}
                value={newItem}
                onChange={(e) => setNewItem(e.target.value)}
                placeholder={"New item"}
              />
              <button>Add</button>
            </form>
          </StackItem>
          <StackItem>
            <Button variant={ButtonVariant.plain} onClick={embeddedTodoListRef.current?.markAllAsCompleted}>
              Mark all as completed
            </Button>
          </StackItem>

          <Divider />

          <StackItem>
            <span># of items: {itemsCount}</span>
          </StackItem>
        </Stack>
      }
    >
      <PageSection>
        <EmbeddedTodoList
          ref={embeddedTodoListRef}
          targetOrigin={window.location.origin}
          envelopePath={"./todo-list-view-envelope.html"}
          apiImpl={apiImpl}
        />
      </PageSection>
    </Page>
  );
}
