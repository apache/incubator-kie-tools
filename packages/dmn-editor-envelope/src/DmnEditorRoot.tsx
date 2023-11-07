import * as __path from "path";
import * as React from "react";
import * as DmnEditor from "@kie-tools/dmn-editor/dist/DmnEditor";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  ContentType,
  ResourceContent,
  SearchType,
  WorkspaceChannelApi,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-editor/dist/Dmn15Spec";
import { DMN_LATEST_VERSION, DmnLatestModel, DmnMarshaller } from "@kie-tools/dmn-marshaller";
import { domParser } from "@kie-tools/xml-parser-ts";
import { buildXmlHref } from "@kie-tools/dmn-editor/dist/xml/xmlHrefs";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { useEffect, useMemo, useState } from "react";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { PMML_NAMESPACE } from "@kie-tools/dmn-editor/dist/store/useDiagramData";

export const EXTERNAL_MODELS_SEARCH_GLOB_PATTERN = "**/*.{dmn,pmml}";

export const EMPTY_DMN = () => `<?xml version="1.0" encoding="UTF-8"?>
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
  onRequestFileList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFile: WorkspaceChannelApi["kogitoWorkspace_openFile"];
};

export type DmnEditorRootState = {
  marshaller: DmnMarshaller<typeof DMN_LATEST_VERSION>;
  stack: DmnLatestModel[];
  pointer: number;
  path: string | undefined;
  externalModelsByNamespace: DmnEditor.ExternalModelsIndex;
};

export class DmnEditorRoot extends React.Component<DmnEditorRootProps, DmnEditorRootState> {
  constructor(props: DmnEditorRootProps) {
    super(props);
    props.exposing(this);
    const marshaller = getMarshaller(EMPTY_DMN(), { upgradeTo: "latest" });
    this.state = {
      externalModelsByNamespace: {},
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
      const newMarshaller = getMarshaller(content || EMPTY_DMN(), { upgradeTo: "latest" });

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

  private setExternalModelsByNamespace = (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => {
    this.setState((prev) => ({ ...prev, externalModelsByNamespace }));
  };

  private onModelChange: DmnEditor.OnDmnModelChange = (model) => {
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

  private onRequestExternalModelsAvailableToInclude: DmnEditor.OnRequestExternalModelsAvailableToInclude = async () => {
    const list = await this.props.onRequestFileList({
      pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN,
      opts: { type: SearchType.TRAVERSAL },
    });

    return list.paths.filter((path) => {
      return path !== this.state.path;
    });
  };

  private onRequestExternalModelByPath: DmnEditor.OnRequestExternalModelByPath = async (path) => {
    const resource = await this.props.onRequestFileContent({ path, opts: { type: ContentType.TEXT } });
    const ext = __path.extname(path).replace(".", "");
    if (ext === "dmn") {
      return {
        path,
        type: "dmn",
        model: getMarshaller(resource?.content ?? "", { upgradeTo: "latest" }).parser.parse(),
        svg: "",
      };
    } else if (ext === "pmml") {
      return {
        path,
        type: "pmml",
        model: XML2PMML(resource?.content ?? ""),
      };
    } else {
      throw new Error(`Unknown extension '${ext}'.`);
    }
  };

  public render() {
    return (
      <>
        <DmnEditor.DmnEditor
          originalVersion={this.state.marshaller.originalVersion}
          model={this.model}
          externalModelsByNamespace={this.state.externalModelsByNamespace}
          evaluationResults={[]}
          validationMessages={[]}
          externalContextName={""}
          externalContextDescription={""}
          issueTrackerHref={""}
          onModelChange={this.onModelChange}
          onRequestExternalModelByPath={this.onRequestExternalModelByPath}
          onRequestExternalModelsAvailableToInclude={this.onRequestExternalModelsAvailableToInclude}
          onRequestToJumpToPath={this.props.onOpenFile}
        />
        <ExternalModelsManager
          thisDmnsPath={this.state.path}
          model={this.model}
          onChange={this.setExternalModelsByNamespace}
          onRequestFileList={this.props.onRequestFileList}
          onRequestFileContent={this.props.onRequestFileContent}
        />
      </>
    );
  }
}

const NAMESPACES_EFFECT_SEPARATOR = " , ";

function ExternalModelsManager({
  thisDmnsPath,
  model,
  onChange,
  onRequestFileContent,
  onRequestFileList,
}: {
  thisDmnsPath: string | undefined;
  model: DmnLatestModel;
  onChange: (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => void;
  onRequestFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onRequestFileList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
}) {
  const namespaces = useMemo(
    () => (model.definitions.import ?? []).map((i) => i["@_namespace"]).join(NAMESPACES_EFFECT_SEPARATOR),
    [model.definitions.import]
  );

  const [externalUpdatesCount, setExternalUpdatesCount] = useState(0);

  // This is a hack. Every time a file is updates in KIE Sandbox, the Shared Worker emits an event to this BroadcastChannel.
  // By listening to it, we can reload the `externalModelsByNamespace` object. This makes the DMN Editor react to external changes,
  // Which is very important for multi-file editing.
  //
  // Now, this mechanism is not ideal. We would ideally only be notified on changes to relevant files, but this sub-system does not exist yet.
  // The consequence of this "hack" is some extra reloads.
  useEffect(() => {
    const bc = new BroadcastChannel("workspaces_files");
    bc.onmessage = () => {
      setExternalUpdatesCount((prev) => prev + 1);
    };
    return () => {
      bc.close();
    };
  }, []);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    onRequestFileList({ pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN, opts: { type: SearchType.TRAVERSAL } })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.paths.length; i++) {
          const path = list.paths[i];
          if (path === thisDmnsPath) {
            continue;
          }

          resources.push(onRequestFileContent({ path, opts: { type: ContentType.TEXT } }));
        }
        return Promise.all(resources);
      })
      .then((resources) => {
        const index: DmnEditor.ExternalModelsIndex = {};

        const namespacesSet = new Set(namespaces.split(NAMESPACES_EFFECT_SEPARATOR));

        for (let i = 0; i < resources.length; i++) {
          const r = resources[i];
          const content = r?.content ?? "";
          const path = r?.path ?? "";
          const ext = __path.extname(path).replace(".", "");

          if (ext === "dmn") {
            const namespace = domParser.getDomDocument(content).documentElement.getAttribute("namespace");
            if (namespace && namespacesSet.has(namespace)) {
              index[namespace] = {
                path,
                model: getMarshaller(content, { upgradeTo: "latest" }).parser.parse(),
                type: "dmn",
                svg: "",
              };
            }
          } else if (ext === "pmml") {
            const namespace = buildXmlHref({ namespace: PMML_NAMESPACE, id: path });
            if (namespace && namespacesSet.has(namespace)) {
              index[namespace] = {
                path,
                model: XML2PMML(content),
                type: "pmml",
              };
            }
          } else {
            throw new Error(`Unknown extension '${ext}'.`);
          }
        }

        if (!canceled) {
          onChange(index);
        }
      });

    return () => {
      canceled = true;
    };
  }, [namespaces, onChange, onRequestFileContent, onRequestFileList, thisDmnsPath, externalUpdatesCount]);

  return <></>;
}
