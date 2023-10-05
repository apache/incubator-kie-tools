import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import "./DocumentationLinksInput.css";
import { KIE__tAttachment } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Namespaced } from "@kie-tools/xml-parser-ts";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/esm/icons/times-icon";
import { FormFieldGroup, FormFieldGroupHeader } from "@patternfly/react-core/dist/js/components/Form";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

const PLACEHOLDER_URL_ALIAS = "Enter the URL alias...";
const PLACEHOLDER_URL = "Enter your documentation URL...";

export function DocumentationLinksFormGroup(props: {
  value?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  const isReadonly = useMemo(() => false, []);

  const onUrlAliasRenamed = useCallback(
    (newUrlAlias: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValue = [...(props.value ?? [])];
      const newKieAttachment = newValue[index] ?? { "@_name": "", "@_url": "" };
      newValue[index] = { "@_name": newUrlAlias, "@_url": newKieAttachment["@_url"] };
      props.onChange?.(newValue);
    },
    [isReadonly, props]
  );

  const onUrlRenamed = useCallback(
    (newUrl: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValue = [...(props.value ?? [])];
      const newKieAttachment = newValue[index] ?? { "@_name": "", "@_url": "" };
      newValue[index] = { "@_name": newKieAttachment["@_name"], "@_url": newUrl };
      props.onChange?.(newValue);
    },
    [isReadonly, props]
  );

  const onRemove = useCallback(
    (index: number) => {
      const newValue = [...(props.value ?? [])];
      newValue.splice(index, 1);
      props.onChange?.(newValue);
    },
    [props]
  );

  return (
    <FormFieldGroup
      style={{ paddingLeft: "0px" }}
      header={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <>
                <label className="pf-c-form__label">
                  <span className="pf-c-form__label-text">Documentation links</span>
                </label>
                <Button
                  variant={"plain"}
                  icon={<PlusCircleIcon />}
                  onClick={() => {
                    const newValue = [...(props.value ?? [])];
                    newValue.push({ "@_name": "", "@_url": "" });
                    props.onChange?.(newValue);
                  }}
                />
              </>
            ),
            id: "documentation-links",
          }}
        />
      }
    >
      <div>
        {props.value?.map((kieAttachment, index) => (
          <>
            <DocumentationLinksInput
              key={index}
              kieAttachment={kieAttachment}
              index={index}
              isReadonly={isReadonly}
              onUrlAliasRenamed={onUrlAliasRenamed}
              onUrlRenamed={onUrlRenamed}
              onRemove={onRemove}
            />
          </>
        ))}
      </div>
    </FormFieldGroup>
  );
}

export function DocumentationLinksInput({
  kieAttachment,
  index,
  isReadonly,
  onUrlAliasRenamed,
  onUrlRenamed,
  onRemove,
}: {
  kieAttachment: Namespaced<"kie", KIE__tAttachment>;
  index: number;
  isReadonly: boolean;
  onUrlAliasRenamed: (newUrlAlias: string, index: number) => void;
  onUrlRenamed: (newUrl: string, index: number) => void;
  onRemove: (index: number) => void;
}) {
  const [isUrlExpanded, setUrlExpanded] = useState<boolean>(true);

  const urlAliasClassName = useMemo(() => (kieAttachment["@_url"] !== "" ? "url-alias" : ""), [kieAttachment]);

  return (
    <>
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
              onRenamed={(newUrlAlias) => onUrlAliasRenamed(newUrlAlias, index)}
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
          <Tooltip content={<Text component={TextVariants.p}>{"Delete"}</Text>}>
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
            onRenamed={(newUrl) => onUrlRenamed(newUrl, index)}
            allUniqueNames={new Map<string, string>([])}
            onKeyDown={(e) => {
              if (e.code === "Enter") {
                setUrlExpanded(false);
              }
            }}
          />
        </FlexItem>
      )}
    </>
  );
}
