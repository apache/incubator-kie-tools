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
import { useCallback, useEffect, useState } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";

const samplePaths = [
  { fileName: "sample", path: "examples/sample.base64png" },
  { fileName: "luiz", path: "examples/luiz.base64png" },
  { fileName: "tiago", path: "examples/tiago.base64png" },
  { fileName: "dorinha", path: "examples/dorinha.base64png" },
];

/**
 * A gallery component with samples to be opened
 */
export function Base64PngGallery({ setFile }: { setFile: React.Dispatch<EmbeddedEditorFile> }) {
  const openSample = useCallback(
    (fileName: string, filePath: string) => {
      setFile({
        isReadOnly: false,
        fileExtension: "base64png",
        fileName: fileName,
        getFileContents: () => fetch(filePath).then((response) => response.text()),
        normalizedPosixPathRelativeToTheWorkspaceRoot: filePath,
      });
    },
    [setFile]
  );

  const [images, setImages] = useState<{ name: string; content: string; path: string }[]>([]);
  useEffect(() => {
    Promise.all(
      samplePaths.map(({ fileName, path }) =>
        fetch(path)
          .then((response) => response.text())
          .then((content) => ({ name: fileName, content: content, path }))
      )
    ).then((samples) => setImages((prev) => [...prev, ...samples]));
  }, []);

  return (
    <div style={{ padding: "16px" }}>
      {images.map((image) => (
        <Card
          key={image.name}
          onClick={() => openSample(image.name, image.path)}
          isSelectable={true}
          style={{ padding: "8px", marginBottom: "16px" }}
        >
          <img alt={image.name} width={"140px"} src={`data:image/png;base64,${image.content}`} />
        </Card>
      ))}
    </div>
  );
}
