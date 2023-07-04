import "@patternfly/react-core/dist/styles/base.css";
import "reactflow/dist/style.css";

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Tab, TabTitleIcon, TabTitleText, Tabs } from "@patternfly/react-core/dist/js/components/Tabs";

import { BoxedExpression } from "./BoxedExpression";
import { Diagram } from "./Diagram";

import { IncludedModels } from "./IncludedModels";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { FileIcon } from "@patternfly/react-icons/dist/js/icons/file-icon";
import { CatalogIcon } from "@patternfly/react-icons/dist/js/icons/catalog-icon";

import "./DmnEditor.css"; // Leave it for last, as this overrides some of the PF and RF styles.
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Documentation } from "./Documentation";
import { DataTypes } from "./DataTypes";

const EMPTY_DMN_14 = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/">
</definitions>`;

export enum DmnEditorTab {
  EDITOR,
  DATA_TYPES,
  INCLUDED_MODELS,
  DOCUMENTATION,
}

export type DmnEditorRef = {
  getContent(): string;
};

export type DmnNodeWithExpression =
  | {
      type: "bkm";
      content: DMN14__tBusinessKnowledgeModel;
    }
  | {
      type: "decision";
      content: DMN14__tDecision;
    };

export const DmnEditor = React.forwardRef((props: { xml: string }, ref: React.Ref<DmnEditorRef>) => {
  const marshaller = useMemo(() => getMarshaller(props.xml.trim() || EMPTY_DMN_14), [props.xml]);

  const dmnInitial: { definitions: DMN14__tDefinitions } = useMemo(
    () => marshaller.parser.parse() as { definitions: DMN14__tDefinitions }, // FIXME: Casting to the latest version, but... what should we do?
    [marshaller.parser]
  );

  const [dmn, setDmn] = useState(dmnInitial);
  useEffect(() => {
    setDmn(dmnInitial);
  }, [dmnInitial]);

  useImperativeHandle(
    ref,
    () => ({
      getContent: () => marshaller.builder.build(dmn),
    }),
    [dmn, marshaller.builder]
  );

  const [openNodeWithExpression, setOpenNodeWithExpression] = useState<DmnNodeWithExpression | undefined>(undefined);

  const [tab, setTab] = useState(DmnEditorTab.EDITOR);
  const onTabChanged = useCallback((e, tab) => {
    setTab(tab);
  }, []);

  const rfContainer = useRef<HTMLDivElement>(null);

  return (
    <>
      <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
        <Tabs isFilled={true} activeKey={tab} onSelect={onTabChanged} role="region" style={{ height: "100%" }}>
          <Tab
            eventKey={DmnEditorTab.EDITOR}
            title={
              <>
                <TabTitleIcon>
                  <PficonTemplateIcon />
                </TabTitleIcon>
                <TabTitleText>Editor</TabTitleText>
              </>
            }
          >
            {tab === DmnEditorTab.EDITOR && (
              <div className={"kie-dmn-editor--diagram-container"} ref={rfContainer}>
                <Label style={{ position: "absolute", bottom: "10px" }}>{`DMN ${marshaller.version}`}</Label>
                {!openNodeWithExpression && (
                  <Diagram
                    dmn={dmn}
                    setDmn={setDmn}
                    container={rfContainer}
                    setOpenNodeWithExpression={setOpenNodeWithExpression}
                  />
                )}
                {openNodeWithExpression && (
                  <BoxedExpression
                    dmn={dmn}
                    setDmn={setDmn}
                    container={rfContainer}
                    openNodeWithExpression={openNodeWithExpression}
                    setOpenNodeWithExpression={setOpenNodeWithExpression}
                  />
                )}
              </div>
            )}
          </Tab>

          <Tab
            eventKey={DmnEditorTab.DATA_TYPES}
            title={
              <>
                <TabTitleIcon>
                  <InfrastructureIcon />
                </TabTitleIcon>
                <TabTitleText>Data types</TabTitleText>
              </>
            }
          >
            {tab === DmnEditorTab.DATA_TYPES && <DataTypes />}
          </Tab>

          <Tab
            eventKey={DmnEditorTab.INCLUDED_MODELS}
            title={
              <>
                <TabTitleIcon>
                  <FileIcon />
                </TabTitleIcon>
                <TabTitleText>Included models</TabTitleText>
              </>
            }
          >
            {tab === DmnEditorTab.INCLUDED_MODELS && <IncludedModels dmn={dmn} setDmn={setDmn} />}
          </Tab>

          <Tab
            eventKey={DmnEditorTab.DOCUMENTATION}
            title={
              <>
                <TabTitleIcon>
                  <CatalogIcon />
                </TabTitleIcon>
                <TabTitleText>Documentation</TabTitleText>
              </>
            }
          >
            {tab === DmnEditorTab.DOCUMENTATION && <Documentation />}
          </Tab>
        </Tabs>
      </PageSection>
    </>
  );
});
