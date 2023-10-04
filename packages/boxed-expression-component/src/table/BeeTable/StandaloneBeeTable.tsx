import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { BoxedExpressionEditorContextProvider } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionRoot.css";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { ResizingWidthsContextProvider } from "../../resizing/ResizingWidthsContext";
import { BeeTable } from "./BeeTable";

export function StandaloneBeeTable<R extends object>(
  props: BeeTableProps<R> & { scrollableParentRef: React.RefObject<HTMLElement> }
) {
  const dataTypes = useMemo(() => {
    return [];
  }, []);

  const setExpression = useCallback(() => {
    // Empty on purpose.
  }, []);

  const expression = useMemo<ExpressionDefinition>(() => {
    return {
      id: generateUuid(),
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    };
  }, []);

  return (
    <div className="expression-container" data-testid="standalone-bee-table">
      <div className="expression-container-box">
        <div className={`standalone-bee-table ${props.tableId}`}>
          <I18nDictionariesProvider
            defaults={boxedExpressionEditorI18nDefaults}
            dictionaries={boxedExpressionEditorDictionaries}
            initialLocale={navigator.language}
            ctx={BoxedExpressionEditorI18nContext}
          >
            <BoxedExpressionEditorContextProvider
              scrollableParentRef={props.scrollableParentRef}
              dataTypes={dataTypes}
              decisionNodeId={""}
              expressionDefinition={expression}
              setExpressionDefinition={setExpression}
            >
              <ResizingWidthsContextProvider>
                <BeeTable {...props} />
              </ResizingWidthsContextProvider>
            </BoxedExpressionEditorContextProvider>
          </I18nDictionariesProvider>
        </div>
      </div>
    </div>
  );
}
