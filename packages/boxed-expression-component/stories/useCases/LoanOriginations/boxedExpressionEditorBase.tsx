import * as React from "react";
import { BoxedExpressionEditor } from "../../../src/expressions";
import { ExpressionDefinition } from "../../../src/api";
import { loanOriginationsDataTypes } from "./dataTypes";
import { beeGwtService, pmmlParams } from "../../boxedExpressionWrapper";

export function BoxedExpressionEditorBase(props: { expressionDefinition: ExpressionDefinition }) {
  const emptyRef = React.useRef<HTMLDivElement>(null);

  return (
    <div ref={emptyRef}>
      <BoxedExpressionEditor
        decisionNodeId={"_00000000-0000-0000-0000-000000000000"}
        expressionDefinition={props.expressionDefinition}
        setExpressionDefinition={() => {}}
        dataTypes={loanOriginationsDataTypes}
        scrollableParentRef={emptyRef}
        beeGwtService={beeGwtService}
        pmmlParams={pmmlParams}
        isResetSupportedOnRootExpression={false}
      />
    </div>
  );
}
