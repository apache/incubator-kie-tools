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
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { UniqueNameIndex } from "../Spec";
import { GripVerticalIcon } from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

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
  // toogle to upda
  const [updateKeyToggle, toogleKey] = useState<boolean>(false);
  // used to perform undo/redo
  const valuesCache = useRef<Namespaced<"kie", KIE__tAttachment>[]>([]);
  // all expaded urls
  const [expandedUrls, setExpandedUrls] = useState<boolean[]>([]);

  const updateKey = useCallback(() => {
    toogleKey((prev) => !prev);
  }, []);

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
    onChange?.(newValues);
    updateKey();
  }, [onChange, updateKey, values]);

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

      valuesCache.current = [...newValues];
      onChange?.(newValues);
      updateKey();

      // Expand the URL
      setExpandedUrls((prev) => {
        const newUrlExpanded = [...prev];
        newUrlExpanded.splice(index, 1);
        return newUrlExpanded;
      });
    },
    [onChange, updateKey, values]
  );

  const setUrlExpanded = useCallback((isExpanded: boolean, index: number) => {
    setExpandedUrls((prev) => {
      const newUrlExpanded = [...prev];
      newUrlExpanded[index] = isExpanded;
      return newUrlExpanded;
    });
  }, []);

  const [source, setSource] = useState<number>(-1);
  const [dest, setDest] = useState<number>(-1);
  const [dragging, setDragging] = useState<boolean>();

  const onDragStart = useCallback((index: number) => {
    // console.log("on drag start", index);
    setDragging(true);
    setSource(index);
  }, []);

  const onDragEnter = useCallback((index: number) => {
    // console.log("on drag enter", index);
    setDest(index);
  }, []);

  const onDragEnd = useCallback((index: number) => {
    // console.log("on drag end", index);\
    setDragging(false);
  }, []);

  React.useEffect(() => {
    if (dragging === false && source !== -1 && dest !== -1) {
      setSource(-1);
      setDest(-1);

      const reordened = [...(values ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);

      onChange?.(reordened);
      updateKey();
    }
  }, [source, dest, dragging, values, onChange, updateKey]);

  React.useEffect(() => {
    if (JSON.stringify(values) !== JSON.stringify(valuesCache.current)) {
      updateKey();
      valuesCache.current = [...(values ?? [])];
    }
  }, [values, updateKey]);

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
      <React.Fragment>
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
          <div>
            {values?.map((kieAttachment, index) => (
              <div
                key={updateKeyToggle ? index + 99999999 : index - 99999999}
                style={{
                  display: "flex",
                  flexDirection: "row",
                  marginTop: index === 0 ? "0" : "16px",
                  borderWidth: "1px",
                  borderStyle: "dashed",
                  borderColor: dragging ? "gray" : "white",
                  borderRadius: "5px",
                  borderTopColor: index === dest && index !== source ? "blue" : dragging ? "gray" : "white",
                }}
              >
                <Icon
                  style={{
                    marginTop: "4px",
                    marginRight: "8px",
                  }}
                  draggable={true}
                  onDragStart={() => onDragStart(index)}
                  onDragEnter={() => onDragEnter(index)}
                  onDragEnd={() => onDragEnd(index)}
                >
                  <GripVerticalIcon style={{ color: "#8080809e" }} />
                </Icon>
                <div style={{ flexGrow: 1 }}>
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
                </div>
              </div>
            ))}
          </div>
        )}
      </React.Fragment>
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
      <div style={{ display: "flex", flexDirection: "row", alignItems: "flex-start" }}>
        <Button
          variant={ButtonVariant.plain}
          style={{ padding: "0 8px 0 0", marginTop: "2px" }}
          onClick={() => {
            toogleExpanded();
          }}
        >
          {(isUrlExpanded && <AngleDownIcon />) || <AngleRightIcon />}
        </Button>
        <div style={{ flexGrow: 1 }}>
          {!isUrlExpanded ? (
            <>
              <div ref={urlTitleRef} style={{ paddingLeft: "2px", marginTop: "2px" }}>
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
            <div style={{ display: "flex", flexDirection: "column", marginTop: "1px" }}>
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
              />
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
            </div>
          )}
        </div>
        <Tooltip content={<Text component={TextVariants.p}>{"Remove documentation link"}</Text>}>
          <Button
            style={{ padding: "0px 16px", marginTop: "2px" }}
            variant={"plain"}
            icon={<TimesIcon />}
            onClick={() => onRemove()}
          />
        </Tooltip>
      </div>
    </React.Fragment>
  );
}
