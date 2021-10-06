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
import { useCallback, useState } from "react";
import { Nav, NavItem, NavList, Title } from "@patternfly/react-core";
import { TodoListApi } from "@kogito-tooling-examples/todo-list-view/dist/api";

interface Props {
  todoListViewRef: React.RefObject<TodoListApi>;
}

/**
 * A sidebar to add new To-do topics
 *
 * @param props
 * @constructor
 */
export function ActionsSidebar(props: Props) {
  const [newItem, setNewItem] = useState("");
  const addItem = useCallback(
    (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      if (newItem.length > 0) {
        props.todoListViewRef.current?.addItem(newItem);
        setNewItem("");
      }
    },
    [newItem]
  );

  return (
    <div>
      <Nav className={"webapp--page-navigation"}>
        <div className={"webapp--page-navigation-title-div"}>
          <Title className={"webapp--page-navigation-title-h3"} headingLevel="h3" size="xl">
            Actions
          </Title>
        </div>
        <NavList>
          <NavItem onClick={props.todoListViewRef.current?.markAllAsCompleted}>Mark all as completed</NavItem>
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
  );
}
