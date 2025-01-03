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
import { useCallback, useMemo } from "react";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { TodoListApi, TodoListChannelApi, TodoListEnvelopeApi } from "../api";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";

export type EmbeddedTodoListRef = TodoListApi & {
  envelopeServer: EnvelopeServer<TodoListChannelApi, TodoListEnvelopeApi>;
};

export type Props = {
  targetOrigin: string;
  envelopePath: string;
  apiImpl: TodoListChannelApi;
};

/**
 * Convenience component to embed a Todo List View.
 *
 * This is aimed to be used mostly by Web applications. It exposes a `ref` to give control to the parent component.
 */
export const EmbeddedTodoList = React.forwardRef<EmbeddedTodoListRef, Props>((props, forwardedRef) => {
  /*
   * This is the pollInit parameter. Used to connect the Envelope with this instance of EnvelopeServer.
   */
  const pollInit = useCallback(
    (
      envelopeServer: EnvelopeServer<TodoListChannelApi, TodoListEnvelopeApi>,
      container: () => HTMLDivElement | HTMLIFrameElement
    ) => {
      return envelopeServer.envelopeApi.requests.todoList__init(
        {
          origin: envelopeServer.origin,
          envelopeServerId: envelopeServer.id,
        },
        { user: "Tiago" }
      );
    },
    []
  );

  /*
   * Function that creates a `ref` to be exposed to the parent components.
   */
  const refDelegate = useCallback(
    (envelopeServer: EnvelopeServer<TodoListChannelApi, TodoListEnvelopeApi>): EmbeddedTodoListRef => ({
      envelopeServer,
      addItem: (item) => envelopeServer.envelopeApi.requests.todoList__addItem(item),
      getItems: () => envelopeServer.envelopeApi.requests.todoList__getItems(),
      markAllAsCompleted: () => envelopeServer.envelopeApi.notifications.todoList__markAllAsCompleted.send(),
    }),
    []
  );

  const config = useMemo(() => {
    return { containerType: ContainerType.IFRAME, envelopePath: props.envelopePath };
  }, [props.envelopePath]);

  return (
    <EmbeddedTodoListEnvelope
      ref={forwardedRef}
      apiImpl={props.apiImpl}
      origin={props.targetOrigin}
      refDelegate={refDelegate}
      pollInit={pollInit}
      config={config}
    />
  );
});

const EmbeddedTodoListEnvelope = React.forwardRef<
  EmbeddedTodoListRef,
  EmbeddedEnvelopeProps<TodoListChannelApi, TodoListEnvelopeApi, EmbeddedTodoListRef>
>(RefForwardingEmbeddedEnvelope);
