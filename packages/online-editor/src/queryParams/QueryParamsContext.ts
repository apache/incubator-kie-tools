import { useLocation } from "react-router";
import { useMemo } from "react";
import { newQueryParamsImpl, QueryParamsImpl } from "../common/Routes";

export function useQueryParams(): QueryParamsImpl<string> {
  const location = useLocation();
  return useMemo(() => newQueryParamsImpl(location.search), [location.search]);
}

export function useQueryParam(name: string) {
  const queryParams = useQueryParams();
  return useMemo(() => queryParams.get(name), [queryParams, name]);
}
