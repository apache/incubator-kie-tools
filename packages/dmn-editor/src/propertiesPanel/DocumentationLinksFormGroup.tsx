import * as React from "react";
import { useMemo, useState, useCallback, useRef, useEffect } from "react";
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
import { InlineFeelNameInput, invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { UniqueNameIndex } from "../Spec";
import { Draggable, DraggableContextProvider } from "./Draggable";

const PLACEHOLDER_URL_TITLE = "Enter a title...";
const PLACEHOLDER_URL = "https://";

export function DocumentationLinksFormGroup({
  isReadonly,
  values,
  onChange,
}: {
  isReadonly: boolean;
  values?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  // used to perform undo/redo
  const valuesCache = useRef<Namespaced<"kie", KIE__tAttachment>[]>([]);
  const valuesUuid = useRef<string[]>([]);
  // all expaded urls
  const [expandedUrls, setExpandedUrls] = useState<boolean[]>([]);

  const onNewUrl = useCallback(() => {
    const newValues = [...(values ?? [])];
    newValues.unshift({ "@_name": "", "@_url": "" });

    // Expand the URL
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded.unshift(true);
      return newUrlExpanded;
    });

    valuesCache.current = [...newValues];
    valuesUuid.current = [generateUuid(), ...valuesUuid.current];
    onChange?.(newValues);
  }, [onChange, values]);

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

      // change reference and save values
      valuesCache.current = [...newValues];
      onChange?.(newValues);
    },
    [isReadonly, onChange, values]
  );

  const onRemove = useCallback(
    (index: number) => {
      const newValues = [...(values ?? [])];
      newValues.splice(index, 1);

      const newUuids = [...valuesUuid.current];
      newUuids.splice(index, 1);
      valuesUuid.current = newUuids;

      // Expand the URL
      setExpandedUrls((prev) => {
        const newUrlExpanded = [...prev];
        newUrlExpanded.splice(index, 1);
        return newUrlExpanded;
      });

      valuesCache.current = [...newValues];
      onChange?.(newValues);
    },
    [onChange, values]
  );

  const setUrlExpanded = useCallback((isExpanded: boolean, index: number) => {
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded[index] = isExpanded;
      return newUrlExpanded;
    });
  }, []);

  const reorder = useCallback(
    (source: number, dest: number) => {
      const reordened = [...(values ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);

      setExpandedUrls((prev) => {
        const newUrlExpanded = [...prev];
        const [removed] = newUrlExpanded.splice(source, 1);
        newUrlExpanded.splice(dest, 0, removed);
        return newUrlExpanded;
      });

      const reordenedUuid = [...valuesUuid.current];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);
      valuesUuid.current = reordenedUuid;

      valuesCache.current = reordened;
      onChange?.(reordened);
    },
    [onChange, values]
  );

  useEffect(() => {
    if (JSON.stringify(values) !== JSON.stringify(valuesCache.current)) {
      valuesUuid.current = valuesUuid.current?.map(() => generateUuid());
      valuesCache.current = [...(values ?? [])];
    }
  }, [values]);

  return (
    <FormGroup
      label={
        <div style={{ display: "flex", flexDirection: "row" }}>
          <label className={"pf-c-form__label"} style={{ flexGrow: 1, cursor: "auto" }}>
            <span className={"pf-c-form__label-text"}>Documentation links</span>
          </label>
          <Button variant={"plain"} icon={<PlusCircleIcon />} onClick={onNewUrl} />
        </div>
      }
    >
      <ul id={"documentation-links-list"}>
        {(values ?? []).length === 0 ? (
          <li className={"kie-dmn-editor--documentation-link--none-yet"}>None yet</li>
        ) : (
          <DraggableContextProvider reorder={reorder}>
            {values?.map((kieAttachment, index) => (
              <li
                key={valuesUuid.current?.[index] ?? generateUuid()}
                id={valuesUuid.current?.[index] ?? generateUuid()}
                className={index !== 0 ? "kie-dmn-editor--documentation-link--not-first-element" : ""}
              >
                <Draggable index={index}>
                  <DocumentationLinksInput
                    autoFocus={index === 0}
                    title={kieAttachment["@_name"] ?? ""}
                    url={kieAttachment["@_url"] ?? ""}
                    isReadonly={isReadonly}
                    onChangeUrlTitle={(newUrlTitle) => onChangeUrl({ newUrlTitle, index })}
                    onChangeUrl={(newUrl) => onChangeUrl({ newUrl, index })}
                    onRemove={() => onRemove(index)}
                    isUrlExpanded={expandedUrls[index]}
                    setUrlExpanded={(isExpanded) => setUrlExpanded(isExpanded, index)}
                  />
                </Draggable>
              </li>
            ))}
          </DraggableContextProvider>
        )}
      </ul>
    </FormGroup>
  );
}

function DocumentationLinksInput({
  autoFocus,
  title,
  url,
  isReadonly,
  isUrlExpanded,
  onChangeUrlTitle,
  onChangeUrl,
  onRemove,
  setUrlExpanded,
}: {
  autoFocus: boolean;
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
  const [validTitle, setValidTitle] = useState(true);

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
  const urlTitleUniqueMap = useMemo(() => new Map<string, string>(), []);
  const urlUniqueMap = useMemo(() => new Map<string, string>(), []);
  const validate = useCallback((id: string, name: string | undefined, allUniqueNames: UniqueNameIndex) => true, []);

  return (
    <React.Fragment>
      <div className={"kie-dmn-editor--documentation-link--row"}>
        <Button
          variant={ButtonVariant.plain}
          className={"kie-dmn-editor--documentation-link--row-expand-toogle"}
          onClick={() => {
            if (url === "" && !isValidUrl(url)) {
              setValidTitle(false);
            } else {
              setValidTitle(true);
            }
            toogleExpanded();
          }}
        >
          {(isUrlExpanded && <AngleDownIcon />) || <AngleRightIcon />}
        </Button>
        <div className={"kie-dmn-editor--documentation-link--row-item"}>
          {!isUrlExpanded ? (
            <>
              <div ref={urlTitleRef} className={"kie-dmn-editor--documentation-link--row-title"}>
                {urlTitleIsLink ? (
                  <a href={url} target={"_blank"}>
                    {title}
                  </a>
                ) : (
                  <p
                    style={validTitle ? {} : invalidInlineFeelNameStyle}
                    onClick={() => {
                      if (!validTitle) {
                        setUrlExpanded(true);
                      }
                    }}
                  >
                    {title !== "" ? title : PLACEHOLDER_URL_TITLE}
                  </p>
                )}
              </div>
              {!isUrlExpanded && (
                <Tooltip
                  content={
                    url !== "" ? (
                      <Text component={TextVariants.p}>{url}</Text>
                    ) : (
                      <Text component={TextVariants.p}>Empty URL</Text>
                    )
                  }
                  position={TooltipPosition.topStart}
                  reference={urlTitleRef}
                />
              )}
            </>
          ) : (
            <div className={"kie-dmn-editor--documentation-link--row-inputs"}>
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
                autoFocus={autoFocus}
                onKeyDown={(e) => {
                  if (e.code === "Enter") {
                    setUrlExpanded(false);
                  }
                }}
              />
              <InlineFeelNameInput
                className={"kie-dmn-editor--documentation-link--row-inputs-url"}
                isPlain={true}
                isReadonly={isReadonly}
                id={`${uuid}-url`}
                shouldCommitOnBlur={true}
                placeholder={PLACEHOLDER_URL}
                name={url ?? ""}
                onRenamed={(newUrl: string) => {
                  if (!newUrl.includes("http://") && !newUrl.includes("https://")) {
                    newUrl = `https://${newUrl}`;
                  }
                  onChangeUrl(newUrl);
                }}
                allUniqueNames={urlUniqueMap}
                validate={validate}
                onKeyDown={(e) => {
                  if (e.code === "Enter") {
                    setUrlExpanded(false);
                  }
                }}
              />
            </div>
          )}
        </div>
        <Tooltip content={<Text component={TextVariants.p}>{"Remove"}</Text>}>
          <Button
            className={"kie-dmn-editor--documentation-link--row-remove"}
            variant={"plain"}
            icon={<TimesIcon />}
            onClick={() => onRemove()}
          />
        </Tooltip>
      </div>
    </React.Fragment>
  );
}
