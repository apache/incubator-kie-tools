import * as React from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";

export function DocumentationLinksInput() {
  return (
    <>
      <TextInput
        aria-label={"Documentation links"}
        type={"text"}
        isDisabled={false}
        value={""}
        placeholder={"Add a documentation link..."}
      />
    </>
  );
}
