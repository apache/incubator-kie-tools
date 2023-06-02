/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Pagination, PaginationVariant, PerPageOptions, Skeleton } from "@patternfly/react-core/dist/js";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory, useLocation } from "react-router";
import { QueryParams } from "../navigation/Routes";
import { setPageTitle } from "../PageTitle";
import { useQueryParam } from "../queryParams/QueryParamsContext";
import { FileLabel } from "../workspace/components/FileLabel";
import { useSampleDispatch } from "./hooks/SampleContext";
import { Sample, SampleCategories, SampleCategory, SampleCoversHashtable } from "./SampleApi";
import { SampleCard } from "./SampleCard";
import { SampleCardSkeleton } from "./SampleCardSkeleton";
import { SamplesLoadError } from "./SamplesLoadError";

type SearchParams = { searchValue: string; category?: SampleCategory };

const PAGE_TITLE = "Samples Catalog";
const SAMPLE_PRIORITY: Record<SampleCategory, number> = {
  ["serverless-workflow"]: 1,
  ["dashbuilder"]: 2,
  ["serverless-decision"]: 3,
};

const LABEL_MAP: Record<SampleCategory, JSX.Element> = {
  ["serverless-workflow"]: <FileLabel extension="sw.yaml" labelProps={{ isCompact: true }} />,
  ["dashbuilder"]: <FileLabel extension="dash.yaml" labelProps={{ isCompact: true }} />,
  ["serverless-decision"]: <FileLabel extension="yard.yaml" labelProps={{ isCompact: true }} />,
};

const ALL_CATEGORIES_LABEL = "All categories";
const CARDS_PER_PAGE = 9;
const CATEGORY_ARRAY = Object.keys(SAMPLE_PRIORITY) as SampleCategory[];

export const SAMPLE_CARDS_PER_PAGE_OPTIONS: PerPageOptions[] = [
  {
    title: `${CARDS_PER_PAGE}`,
    value: CARDS_PER_PAGE,
  },
];

export function SamplesCatalog() {
  const sampleDispatch = useSampleDispatch();
  const [loading, setLoading] = useState<boolean>(true);
  const [samples, setSamples] = useState<Sample[]>([]);
  const [sampleCovers, setSampleCovers] = useState<SampleCoversHashtable>({});
  const [sampleLoadingError, setSampleLoadingError] = useState("");
  const [searchFilter, setSearchFilter] = useState("");
  const [searchParams, setSearchParams] = useState<SearchParams | undefined>(undefined);
  const [page, setPage] = React.useState(1);
  const [isCategoryFilterDropdownOpen, setCategoryFilterDropdownOpen] = useState(false);
  const history = useHistory();
  const location = useLocation();

  const categoryFilter = useQueryParam(QueryParams.SAMPLES_CATEGORY) as SampleCategory;

  const visibleSamples = useMemo(
    () => samples.slice((page - 1) * CARDS_PER_PAGE, page * CARDS_PER_PAGE),
    [samples, page]
  );

  const samplesCount = useMemo(() => samples.length, [samples]);

  const filterResultMessage = useMemo(() => {
    if (samplesCount === 0) {
      return;
    }
    const isPlural = samplesCount > 1;
    return `Showing ${samplesCount} sample${isPlural ? "s" : ""}`;
  }, [samplesCount]);

  const selectedCategory = useMemo(() => {
    if (categoryFilter) {
      return LABEL_MAP[categoryFilter];
    }
    return ALL_CATEGORIES_LABEL;
  }, [categoryFilter]);

  const setCategoryFilter = useCallback(
    (category?: SampleCategory) => {
      const searchParams = new URLSearchParams(location.search);
      if (category) {
        searchParams.set(QueryParams.SAMPLES_CATEGORY, category);
      } else {
        searchParams.delete(QueryParams.SAMPLES_CATEGORY);
      }
      const newSearchString = searchParams.toString();
      history.push({ search: newSearchString });
    },
    [history, location]
  );

  const onSearch = useCallback(
    async (args: SearchParams) => {
      if (searchParams && args.searchValue === searchParams.searchValue && args.category === searchParams.category) {
        return;
      }
      setSearchFilter(args.searchValue);
      setCategoryFilter(args.category);
      setSearchParams(args);
      setPage(1);
      setSamples(await sampleDispatch.getSamples({ searchFilter: args.searchValue, categoryFilter: args.category }));
    },
    [sampleDispatch, setCategoryFilter, searchParams]
  );

  useEffect(() => {
    if (categoryFilter && !SampleCategories.includes(categoryFilter)) {
      setCategoryFilter(undefined);
      return;
    }

    onSearch({ searchValue: searchFilter, category: categoryFilter });
  }, [categoryFilter, onSearch, searchFilter, setCategoryFilter]);

  useEffect(() => {
    if (searchParams && searchFilter === searchParams.searchValue && categoryFilter === searchParams.category) {
      return;
    }
    setSearchParams({ searchValue: searchFilter, category: categoryFilter });

    sampleDispatch
      .getSamples({ categoryFilter })
      .then((data) => {
        const sortedSamples = data.sort(
          (a: Sample, b: Sample) => SAMPLE_PRIORITY[a.definition.category] - SAMPLE_PRIORITY[b.definition.category]
        );
        setSamples([...sortedSamples]);
      })
      .catch((e) => {
        setSampleLoadingError(e.toString());
      })
      .finally(() => {
        setLoading(false);
      });
  }, [sampleDispatch, categoryFilter, searchFilter, searchParams]);

  useEffect(() => {
    sampleDispatch.getSampleCovers({ samples: visibleSamples, prevState: sampleCovers }).then(setSampleCovers);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [visibleSamples, sampleDispatch]);

  const categoryFilterDropdownItems = useMemo(
    () => [
      <DropdownItem
        key="category-filter-all-categories"
        onClick={() => onSearch({ searchValue: searchFilter, category: undefined })}
      >
        {ALL_CATEGORIES_LABEL}
      </DropdownItem>,
      <DropdownSeparator key="category-filter-separator" />,
      ...CATEGORY_ARRAY.map((category: SampleCategory) => (
        <DropdownItem
          key={`category-filter-${category}`}
          onClick={() => onSearch({ searchValue: searchFilter, category })}
        >
          {LABEL_MAP[category]}
        </DropdownItem>
      )),
    ],
    [onSearch, searchFilter]
  );

  const onSetPage = useCallback((_e, v) => {
    setPage(v);
  }, []);

  useEffect(() => {
    setPageTitle([PAGE_TITLE]);
  }, []);

  return (
    <Page>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <Text component={TextVariants.p}>Try one of our samples to start defining your model.</Text>
        </TextContent>
        <Toolbar style={{ paddingBottom: "0" }}>
          <ToolbarContent style={{ paddingLeft: "0", paddingRight: "0", paddingBottom: "0" }}>
            <ToolbarItem variant="search-filter">
              <SearchInput
                value={""}
                type={"search"}
                onChange={(_ev, value) => onSearch({ searchValue: value, category: categoryFilter })}
                onClear={() => onSearch({ searchValue: "", category: categoryFilter })}
                onFocus={() => setCategoryFilterDropdownOpen(false)}
                placeholder={"Find samples"}
                style={{ width: "400px" }}
                onClick={(e) => {
                  e.stopPropagation();
                }}
              />
            </ToolbarItem>
            <ToolbarItem>
              <Dropdown
                style={{ backgroundColor: "white" }}
                onSelect={() => setCategoryFilterDropdownOpen(false)}
                dropdownItems={categoryFilterDropdownItems}
                toggle={
                  <DropdownToggle
                    id="category-filter-dropdown"
                    onToggle={(isOpen: boolean) => setCategoryFilterDropdownOpen(isOpen)}
                  >
                    {selectedCategory}
                  </DropdownToggle>
                }
                isOpen={isCategoryFilterDropdownOpen}
              />
            </ToolbarItem>
            <ToolbarItem>
              {filterResultMessage && (
                <TextContent>
                  <Text>{filterResultMessage}</Text>
                </TextContent>
              )}
            </ToolbarItem>
            <ToolbarItem variant="pagination">
              {loading && <Skeleton width="200px" />}
              {!loading && (
                <Pagination
                  isCompact
                  itemCount={samplesCount}
                  onSetPage={onSetPage}
                  page={page}
                  perPage={CARDS_PER_PAGE}
                  perPageOptions={SAMPLE_CARDS_PER_PAGE_OPTIONS}
                  variant="top"
                />
              )}
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>
      </PageSection>

      <PageSection isFilled>
        {sampleLoadingError && <SamplesLoadError errors={[sampleLoadingError]} />}
        {!sampleLoadingError && (
          <>
            {loading && <SampleCardSkeleton numberOfCards={6} />}
            {!loading && samplesCount === 0 && (
              <PageSection variant={"light"} isFilled={true} style={{ marginRight: "25px" }}>
                <EmptyState style={{ height: "350px" }}>
                  <EmptyStateIcon icon={CubesIcon} />
                  <Title headingLevel="h4" size="lg">
                    {"None of the available samples matched this search"}
                  </Title>
                </EmptyState>
              </PageSection>
            )}
            {!loading && samplesCount > 0 && (
              <>
                <Gallery hasGutter={true} minWidths={{ sm: "calc(100%/3.1 - 16px)", default: "100%" }}>
                  {visibleSamples.map((sample) => (
                    <SampleCard
                      sample={sample}
                      key={`sample-${sample.sampleId}`}
                      cover={sampleCovers[sample.sampleId]}
                    />
                  ))}
                </Gallery>
                <br />
                <Pagination
                  itemCount={samplesCount}
                  onSetPage={onSetPage}
                  page={page}
                  perPage={CARDS_PER_PAGE}
                  perPageComponent="button"
                  perPageOptions={SAMPLE_CARDS_PER_PAGE_OPTIONS}
                  variant={PaginationVariant.bottom}
                  widgetId="bottom-example"
                />
              </>
            )}
          </>
        )}
      </PageSection>
    </Page>
  );
}
