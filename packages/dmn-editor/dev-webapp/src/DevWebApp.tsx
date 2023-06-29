import * as React from "react";
import { useCallback, useState } from "react";

import { DEFAULT_DEV_WEBAPP_DMN } from "./DefaultDmn";
import { DmnEditor } from "../../src/DmnEditor";

export function DevWebApp() {
  const [xml, setXml] = useState(DEFAULT_DEV_WEBAPP_DMN);

  const onDrop = useCallback((e: React.DragEvent) => {
    console.log("File(s) dropped");

    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.

    if (e.dataTransfer.items) {
      // Use DataTransferItemList interface to access the file(s)
      [...e.dataTransfer.items].forEach((item, i) => {
        if (item.kind === "file") {
          const reader = new FileReader();
          reader.addEventListener("load", ({ target }) => setXml(target?.result as string));
          reader.readAsText(item.getAsFile() as any);
        }
      });
    }
  }, []);

  const onDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault(); // Necessary to disable the browser's default 'onDrop' handling.
  }, []);

  return (
    <div className={"dmn-editor-dev-webapp"} onDrop={onDrop} onDragOver={onDragOver}>
      <h4 style={{ display: "inline" }}>DMN Editor :: Dev webapp </h4>
      &nbsp; &nbsp; &nbsp; &nbsp;
      <h5 style={{ display: "inline", textDecoration: "underline" }}>(Drag & drop a file anywhere to open it)</h5>
      <hr />
      <DmnEditor xml={xml} />
    </div>
  );
}
