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

import * as vscode from "vscode";
import { TodoListWebview } from "@kie-tools-examples/micro-frontends-multiplying-architecture-todo-list-view/dist/vscode";
import { TodoListEnvelopeApi } from "@kie-tools-examples/micro-frontends-multiplying-architecture-todo-list-view/dist/api";
import { MessageBusClientApi, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

const OPEN_TODO_LIST_VIEW_COMMAND_ID = "kie-tools-examples.todo-list-view";
const ADD_TODO_ITEM_COMMAND_ID = "kie-tools-examples.todo-list-view.add-item";
const MARK_ALL_AS_COMPLETED_COMMAND_ID = "kie-tools-examples.todo-list-view.mark-all-as-completed";

/**
 * This method is called when the extension is activated.
 *
 * @param context
 */
export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const todoListWebview = new TodoListWebview(
    context,
    {
      envelopePath: "dist/todo-list-view-envelope.js",
      title: "// TODO", // This is displayed as the title of the Webview tab.
      targetOrigin: "vscode",
    },
    {
      /*
       * This is the implementation of TodoListChannelApi
       */
      todoList__potentialNewItem(): SharedValueProvider<string> {
        return { defaultValue: "" };
      },
      todoList__itemRemoved: (item) => {
        vscode.window.showInformationMessage(`Item '${item}' successfully removed.`);
      },
    }
  );

  // Will store the active envelopeApiImpl.
  let envelopeApiImpl: MessageBusClientApi<TodoListEnvelopeApi> | undefined;

  context.subscriptions.push(
    vscode.commands.registerCommand(OPEN_TODO_LIST_VIEW_COMMAND_ID, () => {
      envelopeApiImpl = todoListWebview.open("todo-list-view", {
        onClose: () => (envelopeApiImpl = undefined),
      });
    })
  );

  context.subscriptions.push(
    vscode.commands.registerCommand(MARK_ALL_AS_COMPLETED_COMMAND_ID, () => {
      envelopeApiImpl?.notifications.todoList__markAllAsCompleted.send();
    })
  );

  context.subscriptions.push(
    vscode.commands.registerCommand(ADD_TODO_ITEM_COMMAND_ID, async () => {
      const textEditor = vscode.window.activeTextEditor;
      if (!textEditor) {
        throw new Error("Can't find selection of non-existent Text Editor");
      }

      const selectedText = textEditor.document.getText(textEditor.selection);
      if (selectedText.length <= 0) {
        vscode.window.showErrorMessage(`Cannot add empty 'To do' item.`);
        return;
      }

      const items = selectedText.split("\n");

      if (envelopeApiImpl) {
        addItems(items, envelopeApiImpl);
        return;
      }

      const selected = await vscode.window.showInformationMessage(
        `'To do' List not open. Would you like to open it and add ${items.length} item(s)?`,
        "Yes!"
      );

      if (!selected) {
        return;
      }

      await vscode.commands.executeCommand(OPEN_TODO_LIST_VIEW_COMMAND_ID);
      addItems(items, envelopeApiImpl);
    })
  );

  console.info("Extension is successfully setup.");
}

function addItems(items: string[], envelopeApi?: MessageBusClientApi<TodoListEnvelopeApi>) {
  for (const item of items) {
    envelopeApi?.requests.todoList__addItem(item);
  }
}

export function deactivate() {
  console.info("Extension is deactivating");
}
