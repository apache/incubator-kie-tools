import * as React from "react";
import { useCallback, useMemo, useRef, useState } from "react";

import "@patternfly/react-core/dist/styles/base.css";

import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";

import { DEFAULT_DEV_WEBAPP_DMN } from "./DefaultDmn";
import * as DmnEditor from "../../src/DmnEditor";
import { DmnDefinitions, DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";

import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { SPEC } from "../../src/Spec";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { avaiableModelsByPath, modelsByNamespace } from "./AvailableModelsToInclude";

const initialDmnMarshaller = getMarshaller(DEFAULT_DEV_WEBAPP_DMN);

const EMPTY_DMN_15 = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export function DevWebApp() {
  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("DMN Editor :: Dev webapp :: File(s) dropped! Opening it.");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const reader = new FileReader();
          reader.addEventListener("load", ({ target }) => {
            const marshaller = getMarshaller(target?.result as string);
            setState({ marshaller, stack: [marshaller.parser.parse()], pointer: 0 });
          });
          reader.readAsText(item.getAsFile() as any);
        }
      });
    }
  }, []);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  const ref = useRef<DmnEditor.DmnEditorRef>(null);

  const reset = useCallback(() => {
    const marshaller = getMarshaller(EMPTY_DMN_15());
    setState({
      marshaller,
      stack: [marshaller.parser.parse()],
      pointer: 0,
    });
  }, []);

  const [state, setState] = useState<{ marshaller: DmnMarshaller; stack: DmnDefinitions[]; pointer: number }>({
    marshaller: initialDmnMarshaller,
    stack: [initialDmnMarshaller.parser.parse()],
    pointer: 0,
  });

  const currentModel = state.stack[state.pointer];

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAsXml = useCallback(() => {
    if (downloadRef.current) {
      const fileBlob = new Blob([state.marshaller.builder.build(currentModel)], { type: "text/xml" });
      downloadRef.current.download = `dmn-${makeid(10)}.dmn`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    }
  }, [currentModel, state.marshaller.builder]);

  const copyAsXml = useCallback(() => {
    navigator.clipboard.writeText(state.marshaller.builder.build(currentModel));
  }, [currentModel, state.marshaller.builder]);

  const undo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }, []);

  const redo = useCallback(() => {
    setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }, []);

  const onModelChange = useCallback<DmnEditor.OnDmnModelChange>((model) => {
    setState((prev) => {
      const newStack = prev.stack.slice(0, prev.pointer + 1);
      return {
        ...prev,
        stack: [...newStack, model],
        pointer: newStack.length,
      };
    });
  }, []);

  const dependenciesByNamespace = useMemo<DmnEditor.DependenciesByNamespace>(() => {
    return (currentModel.definitions.import ?? []).reduce((acc, i) => {
      acc[i["@_namespace"]] = modelsByNamespace[i["@_namespace"]];
      return acc;
    }, {} as DmnEditor.DependenciesByNamespace);
  }, [currentModel.definitions.import]);

  const onRequestModelByPath = useCallback<DmnEditor.OnRequestModelByPath>(async (path) => {
    return avaiableModelsByPath[path] ?? null;
  }, []);

  const onRequestModelsAvailableToInclude = useCallback<DmnEditor.OnRequestModelsAvailableToInclude>(async () => {
    return Object.keys(avaiableModelsByPath);
  }, []);

  const evaluationResults = useMemo<DmnEditor.EvaluationResults>(() => {
    return {};
  }, []);

  const validationMessages = useMemo<DmnEditor.ValidationMessages>(() => {
    return {};
  }, []);

  return (
    <>
      <Page onDragOver={onDragOver} onDrop={onDrop}>
        <PageSection variant={"light"} isFilled={false} padding={{ default: "padding" }}>
          <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
            <FlexItem shrink={{ default: "shrink" }}>
              <h3>DMN Editor :: Dev webapp </h3>
            </FlexItem>
            <FlexItem>
              <h5>(Drag & drop a file anywhere to open it)</h5>
            </FlexItem>
            <FlexItem shrink={{ default: "shrink" }}>
              <button onClick={undo}>Undo</button>
              &nbsp; &nbsp;
              <button onClick={redo}>Redo</button>
              &nbsp; &nbsp; | &nbsp; &nbsp;
              <button onClick={reset}>Reset</button>
              &nbsp; &nbsp;
              <button onClick={copyAsXml}>Copy as XML</button>
              &nbsp; &nbsp;
              <button onClick={downloadAsXml}>Download as XML</button>
            </FlexItem>
          </Flex>
          <a ref={downloadRef} />
        </PageSection>
        <hr />
        <PageSection variant={"light"} isFilled={true} hasOverflowScroll={true} aria-label={"editor"}>
          <DmnEditor.DmnEditor
            ref={ref}
            model={currentModel}
            onModelChange={onModelChange}
            onRequestModelByPath={onRequestModelByPath}
            onRequestModelsAvailableToInclude={onRequestModelsAvailableToInclude}
            dependenciesByNamespace={dependenciesByNamespace}
            validationMessages={validationMessages}
            evaluationResults={evaluationResults}
          />
        </PageSection>
      </Page>
    </>
  );
}

function makeid(length: number) {
  let result = "";
  const characters = "abcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}
