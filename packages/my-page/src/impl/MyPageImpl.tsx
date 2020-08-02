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

import { MyPageApi, MyPageChannelApi, MyPageInitArgs, SvgDiagram } from "../api";
import { MessageBusClient } from "@kogito-tooling/envelope-bus/dist/api";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { useSubscription } from "@kogito-tooling/envelope-bus/dist/react";

interface Props {
  initArgs: MyPageInitArgs;
  channelApi: MessageBusClient<MyPageChannelApi>;
}

export const MyPageImpl = React.forwardRef<MyPageApi, Props>((props, forwardedRef) => {
  const [text, setText] = useState<string>();
  const [files, setFiles] = useState<string[]>([]);
  const [diagramSvgs, setDiagramSvgs] = useState<SvgDiagram[]>([]);

  const myPageApi: MyPageApi = useMemo(
    () => ({
      setText
    }),
    []
  );

  useImperativeHandle(forwardedRef, () => myPageApi, [myPageApi]);

  const requestOpenDiagrams = async () => {
    const svgs = await props.channelApi.request("getOpenDiagrams");
    setDiagramSvgs(svgs);
  };

  useSubscription(props.channelApi, "receive_ready", () => requestOpenDiagrams());

  useSubscription(props.channelApi, "receive_newEdit", () => requestOpenDiagrams());

  useEffect(() => {
    props.channelApi.request("receive_resourceListRequest", { pattern: "**" }).then(f => {
      setFiles(f.paths);
    });

    requestOpenDiagrams();
  }, []);

  const openFile = useCallback((path: string) => {
    props.channelApi.notify("receive_openFile", path);
  }, []);

  return (
    <>
      {props.initArgs.filePath && (
        <>
          <p>
            <b>The following file was open when this Page was open:</b>
          </p>
          <p>{props.initArgs.filePath}</p>
          <br />
          <br />
        </>
      )}
      {text && (
        <>
          <p>
            <b>This is the text set by default by the Envelope:</b>
          </p>
          <p>
            <i>"{text}"</i>
          </p>
          <br />
          <br />
        </>
      )}
      {files.length > 0 && (
        <>
          <p>
            <b>Files on workspace:</b>
          </p>
          <ul>
            {files.map(path => (
              <li key={path}>
                {path} <button onClick={() => openFile(path)}>Open</button>
              </li>
            ))}
          </ul>
          <br />
          <br />
        </>
      )}
      {diagramSvgs.length > 0 && (
        <>
          <p>
            <b>Open diagrams:</b>
          </p>
          {diagramSvgs.map(svg => {
            const url = URL.createObjectURL(new Blob([svg.img], { type: "image/svg+xml" }));
            return (
              <>
                <div style={{ background: "white", border: "1px solid black" }} key={svg.path}>
                  <p style={{ fontSize: "0.8em", color: "black" }}>{svg.path}</p>
                  <img style={{ height: "100px" }} src={url} onLoad={() => URL.revokeObjectURL(url)} />
                </div>
                <br />
              </>
            );
          })}
        </>
      )}
    </>
  );
});
