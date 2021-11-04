import { Form, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import * as React from "react";
import { FormEvent, useCallback, useMemo } from "react";
import { UrlType, useImportableUrl } from "../hooks/ImportableUrlHooks";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";

export function ImportFromUrlForm(props: {
  url?: string;
  importingError?: string;
  onChange: (url: string) => void;
  onSubmit: () => void;
  urlInputRef?: React.RefObject<HTMLInputElement>;
  allowedTypes?: UrlType[];
}) {
  const importableUrl = useImportableUrl(props.url, props.allowedTypes);

  const onSubmit = useCallback(
    (e: FormEvent) => {
      e.preventDefault();
      e.stopPropagation();

      if (importableUrl.errors) {
        return;
      }

      props.onSubmit();
    },
    [importableUrl.errors, props]
  );

  const validatedOption = useMemo(() => {
    if (!props.url) {
      return ValidatedOptions.default;
    }

    if (importableUrl.errors || props.importingError) {
      return ValidatedOptions.error;
    }

    return ValidatedOptions.success;
  }, [props.url, props.importingError, importableUrl.errors]);

  const displayError = useMemo(() => {
    if (importableUrl.errors) {
      return importableUrl.errors.join("\n");
    }

    if (props.importingError) {
      return `Error: ${props.importingError}`;
    }

    return "";
  }, [importableUrl.errors, props.importingError]);

  return (
    <Form onSubmit={onSubmit}>
      <FormGroup
        helperTextInvalid={displayError}
        helperText={<FormHelperText icon={<CheckCircleIcon />} isHidden={false} style={{ visibility: "hidden" }} />}
        helperTextInvalidIcon={<ExclamationCircleIcon />}
        fieldId="import-url-form-input"
        validated={validatedOption}
      >
        <TextInput
          ref={props.urlInputRef}
          id={"import-url-form-input"}
          validated={validatedOption}
          isRequired={true}
          placeholder={"URL"}
          value={props.url}
          onChange={props.onChange}
        />
      </FormGroup>
    </Form>
  );
}
