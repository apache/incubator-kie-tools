import * as React from "react";
import { BoxedExpressionEditor } from "../src/expressions";
import { BeeGwtService, DmnDataType, ExpressionDefinition, PmmlParam } from "../src/api";
import { beeGwtService, pmmlParams, dataTypes } from "./boxedExpressionStoriesWrapper";

export function BoxedExpressionComponentWrapper(props: {
  expressionDefinition: ExpressionDefinition;
  dataTypes?: DmnDataType[];
  pmmlParams?: PmmlParam[];
  beeGwtService?: BeeGwtService;
  isResetSupportedOnRootExpression?: boolean;
}) {
  const emptyRef = React.useRef<HTMLDivElement>(null);

  return (
    <div ref={emptyRef}>
      <BoxedExpressionEditor
        decisionNodeId={"_00000000-0000-0000-0000-000000000000"}
        expressionDefinition={props.expressionDefinition}
        setExpressionDefinition={() => {}}
        dataTypes={props.dataTypes ?? dataTypes}
        scrollableParentRef={emptyRef}
        beeGwtService={props.beeGwtService ?? beeGwtService}
        pmmlParams={props.pmmlParams ?? pmmlParams}
        isResetSupportedOnRootExpression={props.isResetSupportedOnRootExpression}
      />
    </div>
  );
}
