import { useHistory } from "react-router";
import { useMemo } from "react";

export function useQueryParams() {
  const history = useHistory();
  return useMemo(() => {
    return new URLSearchParams(history.location.search);
  }, [history.location.search]);
}

export enum QueryParams {
  SETTINGS = "settings",
  READONLY = "readonly",
  EXT = "ext",
  FILE = "file",
  DMN_RUNNER_FORM_INPUTS = "formInputs",
}
