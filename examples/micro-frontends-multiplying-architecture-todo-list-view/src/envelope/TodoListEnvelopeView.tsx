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

import { ApiSharedValueConsumers, MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { Item, TodoListChannelApi, TodoListEnvelopeApi } from "../api";
import "./styles.scss";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";

export interface TodoListEnvelopeViewApi {
  setUser(user: string): void;
  addItem(item: string): void;
  getItems(): Item[];
  markAllAsCompleted(): void;
}

interface Props {
  channelApi: MessageBusClientApi<TodoListChannelApi>;
  shared: ApiSharedValueConsumers<TodoListEnvelopeApi>;
}

/**
 * The actual implementation of the Todo List View.
 * In this case, it's a React component. See TodoListEnvelope.tsx.
 *
 * Provides an imperative handle to give control of this component to its containing components.
 */
export const TodoListEnvelopeView = React.forwardRef<TodoListEnvelopeViewApi, Props>((props, forwardedRef) => {
  const [user, setUser] = useState<string | undefined>();
  const [items, setItems] = useState<Item[]>([]);

  const removeItem = useCallback(
    (e: React.MouseEvent<HTMLAnchorElement, MouseEvent>, item: Item) => {
      e.preventDefault();
      const itemsCopy = [...items];
      const i = itemsCopy.indexOf(item);
      if (i >= 0) {
        itemsCopy.splice(i, 1);
        setItems(itemsCopy);
        props.channelApi.notifications.todoList__itemRemoved.send(item.label);
      }
    },
    [items, props.channelApi]
  );

  const updateItemCompletedStatus = useCallback(
    (e: React.MouseEvent<HTMLAnchorElement, MouseEvent>, item: Item, completed: boolean) => {
      e.preventDefault();
      const itemsCopy = [...items];
      const i = itemsCopy.indexOf(item);
      if (i >= 0) {
        itemsCopy[i].completed = completed;
        setItems(itemsCopy);
      }
    },
    [items]
  );

  const allCompleted = useMemo(() => {
    const completedItems = items.filter((i) => i.completed);
    return items.length > 0 && completedItems.length === items.length;
  }, [items]);

  useImperativeHandle(
    forwardedRef,
    () => ({
      setUser,
      addItem: (item) => setItems([...items, { label: item, completed: false }]),
      getItems: () => items,
      markAllAsCompleted: () => setItems(items.map((item) => ({ ...item, completed: true }))),
    }),
    [items]
  );

  // State that is updated whenever the Channel changes the `potentialNewItem` Shared value.
  // Making the Envelope able to react to changes done to it.
  const [potentialNewItem, _] = useSharedValue(props.channelApi.shared.todoList__potentialNewItem);

  // Keeps the `itemsCount` Shared value current.
  useEffect(() => {
    props.shared.todoList__itemsCount.set(items.length);
  }, [items.length, props.shared.todoList__itemsCount]);

  // Handles set operations to `itemsCount` that do not match items.length.
  // As a Channel also has write access to a Shared value, there no way to tell if an
  // invalid attempt to change this directly will be done, so we need to handle it properly.
  useEffect(() => {
    if (!props.shared.todoList__itemsCount) {
      return;
    }

    const itemsCountSubs = props.shared.todoList__itemsCount.subscribe((newItemsCount) => {
      if (newItemsCount !== items.length) {
        console.log("Rejecting operation on `itemsCount` Shared value because it doesn't match the actual value.");
        props.shared.todoList__itemsCount.set(items.length); // Reverts whatever value was set.
      } else {
        // Ignore, itemsCount matches actual value.
      }
    });

    return () => {
      props.shared.todoList__itemsCount.unsubscribe(itemsCountSubs);
    };
  }, [items.length, props.shared.todoList__itemsCount]);

  return (
    <>
      {user && (
        <>
          <p>
            Welcome, <b>{user}</b>!
          </p>

          <hr />

          <h2>{`Here's your 'To do' List:`}</h2>

          {(items.length <= 0 && !potentialNewItem && (
            <>
              <p>Nothing to do 😎</p>
            </>
          )) || (
            <ol>
              {items.map((item) => (
                <li key={item.label} className={"todo-list--list-items"}>
                  {(item.completed && (
                    <>
                      <span className={"todo-list--list-item-completed"}>{item.label}</span>
                    </>
                  )) || (
                    <>
                      <span>{item.label}</span>
                    </>
                  )}

                  <span>{" - "}</span>

                  {(!item.completed && (
                    <a href={"#"} onClick={(e) => updateItemCompletedStatus(e, item, true)}>
                      Mark as completed
                    </a>
                  )) || (
                    <a href={"#"} onClick={(e) => updateItemCompletedStatus(e, item, false)}>
                      Unmark as completed
                    </a>
                  )}

                  <span>{" / "}</span>

                  <a href={"#"} onClick={(e) => removeItem(e, item)}>
                    Remove
                  </a>
                </li>
              ))}
              {potentialNewItem && (
                <li key={"potential-new-item"} className={"todo-list--list-items"} style={{ color: "gray" }}>
                  <i>{potentialNewItem}</i>
                </li>
              )}
            </ol>
          )}
          {allCompleted && (
            <>
              <hr />
              <div>
                <b>Congratulations!</b>
                &nbsp;
                {`You've completed all your items! 🎉`}
              </div>
            </>
          )}
        </>
      )}
    </>
  );
});
