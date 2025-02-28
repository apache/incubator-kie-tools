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

import { TodoListEnvelopeContext } from "./TodoListEnvelopeContext";
import { Association, TodoListChannelApi, TodoListEnvelopeApi, TodoListInitArgs } from "../api";
import { TodoListEnvelopeViewApi } from "./TodoListEnvelopeView";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

/**
 * Implements the TodoListEnvelopeApi.
 *
 * These are the methods that the Channel can call.
 */
export class TodoListEnvelopeApiImpl implements TodoListEnvelopeApi {
  private view: () => TodoListEnvelopeViewApi;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      TodoListEnvelopeApi,
      TodoListChannelApi,
      TodoListEnvelopeViewApi,
      TodoListEnvelopeContext
    >
  ) {}

  /**
   * Inits the Todo List View.
   *
   * Calling envelopeClient.associate is mandatory if this Envelope will send messages
   * back to the Editor (which is almost always the case).
   *
   * @param association
   * @param initArgs Initial arguments of this Envelope. The `user` object is only for example purposes.
   */
  public async todoList__init(association: Association, initArgs: TodoListInitArgs) {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);
    this.view = await this.args.viewDelegate();
    this.view().setUser(initArgs.user);
  }

  /**
   * Adds a new item to the Todo List View
   * @param item The item to be added.
   */
  public async todoList__addItem(item: string) {
    return this.view().addItem(item);
  }

  /**
   * Returns the current items on the Todo List View
   */
  public async todoList__getItems() {
    return this.view().getItems();
  }

  /**
   * Marks all items on the Todo List View as completed.
   */
  public todoList__markAllAsCompleted() {
    this.view().markAllAsCompleted();
  }

  /**
   * Holds the current count of items in the list
   */
  public todoList__itemsCount(): SharedValueProvider<number> {
    return {
      defaultValue: 0,
    };
  }
}
