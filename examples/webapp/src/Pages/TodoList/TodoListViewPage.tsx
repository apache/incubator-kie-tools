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
import { useRef } from "react";
import { Page, PageSection } from "@patternfly/react-core";
import { EmbeddedTodoList } from "@kogito-tooling-examples/todo-list-view/dist/embedded";
import { TodoListApi } from "@kogito-tooling-examples/todo-list-view/dist/api";
import { ActionsSidebar } from "./ActionsSidebar";

export function TodoListViewPage() {
  const todoListViewRef = useRef<TodoListApi>(null);

  return (
    <Page>
      <div className={"webapp--page-main-div"}>
        <ActionsSidebar todoListViewRef={todoListViewRef} />
        <PageSection>
          <EmbeddedTodoList
            ref={todoListViewRef}
            targetOrigin={window.location.origin}
            envelopePath={"envelope/todo-list-view.html"}
            todoList__itemRemoved={(item) => window.alert(`Item '${item}' removed successfully!`)}
          />
        </PageSection>
      </div>
    </Page>
  );
}
