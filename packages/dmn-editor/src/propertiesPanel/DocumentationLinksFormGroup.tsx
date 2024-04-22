/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useMemo, useState, useCallback, useRef, useEffect } from "react";
import { KIE__tAttachment } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Namespaced } from "@kie-tools/xml-parser-ts";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip, TooltipPosition } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import { InlineFeelNameInput, invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { Draggable, DragAndDrop, useDraggableItemContext } from "../draggable/Draggable";
import "./DocumentationLinksFormGroup.css";

const PLACEHOLDER_URL_TITLE = "Enter a title...";
const PLACEHOLDER_URL = "https://...";

export function DocumentationLinksFormGroup({
  isReadonly,
  values,
  onChange,
}: {
  isReadonly: boolean;
  values?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  const [autoFocusFirst, setAutoFocusFirst] = useState(true);
  // Start - Values Cache and UUID
  // A cache is created to keep this component updated even when the values prop is changed
  // by an outside event. If the values prop is changed new UUIDs are generated to force
  // a re-render in the list. This is necessary as the values elements doesn't have an ID.
  const [valuesUuid, setValuesUuid] = useState((values ?? [])?.map((_) => generateUuid()));
  const valuesCache = useRef<Namespaced<"kie", KIE__tAttachment>[]>(values ?? []);
  useEffect(() => {
    if (JSON.stringify(values) !== JSON.stringify(valuesCache.current)) {
      setValuesUuid((values ?? [])?.map(() => generateUuid()));
      valuesCache.current = [...(values ?? [])];
    }
  }, [values]);
  // END - Values Cache and UUID

  // Controls if a url is expaded or not
  const [expandedUrls, setExpandedUrls] = useState<boolean[]>([]);

  const onInternalChange = useCallback(
    (newValues: Namespaced<"kie", KIE__tAttachment>[]) => {
      valuesCache.current = [...newValues];
      onChange?.(newValues);
    },
    [onChange]
  );

  const onAdd = useCallback(() => {
    setAutoFocusFirst(true);
    const newValues = [...(values ?? [])];
    newValues.unshift({ "@_name": "", "@_url": "" });

    // Expand the URL
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded.unshift(true);
      return newUrlExpanded;
    });

    setValuesUuid((prev) => [generateUuid(), ...prev]);
    onInternalChange(newValues);
  }, [onInternalChange, values]);

  const onChangeKieAttachment = useCallback(
    (args: { index: number; newUrlTitle?: string; newUrl?: string }) => {
      setAutoFocusFirst(false);
      if (isReadonly) {
        return;
      }

      const newValues = [...(values ?? [])];
      const newKieAttachment = newValues[args.index] ?? { "@_name": "", "@_url": "" };

      newValues[args.index] = {
        "@_name": args.newUrlTitle ?? newKieAttachment["@_name"],
        "@_url": args.newUrl ?? newKieAttachment["@_url"],
      };
      onInternalChange(newValues);
    },
    [isReadonly, onInternalChange, values]
  );

  const onRemove = useCallback(
    (index: number) => {
      setAutoFocusFirst(false);
      const newValues = [...(values ?? [])];
      newValues.splice(index, 1);

      setValuesUuid((prev) => {
        const newUuids = [...prev];
        newUuids.splice(index, 1);
        return newUuids;
      });

      // Expand the URL
      setExpandedUrls((prev) => {
        const newUrlExpanded = [...prev];
        newUrlExpanded.splice(index, 1);
        return newUrlExpanded;
      });
      onInternalChange(newValues);
    },
    [onInternalChange, values]
  );

  const setUrlExpanded = useCallback((isExpanded: boolean, index: number) => {
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded[index] = isExpanded;
      return newUrlExpanded;
    });
  }, []);

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...(values ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onInternalChange(reordened);
    },
    [onInternalChange, values]
  );

  const reorder = useCallback((source: number, dest: number) => {
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      const [removed] = newUrlExpanded.splice(source, 1);
      newUrlExpanded.splice(dest, 0, removed);
      return newUrlExpanded;
    });

    setValuesUuid((prev) => {
      const reordenedUuid = [...prev];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);
      return reordenedUuid;
    });
  }, []);

  const draggableItem = useCallback(
    (kieAttachment: Namespaced<"kie", KIE__tAttachment>, index: number) => {
      return (
        <Draggable
          key={valuesUuid?.[index] ?? generateUuid()}
          index={index}
          rowClassName={index !== 0 ? "kie-dmn-editor--documentation-link--not-first-element" : ""}
          handlerStyle={
            expandedUrls[index]
              ? { alignSelf: "flex-start", paddingTop: "8px", paddingLeft: "16px", paddingRight: "16px" }
              : { paddingLeft: "16px", paddingRight: "16px" }
          }
          isDisabled={isReadonly}
        >
          <li>
            <DocumentationLinksInput
              title={kieAttachment["@_name"] ?? ""}
              url={kieAttachment["@_url"] ?? ""}
              isReadonly={isReadonly}
              onChange={(newUrlTitle, newUrl) => onChangeKieAttachment({ newUrlTitle, newUrl, index })}
              onRemove={() => onRemove(index)}
              isUrlExpanded={expandedUrls[index]}
              setUrlExpanded={(isExpanded) => setUrlExpanded(isExpanded, index)}
              autoFocus={autoFocusFirst ? index === 0 : false}
            />
          </li>
        </Draggable>
      );
    },
    [autoFocusFirst, expandedUrls, isReadonly, onChangeKieAttachment, onRemove, setUrlExpanded, valuesUuid]
  );

  return (
    <FormGroup
      label={
        <div style={{ display: "flex", flexDirection: "row" }}>
          <label className={"pf-c-form__label"} style={{ flexGrow: 1, cursor: "auto" }}>
            <span className={"pf-c-form__label-text"}>Documentation links</span>
          </label>
          {!isReadonly && (
            <Button variant={"plain"} icon={<PlusCircleIcon />} onClick={onAdd} title={"Add documentation link"} />
          )}
        </div>
      }
    >
      <ul>
        {(values ?? []).length === 0 && (
          <li className={"kie-dmn-editor--documentation-link--empty-state"}>{isReadonly ? "None" : "None yet"}</li>
        )}
        <DragAndDrop
          reorder={reorder}
          onDragEnd={onDragEnd}
          values={values}
          draggableItem={draggableItem}
          isDisabled={isReadonly}
        />
      </ul>
    </FormGroup>
  );
}

function DocumentationLinksInput({
  title,
  url,
  isReadonly,
  isUrlExpanded,
  onChange,
  onRemove,
  setUrlExpanded,
  autoFocus: parentAutoFocus,
}: {
  title: string;
  url: string;
  isReadonly: boolean;
  isUrlExpanded: boolean;
  onChange: (newUrlTitle: string, newUrl: string) => void;
  onRemove: () => void;
  setUrlExpanded: (isExpanded: boolean) => void;
  autoFocus: boolean;
}) {
  const urlTitleRef = useRef<HTMLInputElement>(null);
  const uuid = useMemo(() => generateUuid(), []);
  const [titleIsUrl, setTitleIsUrl] = useState(false);
  const updatedOnToogle = useRef(false);
  const { hovered } = useDraggableItemContext();
  const [autoFocus, setAutoFocus] = useState(false);

  const parseUrl = useCallback((newUrl: string) => {
    try {
      const url = new URL(newUrl);
      return url.toString();
    } catch (error) {
      try {
        if (!newUrl.includes("http://") && !newUrl.includes("https://")) {
          const urlWithProtocol = "https://" + newUrl + "/";
          const url = new URL(urlWithProtocol);
          // the new URL automatically converts the whitespaces to %20
          // this check verifies if the url has whitespaces
          return url.toString() === urlWithProtocol ? url.toString() : undefined;
        }
      } catch (error) {
        return undefined;
      }
      return undefined;
    }
  }, []);

  const toogleExpanded = useCallback(
    (title: string, url: string) => {
      const parsedUrl = parseUrl(url);
      if (parsedUrl !== undefined && isUrlExpanded === true && (title === "" || titleIsUrl)) {
        // valid parsed url and empty title
        setTitleIsUrl(true);
        updatedOnToogle.current = true;
        onChange(parsedUrl, parsedUrl);
        setUrlExpanded(false);
        setAutoFocus(false);
      } else if (parsedUrl !== undefined && parsedUrl !== url && isUrlExpanded === true) {
        // valid parsed url and different than the current url
        updatedOnToogle.current = true;
        onChange(title, parsedUrl);
        setUrlExpanded(false);
        setAutoFocus(false);
      } else if (url !== "" && parsedUrl === undefined && title === "") {
        // invalid parsed url and empty title
        updatedOnToogle.current = true;
        onChange("", url);
      } else if (url !== "" && parsedUrl === undefined) {
        // nothing should be done with an invalid url
      } else {
        setUrlExpanded(!isUrlExpanded);
        setAutoFocus(!isUrlExpanded);
      }
    },
    [isUrlExpanded, titleIsUrl, parseUrl, setUrlExpanded, onChange]
  );

  const isUrl = useMemo(() => {
    try {
      return new URL(url) && !isUrlExpanded;
    } catch (error) {
      return false;
    }
  }, [url, isUrlExpanded]);

  const allUniqueNames = useMemo(() => new Map<string, string>(), []);

  const validateTitle = useCallback((id, name, allUniqueNames) => true, []);

  const validateUrl = useCallback(
    (id: string, url: string | undefined, allUniqueNames) => {
      if (url !== undefined && url !== "") {
        return parseUrl(url) !== undefined;
      }
      return true;
    },
    [parseUrl]
  );

  const urlDescriptionTooltip = useMemo(() => {
    return url !== "" ? (
      <Text component={TextVariants.p}>{url}</Text>
    ) : (
      <Text component={TextVariants.p}>Empty URL</Text>
    );
  }, [url]);
  const removeTooltip = useMemo(() => <Text component={TextVariants.p}>Remove</Text>, []);

  return (
    <React.Fragment>
      <div
        className={"kie-dmn-editor--documentation-link--row"}
        data-testid={"kie-tools--dmn-editor--documentation-link--row"}
      >
        <Button
          title={"Expand / collapse documentation link"}
          variant={ButtonVariant.plain}
          className={"kie-dmn-editor--documentation-link--row-expand-toogle"}
          onClick={() => toogleExpanded(title, url)}
        >
          {(isUrlExpanded && <AngleDownIcon />) || <AngleRightIcon />}
        </Button>
        <div className={"kie-dmn-editor--documentation-link--row-item"}>
          {!isUrlExpanded ? (
            <>
              <div ref={urlTitleRef} className={"kie-dmn-editor--documentation-link--row-title"}>
                {isUrl ? (
                  <a href={url} target={"_blank"} data-testid={"kie-tools--dmn-editor--documentation-link--row-title"}>
                    {title}
                  </a>
                ) : (
                  <p style={title === "" ? {} : invalidInlineFeelNameStyle} onClick={() => setUrlExpanded(true)}>
                    {title !== "" ? title : PLACEHOLDER_URL_TITLE}
                  </p>
                )}
              </div>
              {!isUrlExpanded && (
                <Tooltip content={urlDescriptionTooltip} position={TooltipPosition.topStart} reference={urlTitleRef} />
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
                onRenamed={(newUrlTitle) => {
                  if (!updatedOnToogle.current && newUrlTitle !== title) {
                    onChange(newUrlTitle, url);
                    setTitleIsUrl(false);
                  }
                  // reset the changedByToogle
                  updatedOnToogle.current = false;
                }}
                allUniqueNames={() => allUniqueNames}
                validate={validateTitle}
                autoFocus={parentAutoFocus || autoFocus}
                onKeyDown={(e) => {
                  if (e.code === "Enter") {
                    // onRenamed and onKeyDown are performed simultaneously, calling the toggleExpdaded callback
                    // with a outdate title value, making it necessary to use the e.currentTarget.value.
                    toogleExpanded(e.currentTarget.value, url);
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
                  if (!updatedOnToogle.current && newUrl !== url) {
                    onChange(title, newUrl);
                  }
                  // reset the changedByToogle
                  updatedOnToogle.current = false;
                }}
                allUniqueNames={() => allUniqueNames}
                validate={validateUrl}
                saveInvalidValue={true}
                onKeyDown={(e) => {
                  if (e.code === "Enter") {
                    // onRenamed and onKeyDown are performed simultaneously, calling the toggleExpdaded callback
                    // with a outdate url value, making it necessary to use the e.currentTarget.value.
                    toogleExpanded(title, e.currentTarget.value);
                  }
                }}
              />
            </div>
          )}
        </div>
        {hovered && (
          <Tooltip content={removeTooltip}>
            <Button
              title={"Remove documentation link"}
              className={"kie-dmn-editor--documentation-link--row-remove"}
              variant={"plain"}
              icon={<TimesIcon />}
              onClick={() => onRemove()}
            />
          </Tooltip>
        )}
      </div>
    </React.Fragment>
  );
}
