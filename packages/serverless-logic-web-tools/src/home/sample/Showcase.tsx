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

import * as React from "react";
import { useEffect, useState, useCallback, useMemo } from "react";
import { TextContent, Text } from "@patternfly/react-core/dist/js/components/Text";
import { SampleCard } from "./SampleCard";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Sample, SampleCategory } from "./sampleApi";
import { SampleCardSkeleton } from "./SampleCardSkeleton";
import { SamplesLoadError } from "./SamplesLoadError";
import { useSampleDispatch } from "./hooks/SampleContext";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { FileLabel } from "../../workspace/components/FileLabel";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

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

const CATEGORY_ARRAY = Object.keys(SAMPLE_PRIORITY) as SampleCategory[];

export function Showcase() {
  const sampleDispatch = useSampleDispatch();
  const [loading, setLoading] = useState<boolean>(true);
  const [samples, setSamples] = useState<Sample[]>([]);
  const [sampleLoadingError, setSampleLoadingError] = useState("");
  const [searchFilter, setSearchFilter] = useState("");
  const [categoryFilter, setCategoryFilter] = useState<SampleCategory | undefined>();
  const [isCategoryFilterDropdownOpen, setCategoryFilterDropdownOpen] = useState(false);

  const onSearch = useCallback(
    async (args: { searchValue: string; category?: SampleCategory }) => {
      if (args.searchValue === searchFilter && args.category === categoryFilter) {
        return;
      }
      setSearchFilter(args.searchValue);
      setCategoryFilter(args.category);
      setSamples(await sampleDispatch.getSamples({ searchFilter: args.searchValue, categoryFilter: args.category }));
    },
    [categoryFilter, sampleDispatch, searchFilter]
  );

  useEffect(() => {
    sampleDispatch
      .getSamples({})
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
  }, [sampleDispatch]);

  const filterResultMessage = useMemo(() => {
    if (samples.length === 0) {
      return;
    }
    const isPlural = samples.length > 1;
    return `Showing ${samples.length} sample${isPlural ? "s" : ""}`;
  }, [samples.length]);

  const selectedCategory = useMemo(() => {
    if (categoryFilter) {
      return LABEL_MAP[categoryFilter];
    }
    return ALL_CATEGORIES_LABEL;
  }, [categoryFilter]);

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

  return (
    <>
      {sampleLoadingError && <SamplesLoadError errors={[sampleLoadingError]} />}
      {!sampleLoadingError && (
        <>
          <TextContent>
            <Text component="h1">Samples Showcase</Text>
          </TextContent>
          <br />
          <Flex flexWrap={{ default: "wrap" }}>
            <FlexItem style={{ marginRight: 0 }}>
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
            </FlexItem>
            <FlexItem>
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
            </FlexItem>
            <FlexItem>
              {filterResultMessage && (
                <TextContent>
                  <Text>{filterResultMessage}</Text>
                </TextContent>
              )}
            </FlexItem>
          </Flex>
          <br />
          {loading && <SampleCardSkeleton numberOfCards={4} />}
          {!loading && samples.length === 0 && (
            <PageSection variant={"light"} isFilled={true} style={{ marginRight: "25px" }}>
              <EmptyState style={{ height: "350px" }}>
                <EmptyStateIcon icon={CubesIcon} />
                <Title headingLevel="h4" size="lg">
                  {"None of the available samples matched this search"}
                </Title>
              </EmptyState>
            </PageSection>
          )}
          {!loading && samples.length > 0 && (
            <Gallery
              hasGutter={true}
              minWidths={{ sm: "calc(100%/3.1 - 16px)", default: "100%" }}
              style={{
                overflowX: "auto",
                gridAutoFlow: "column",
                gridAutoColumns: "minmax(calc(100%/3.1 - 16px),1fr)",
                paddingBottom: "8px",
                paddingRight: "var(--pf-c-page__main-section--xl--PaddingRight)",
              }}
            >
              {samples.map((sample) => (
                <SampleCard sample={sample} key={`sample-${sample.sampleId}`} />
              ))}
            </Gallery>
          )}
        </>
      )}
    </>
  );
}
