import * as React from "react";
import { useMemo, useState, useCallback } from "react";
import { TextInput, TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import { KIE__tAttachment } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Namespaced } from "@kie-tools/xml-parser-ts";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { PencilAltIcon } from "@patternfly/react-icons/dist/esm/icons/pencil-alt-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/esm/icons/times-icon";

export function DocumentationLinksInput({
  onChange,
  ...props
}: {
  value?: Namespaced<"kie", KIE__tAttachment>[];
  onChange?: (newExtensionElements: Namespaced<"kie", KIE__tAttachment>[]) => void;
}) {
  const [name, setName] = useState<string>("");
  const [url, setUrl] = useState<string>("");
  const [next, setNext] = useState(false);

  // React.useEffect(() => {
  //   onChange?.([
  //     { "@_name": "google", "@_url": "https://google.com" },
  //     { "@_name": "https://x.com", "@_url": "https://x.com" },
  //   ]);
  // }, []);

  const currentIndex = useMemo(() => (props.value?.length ? props.value?.length - 1 : 0), [props.value]);

  const onAddUrl = useCallback(() => {
    setNext(true);
  }, []);

  const onAddUrlName = useCallback(() => {
    setNext(false);
    onChange?.([{ "@_name": name === "" ? url : name, "@_url": url }]);
    setUrl("");
    setName("");
  }, [name, onChange, url]);

  return (
    <>
      {!next ? (
        <DocumentationLinkInputGroup
          value={url}
          setValue={setUrl}
          onSubmit={onAddUrl}
          textInputType={"url"}
          textInputAriaLabel={"Documentation links"}
          textInputPlaceholder={"Add a documentation link..."}
          tooltipAriaLabel={"add documentation button"}
          tooltipContent={"Adds a new documentation link"}
          buttonAriaLabel={"add documentation link"}
        />
      ) : (
        <DocumentationLinkInputGroup
          value={name}
          setValue={setName}
          onSubmit={onAddUrlName}
          textInputType={"text"}
          textInputAriaLabel={"Links alias"}
          textInputPlaceholder={"Add an alias"}
          tooltipAriaLabel={"add link alias button"}
          tooltipContent={"Adds an alias for your link"}
          buttonAriaLabel={"add alias for your link"}
        />
      )}

      <div style={{ marginTop: "10px" }}>
        {props.value?.map((kieAttachment, index) => (
          <InputGroup key={index} style={{ marginBottom: "1px" }}>
            <InputGroupText style={{ width: "100%" }}>
              <Tooltip content={<Text component={TextVariants.p}>{kieAttachment["@_url"]}</Text>}>
                <Button variant={"link"} component={"a"} href={kieAttachment["@_url"]} target={"_blank"}>
                  {kieAttachment["@_name"]}
                </Button>
              </Tooltip>
            </InputGroupText>
            <InputGroupText>
              <Tooltip content={<Text component={TextVariants.p}>{"Edit"}</Text>}>
                <Button style={{ padding: "0 7px 0 7px" }} variant={"plain"} icon={<PencilAltIcon />} />
              </Tooltip>
            </InputGroupText>
            <InputGroupText>
              <Tooltip content={<Text component={TextVariants.p}>{"Delete"}</Text>}>
                <Button style={{ padding: "0 7px 0 7px" }} variant={"plain"} icon={<TimesIcon />} />
              </Tooltip>
            </InputGroupText>
          </InputGroup>
        ))}
      </div>
    </>
  );
}

function DocumentationLinkInputGroup({
  onSubmit,
  ...props
}: {
  value: string;
  setValue: React.Dispatch<React.SetStateAction<string>>;
  onSubmit: () => void;
  textInputType: TextInputProps["type"];
  textInputAriaLabel: string;
  textInputPlaceholder: string;
  tooltipAriaLabel: string;
  tooltipContent: string;
  buttonAriaLabel: string;
}) {
  const onKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.code === "Enter" || e.code === "Tab") {
        onSubmit();
      }
    },
    [onSubmit]
  );

  return (
    <InputGroup>
      <TextInput
        onKeyDown={onKeyDown}
        aria-label={props.textInputAriaLabel}
        type={props.textInputType}
        isDisabled={false}
        value={props.value}
        onChange={props.setValue}
        placeholder={props.textInputPlaceholder}
      />
      <Tooltip aria-label={props.tooltipAriaLabel} content={props.tooltipContent}>
        <Button variant={"control"} aria-label={props.buttonAriaLabel} onClick={onSubmit}>
          <PlusCircleIcon />
        </Button>
      </Tooltip>
    </InputGroup>
  );
}
