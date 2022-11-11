/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, Position } from "vscode-languageserver-types";
import {
  SwfLanguageServiceCommandArgs,
  SwfLanguageServiceCommandExecution,
  SwfLanguageServiceCommandTypes,
} from "../api";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { SwfLanguageServiceConfig } from "./SwfLanguageService";
import { CodeCompletionStrategy, SwfJsonPath, SwfLsNode, SwfLsNodeType } from "./types";

export type SwfLanguageServiceCodeLensesFunctionsArgs = {
  config: SwfLanguageServiceConfig;
  document: TextDocument;
  content: string;
  rootNode: SwfLsNode;
  codeCompletionStrategy: CodeCompletionStrategy;
};

const createCodeLenses = (args: {
  document: TextDocument;
  rootNode: SwfLsNode;
  jsonPath: SwfJsonPath;
  commandDelegates: (args: {
    position: Position;
    node: SwfLsNode;
  }) => ({ title: string } & SwfLanguageServiceCommandExecution<any>)[];
  positionLensAt: "begin" | "end";
}): CodeLens[] => {
  const nodes = findNodesAtLocation({ root: args.rootNode, path: args.jsonPath });
  const codeLenses = nodes.flatMap((node) => {
    // Only position at the end if the type is object or array and has at least one child.
    const position =
      args.positionLensAt === "end" &&
      (node.type === "object" || node.type === "array") &&
      (node.children?.length ?? 0) > 0
        ? args.document.positionAt(node.offset + node.length)
        : args.document.positionAt(node.offset);

    return args.commandDelegates({ position, node }).map((command) => ({
      command: {
        command: command.name,
        title: command.title,
        arguments: command.args,
      },
      range: {
        start: position,
        end: position,
      },
    }));
  });

  return codeLenses;
};

/**
 * Create code lenses for the 'OpenCompletionItems' command
 *
 * @param args -
 * @returns the CodeLenses created
 */
const createOpenCompletionItemsCodeLenses = (
  args: {
    jsonPath: string[];
    title: string;
    nodeType: SwfLsNodeType;
    commandName?: SwfLanguageServiceCommandTypes;
  } & SwfLanguageServiceCodeLensesFunctionsArgs
): CodeLens[] =>
  createCodeLenses({
    ...args,
    positionLensAt: "begin",
    commandDelegates: ({ node }) => {
      const commandName: SwfLanguageServiceCommandTypes = args.commandName || "swf.ls.commands.OpenCompletionItems";

      if (
        node.type !== args.nodeType ||
        !args.codeCompletionStrategy.shouldCreateCodelens({ node, commandName, content: args.content })
      ) {
        return [];
      }

      const newCursorPosition = args.codeCompletionStrategy.getStartNodeValuePosition(args.document, node);

      return [
        {
          name: commandName,
          title: args.title,
          args: [{ newCursorPosition } as SwfLanguageServiceCommandArgs[typeof commandName]],
        },
      ];
    },
  });

/**
 * Functions to create CodeLenses
 */
export const SwfLanguageServiceCodeLenses = {
  createNewSWF: (): CodeLens[] => {
    const position = Position.create(0, 0);
    const command: SwfLanguageServiceCommandTypes = "swf.ls.commands.OpenCompletionItems";

    return [
      {
        command: {
          command,
          title: "Create a Serverless Workflow",
          arguments: [{ newCursorPosition: position } as SwfLanguageServiceCommandArgs[typeof command]],
        },
        range: {
          start: position,
          end: position,
        },
      },
    ];
  },

  addFunction: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createOpenCompletionItemsCodeLenses({
      ...args,
      jsonPath: ["functions"],
      title: "+ Add function...",
      nodeType: "array",
    }),

  addEvent: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createOpenCompletionItemsCodeLenses({
      ...args,
      jsonPath: ["events"],
      title: "+ Add event...",
      nodeType: "array",
    }),

  addState: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createOpenCompletionItemsCodeLenses({
      ...args,
      jsonPath: ["states"],
      title: "+ Add state...",
      nodeType: "array",
    }),

  setupServiceRegistries: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createCodeLenses({
      document: args.document,
      rootNode: args.rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        const commandName: SwfLanguageServiceCommandTypes = "swf.ls.commands.OpenServiceRegistriesConfig";

        if (
          node.type !== "array" ||
          !args.codeCompletionStrategy.shouldCreateCodelens({ node, commandName, content: args.content }) ||
          !args.config.shouldConfigureServiceRegistries()
        ) {
          return [];
        }

        return [
          {
            name: commandName,
            title: "↪ Setup Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs[typeof commandName]],
          },
        ];
      },
    }),

  logInServiceRegistries: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createCodeLenses({
      document: args.document,
      rootNode: args.rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        const commandName: SwfLanguageServiceCommandTypes = "swf.ls.commands.LogInServiceRegistries";

        if (
          node.type !== "array" ||
          !args.codeCompletionStrategy.shouldCreateCodelens({ node, commandName, content: args.content }) ||
          args.config.shouldConfigureServiceRegistries() ||
          !args.config.shouldServiceRegistriesLogIn()
        ) {
          return [];
        }

        return [
          {
            name: commandName,
            title: "↪ Log in Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs[typeof commandName]],
          },
        ];
      },
    }),

  refreshServiceRegistries: (args: SwfLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createCodeLenses({
      document: args.document,
      rootNode: args.rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        const commandName: SwfLanguageServiceCommandTypes = "swf.ls.commands.RefreshServiceRegistries";

        if (
          node.type !== "array" ||
          !args.codeCompletionStrategy.shouldCreateCodelens({ node, commandName, content: args.content }) ||
          args.config.shouldConfigureServiceRegistries() ||
          !args.config.canRefreshServices()
        ) {
          return [];
        }

        return [
          {
            name: commandName,
            title: "↺ Refresh Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs[typeof commandName]],
          },
        ];
      },
    }),
};
