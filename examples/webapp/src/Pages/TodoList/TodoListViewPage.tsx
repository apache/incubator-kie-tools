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
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { EmbeddedTodoList, EmbeddedTodoListRef } from "@kie-tools-examples/todo-list-view/dist/embedded";
import { useStateAsSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";

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

  const apiImpl = useMemo(() => {
    return {
      todoList__itemRemoved: handleItemRemoved,
      todoList__potentialNewItem: () => ({ defaultValue: "" }),
    };
  }, [handleItemRemoved]);

  return (
    <Page>
      <div className={"webapp--page-main-div"}>
        <div>
          <Nav className={"webapp--page-navigation"}>
            <div className={"webapp--page-navigation-title-div"}>
              <Title className={"webapp--page-navigation-title-h3"} headingLevel="h3" size="xl">
                Actions
              </Title>
            </div>
            <NavList>
              <NavItem onClick={embeddedTodoListRef.current?.markAllAsCompleted}>Mark all as completed</NavItem>
              <NavItem>
                <form onSubmit={addItem}>
                  <input
                    type={"text"}
                    value={newItem}
                    onChange={(e) => setNewItem(e.target.value)}
                    placeholder={"New item"}
                  />
                  <button>Add</button>
                </form>
              </NavItem>
            </NavList>
          </Nav>
        </div>
        <PageSection>
          <EmbeddedTodoList
            ref={embeddedTodoListRef}
            targetOrigin={window.location.origin}
            envelopePath={"envelope/todo-list-view.html"}
            apiImpl={apiImpl}
          />
        </PageSection>
      </div>
    </Page>
  );
}
