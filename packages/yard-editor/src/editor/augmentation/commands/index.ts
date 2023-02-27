import { YardEditorChannelApi } from "../../../api";
import { editor, Position } from "monaco-editor";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export type YardLanguageServiceCommandTypes = "yard.ls.commands.AddRowToTable";
export type YardLanguageServiceCommandArgs = {
  "yard.ls.commands.AddRowToTable": {};
};
export type YardLanguageServiceCommandIds = Record<YardLanguageServiceCommandTypes, string>;

export function initAugmentationCommands(
  editorInstance: editor.IStandaloneCodeEditor,
  channelApi: MessageBusClientApi<YardEditorChannelApi>
): YardLanguageServiceCommandIds {
  return {
    "yard.ls.commands.AddRowToTable": editorInstance.addCommand(
      0,
      async (ctx, args: YardLanguageServiceCommandArgs["yard.ls.commands.AddRowToTable"]) => {
        // TODO send to channel API . notifications
      }
    )!,
  };
}
