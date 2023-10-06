import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
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
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { UniqueNameIndex } from "../Spec";

const PLACEHOLDER_URL_TITLE = "Enter a title...";
const PLACEHOLDER_URL = "Enter a URL...";

export function DocumentationLinksFormGroup({
  isReadonly,
  values,
  onChange,
}: {
  isReadonly: boolean;
  values?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  const onChangeUrlTitle = useCallback(
    (newUrlTitle: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValues = [...(values ?? [])];
      const newKieAttachment = newValues[index] ?? { "@_name": "", "@_url": "" };
      newValues[index] = { "@_name": newUrlTitle, "@_url": newKieAttachment["@_url"] };
      onChange?.(newValues);
    },
    [isReadonly, onChange, values]
  );

  const onChangeUrl = useCallback(
    (newUrl: string, index: number) => {
      if (isReadonly) {
        return;
      }

      const newValues = [...(values ?? [])];
      const newKieAttachment = newValues[index] ?? { "@_name": "", "@_url": "" };
      newValues[index] = { "@_name": newKieAttachment["@_name"], "@_url": newUrl };
      onChange?.(newValues);
    },
    [isReadonly, onChange, values]
  );

  const onRemove = useCallback(
    (index: number) => {
      const newValue = [...(values ?? [])];
      newValue.splice(index, 1);
      onChange?.(newValue);
    },
    [onChange, values]
  );

  // This is required to give each documentation row an unique
  // key, and not update it on every change.
  const documentationKeys = useMemo(() => {
    return new Map<number, string>(
      Array(values?.length ?? 0)
        .fill(0)
        .map((_, index) => [index, generateUuid()])
    );
  }, [values?.length]);

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
                const newValues = [...(values ?? [])];
                newValues.push({ "@_name": "", "@_url": "" });
                onChange?.(newValues);
              }}
            />
          </FlexItem>
        </Flex>
      }
    >
      <div>
        {values?.map((kieAttachment, index) => (
          <div key={documentationKeys.get(index)} style={{ paddingTop: index === 0 ? "0" : "16px" }}>
            <DocumentationLinksInput
              kieAttachment={kieAttachment}
              index={index}
              isReadonly={isReadonly}
              onChangeUrlTitle={onChangeUrlTitle}
              onChangeUrl={onChangeUrl}
              onRemove={onRemove}
            />
          </div>
        ))}
      </div>
    </FormGroup>
  );
}

function DocumentationLinksInput({
  kieAttachment,
  index,
  isReadonly,
  onChangeUrlTitle,
  onChangeUrl,
  onRemove,
}: {
  kieAttachment: Namespaced<"kie", KIE__tAttachment>;
  index: number;
  isReadonly: boolean;
  onChangeUrlTitle: (newUrlTitle: string, index: number) => void;
  onChangeUrl: (newUrl: string, index: number) => void;
  onRemove: (index: number) => void;
}) {
  const [isUrlExpanded, setUrlExpanded] = useState<boolean>(kieAttachment["@_url"] !== "" ? false : true);

  const urlTitleRef = useRef<HTMLInputElement>(null);

  const isValidUrl = useCallback((urlString) => {
    try {
      const url = new URL(urlString);
      return url.protocol === "http:" || url.protocol === "https:";
    } catch (error) {
      return false;
    }
  }, []);

  const urlTitleIsLink = useMemo(
    () => isValidUrl(kieAttachment["@_url"]) && !isUrlExpanded,
    [isValidUrl, isUrlExpanded, kieAttachment]
  );
  const shouldRenderTooltip = useMemo(
    () => kieAttachment["@_url"] !== "" && !isUrlExpanded,
    [isUrlExpanded, kieAttachment]
  );
  const urlTitleClassName = useMemo(
    () => (urlTitleIsLink ? "url-title url-title-is-link" : "url-title"),
    [urlTitleIsLink]
  );
  const urlTitleUniqueMap = useMemo(() => new Map<string, string>(), []);
  const urlUniqueMap = useMemo(() => new Map<string, string>(), []);
  const validate = useCallback((id: string, name: string | undefined, allUniqueNames: UniqueNameIndex) => true, []);

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
          <div
            ref={urlTitleRef}
            className={urlTitleClassName}
            onClick={() => {
              if (urlTitleIsLink) {
                window.open(kieAttachment["@_url"]);
              } else if (!isUrlExpanded) {
                setUrlExpanded(true);
              }
            }}
          >
            <InlineFeelNameInput
              isPlain={true}
              isReadonly={!isUrlExpanded || isReadonly}
              id={`${index}-name`}
              shouldCommitOnBlur={true}
              placeholder={PLACEHOLDER_URL_TITLE}
              name={kieAttachment["@_name"] ?? ""}
              onRenamed={(newUrlTitle) => onChangeUrlTitle(newUrlTitle, index)}
              allUniqueNames={urlTitleUniqueMap}
              validate={validate}
            />
          </div>

          {shouldRenderTooltip && (
            <Tooltip
              content={<Text component={TextVariants.p}>{kieAttachment["@_url"]}</Text>}
              position={TooltipPosition.topStart}
              reference={urlTitleRef}
            />
          )}
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
            paddingLeft: "40px",
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
            allUniqueNames={urlUniqueMap}
            validate={validate}
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
