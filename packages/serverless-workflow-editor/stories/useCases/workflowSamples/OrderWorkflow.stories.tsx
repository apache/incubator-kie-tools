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
import type { Meta, StoryObj } from "@storybook/react";
import { Empty } from "../../misc/empty/Empty.stories";
import { SwfEditor, SwfEditorProps } from "../../../src/SwfEditor";
import { StorybookSwfEditorProps } from "../../swfEditorStoriesWrapper";
import { Specification, WorkflowValidator } from "@serverlessworkflow/sdk-typescript";

const workflow = {
  id: "order",
  version: "1.0",
  specVersion: "0.8",
  name: "Order Workflow",
  description: "Order Workflow Sample",
  start: "Order Received",
  functions: [
    {
      name: "printMessage",
      type: "custom",
      operation: "sysout",
    },
    {
      name: "sendOrder",
      operation: "specs/supplier.yaml#sendOrder",
      type: "rest",
    },
    {
      name: "cancelOrder",
      operation: "specs/supplier.yaml#cancelOrder",
      type: "rest",
    },
  ],
  events: [
    {
      name: "orderEvent",
      kind: "consumed",
      type: "OrderEventType",
      source: "Client",
      correlation: [
        {
          contextAttributeName: "orderid",
        },
      ],
    },
    {
      name: "shippingEvent",
      kind: "consumed",
      type: "ShippingEventType",
      source: "Shipper",
      correlation: [
        {
          contextAttributeName: "orderid",
        },
      ],
    },
    {
      name: "cancelEvent",
      kind: "consumed",
      type: "CancelEventType",
      source: "Client",
      correlation: [
        {
          contextAttributeName: "orderid",
        },
      ],
    },
  ],
  states: [
    {
      name: "Order Received",
      type: "event",
      onEvents: [
        {
          eventRefs: ["orderEvent"],
        },
      ],
      transition: "Check Inventory",
    },
    {
      name: "Check Inventory",
      type: "operation",
      actions: [
        {
          name: "printAction",
          functionRef: {
            refName: "printMessage",
            arguments: {
              message: '"Check Inventory " + .orderId',
            },
          },
          actionDataFilter: {
            fromStateData: ".",
            results: '{inventory: .item | test("0+") }',
          },
        },
      ],
      transition: "Item Available?",
    },
    {
      name: "Item Available?",
      type: "switch",
      dataConditions: [
        {
          condition: ".inventory",
          transition: "Prepare for Shipping",
        },
      ],
      defaultCondition: {
        transition: "Forward to External Supplier",
      },
    },
    {
      name: "Prepare for Shipping",
      type: "operation",
      transition: "Order Shipped or Cancelled",
      actions: [
        {
          name: "printAction",
          functionRef: {
            refName: "printMessage",
            arguments: {
              message: '"Prepare for Shipping"',
            },
          },
        },
      ],
    },
    {
      name: "Forward to External Supplier",
      type: "operation",
      transition: "Order Shipped or Cancelled",
      actions: [
        {
          name: "sendOrderRestCall",
          functionRef: {
            refName: "sendOrder",
            arguments: {
              "supplier-id": '"1"',
              content: ".orderId",
            },
          },
        },
      ],
    },
    {
      name: "Order Shipped or Cancelled",
      type: "event",
      transition: "Is Shipped?",
      exclusive: true,
      onEvents: [
        {
          eventRefs: ["shippingEvent"],
        },
        {
          eventRefs: ["cancelEvent"],
          eventDataFilter: {
            data: "{cancel:true}",
          },
        },
      ],
    },
    {
      name: "Is Shipped?",
      type: "switch",
      dataConditions: [
        {
          name: "order cancelled",
          condition: ".cancel == true",
          transition: "Compensate Order",
        },
      ],
      defaultCondition: {
        transition: "Notify Customer",
      },
    },
    {
      name: "Compensate Order",
      type: "operation",
      actions: [
        {
          name: "printAction",
          functionRef: {
            refName: "printMessage",
            arguments: {
              message: '"Compensate Order"',
            },
          },
        },
      ],
      end: {
        terminate: true,
        compensate: true,
      },
    },
    {
      name: "Notify Customer",
      type: "operation",
      actions: [
        {
          name: "printAction",
          functionRef: {
            refName: "printMessage",
            arguments: {
              message: '"Notify Customer"',
            },
          },
        },
      ],
      end: {
        terminate: true,
      },
    },
  ],
};

const initialContent = JSON.stringify(workflow);

const meta: Meta<SwfEditorProps> = {
  title: "Use cases/Order Workflow",
  component: SwfEditor,
  includeStories: /^[A-Z]/,
};

export default meta;
type Story = StoryObj<StorybookSwfEditorProps>;
const model = Specification.Workflow.fromSource(initialContent, true);

if (!model) {
  const validator = new WorkflowValidator(model);
  const errors = validator.isValid ? [] : validator.errors;

  errors.forEach((error) => {
    console.log(error.message);
  });

  throw new Error("SWF - model is null!!!!");
}

export const OrderWorkflow: Story = {
  render: Empty.render,
  args: {
    model: model,
    issueTrackerHref: "",
    isReadOnly: true,
    rawContent: initialContent,
  },
};
