import { useLocation } from "react-router";
import { useMemo } from "react";

interface QueryParamsImpl {
  has(name: string): boolean;
  get(name: string): string | undefined;
  with(name: string, value: string): QueryParamsImpl;
  without(name: string): QueryParamsImpl;
  toString(): string;
}

function newQueryParamsImpl(searchString: string): QueryParamsImpl {
  return {
    has: (name) => new URLSearchParams(searchString).has(name),
    get: (name) => {
      const val = new URLSearchParams(searchString).get(name);
      return !val ? undefined : decodeURIComponent(val);
    },
    with: (name, value) => {
      const urlSearchParams = new URLSearchParams(searchString);
      urlSearchParams.set(name, value);
      return newQueryParamsImpl(decodeURIComponent(urlSearchParams.toString()));
    },
    without: (name) => {
      const urlSearchParams = new URLSearchParams(searchString);
      urlSearchParams.delete(name);
      return newQueryParamsImpl(decodeURIComponent(urlSearchParams.toString()));
    },
    toString: () => {
      return decodeURIComponent(new URLSearchParams(searchString).toString());
    },
  };
}

export function useQueryParams(): QueryParamsImpl {
  const location = useLocation();
  return useMemo(() => newQueryParamsImpl(location.search), [location.search]);
}

export enum QueryParams {
  SETTINGS = "settings",
  READONLY = "readonly",
  EXT = "ext",
  FILE = "file",
  DMN_RUNNER_FORM_INPUTS = "formInputs",
}
