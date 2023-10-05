import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import "./DocumentationLinksFormGroup.css";
import { KIE__tAttachment } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Namespaced } from "@kie-tools/xml-parser-ts";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/esm/icons/times-icon";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

const PLACEHOLDER_URL_ALIAS = "Enter the URL alias...";
const PLACEHOLDER_URL = "Enter your documentation URL...";

export function DocumentationLinksFormGroup({
  isReadonly,
  value,
  onChange,
}: {
  isReadonly: boolean;
  value?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  const onChangeUrlAlias = useCallback(
    (newUrlAlias: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValue = [...(value ?? [])];
      const newKieAttachment = newValue[index] ?? { "@_name": "", "@_url": "" };
      newValue[index] = { "@_name": newUrlAlias, "@_url": newKieAttachment["@_url"] };
      onChange?.(newValue);
    },
    [isReadonly, onChange, value]
  );

  const onChangeUrl = useCallback(
    (newUrl: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValue = [...(value ?? [])];
      const newKieAttachment = newValue[index] ?? { "@_name": "", "@_url": "" };
      newValue[index] = { "@_name": newKieAttachment["@_name"], "@_url": newUrl };
      onChange?.(newValue);
    },
    [isReadonly, onChange, value]
  );

  const onRemove = useCallback(
    (index: number) => {
      const newValue = [...(value ?? [])];
      newValue.splice(index, 1);
      onChange?.(newValue);
    },
    [onChange, value]
  );

  return (
    <FormGroup
      label={
        <Flex direction={{ default: "row" }}>
          <FlexItem>
            <Text>Documentation links</Text>
          </FlexItem>
          <FlexItem>
            <Button
              variant={"plain"}
              icon={<PlusCircleIcon />}
              onClick={() => {
                const newValue = [...(value ?? [])];
                newValue.push({ "@_name": "", "@_url": "" });
                onChange?.(newValue);
              }}
            />
          </FlexItem>
        </Flex>
      }
    >
      <div>
        {value?.map((kieAttachment, index) => (
          <DocumentationLinksInput
            key={`${index}-${kieAttachment["@_name"]}-${kieAttachment["@_url"]}`}
            kieAttachment={kieAttachment}
            index={index}
            isReadonly={isReadonly}
            onChangeUrlAlias={onChangeUrlAlias}
            onChangeUrl={onChangeUrl}
            onRemove={onRemove}
          />
        ))}
      </div>
    </FormGroup>
  );
}

function DocumentationLinksInput({
  kieAttachment,
  index,
  isReadonly,
  onChangeUrlAlias,
  onChangeUrl,
  onRemove,
}: {
  kieAttachment: Namespaced<"kie", KIE__tAttachment>;
  index: number;
  isReadonly: boolean;
  onChangeUrlAlias: (newUrlAlias: string, index: number) => void;
  onChangeUrl: (newUrl: string, index: number) => void;
  onRemove: (index: number) => void;
}) {
  const [isUrlExpanded, setUrlExpanded] = useState<boolean>(kieAttachment["@_url"] !== "" ? false : true);

  const urlAliasClassName = useMemo(() => (kieAttachment["@_url"] !== "" ? "url-alias" : ""), [kieAttachment]);

  return (
    <React.Fragment>
      <Flex direction={{ default: "row" }}>
        <FlexItem>
          <Button
            variant={ButtonVariant.plain}
            style={{ padding: "0 8px 0 0" }}
            onClick={(e) => {
              setUrlExpanded((prev) => !prev);
            }}
          >
            {(isUrlExpanded && <AngleDownIcon />) || <AngleRightIcon />}
          </Button>
        </FlexItem>
        <FlexItem style={{ flexGrow: 1 }}>
          <Tooltip
            content={<Text component={TextVariants.p}>{kieAttachment["@_url"]}</Text>}
            position={TooltipPosition.topStart}
          >
            <InlineFeelNameInput
              isPlain={true}
              isReadonly={isReadonly}
              id={`${index}-name`}
              shouldCommitOnBlur={true}
              placeholder={PLACEHOLDER_URL_ALIAS}
              name={kieAttachment["@_name"] ?? ""}
              onRenamed={(newUrlAlias) => onChangeUrlAlias(newUrlAlias, index)}
              allUniqueNames={new Map<string, string>([])}
              onClick={() => {
                if (kieAttachment["@_url"] !== "") {
                  window.open(kieAttachment["@_url"]);
                }
              }}
              className={urlAliasClassName}
            />
          </Tooltip>
        </FlexItem>
        <FlexItem>
          <Tooltip content={<Text component={TextVariants.p}>{"Remove documentation link"}</Text>}>
            <Button
              style={{ padding: "0px 16px" }}
              variant={"plain"}
              icon={<TimesIcon />}
              onClick={() => onRemove(index)}
            />
          </Tooltip>
        </FlexItem>
      </Flex>
      {isUrlExpanded && (
        <FlexItem
          style={{
            paddingLeft: `${72}px`,
          }}
        >
          <InlineFeelNameInput
            isPlain={true}
            isReadonly={isReadonly}
            id={`${index}-url`}
            shouldCommitOnBlur={true}
            placeholder={PLACEHOLDER_URL}
            name={kieAttachment["@_url"] ?? ""}
            onRenamed={(newUrl) => onChangeUrl(newUrl, index)}
            allUniqueNames={new Map<string, string>([])}
            onKeyDown={(e) => {
              if (e.code === "Enter") {
                setUrlExpanded(false);
              }
            }}
          />
        </FlexItem>
      )}
    </React.Fragment>
  );
}
