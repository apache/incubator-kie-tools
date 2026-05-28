/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { CustomTask } from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { useBpmnEditorStoreApi } from "@kie-tools/bpmn-editor/dist/store/StoreContext";
import { useKogitoEditorEnvelopeContext, ChannelType, KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { useBpmnEditorChannelType } from "../BpmnMultiplyingArchitectureEditorFactory";
import { PropertiesPanelHeaderFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/singleNodeProperties/_PropertiesPanelHeaderFormSection";
import { NameDocumentationAndId } from "@kie-tools/bpmn-editor/dist/propertiesPanel/nameDocumentationAndId/NameDocumentationAndId";
import { Select, SelectList, SelectOption } from "@patternfly/react-core/dist/js/components/Select";
import { MenuToggle, MenuToggleElement } from "@patternfly/react-core/dist/js/components/MenuToggle";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { Table, Thead, Tr, Th, Tbody, Td } from "@patternfly/react-table/dist/js/components/Table";
import {
  BidirectionalDataMappingFormSection,
  useDataMapping,
} from "@kie-tools/bpmn-editor/dist/propertiesPanel/dataMapping/DataMappingFormSection";
import { OnEntryAndExitScriptsFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { FormGroup, FormSection, ActionGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { InputGroup, InputGroupItem } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import HelpIcon from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries } from "../i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { DataMapping, setDataMappingForElement } from "@kie-tools/bpmn-editor/dist/mutations/_dataMapping";
import { DEFAULT_DATA_TYPES } from "@kie-tools/bpmn-editor/dist/mutations/addOrGetItemDefinitions";
import { BpmnEditorChannelApi } from "../BpmnEditorChannelApi";
import {
  RestProperties,
  REST_TASK_ICON,
  REST_PROPERTIES_DATA_TYPES,
  REST_PROPERTIES_KEYS,
  HTTP_METHODS_OPTIONS,
  AUTH_STRATEGIES_OPTIONS,
  HttpMethod,
  AuthStrategy,
  HEADER_PREFIX,
  QUERY_PREFIX,
} from "./RestServiceTaskConstants";
import { ContentDataInput } from "./ContentDataInput";
import "@kie-tools/bpmn-editor/dist/propertiesPanel/metadata/Metadata.css";

export type HeaderParameter = {
  id: string;
  name: string;
  value: string;
};

export type QueryParameter = {
  id: string;
  name: string;
  value: string;
};

export type ContentDataVariable = {
  variableName: string;
  variableValue: string | null;
};

export const RestServiceTaskPropertiesPanel: CustomTask["propertiesPanelComponent"] = ({ task }) => {
  const i18n = new I18n(bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries).getCurrent();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { inputDataMapping, outputDataMapping } = useDataMapping(task, () => {});
  const { channelApi } = useKogitoEditorEnvelopeContext<KogitoEditorEnvelopeApi, BpmnEditorChannelApi>();

  const [localUrl, setLocalUrl] = React.useState<string>("");
  const [localProtocol, setLocalProtocol] = React.useState<string>("");
  const [localHost, setLocalHost] = React.useState<string>("");
  const [localPort, setLocalPort] = React.useState<string>("");
  const [localRequestTimeout, setLocalRequestTimeout] = React.useState<string>("");
  const [localRestServiceCallTaskId, setLocalRestServiceCallTaskId] = React.useState<string>("");

  const dataInputVariables = React.useMemo(
    () =>
      inputDataMapping
        ?.filter((data) => data.name && !(REST_PROPERTIES_KEYS as readonly string[]).includes(data.name))
        ?.map((dataMapping) => dataMapping?.name)
        ?.filter((name): name is string => name !== undefined),
    [inputDataMapping]
  );

  const channelType = useBpmnEditorChannelType();
  const isVSCode = React.useMemo(
    () => channelType === ChannelType.VSCODE_DESKTOP || channelType === ChannelType.VSCODE_WEB,
    [channelType]
  );

  const [testResult, setTestResult] = React.useState<{ status: number; data?: any } | null>(null);
  const [testError, setTestError] = React.useState<string | null>(null);
  const [isLoading, setIsLoading] = React.useState(false);
  const [testToken, setTestToken] = React.useState<string>("");
  const [useCorsProxy, setUseCorsProxy] = React.useState<boolean>(false);
  const [isMethodDropdownOpen, setIsMethodDropdownOpen] = React.useState(false);
  const [isAuthStrategyDropdownOpen, setIsAuthStrategyDropdownOpen] = React.useState(false);

  const [headers, setHeaders] = React.useState<HeaderParameter[]>([]);
  const [queryParams, setQueryParams] = React.useState<QueryParameter[]>([]);
  const [contentDataVariables, setContentDataVariables] = React.useState<ContentDataVariable[]>([]);
  const [contentDataValue, setContentDataValue] = React.useState<string>("");
  const headerIdMapRef = React.useRef<Map<string, string>>(new Map());
  const queryIdMapRef = React.useRef<Map<string, string>>(new Map());
  const entryStyle = {
    padding: "4px",
    margin: "8px",
    width: "calc(100% - 2 * 4px - 2 * 8px)",
  };

  const [hoveredHeaderIndex, setHoveredHeaderIndex] = React.useState<number | undefined>(undefined);
  const [hoveredQueryIndex, setHoveredQueryIndex] = React.useState<number | undefined>(undefined);

  // Detect if URL contains protocol, host, or port
  const hasUrlProtocol = React.useMemo(() => {
    return /^https?:\/\//i.test(localUrl.trim());
  }, [localUrl]);

  const hasUrlHost = React.useMemo(() => {
    return /^https?:\/\/[^/]+/i.test(localUrl.trim());
  }, [localUrl]);

  const hasUrlPort = React.useMemo(() => {
    return /:\d+/.test(localUrl.trim());
  }, [localUrl]);

  const protocolConflict = localProtocol.trim() && hasUrlProtocol;
  const hostConflict = localHost.trim() && hasUrlHost;
  const portConflict = localPort.trim() && hasUrlPort;

  const upsert = React.useCallback(
    (list: typeof inputDataMapping | undefined, name: RestProperties, val: string): typeof inputDataMapping => {
      const safeList = list ?? [];
      const idx = safeList.findIndex((d) => d?.name === name);

      if (!val || val.trim() === "") {
        if (idx >= 0) {
          return safeList.filter((_, i) => i !== idx);
        }
        return safeList;
      }

      if (idx >= 0) {
        return safeList.map((item, i) => (i === idx ? { ...item, value: val } : item));
      }
      return [
        ...safeList,
        {
          name,
          dtype: REST_PROPERTIES_DATA_TYPES?.[name] ?? DEFAULT_DATA_TYPES.STRING,
          isExpression: true,
          value: val,
        },
      ];
    },
    []
  );

  const updateRestProperties = React.useCallback(
    (key: RestProperties, value: string | undefined) => {
      const updatedInputDataMapping = upsert(inputDataMapping, key, value ?? "");

      bpmnEditorStoreApi.setState((s) => {
        setDataMappingForElement({
          definitions: s.bpmn.model.definitions,
          inputDataMapping: updatedInputDataMapping,
          outputDataMapping,
          elementId: task["@_id"],
          element: task.__$$element,
        });
      });
    },
    [task, inputDataMapping, bpmnEditorStoreApi, outputDataMapping, upsert]
  );

  const getValue = React.useCallback(
    (fieldName: RestProperties) => {
      const mapping = (inputDataMapping ?? []).find((dm) => dm.isExpression && dm.name === fieldName);
      return mapping && mapping.isExpression ? mapping.value : "";
    },
    [inputDataMapping]
  );

  React.useEffect(() => {
    setLocalUrl(getValue(RestProperties.Url));
    setLocalProtocol(getValue(RestProperties.Protocol));
    setLocalHost(getValue(RestProperties.Host));
    setLocalPort(getValue(RestProperties.Port));
    setLocalRequestTimeout(getValue(RestProperties.RequestTimeout));
    setLocalRestServiceCallTaskId(getValue(RestProperties.RestServiceCallTaskId));
  }, [getValue]);

  React.useEffect(() => {
    const currentMethod = getValue(RestProperties.Method);
    if (["POST", "PUT", "PATCH"].includes(currentMethod)) {
      const storedValue = getValue(RestProperties.ContentData);
      setContentDataValue(storedValue);
    } else {
      setContentDataValue("");
    }
  }, [getValue]);

  const handleTextInputChange = React.useCallback(
    (key: RestProperties, value: string, setLocalState: React.Dispatch<React.SetStateAction<string>>) => {
      setLocalState(value);
      updateRestProperties(key, value);
    },
    [updateRestProperties]
  );

  const handleMethodChange = React.useCallback(
    (_event: React.MouseEvent<Element, MouseEvent> | undefined, value: string | number | undefined) => {
      if (value) {
        setTestError(null);
        setTestResult(null);
        if (!["POST", "PUT", "PATCH"].includes(String(value))) {
          let updatedInputDataMapping = upsert(inputDataMapping, RestProperties.Method, String(value));
          updatedInputDataMapping = upsert(updatedInputDataMapping, RestProperties.ContentData, "");

          bpmnEditorStoreApi.setState((s) => {
            setDataMappingForElement({
              definitions: s.bpmn.model.definitions,
              inputDataMapping: updatedInputDataMapping,
              outputDataMapping,
              elementId: task["@_id"],
              element: task.__$$element,
            });
          });
        } else {
          updateRestProperties(RestProperties.Method, String(value));
        }
      }
      setIsMethodDropdownOpen(false);
    },
    [inputDataMapping, outputDataMapping, task, bpmnEditorStoreApi, upsert, updateRestProperties]
  );

  React.useEffect(() => {
    if (!dataInputVariables) return;

    if (!contentDataValue) {
      setContentDataVariables([]);
      return;
    }

    const matches = contentDataValue.match(/#\{\s*([\w.-]+)\s*\}/g) || [];
    const variableNames = matches.map((m) => m.replace(/#\{\s*|\s*\}/g, ""));
    const validVariables = variableNames.filter((name) => dataInputVariables.includes(name));

    setContentDataVariables((prevVariables) => {
      const preservedEntries = prevVariables.filter((entry) => validVariables.includes(entry.variableName));

      const newEntries = validVariables
        .filter((name) => !preservedEntries.some((entry) => entry.variableName === name))
        .map((name) => ({
          variableName: name,
          variableValue: null,
        }));

      const updatedVariables = [...preservedEntries, ...newEntries];

      const isSame =
        updatedVariables.length === prevVariables.length &&
        updatedVariables.every((item, index) => {
          const current = prevVariables[index];
          return current && current.variableName === item.variableName && current.variableValue === item.variableValue;
        });

      return isSame ? prevVariables : updatedVariables;
    });
  }, [dataInputVariables, contentDataValue]);

  React.useEffect(() => {
    const extractedHeaders: HeaderParameter[] = [];
    const extractedQueryParams: QueryParameter[] = [];

    (inputDataMapping ?? []).forEach((item) => {
      if (item?.name && item?.isExpression) {
        if (item.name.startsWith(HEADER_PREFIX)) {
          const headerName = item.name.substring(HEADER_PREFIX.length);
          const key = `HEADER_${headerName}`;

          if (!headerIdMapRef.current.has(key)) {
            headerIdMapRef.current.set(key, generateUuid());
          }

          extractedHeaders.push({
            id: headerIdMapRef.current.get(key)!,
            name: headerName,
            value: item.value || "",
          });
        } else if (item.name.startsWith(QUERY_PREFIX)) {
          const queryName = item.name.substring(QUERY_PREFIX.length);
          const key = `QUERY_${queryName}`;

          if (!queryIdMapRef.current.has(key)) {
            queryIdMapRef.current.set(key, generateUuid());
          }

          extractedQueryParams.push({
            id: queryIdMapRef.current.get(key)!,
            name: queryName,
            value: item.value || "",
          });
        }
      }
    });

    setHeaders(extractedHeaders);
    setQueryParams(extractedQueryParams);
  }, [inputDataMapping]);

  const handleTestRequest = React.useCallback(async () => {
    setTestError(null);
    setTestResult(null);
    setIsLoading(true);

    try {
      let url = localUrl.trim();
      const method = getValue(RestProperties.Method).toUpperCase() || "GET";
      const authStrategy = getValue(RestProperties.AccessTokenAcquisitionStrategy);
      const protocol = localProtocol.trim();
      const host = localHost.trim();
      const port = localPort.trim();

      const hasExpression = url && /#{[^}]+}/.test(url);

      const isCompleteUrl = url && (/^https?:\/\//i.test(url) || hasExpression);

      if (!isCompleteUrl) {
        if (!host) {
          if (!url) {
            throw new Error(i18n.restService.urlRequired);
          }
          throw new Error(i18n.restService.hostRequiredError);
        }

        const baseUrl = `${protocol ? `${protocol}://` : ""}${host}${port ? `:${port}` : ""}`;

        if (url) {
          const path = url.startsWith("/") ? url : `/${url}`;
          url = `${baseUrl}${path}`;
        } else {
          url = baseUrl;
        }
      }

      if ((authStrategy === "propagated" || authStrategy === "configured") && !testToken.trim()) {
        throw new Error(`${i18n.restService.testTokenRequiredError}: ${authStrategy}`);
      }

      if (authStrategy === "configured" && !localRestServiceCallTaskId.trim()) {
        throw new Error(i18n.restService.restServiceCallTaskIdRequiredError);
      }

      if (Array.isArray(contentDataVariables) && contentDataVariables.length > 0) {
        for (const { variableName, variableValue } of contentDataVariables) {
          if (variableValue == null || String(variableValue).trim() === "") {
            throw new Error(`${i18n.restService.testVariableMissingError} (#{${variableName}})`);
          }
        }
      }

      const requestHeaders: Record<string, string> = {};

      if (testToken.trim() && authStrategy !== "none") {
        requestHeaders["Authorization"] = `Bearer ${testToken.trim()}`;
      }

      let contentData = getValue(RestProperties.ContentData);

      if (contentData && contentDataVariables.length > 0) {
        const lookup = Object.fromEntries(
          contentDataVariables
            .filter((item) => item.variableValue != null)
            .map((item) => [item.variableName, item.variableValue])
        );

        contentData = contentData.replace(/#\{\s*(.*?)\s*\}/g, (match, varName) => {
          if (!(varName in lookup)) return match;

          const value = lookup[varName];
          const replacementValue = typeof value === "string" ? value : JSON.stringify(value);

          return replacementValue.replace(/\$/g, "$$");
        });
      }

      headers.forEach((header) => {
        if (header.name.trim() && header.value.trim()) {
          requestHeaders[header.name] = header.value;
        }
      });

      if (contentData && ["POST", "PUT", "PATCH"].includes(method) && !requestHeaders["Content-Type"]) {
        requestHeaders["Content-Type"] = "application/json";
      }

      const queryString = queryParams
        .filter((q) => q.name.trim())
        .map((q) => `${encodeURIComponent(q.name)}=${encodeURIComponent(q.value)}`)
        .join("&");

      if (queryString) {
        url = url.includes("?") ? `${url}&${queryString}` : `${url}?${queryString}`;
      }

      let body: string | undefined;
      if (["POST", "PUT", "PATCH"].includes(method) && contentData) {
        body = contentData;
      }

      if (!channelApi.requests.bpmnEditor_restTaskTest) {
        throw new Error(i18n.restService.apiNotAvailableError);
      }

      const result = await channelApi.requests.bpmnEditor_restTaskTest({
        url,
        method,
        headers: requestHeaders,
        body,
        useCorsProxy: isVSCode ? false : useCorsProxy,
      });

      setTestResult({ status: result.status, data: result.data });
    } catch (error) {
      console.error("REST Test Error:", error);
      setTestError((error as Error).message || i18n.restService.genericTestError);
    } finally {
      setIsLoading(false);
    }
  }, [
    localUrl,
    getValue,
    localProtocol,
    localHost,
    localPort,
    testToken,
    localRestServiceCallTaskId,
    contentDataVariables,
    headers,
    queryParams,
    channelApi.requests,
    isVSCode,
    useCorsProxy,
    i18n.restService.hostRequiredError,
    i18n.restService.urlRequired,
    i18n.restService.testTokenRequiredError,
    i18n.restService.restServiceCallTaskIdRequiredError,
    i18n.restService.testVariableMissingError,
    i18n.restService.apiNotAvailableError,
    i18n.restService.genericTestError,
  ]);

  const updateParameterMapping = React.useCallback(
    (prefix: typeof HEADER_PREFIX | typeof QUERY_PREFIX, parameters: Array<{ name: string; value: string }>) => {
      bpmnEditorStoreApi.setState((s) => {
        const filteredInputs = (inputDataMapping ?? []).filter((item) => !item?.name?.startsWith(prefix));

        const paramMappings: DataMapping[] = parameters
          .filter((p) => p.name.trim() || p.value.trim())
          .map((p) => ({
            name: `${prefix}${p.name}`,
            dtype: DEFAULT_DATA_TYPES.STRING,
            isExpression: true,
            value: p.value,
          }));

        const updatedInputDataMapping = [...filteredInputs, ...paramMappings];

        setDataMappingForElement({
          definitions: s.bpmn.model.definitions,
          inputDataMapping: updatedInputDataMapping,
          outputDataMapping,
          elementId: task["@_id"],
          element: task.__$$element,
        });
      });
    },
    [bpmnEditorStoreApi, inputDataMapping, outputDataMapping, task]
  );

  const addHeader = React.useCallback(() => {
    const newHeader: HeaderParameter = {
      id: generateUuid(),
      name: "",
      value: "",
    };
    setHeaders((prev) => [...prev, newHeader]);
  }, []);

  const updateHeader = React.useCallback(
    (id: string, field: "name" | "value", value: string) => {
      setHeaders((prev) => {
        if (field === "name") {
          const oldHeader = prev.find((h) => h.id === id);
          if (oldHeader && oldHeader.name) {
            const oldKey = `HEADER_${oldHeader.name}`;
            headerIdMapRef.current.delete(oldKey);
          }
          if (value.trim()) {
            const newKey = `HEADER_${value}`;
            headerIdMapRef.current.set(newKey, id);
          }
        }

        const updated = prev.map((h) => (h.id === id ? { ...h, [field]: value } : h));
        updateParameterMapping(HEADER_PREFIX, updated);
        return updated;
      });
    },
    [updateParameterMapping]
  );

  const removeHeader = React.useCallback(
    (id: string) => {
      setHeaders((prev) => {
        const headerToRemove = prev.find((h) => h.id === id);
        if (headerToRemove) {
          const key = `HEADER_${headerToRemove.name}`;
          headerIdMapRef.current.delete(key);
        }
        const updated = prev.filter((h) => h.id !== id);
        updateParameterMapping(HEADER_PREFIX, updated);
        return updated;
      });
    },
    [updateParameterMapping]
  );

  const addQueryParam = React.useCallback(() => {
    const newParam: QueryParameter = {
      id: generateUuid(),
      name: "",
      value: "",
    };
    setQueryParams((prev) => [...prev, newParam]);
  }, []);

  const updateQueryParam = React.useCallback(
    (id: string, field: "name" | "value", value: string) => {
      setQueryParams((prev) => {
        if (field === "name") {
          const oldParam = prev.find((q) => q.id === id);
          if (oldParam && oldParam.name) {
            const oldKey = `QUERY_${oldParam.name}`;
            queryIdMapRef.current.delete(oldKey);
          }
          if (value.trim()) {
            const newKey = `QUERY_${value}`;
            queryIdMapRef.current.set(newKey, id);
          }
        }

        const updated = prev.map((q) => (q.id === id ? { ...q, [field]: value } : q));
        updateParameterMapping(QUERY_PREFIX, updated);
        return updated;
      });
    },
    [updateParameterMapping]
  );

  const removeQueryParam = React.useCallback(
    (id: string) => {
      setQueryParams((prev) => {
        const queryToRemove = prev.find((q) => q.id === id);
        if (queryToRemove) {
          const key = `QUERY_${queryToRemove.name}`;
          queryIdMapRef.current.delete(key);
        }
        const updated = prev.filter((q) => q.id !== id);
        updateParameterMapping(QUERY_PREFIX, updated);
        return updated;
      });
    },
    [updateParameterMapping]
  );

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={task["@_name"] || i18n.restService.name}
        icon={REST_TASK_ICON}
        shouldStartExpanded={true}
      >
        <NameDocumentationAndId element={task} />
        <FormSection>
          <FormGroup label={i18n.restService.url} isRequired fieldId="rest-url">
            <InputGroup>
              <InputGroupItem style={{ width: "120px", flexShrink: 0 }}>
                <Select
                  id="rest-method"
                  isOpen={isMethodDropdownOpen}
                  selected={getValue(RestProperties.Method) || HttpMethod.GET}
                  shouldFocusFirstItemOnOpen={false}
                  onSelect={handleMethodChange}
                  onOpenChange={setIsMethodDropdownOpen}
                  toggle={(toggleRef: React.Ref<MenuToggleElement>) => (
                    <MenuToggle
                      ref={toggleRef}
                      onClick={() => setIsMethodDropdownOpen(!isMethodDropdownOpen)}
                      isExpanded={isMethodDropdownOpen}
                      isFullWidth
                    >
                      {
                        i18n.restService[
                          HTTP_METHODS_OPTIONS.find(
                            (opt) => opt.value === (getValue(RestProperties.Method) || HttpMethod.GET)
                          )?.labelKey as keyof typeof i18n.restService
                        ]
                      }
                    </MenuToggle>
                  )}
                >
                  <SelectList>
                    {HTTP_METHODS_OPTIONS.map((option) => (
                      <SelectOption key={option.value} value={option.value}>
                        {i18n.restService[option.labelKey]}
                      </SelectOption>
                    ))}
                  </SelectList>
                </Select>
              </InputGroupItem>
              <InputGroupItem isFill>
                <TextInput
                  id="rest-url"
                  value={localUrl}
                  onChange={(_, value) => handleTextInputChange(RestProperties.Url, value, setLocalUrl)}
                  aria-describedby="rest-url-helper"
                />
              </InputGroupItem>
            </InputGroup>
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="indeterminate">{i18n.restService.urlHelp}</HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>

          <FormGroup label={i18n.restService.protocol} fieldId="rest-protocol">
            <TextInput
              id="rest-protocol"
              value={localProtocol}
              validated={protocolConflict ? "error" : "default"}
              onChange={(_, value) => handleTextInputChange(RestProperties.Protocol, value, setLocalProtocol)}
              aria-describedby="rest-protocol-helper"
            />
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant={protocolConflict ? "error" : "indeterminate"}>
                  {protocolConflict ? i18n.restService.protocolConflictError : i18n.restService.protocolHelp}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>

          <FormGroup label={i18n.restService.host} fieldId="rest-host">
            <TextInput
              id="rest-host"
              value={localHost}
              validated={hostConflict ? "error" : "default"}
              onChange={(_, value) => handleTextInputChange(RestProperties.Host, value, setLocalHost)}
              aria-describedby="rest-host-helper"
            />
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant={hostConflict ? "error" : "indeterminate"}>
                  {hostConflict ? i18n.restService.hostConflictError : i18n.restService.hostHelp}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>

          <FormGroup label={i18n.restService.port} fieldId="rest-port">
            <TextInput
              id="rest-port"
              type="number"
              value={localPort}
              validated={portConflict ? "error" : "default"}
              onChange={(_, value) => handleTextInputChange(RestProperties.Port, value, setLocalPort)}
              aria-describedby="rest-port-helper"
            />
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant={portConflict ? "error" : "indeterminate"}>
                  {portConflict ? i18n.restService.portConflictError : i18n.restService.portHelp}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>

          {["POST", "PUT", "PATCH"].includes(getValue(RestProperties.Method)) && (
            <>
              <FormGroup label={i18n.restService.contentData} fieldId="rest-content-data">
                <ContentDataInput
                  value={contentDataValue}
                  onChange={(value) => {
                    setContentDataValue(value);
                    updateRestProperties(RestProperties.ContentData, value);
                  }}
                  variableSuggestions={dataInputVariables}
                  aria-describedby="rest-content-data-helper"
                />
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="indeterminate">{i18n.restService.contentDataHelp}</HelperTextItem>
                  </HelperText>
                </FormHelperText>
              </FormGroup>
            </>
          )}

          <FormGroup label={i18n.restService.requestTimeout} fieldId="rest-timeout">
            <TextInput
              id="rest-timeout"
              type="number"
              value={localRequestTimeout}
              onChange={(_, value) =>
                handleTextInputChange(RestProperties.RequestTimeout, value, setLocalRequestTimeout)
              }
            />
          </FormGroup>

          <FormGroup label={i18n.restService.headers} fieldId="rest-headers">
            {headers.length > 0 ? (
              <>
                <div>
                  <Grid md={6}>
                    <GridItem span={5}>
                      <div style={entryStyle}>{i18n.restService.headerName}</div>
                    </GridItem>
                    <GridItem span={6}>
                      <div style={entryStyle}>{i18n.restService.headerValue}</div>
                    </GridItem>
                    <GridItem span={1}>
                      <Button variant="plain" onClick={addHeader} aria-label="Add header">
                        <PlusCircleIcon />
                      </Button>
                    </GridItem>
                  </Grid>
                </div>
                {headers.map((header, index) => (
                  <div key={header.id}>
                    <Grid
                      md={6}
                      className={"kie-bpmn-editor--properties-panel--metadata-entry"}
                      onMouseEnter={() => setHoveredHeaderIndex(index)}
                      onMouseLeave={() => setHoveredHeaderIndex(undefined)}
                    >
                      <GridItem span={5}>
                        <input
                          autoFocus={true}
                          style={entryStyle}
                          type="text"
                          value={header.name}
                          onChange={(e) => updateHeader(header.id, "name", e.target.value)}
                          aria-label={`Header ${index + 1} name`}
                        />
                      </GridItem>
                      <GridItem span={6}>
                        <input
                          style={entryStyle}
                          type="text"
                          value={header.value}
                          onChange={(e) => updateHeader(header.id, "value", e.target.value)}
                          aria-label={`Header ${index + 1} value`}
                        />
                      </GridItem>
                      <GridItem span={1}>
                        {hoveredHeaderIndex === index && (
                          <Button
                            variant="plain"
                            style={{ paddingLeft: 0 }}
                            onClick={() => removeHeader(header.id)}
                            aria-label={`Remove header ${index + 1}`}
                          >
                            <TimesIcon />
                          </Button>
                        )}
                      </GridItem>
                    </Grid>
                  </div>
                ))}
              </>
            ) : (
              <div style={{ position: "relative" }}>
                <div style={{ padding: "10px", background: "#eee", borderRadius: "10px", textAlign: "center" }}>
                  {i18n.restService.noHeaders}
                </div>
                <div style={{ position: "absolute", top: "calc(50% - 16px)", right: "0" }}>
                  <Button variant="plain" style={{ paddingLeft: 0 }} onClick={addHeader}>
                    <PlusCircleIcon />
                  </Button>
                </div>
              </div>
            )}
          </FormGroup>

          <FormGroup label={i18n.restService.queryParameters} fieldId="rest-query-parameters">
            {queryParams.length > 0 ? (
              <>
                <div>
                  <Grid md={6}>
                    <GridItem span={5}>
                      <div style={entryStyle}>{i18n.restService.queryParameterName}</div>
                    </GridItem>
                    <GridItem span={6}>
                      <div style={entryStyle}>{i18n.restService.queryParameterValue}</div>
                    </GridItem>
                    <GridItem span={1}>
                      <div style={{ textAlign: "right" }}>
                        <Button
                          variant="plain"
                          style={{ paddingLeft: 0 }}
                          onClick={addQueryParam}
                          aria-label="Add query parameter"
                        >
                          <PlusCircleIcon />
                        </Button>
                      </div>
                    </GridItem>
                  </Grid>
                </div>
                {queryParams.map((param, index) => (
                  <div key={param.id}>
                    <Grid
                      md={6}
                      className={"kie-bpmn-editor--properties-panel--metadata-entry"}
                      onMouseEnter={() => setHoveredQueryIndex(index)}
                      onMouseLeave={() => setHoveredQueryIndex(undefined)}
                    >
                      <GridItem span={5}>
                        <input
                          autoFocus={true}
                          style={entryStyle}
                          type="text"
                          value={param.name}
                          onChange={(e) => updateQueryParam(param.id, "name", e.target.value)}
                          aria-label={`Query parameter ${index + 1} name`}
                        />
                      </GridItem>
                      <GridItem span={6}>
                        <input
                          style={entryStyle}
                          type="text"
                          value={param.value}
                          onChange={(e) => updateQueryParam(param.id, "value", e.target.value)}
                          aria-label={`Query parameter ${index + 1} value`}
                        />
                      </GridItem>
                      <GridItem span={1}>
                        {hoveredQueryIndex === index && (
                          <Button
                            variant="plain"
                            style={{ paddingLeft: 0 }}
                            onClick={() => removeQueryParam(param.id)}
                            aria-label={`Remove query parameter ${index + 1}`}
                          >
                            <TimesIcon />
                          </Button>
                        )}
                      </GridItem>
                    </Grid>
                  </div>
                ))}
              </>
            ) : (
              <div style={{ position: "relative" }}>
                <div style={{ padding: "10px", background: "#eee", borderRadius: "10px", textAlign: "center" }}>
                  {i18n.restService.noQueryParameters}
                </div>
                <div style={{ position: "absolute", top: "calc(50% - 16px)", right: "0" }}>
                  <Button variant="plain" style={{ paddingLeft: 0 }} onClick={addQueryParam}>
                    <PlusCircleIcon />
                  </Button>
                </div>
              </div>
            )}
          </FormGroup>

          <FormGroup label={i18n.restService.accessTokenStrategy} isRequired fieldId="rest-auth-strategy">
            <Select
              id="rest-auth-strategy"
              isOpen={isAuthStrategyDropdownOpen}
              selected={getValue(RestProperties.AccessTokenAcquisitionStrategy) || AuthStrategy.NONE}
              shouldFocusFirstItemOnOpen={false}
              onSelect={(
                _event: React.MouseEvent<Element, MouseEvent> | undefined,
                value: string | number | undefined
              ) => {
                if (value) {
                  updateRestProperties(RestProperties.AccessTokenAcquisitionStrategy, String(value));
                }
                setIsAuthStrategyDropdownOpen(false);
              }}
              onOpenChange={setIsAuthStrategyDropdownOpen}
              toggle={(toggleRef: React.Ref<MenuToggleElement>) => (
                <MenuToggle
                  ref={toggleRef}
                  onClick={() => setIsAuthStrategyDropdownOpen(!isAuthStrategyDropdownOpen)}
                  isExpanded={isAuthStrategyDropdownOpen}
                  isFullWidth
                >
                  {
                    i18n.restService[
                      AUTH_STRATEGIES_OPTIONS.find(
                        (opt) =>
                          opt.value === (getValue(RestProperties.AccessTokenAcquisitionStrategy) || AuthStrategy.NONE)
                      )?.labelKey as keyof typeof i18n.restService
                    ]
                  }
                </MenuToggle>
              )}
            >
              <SelectList>
                {AUTH_STRATEGIES_OPTIONS.map((option) => (
                  <SelectOption key={option.value} value={option.value}>
                    {i18n.restService[option.labelKey]}
                  </SelectOption>
                ))}
              </SelectList>
            </Select>
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="indeterminate">
                  {getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.PROPAGATED &&
                    i18n.restService.accessTokenStrategyPropagatedHelp}
                  {getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.CONFIGURED &&
                    i18n.restService.accessTokenStrategyConfiguredHelp}
                  {(getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.NONE ||
                    !getValue(RestProperties.AccessTokenAcquisitionStrategy)) &&
                    i18n.restService.accessTokenStrategyNoneHelp}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>

          <FormGroup
            label={i18n.restService.restServiceCallTaskId}
            isRequired={getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.CONFIGURED}
            fieldId="rest-task-id"
          >
            <TextInput
              id="rest-task-id"
              value={localRestServiceCallTaskId}
              onChange={(_, value) =>
                handleTextInputChange(RestProperties.RestServiceCallTaskId, value, setLocalRestServiceCallTaskId)
              }
              placeholder={i18n.restService.restServiceCallTaskIdPlaceholder}
            />
          </FormGroup>
        </FormSection>

        <FormSection title={i18n.restService.testSection}>
          {["POST", "PUT", "PATCH"].includes(getValue(RestProperties.Method)) && contentDataVariables.length > 0 && (
            <FormGroup
              label={i18n.restService.testVariables}
              fieldId="rest-content-data-variables"
              labelIcon={
                <Popover
                  headerContent={i18n.restService.testVariables}
                  bodyContent={
                    <List>
                      <ListItem>{i18n.restService.testVariablesHelp}</ListItem>
                    </List>
                  }
                >
                  <button
                    type="button"
                    aria-label={i18n.restService.moreInfoForTestVariables}
                    onClick={(e) => e.preventDefault()}
                    className="pf-v5-c-form__group-label-help"
                  >
                    <HelpIcon />
                  </button>
                </Popover>
              }
            >
              <div style={{ marginTop: "8px" }}>
                <Table aria-label={i18n.restService.contentDataVariablesTable} variant="compact">
                  <Thead>
                    <Tr>
                      <Th>{i18n.restService.variableName}</Th>
                      <Th>{i18n.restService.variableValue}</Th>
                    </Tr>
                  </Thead>
                  <Tbody>
                    {contentDataVariables.map((variable, index) => (
                      <Tr key={`${variable.variableName}-${index}`}>
                        <Td dataLabel={i18n.restService.variableName}>{variable.variableName}</Td>
                        <Td dataLabel={i18n.restService.variableValue}>
                          <TextInput
                            aria-label={i18n.restService.valueForVariable.replace(
                              "{{variableName}}",
                              variable.variableName
                            )}
                            value={variable.variableValue || ""}
                            onChange={(_, value) => {
                              const updatedVariables = [...contentDataVariables];
                              updatedVariables[index] = {
                                ...updatedVariables[index],
                                variableValue: value,
                              };
                              setContentDataVariables(updatedVariables);
                            }}
                            isRequired={true}
                          />
                        </Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </div>
            </FormGroup>
          )}

          {(getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.PROPAGATED ||
            getValue(RestProperties.AccessTokenAcquisitionStrategy) === AuthStrategy.CONFIGURED) && (
            <FormGroup
              label={i18n.restService.testToken}
              fieldId="rest-test-token"
              labelIcon={
                <Popover
                  headerContent={i18n.restService.testToken}
                  bodyContent={
                    <List>
                      <ListItem>{i18n.restService.testTokenHelper}</ListItem>
                    </List>
                  }
                >
                  <button
                    type="button"
                    aria-label="More info for test token"
                    onClick={(e) => e.preventDefault()}
                    className="pf-v5-c-form__group-label-help"
                  >
                    <HelpIcon />
                  </button>
                </Popover>
              }
            >
              <TextInput
                id="rest-test-token"
                type="password"
                value={testToken}
                onChange={(_, value) => setTestToken(value)}
                placeholder={i18n.restService.testTokenPlaceholder}
                aria-describedby="rest-test-token-helper"
              />
            </FormGroup>
          )}

          {!isVSCode && (
            <FormGroup fieldId="rest-use-cors-proxy">
              <Checkbox
                id="rest-use-cors-proxy"
                label={i18n.restService.useCorsProxy}
                isChecked={useCorsProxy}
                onChange={(_, checked) => setUseCorsProxy(checked)}
                aria-label={i18n.restService.useCorsProxyAriaLabel}
                aria-describedby="rest-use-cors-proxy-helper"
              />
              <FormHelperText>
                <HelperText>
                  <HelperTextItem variant="indeterminate">{i18n.restService.useCorsProxyHelper}</HelperTextItem>
                </HelperText>
              </FormHelperText>
            </FormGroup>
          )}

          <ActionGroup>
            <Button
              variant="primary"
              isBlock
              id="rest-test-btn"
              onClick={() => handleTestRequest()}
              isDisabled={isLoading || !localUrl}
            >
              {isLoading ? i18n.restService.testing : i18n.restService.testRequest}
            </Button>
          </ActionGroup>
        </FormSection>

        <FormSection>
          {testError && (
            <Alert variant="danger" isInline title={i18n.restService.testFailed}>
              {testError}
            </Alert>
          )}

          {testResult && (
            <>
              <FormGroup label={i18n.restService.testResult} fieldId="rest-test-result">
                <Alert
                  variant={testResult.status >= 200 && testResult.status < 300 ? "success" : "warning"}
                  isInline
                  title={`Status: ${testResult.status}`}
                />
              </FormGroup>
              {testResult.data && (
                <FormGroup label={i18n.restService.response} fieldId="rest-test-response">
                  <TextArea
                    id="rest-test-response"
                    value={
                      typeof testResult.data === "string" ? testResult.data : JSON.stringify(testResult.data, null, 2)
                    }
                    readOnly
                    rows={10}
                    resizeOrientation="vertical"
                    aria-label="Response data"
                  />
                </FormGroup>
              )}
            </>
          )}
        </FormSection>
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={task} />

      <OnEntryAndExitScriptsFormSection element={task} />
    </>
  );
};
