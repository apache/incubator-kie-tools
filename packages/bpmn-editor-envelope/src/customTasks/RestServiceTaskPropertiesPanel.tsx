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
import { useKogitoEditorEnvelopeContext, ChannelType } from "@kie-tools-core/editor/dist/api";
import { useBpmnEditorChannelType } from "../BpmnMultiplyingArchitectureEditorFactory";
import { PropertiesPanelHeaderFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/singleNodeProperties/_PropertiesPanelHeaderFormSection";
import { NameDocumentationAndId } from "@kie-tools/bpmn-editor/dist/propertiesPanel/nameDocumentationAndId/NameDocumentationAndId";
import {
  BidirectionalDataMappingFormSection,
  useDataMapping,
} from "@kie-tools/bpmn-editor/dist/propertiesPanel/dataMapping/DataMappingFormSection";
import { OnEntryAndExitScriptsFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { FormGroup, FormSection, ActionGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { BpmnEditorEnvelopeI18n, bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries } from "../i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { DataMapping, setDataMappingForElement } from "@kie-tools/bpmn-editor/dist/mutations/_dataMapping";
import { DEFAULT_DATA_TYPES } from "@kie-tools/bpmn-editor/dist/mutations/addOrGetItemDefinitions";
import { BpmnEditorChannelApi } from "../BpmnEditorChannelApi";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import {
  RestProperties,
  REST_PROPERTIES_KEYS,
  REST_TASK_ICON,
  REST_PROPERTIES_DATA_TYPES,
} from "./RestServiceTaskConstants";

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

const HTTP_METHODS = ["GET", "POST", "PUT", "PATCH", "DELETE"];
const AUTH_STRATEGIES = ["propagated", "configured", "none"];

export const RestServiceTaskPropertiesPanel: CustomTask["propertiesPanelComponent"] = ({ task }) => {
  const i18n = new I18n(bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries).getCurrent();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { inputDataMapping, outputDataMapping } = useDataMapping(task, () => {});
  const envelopeContext = useKogitoEditorEnvelopeContext();
  const channelApi = envelopeContext.channelApi as unknown as MessageBusClientApi<BpmnEditorChannelApi>;

  const channelType = useBpmnEditorChannelType();
  const isVSCode = React.useMemo(
    () => channelType === ChannelType.VSCODE_DESKTOP || channelType === ChannelType.VSCODE_WEB,
    [channelType]
  );

  const [testResult, setTestResult] = React.useState<{ status: number; data: any; headers?: any } | null>(null);
  const [testError, setTestError] = React.useState<string | null>(null);
  const [isLoading, setIsLoading] = React.useState(false);
  const [testToken, setTestToken] = React.useState<string>("");
  const [useCorsProxy, setUseCorsProxy] = React.useState<boolean>(false);

  const [headers, setHeaders] = React.useState<HeaderParameter[]>([]);
  const [queryParams, setQueryParams] = React.useState<QueryParameter[]>([]);
  const headerIdMapRef = React.useRef<Map<string, string>>(new Map());
  const queryIdMapRef = React.useRef<Map<string, string>>(new Map());

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
    (fieldName: RestProperties) =>
      (inputDataMapping ?? []).filter((dm) => dm.isExpression).find((dm) => dm.name === fieldName)?.value ?? "",
    [inputDataMapping]
  );

  React.useEffect(() => {
    const extractedHeaders: HeaderParameter[] = [];
    const extractedQueryParams: QueryParameter[] = [];

    (inputDataMapping ?? []).forEach((item) => {
      if (item?.name && item?.isExpression) {
        if (item.name.startsWith("HEADER_")) {
          const headerName = item.name.substring(7);
          const key = `HEADER_${headerName}`;

          if (!headerIdMapRef.current.has(key)) {
            headerIdMapRef.current.set(key, generateUuid());
          }

          extractedHeaders.push({
            id: headerIdMapRef.current.get(key)!,
            name: headerName,
            value: item.value || "",
          });
        } else if (item.name.startsWith("QUERY_")) {
          const queryName = item.name.substring(6);
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
      let url = getValue(RestProperties.Url).trim();
      const method = getValue(RestProperties.Method).toUpperCase() || "GET";
      const authStrategy = getValue(RestProperties.AccessTokenAcquisitionStrategy);
      const protocol = getValue(RestProperties.Protocol).trim();
      const host = getValue(RestProperties.Host).trim();
      const port = getValue(RestProperties.Port).trim();

      const hasExpression = url && /#{[^}]+}/.test(url);

      const isCompleteUrl = url && (/^https?:\/\//i.test(url) || hasExpression);

      if (!isCompleteUrl) {
        if (!host) {
          if (!url) {
            throw new Error(i18n.restService.urlRequired);
          }
          throw new Error("Host is required when URL is not complete");
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
        throw new Error(`Test token is required for ${authStrategy} authentication strategy`);
      }

      const requestHeaders: Record<string, string> = {};

      if (testToken.trim() && authStrategy !== "none") {
        requestHeaders["Authorization"] = `Bearer ${testToken.trim()}`;
      }

      const contentData = getValue(RestProperties.ContentData);
      if (contentData && ["POST", "PUT", "PATCH"].includes(method)) {
        requestHeaders["Content-Type"] = "application/json";
      }

      headers.forEach((header) => {
        if (header.name.trim() && header.value.trim()) {
          requestHeaders[header.name] = header.value;
        }
      });

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
        throw new Error("REST task test API is not available");
      }

      const result = await channelApi.requests.bpmnEditor_restTaskTest({
        url,
        method,
        headers: requestHeaders,
        body,
        useCorsProxy: isVSCode ? false : useCorsProxy,
      });

      setTestResult(result);
    } catch (error) {
      console.error("REST Test Error:", error);
      setTestError((error as Error).message || "An error occurred while testing the REST call");
    } finally {
      setIsLoading(false);
    }
  }, [
    getValue,
    testToken,
    headers,
    queryParams,
    channelApi.requests,
    isVSCode,
    useCorsProxy,
    i18n.restService.urlRequired,
  ]);

  const updateParameterMapping = React.useCallback(
    (prefix: "HEADER_" | "QUERY_", parameters: Array<{ name: string; value: string }>) => {
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
        updateParameterMapping("HEADER_", updated);
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
        updateParameterMapping("HEADER_", updated);
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
        updateParameterMapping("QUERY_", updated);
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
        updateParameterMapping("QUERY_", updated);
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
        shouldStartExpanded={false}
      >
        <NameDocumentationAndId element={task} />
      </PropertiesPanelHeaderFormSection>
      <FormSection style={{ "--pf-v5-c-form__section--Gap": "0.5rem" } as React.CSSProperties}>
        <FormGroup label={i18n.restService.url} isRequired fieldId="rest-url">
          <TextInput
            id="rest-url"
            value={getValue(RestProperties.Url)}
            onChange={(_, value) => updateRestProperties(RestProperties.Url, value)}
            placeholder={i18n.restService.urlPlaceholder}
          />
          <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.25rem" }}>{i18n.restService.urlHelp}</div>
        </FormGroup>

        <FormGroup label={i18n.restService.method} fieldId="rest-method">
          <FormSelect
            id="rest-method"
            value={getValue(RestProperties.Method) || "GET"}
            onChange={(_, value) => updateRestProperties(RestProperties.Method, value)}
          >
            {HTTP_METHODS.map((method) => (
              <FormSelectOption key={method} value={method} label={method} />
            ))}
          </FormSelect>
        </FormGroup>

        <FormGroup label={i18n.restService.protocol} fieldId="rest-protocol">
          <TextInput
            id="rest-protocol"
            value={getValue(RestProperties.Protocol)}
            onChange={(_, value) => updateRestProperties(RestProperties.Protocol, value)}
            placeholder={i18n.restService.protocolPlaceholder}
          />
          <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.25rem" }}>
            {i18n.restService.protocolHelp}
          </div>
        </FormGroup>

        <FormGroup label={i18n.restService.host} fieldId="rest-host">
          <TextInput
            id="rest-host"
            value={getValue(RestProperties.Host)}
            onChange={(_, value) => updateRestProperties(RestProperties.Host, value)}
            placeholder={i18n.restService.hostPlaceholder}
          />
          <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.25rem" }}>
            {i18n.restService.hostHelp}
          </div>
        </FormGroup>

        <FormGroup label={i18n.restService.port} fieldId="rest-port">
          <TextInput
            id="rest-port"
            type="number"
            value={getValue(RestProperties.Port)}
            onChange={(_, value) => updateRestProperties(RestProperties.Port, value)}
            placeholder={i18n.restService.portPlaceholder}
          />
          <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.25rem" }}>
            {i18n.restService.portHelp}
          </div>
        </FormGroup>

        {["POST", "PUT", "PATCH"].includes(getValue(RestProperties.Method)) && (
          <FormGroup label={i18n.restService.contentData} fieldId="rest-content-data">
            <TextArea
              id="rest-content-data"
              value={getValue(RestProperties.ContentData)}
              onChange={(_, value) => updateRestProperties(RestProperties.ContentData, value)}
              placeholder={i18n.restService.contentDataPlaceholder}
              rows={5}
            />
          </FormGroup>
        )}

        <FormGroup label={i18n.restService.requestTimeout} fieldId="rest-timeout">
          <TextInput
            id="rest-timeout"
            type="number"
            value={getValue(RestProperties.RequestTimeout)}
            onChange={(_, value) => updateRestProperties(RestProperties.RequestTimeout, value)}
            placeholder="30000"
          />
        </FormGroup>
      </FormSection>

      <FormSection
        title={i18n.restService.headers}
        style={{ "--pf-v5-c-form__section--Gap": "1rem" } as React.CSSProperties}
      >
        {headers.map((header, index) => (
          <Grid key={header.id} hasGutter style={{ marginBottom: "0.25rem" }}>
            <GridItem span={5}>
              <TextInput
                id={`header-name-${header.id}`}
                value={header.name}
                onChange={(_, value) => updateHeader(header.id, "name", value)}
                aria-label={`Header ${index + 1} name`}
              />
            </GridItem>
            <GridItem span={5}>
              <TextInput
                id={`header-value-${header.id}`}
                value={header.value}
                onChange={(_, value) => updateHeader(header.id, "value", value)}
                aria-label={`Header ${index + 1} value`}
              />
            </GridItem>
            <GridItem span={2}>
              <Button
                variant="plain"
                onClick={() => removeHeader(header.id)}
                aria-label={`Remove header ${index + 1}`}
                style={{
                  fontSize: "1.5rem",
                  padding: "0.25rem 0.5rem",
                  color: "#c9190b",
                }}
              >
                ×
              </Button>
            </GridItem>
          </Grid>
        ))}

        <Button variant="secondary" onClick={addHeader} style={{ marginTop: "0.5rem" }}>
          {i18n.restService.addHeader}
        </Button>
      </FormSection>

      <FormSection
        title={i18n.restService.queryParameters}
        style={{ "--pf-v5-c-form__section--Gap": "1rem" } as React.CSSProperties}
      >
        {queryParams.map((param, index) => (
          <Grid key={param.id} hasGutter style={{ marginBottom: "0.25rem" }}>
            <GridItem span={5}>
              <TextInput
                id={`query-name-${param.id}`}
                value={param.name}
                onChange={(_, value) => updateQueryParam(param.id, "name", value)}
                aria-label={`Query parameter ${index + 1} name`}
              />
            </GridItem>
            <GridItem span={5}>
              <TextInput
                id={`query-value-${param.id}`}
                value={param.value}
                onChange={(_, value) => updateQueryParam(param.id, "value", value)}
                aria-label={`Query parameter ${index + 1} value`}
              />
            </GridItem>
            <GridItem span={2}>
              <Button
                variant="plain"
                onClick={() => removeQueryParam(param.id)}
                aria-label={`Remove query parameter ${index + 1}`}
                style={{
                  fontSize: "1.5rem",
                  padding: "0.25rem 0.5rem",
                  color: "#c9190b",
                }}
              >
                ×
              </Button>
            </GridItem>
          </Grid>
        ))}

        <Button variant="secondary" onClick={addQueryParam} style={{ marginTop: "0.5rem" }}>
          {i18n.restService.addQueryParameter}
        </Button>
      </FormSection>

      <FormSection style={{ "--pf-v5-c-form__section--Gap": "1rem" } as React.CSSProperties}>
        <FormGroup label={i18n.restService.accessTokenStrategy} isRequired fieldId="rest-auth-strategy">
          <FormSelect
            id="rest-auth-strategy"
            value={getValue(RestProperties.AccessTokenAcquisitionStrategy) || "none"}
            onChange={(_, value) => updateRestProperties(RestProperties.AccessTokenAcquisitionStrategy, value)}
          >
            {AUTH_STRATEGIES.map((strategy) => (
              <FormSelectOption key={strategy} value={strategy} label={strategy} />
            ))}
          </FormSelect>
        </FormGroup>

        {getValue(RestProperties.AccessTokenAcquisitionStrategy) === "configured" && (
          <FormGroup label={i18n.restService.restServiceCallTaskId} isRequired fieldId="rest-task-id">
            <TextInput
              id="rest-task-id"
              value={getValue(RestProperties.RestServiceCallTaskId)}
              onChange={(_, value) => updateRestProperties(RestProperties.RestServiceCallTaskId, value)}
              placeholder={i18n.restService.restServiceCallTaskIdPlaceholder}
            />
          </FormGroup>
        )}

        {(getValue(RestProperties.AccessTokenAcquisitionStrategy) === "propagated" ||
          getValue(RestProperties.AccessTokenAcquisitionStrategy) === "configured") && (
          <FormGroup label={i18n.restService.testToken} fieldId="rest-test-token">
            <TextInput
              id="rest-test-token"
              type="password"
              value={testToken}
              onChange={(_, value) => setTestToken(value)}
              placeholder={i18n.restService.testTokenPlaceholder}
            />
            <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.5rem" }}>
              {i18n.restService.testTokenHelper}
            </div>
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
            />
            <div style={{ fontSize: "0.875rem", color: "#6a6e73", marginTop: "0.25rem" }}>
              {i18n.restService.useCorsProxyHelper}
            </div>
          </FormGroup>
        )}

        <ActionGroup>
          <Button
            variant="primary"
            isBlock
            id="rest-test-btn"
            onClick={() => handleTestRequest()}
            isDisabled={isLoading || !getValue(RestProperties.Url)}
          >
            {isLoading ? i18n.restService.testing : i18n.restService.testRequest}
          </Button>
        </ActionGroup>
      </FormSection>

      <FormSection style={{ "--pf-v5-c-form__section--Gap": "1rem" } as React.CSSProperties}>
        {testError && (
          <Alert variant="danger" isInline title={i18n.restService.testFailed}>
            {testError}
          </Alert>
        )}

        {testResult && (
          <FormGroup label={i18n.restService.testResult} fieldId="rest-test-result">
            <Alert
              variant={testResult.status >= 200 && testResult.status < 300 ? "success" : "warning"}
              isInline
              title={`Status: ${testResult.status}`}
            />
          </FormGroup>
        )}
      </FormSection>

      <BidirectionalDataMappingFormSection element={task} />

      <OnEntryAndExitScriptsFormSection element={task} />
    </>
  );
};
