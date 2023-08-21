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
import { useRef, useState, useEffect, useCallback } from "react";

interface Props {
  content: string;
}

export const Dashbuilder = (props: Props) => {
  const container = useRef<HTMLIFrameElement>(null);
  const [ready, setReady] = useState<boolean>(false);

  useEffect(() => {
    if (ready) {
      container.current!.contentWindow!.postMessage(props.content);
    }
  }, [props.content, ready]);

  return (
    <>
      <iframe
        ref={container}
        src="dashbuilder-client/index.html"
        onLoad={(e) => {
          window.onmessage = (e) => {
            if (e.data === "ready") {
              setReady(true);
              window.onmessage = null;
            }
          };
        }}
        width={"100%"}
        height={"100%"}
        style={{
          width: "100%",
          height: "100%",
        }}
      ></iframe>
    </>
  );
};
