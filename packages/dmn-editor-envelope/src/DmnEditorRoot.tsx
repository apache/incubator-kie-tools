import * as React from "react";
import { DmnEditor, OnDmnModelChange } from "@kie-tools/dmn-editor/dist/DmnEditor";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-editor/dist/Dmn15Spec";
import { DMN_LATEST_VERSION, DmnLatestModel, DmnMarshaller } from "@kie-tools/dmn-marshaller";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";

export const EMPTY_DMN_15 = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export type DmnEditorRootProps = {
  exposing: (s: DmnEditorRoot) => void;
  onReady: () => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
};

export type DmnEditorRootState = {
  marshaller: DmnMarshaller<typeof DMN_LATEST_VERSION>;
  stack: DmnLatestModel[];
  pointer: number;
  path: string | undefined;
};

export class DmnEditorRoot extends React.Component<DmnEditorRootProps, DmnEditorRootState> {
  constructor(props: DmnEditorRootProps) {
    super(props);
    props.exposing(this);
    const marshaller = getMarshaller(EMPTY_DMN_15(), { upgradeTo: "latest" });
    this.state = {
      marshaller,
      stack: [marshaller.parser.parse()],
      pointer: 0,
      path: undefined,
    };
  }

  public componentDidMount() {
    this.props.onReady();
  }

  // Exposed API

  public async undo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }

  public async redo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }

  public async getContent(): Promise<string> {
    return this.state.marshaller.builder.build(this.model);
  }

  public async setContent(path: string, content: string): Promise<void> {
    this.setState((prev) => {
      const newMarshaller = getMarshaller(content || EMPTY_DMN_15(), { upgradeTo: "latest" });

      // External change to the same file.
      if (prev.path === path) {
        const newStack = prev.stack.slice(0, prev.pointer + 1);
        return {
          path,
          marshaller: newMarshaller,
          stack: [...newStack, newMarshaller.parser.parse()],
          pointer: newStack.length,
        };
      }

      // Different file opened. Need to reset everything.
      else {
        return {
          path,
          marshaller: newMarshaller,
          stack: [newMarshaller.parser.parse()],
          pointer: 0,
        };
      }
    });
  }

  // Internal methods

  public get model() {
    return this.state.stack[this.state.pointer];
  }

  private onModelChange: OnDmnModelChange = (model) => {
    this.setState(
      (prev) => {
        const newStack = prev.stack.slice(0, prev.pointer + 1);
        return {
          ...prev,
          stack: [...newStack, model],
          pointer: newStack.length,
        };
      },
      () => this.props.onNewEdit({ id: `${this.state.path}__${generateUuid()}` })
    );
  };

  public render() {
    return (
      <DmnEditor
        originalVersion={this.state.marshaller.originalVersion}
        model={this.model}
        externalModelsByNamespace={{}}
        evaluationResults={[]}
        validationMessages={[]}
        externalContextName={""}
        externalContextDescription={""}
        issueTrackerHref={""}
        onModelChange={this.onModelChange}
        onRequestExternalModelByPath={() => Promise.resolve(null)}
        onRequestExternalModelsAvailableToInclude={() => Promise.resolve([])}
        onRequestToJumpToPath={() => {}}
      />
    );
  }
}
