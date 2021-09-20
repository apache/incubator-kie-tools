import { useLocation } from "react-router";
import { useMemo } from "react";

export function useQueryParams() {
  const location = useLocation();
  return useMemo(() => {
    return new URLSearchParams(location.search);
  }, [location.search]);
}

export enum QueryParams {
  SETTINGS = "settings",
  READONLY = "readonly",
  EXT = "ext",
  FILE = "file",
  DMN_RUNNER_FORM_INPUTS = "formInputs",
}
