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
import { FormFieldGroupHeader, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
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
  // This is required to give each documentation row an unique
  // key, and not update it on every change.
  const documentationKeys = useMemo(
    () =>
      new Map<number, string>(
        Array(values?.length ?? 0)
          .fill(0)
          .map((_, index) => [index, generateUuid()])
      ),
    [values?.length]
  );
  const [expandedUrls, setExpandedUrls] = useState<boolean[]>([]);

  const onNewUrl = useCallback(() => {
    const newValues = [...(values ?? [])];
    const newLength = newValues.unshift({ "@_name": "", "@_url": "" });

    // Expand the URL
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded.unshift(true);
      return newUrlExpanded;
    });

    // Adds the uuid
    if (newLength === 1) {
      documentationKeys.set(0, generateUuid());
    } else {
      [...documentationKeys.values()].forEach((uuid, index) => {
        documentationKeys.set(index + 1, uuid);
      });
      documentationKeys.set(0, generateUuid());
    }
    onChange?.(newValues);
  }, [onChange, values, documentationKeys]);

  const onChangeUrl = useCallback(
    (args: { index: number; newUrlTitle?: string; newUrl?: string }) => {
      if (isReadonly) {
        return;
      }

      const newValues = [...(values ?? [])];
      const newKieAttachment = newValues[args.index] ?? { "@_name": "", "@_url": "" };
      newValues[args.index] = {
        "@_name": args.newUrlTitle ?? newKieAttachment["@_name"],
        "@_url": args.newUrl ?? newKieAttachment["@_url"],
      };
      onChange?.(newValues);
    },
    [isReadonly, onChange, values]
  );

  const onRemove = useCallback(
    (index: number) => {
      const newValue = [...(values ?? [])];
      newValue.splice(index, 1);
      onChange?.(newValue);

      // Expand the URL
      setExpandedUrls((prev) => {
        const newUrlExpanded = [...prev];
        newUrlExpanded.splice(index, 1);
        return newUrlExpanded;
      });

      // Removes the uuid
      documentationKeys.delete(index);
      [...documentationKeys.values()].forEach((uuid, index) => {
        documentationKeys.set(index, uuid);
      });
    },
    [documentationKeys, onChange, values]
  );

  const setUrlExpanded = useCallback((isExpanded: boolean, index: number) => {
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded[index] = isExpanded;
      return newUrlExpanded;
    });
  }, []);

  return (
    <FormGroup
      label={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <label className="pf-c-form__label">
                <span className="pf-c-form__label-text">Documentation links</span>
              </label>
            ),
            id: "documentation-links",
          }}
          actions={<Button variant={"plain"} icon={<PlusCircleIcon />} onClick={onNewUrl} />}
        />
      }
    >
      <>
        {(values ?? []).length === 0 ? (
          <div
            style={{
              padding: "10px",
              background: "#eee",
              borderRadius: "10px",
              textAlign: "center",
            }}
          >
            None yet
          </div>
        ) : (
          values?.map((kieAttachment, index) => (
            <div key={documentationKeys.get(index)} style={{ paddingTop: index === 0 ? "0" : "16px" }}>
              <DocumentationLinksInput
                title={kieAttachment["@_name"] ?? ""}
                url={kieAttachment["@_url"] ?? ""}
                isReadonly={isReadonly}
                onChangeUrlTitle={(newUrlTitle) => onChangeUrl({ newUrlTitle, index })}
                onChangeUrl={(newUrl) => onChangeUrl({ newUrl, index })}
                onRemove={() => onRemove(index)}
                isUrlExpanded={expandedUrls[index]}
                setUrlExpanded={(isExpanded) => setUrlExpanded(isExpanded, index)}
              />
            </div>
          ))
        )}
      </>
    </FormGroup>
  );
}

function DocumentationLinksInput({
  title,
  url,
  isReadonly,
  isUrlExpanded,
  onChangeUrlTitle,
  onChangeUrl,
  onRemove,
  setUrlExpanded,
}: {
  title: string;
  url: string;
  isReadonly: boolean;
  isUrlExpanded: boolean;
  onChangeUrlTitle: (newUrlTitle: string) => void;
  onChangeUrl: (newUrl: string) => void;
  onRemove: () => void;
  setUrlExpanded: (isExpanded: boolean) => void;
}) {
  const urlTitleRef = useRef<HTMLInputElement>(null);
  const uuid = useMemo(() => generateUuid(), []);

  const isValidUrl = useCallback((urlString) => {
    try {
      const url = new URL(urlString);
      return url.protocol === "http:" || url.protocol === "https:";
    } catch (error) {
      return false;
    }
  }, []);

  const toogleExpanded = useCallback(() => {
    // If the title is empty the title should be the URL.
    if (isUrlExpanded === true && title === "" && url !== "") {
      onChangeUrlTitle(url ?? "");
    }
    setUrlExpanded(!isUrlExpanded);
  }, [isUrlExpanded, title, url, setUrlExpanded, onChangeUrlTitle]);

  const urlTitleIsLink = useMemo(() => isValidUrl(url) && !isUrlExpanded, [isValidUrl, url, isUrlExpanded]);
  const shouldRenderTooltip = useMemo(() => url !== "" && !isUrlExpanded, [isUrlExpanded, url]);
  const urlTitleClassName = useMemo(
    () =>
      urlTitleIsLink
        ? "kie-dmn-editor--documentation-link-title kie-dmn-editor--documentation-link-title--is-link"
        : "kie-dmn-editor--documentation-link-title",
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
            onClick={() => {
              toogleExpanded();
            }}
          >
            {(isUrlExpanded && <AngleDownIcon />) || <AngleRightIcon />}
          </Button>
        </FlexItem>
        <FlexItem style={{ flexGrow: 1 }}>
          {!isUrlExpanded ? (
            <>
              <div ref={urlTitleRef}>
                {urlTitleIsLink ? (
                  <a className={urlTitleClassName} href={url} target={"_blank"}>
                    {title}
                  </a>
                ) : (
                  <p style={{ color: title !== "" ? "black" : "gray" }}>
                    {title !== "" ? title : PLACEHOLDER_URL_TITLE}
                  </p>
                )}
              </div>
              {shouldRenderTooltip && (
                <Tooltip
                  content={<Text component={TextVariants.p}>{url}</Text>}
                  position={TooltipPosition.topStart}
                  reference={urlTitleRef}
                />
              )}
            </>
          ) : (
            <InlineFeelNameInput
              isPlain={true}
              isReadonly={isReadonly}
              id={`${uuid}-name`}
              shouldCommitOnBlur={true}
              placeholder={PLACEHOLDER_URL_TITLE}
              name={title ?? ""}
              onRenamed={(newUrlTitle) => onChangeUrlTitle(newUrlTitle)}
              allUniqueNames={urlTitleUniqueMap}
              validate={validate}
            />
          )}
        </FlexItem>
        <FlexItem>
          <Tooltip content={<Text component={TextVariants.p}>{"Remove documentation link"}</Text>}>
            <Button style={{ padding: "0px 16px" }} variant={"plain"} icon={<TimesIcon />} onClick={() => onRemove()} />
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
            style={{ fontStyle: "italic" }}
            isPlain={true}
            isReadonly={isReadonly}
            id={`${uuid}-url`}
            shouldCommitOnBlur={true}
            placeholder={PLACEHOLDER_URL}
            name={url ?? ""}
            onRenamed={(newUrl) => onChangeUrl(newUrl)}
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
