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

import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, Position } from "vscode-languageserver-types";
import {
  EditorLanguageServiceCommandArgs,
  EditorLanguageServiceCommandExecution,
  EditorLanguageServiceCommandTypes,
} from "../api";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { ELsCodeCompletionStrategy, ELsJsonPath, ELsNode, ELsNodeType } from "./types";

export type EditorLanguageServiceCodeLensesFunctionsArgs<CommandTypes = never> = {
  document: TextDocument;
  content: string;
  rootNode: ELsNode;
  codeCompletionStrategy: ELsCodeCompletionStrategy<CommandTypes>;
};

export const createCodeLenses = (args: {
  document: TextDocument;
  rootNode: ELsNode;
  jsonPath: ELsJsonPath;
  commandDelegates: (args: {
    position: Position;
    node: ELsNode;
  }) => ({ title: string } & EditorLanguageServiceCommandExecution<any, any>)[];
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
export const createOpenCompletionItemsCodeLenses = (
  args: {
    jsonPath: string[];
    title: string;
    nodeType: ELsNodeType;
    commandName?: EditorLanguageServiceCommandTypes;
  } & EditorLanguageServiceCodeLensesFunctionsArgs<EditorLanguageServiceCommandTypes>
): CodeLens[] =>
  createCodeLenses({
    ...args,
    positionLensAt: "begin",
    commandDelegates: ({ node }) => {
      const commandName: EditorLanguageServiceCommandTypes =
        args.commandName || "editor.ls.commands.OpenCompletionItems";

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
          args: [{ newCursorPosition } as EditorLanguageServiceCommandArgs[typeof commandName]],
        },
      ];
    },
  });

/**
 * Create a CodeLens for creating a new file to show when the editor is empty
 *
 * @param title the title of the CodeLens
 * @returns
 */
export const createNewFileCodeLens = (title: string): CodeLens[] => {
  const position = Position.create(0, 0);
  const command: EditorLanguageServiceCommandTypes = "editor.ls.commands.OpenCompletionItems";

  return [
    {
      command: {
        command,
        title,
        arguments: [{ newCursorPosition: position } as EditorLanguageServiceCommandArgs[typeof command]],
      },
      range: {
        start: position,
        end: position,
      },
    },
  ];
};

/**
 * Functions to create CodeLenses
 */
export type EditorLanguageServiceCodeLenses = {
  [name: string]: (args: EditorLanguageServiceCodeLensesFunctionsArgs) => CodeLens[];
  createNewFile: () => CodeLens[];
};
