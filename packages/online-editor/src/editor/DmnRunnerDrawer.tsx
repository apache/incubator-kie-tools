/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import React, { useEffect, useMemo, useRef, useState } from "react";
import { useCallback } from "react";
import { DmnRunner } from "../common/DmnRunner";
import { AutoForm } from "uniforms-patternfly";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import {
  Alert,
  DescriptionListTerm,
  DescriptionListGroup,
  DescriptionListDescription,
  Title,
  DrawerContent,
  DrawerContentBody,
  Drawer,
  DrawerPanelContent
} from "@patternfly/react-core";

enum DmnRunnerStatusResponse {
  SUCCESS,
  WARNING,
  NONE
}

interface Props {
  editorContent: (() => Promise<string>) | undefined;
  jsonSchemaBridge: JSONSchemaBridge | undefined;
  onRunDmn: (e: React.MouseEvent<any>) => void;
}

const PF_BREAKPOINT_SM = 576;
const PF_BREAKPOINT_MD = 768;
const PF_BREAKPOINT_LG = 992;
const PF_BREAKPOINT_XL = 1200;
const PF_BREAKPOINT_2XL = 1450;

export function DmnRunnerDrawer(props: Props) {
  const [dmnRunnerResponse, setDmnRunnerResponse] = useState();
  const [dmnRunnerResponseStatus, setDmnRunnerResponseStatus] = useState(DmnRunnerStatusResponse.NONE);
  const autoFormRef = useRef<HTMLFormElement>();

  const alertMessage = useMemo(() => {
    switch (dmnRunnerResponseStatus) {
      case DmnRunnerStatusResponse.SUCCESS:
        return <Alert title={"Your request has been successfully processed"} variant={"success"} isInline={true} />;
      case DmnRunnerStatusResponse.WARNING:
        return <Alert title={"Your request couldn't be processed"} variant={"warning"} isInline={true} />;
      case DmnRunnerStatusResponse.NONE:
        return;
    }
  }, [dmnRunnerResponseStatus]);

  const onSubmit = useCallback(
    ({ context }) => {
      if (props.editorContent) {
        props.editorContent().then((model: string) => {
          DmnRunner.validateForm({ context, model })
            .then(res => res.json())
            .then(json => {
              setDmnRunnerResponse(json);
              setDmnRunnerResponseStatus(DmnRunnerStatusResponse.SUCCESS);
            })
            .catch(() => setDmnRunnerResponseStatus(DmnRunnerStatusResponse.WARNING));
        });
      }
    },
    [props.editorContent]
  );

  useEffect(() => {
    if (props.jsonSchemaBridge) {
      const form = document.getElementById("form") as HTMLFormElement;
      Array.from(form.getElementsByTagName("input")).forEach(input =>
        input.addEventListener("change", () => autoFormRef.current!.onSubmit())
      );
      (form.querySelector("button[type='submit']")! as HTMLButtonElement).style.display = "none";

      const observables = Array.from(form.querySelectorAll(".pf-c-select")).map(select => {
        const observer = new MutationObserver(() => autoFormRef.current!.onSubmit());
        observer.observe(select, {
          childList: true,
          subtree: true
        });
        return observer;
      });

      return () => {
        observables.forEach(observable => observable.disconnect());
      };
    }
  }, [props.jsonSchemaBridge, autoFormRef]);

  const [drawerPosition, setDrawerPosition] = useState<"right" | "bottom">("right");

  const handleResize = useCallback(() => {
    const width = window.innerWidth;

    if (width <= PF_BREAKPOINT_XL) {
      setDrawerPosition("bottom");
    } else {
      setDrawerPosition("right");
    }
  }, []);

  useEffect(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  return (
    <Drawer isStatic={true} position={drawerPosition}>
      <DrawerContent
        panelContent={
          <DrawerPanelContent widths={{ default: "width_50" }}>
            <p>Response Empty State</p>
          </DrawerPanelContent>
        }
      >
        <DrawerContentBody>
          {props.jsonSchemaBridge ? (
            <AutoForm id={"form"} ref={autoFormRef} schema={props.jsonSchemaBridge} onSubmit={onSubmit} />
          ) : (
            <p>Form Empty State</p>
          )}
        </DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
}

interface RecursiveJitResponseProps {
  responseObject: object;
  withoutPadding: boolean;
}

function JitResponse(props: RecursiveJitResponseProps) {
  return (
    <div style={{ padding: "10px" }}>
      {[...Object.entries(props.responseObject)].map(([key, value]: any[], index) => (
        <div key={`${key}-${index}-jit-response`}>
          {typeof value === "object" && value !== null ? (
            <div>
              <Title headingLevel={"h5"}>{key}</Title>
              <JitResponse responseObject={value} withoutPadding={true} />
            </div>
          ) : (
            <div style={props.withoutPadding ? {} : { padding: "10px" }}>
              <DescriptionListGroup>
                <DescriptionListTerm>{key}</DescriptionListTerm>
                {value ? (
                  <DescriptionListDescription>{value}</DescriptionListDescription>
                ) : (
                  <DescriptionListDescription>Not provided</DescriptionListDescription>
                )}
              </DescriptionListGroup>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
