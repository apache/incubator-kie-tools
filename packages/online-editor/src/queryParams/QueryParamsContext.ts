import { useLocation } from "react-router";
import { useMemo } from "react";
import { newQueryParamsImpl, QueryParamsImpl } from "../common/Routes";

export function useQueryParams(): QueryParamsImpl<string> {
  const location = useLocation();
  return useMemo(() => newQueryParamsImpl(location.search), [location.search]);
}
