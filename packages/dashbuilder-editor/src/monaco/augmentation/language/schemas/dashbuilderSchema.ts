/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

export const DASHBUILDER_SCHEMA = {
  $id: "https://dashbuilder.org/schemas/0.1/dashbuilder.json",
  $schema: "http://json-schema.org/draft-07/schema#",
  description: "Schema for Dashboards",
  type: "object",
  properties: {
    pages: {
      description: "A page can contain rows, columns and components.",
      type: "array",
      items: {
        $ref: "#/definitions/Page",
      },
    },
    datasets: {
      type: "array",
      description: "Datasets are used to declare source data",
      items: {
        $ref: "#/definitions/Dataset",
      },
    },
    navTree: {
      description: "The pages can be organized in a menu using navigation",
      $ref: "#/definitions/NavTree",
    },
    properties: {
      description: "Allows customization in certain parts of the document.",
      $ref: "#/definitions/CustomProperties",
    },
    global: {
      description: "Allows customization in global level.",
      $ref: "#/definitions/GlobalProperties",
    },
  },
  required: ["pages"],
  definitions: {
    GlobalProperties: {
      type: "object",
      properties: {
        mode: {
          $ref: "#/definitions/ColourModes",
        },
        settings: {
          $ref: "#/definitions/DisplayerSettings",
        },
      },
      title: "Global Properties",
    },
    Page: {
      type: "object",
      properties: {
        name: {
          type: "string",
        },
        components: {
          type: "array",
          items: {
            $ref: "#/definitions/PageComponent",
          },
        },
        rows: {
          type: "array",
          items: {
            $ref: "#/definitions/Row",
          },
        },
        properties: {
          $ref: "#/definitions/CustomProperties",
        },
      },
      title: "Page",
    },
    PageComponent: {
      type: "object",
      properties: {
        html: {
          type: "string",
        },
        settings: {
          $ref: "#/definitions/DisplayerSettings",
        },
      },
      title: "PageComponent",
    },
    Row: {
      type: "object",
      properties: {
        columns: {
          type: "array",
          items: {
            $ref: "#/definitions/RowColumn",
          },
        },
        properties: {
          $ref: "#/definitions/CustomProperties",
        },
      },
      required: ["columns"],
      title: "Row",
    },
    RowColumn: {
      type: "object",
      properties: {
        span: {
          type: "integer",
        },
        components: {
          type: "array",
          items: {
            $ref: "#/definitions/ColumnComponent",
          },
        },
        rows: {
          type: "array",
          items: {
            $ref: "#/definitions/Row",
          },
        },
        properties: {
          $ref: "#/definitions/CustomProperties",
        },
      },
      required: ["components"],
      title: "RowColumn",
    },
    ColumnComponent: {
      type: "object",
      properties: {
        html: {
          type: "string",
        },
        type: {
          $ref: "#/definitions/NavComponentTypes",
        },
        settings: {
          $ref: "#/definitions/DisplayerSettings",
        },
      },
      title: "ColumnComponent",
    },
    Dataset: {
      type: "object",
      properties: {
        uuid: {
          type: "string",
        },
        url: {
          type: "string",
          format: "uri",
        },
        content: {
          type: "string",
        },
        columns: {
          type: "array",
          items: {
            $ref: "#/definitions/DatasetColumn",
          },
        },
        expression: {
          type: "string",
          minLength: 1,
        },
        cacheEnabled: {
          type: "boolean",
        },
        refreshTime: {
          type: "string",
        },
      },
      oneOf: [
        {
          properties: {
            content: {
              type: "string",
            },
            url: {
              type: "string",
              format: "uri",
            },
          },
          required: ["content"],
          not: {
            required: ["url"],
          },
        },
        {
          properties: {
            content: {
              type: "string",
            },
            url: {
              type: "string",
              format: "uri",
            },
          },
          required: ["url"],
          not: {
            required: ["content"],
          },
        },
      ],
      required: ["uuid"],
      title: "Dataset",
    },
    DatasetColumn: {
      type: "object",
      properties: {
        id: {
          type: "string",
        },
        type: {
          $ref: "#/definitions/DataSetType",
        },
      },
      required: ["id", "type"],
      title: "DatasetColumn",
    },
    DataSetLookup: {
      type: "object",
      properties: {
        uuid: {
          type: "string",
        },
        rowCount: {
          type: "integer",
        },
        rowOffset: {
          type: "integer",
        },
        sort: {
          type: "array",
          items: {
            $ref: "#/definitions/Sort",
          },
        },
        filter: {
          type: "array",
          items: {
            $ref: "#/definitions/FilterComponent",
          },
        },
        group: {
          type: "array",
          items: {
            $ref: "#/definitions/DatasetlookupGroup",
          },
        },
      },
      required: ["uuid"],
      title: "DataSetLookup",
    },
    SettingsFilter: {
      type: "object",
      properties: {
        enabled: {
          type: "boolean",
        },
        listening: {
          type: "boolean",
        },
        notification: {
          type: "boolean",
        },
        selfapply: {
          type: "boolean",
        },
      },
      required: ["enabled", "listening", "notification", "selfapply"],
      title: "SettingsFilter",
    },
    SettingsColumn: {
      type: "object",
      properties: {
        id: {
          type: "string",
        },
        name: {
          type: "string",
        },
        expression: {
          type: "string",
        },
        pattern: {
          type: "string",
        },
        required: ["id", "name", "expression", "pattern"],
      },
      title: "SettingsColumn",
    },
    SettingsTable: {
      type: "object",
      properties: {
        pageSize: {
          type: "integer",
        },
        show_column_picker: {
          type: "string",
        },
        sort: {
          $ref: "#/definitions/TableSort",
        },
      },
      required: ["pageSize", "show_column_picker", "sort"],
      title: "SettingsTable",
    },
    TableSort: {
      type: "object",
      properties: {
        enabled: {
          type: "boolean",
        },
        columnId: {
          type: "string",
        },
        order: {
          $ref: "#/definitions/sortEnum",
        },
      },
      required: ["enabled", "columnId", "order"],
      title: "TableSort",
    },
    Sort: {
      type: "object",
      properties: {
        column: {
          type: "string",
        },
        sortOrder: {
          $ref: "#/definitions/sortEnum",
        },
      },
      required: ["column", "sortOrder"],
      title: "Sort",
    },
    sortEnum: {
      type: "string",
      enum: ["ASCENDING", "DESCENDING"],
      additionalProperties: false,
      title: "sortEnum",
    },
    FilterComponent: {
      type: "object",
      properties: {
        column: {
          type: "string",
        },
        function: {
          $ref: "#/definitions/FunctionList",
        },
        args: {
          type: "array",
        },
      },
      required: ["column", "function", "args"],
      allOf: [
        {
          if: {
            properties: {
              function: {
                const: "OR",
              },
            },
          },
          then: {
            properties: {
              args: {
                type: "array",
                items: {
                  $ref: "#/definitions/FilterComponent",
                },
              },
            },
          },
        },
        {
          if: {
            properties: {
              function: {
                const: "AND",
              },
            },
          },
          then: {
            properties: {
              args: {
                type: "array",
                items: {
                  $ref: "#/definitions/FilterComponent",
                },
              },
            },
          },
        },
        {
          if: {
            properties: {
              function: {
                const: "NOT",
              },
            },
          },
          then: {
            properties: {
              args: {
                type: "array",
                items: {
                  $ref: "#/definitions/FilterComponent",
                },
              },
            },
          },
        },
      ],
      title: "FilterComponent",
    },
    DatasetlookupGroup: {
      type: "object",
      properties: {
        columnGroup: {
          type: "object",
          properties: {
            source: {
              type: "string",
            },
          },
          required: ["source"],
        },
        groupFunctions: {
          type: "array",
          items: {
            $ref: "#/definitions/ColumnGroupFunctions",
          },
        },
      },
      title: "DatasetlookupGroup",
    },
    ColumnGroupFunctions: {
      type: "object",
      properties: {
        source: {
          type: "string",
        },
        function: {
          type: "string",
          enum: ["SUM", "MAX", "MIN", "AVERAGE"],
          additionalProperties: false,
        },
        column: {
          type: "string",
        },
      },
      required: ["source"],
      title: "ColumnGroupFunctions",
    },
    NavTree: {
      type: "object",
      properties: {
        root_items: {
          type: "array",
          items: {
            $ref: "#/definitions/RootItem",
          },
        },
      },
      required: ["root_items"],
      title: "NavTree",
    },
    RootItem: {
      type: "object",
      properties: {
        type: {
          type: "string",
        },
        id: {
          type: "string",
        },
        name: {
          type: "string",
        },
        children: {
          type: "array",
          items: {
            $ref: "#/definitions/Child",
          },
        },
      },
      title: "RootItem",
    },
    Child: {
      type: "object",
      properties: {
        page: {
          type: "string",
        },
      },
      required: ["page"],
      title: "Child",
    },
    DataSetType: {
      type: "string",
      enum: ["LABEL", "NUMBER", "TEXT", "DATE"],
      title: "Type",
    },
    FunctionList: {
      type: "string",
      enum: [
        "IS_NULL",
        "NOT_NULL",
        "EQUALS_TO",
        "NOT_EQUALS_TO",
        "LIKE_TO",
        "GREATER_THAN",
        "GREATER_OR_EQUALS_TO",
        "LOWER_THAN",
        "LOWER_OR_EQUALS_TO",
        "BETWEEN",
        "TIME_FRAME",
        "IN",
        "NOT_IN",
        "OR",
        "AND",
        "NOT",
      ],
      title: "FunctionList",
    },
    ChartProperties: {
      type: "object",
      properties: {
        bgColor: {
          type: "string",
        },
        width: {
          type: "number",
        },
        height: {
          type: "number",
        },
        zoom: {
          type: "boolean",
        },
        margin: {
          type: "object",
          properties: {
            right: {
              type: "number",
            },
            top: {
              type: "number",
            },
            bottom: {
              type: "number",
            },
            left: {
              type: "number",
            },
          },
        },
        resizable: {
          type: "boolean",
        },
        legend: {
          type: "object",
          properties: {
            show: {
              type: "boolean",
            },
            position: {
              type: "string",
              enum: ["in", "right", "bottom"],
            },
          },
        },
        grid: {
          type: "object",
          properties: {
            x: {
              type: "boolean",
            },
            y: {
              type: "boolean",
            },
          },
        },
        general: {
          type: "object",
          properties: {
            visible: {
              type: "boolean",
            },
            title: {
              type: "string",
            },
          },
        },
      },
    },
    CustomProperties: {
      type: "object",
      description: "The properties can be CSS properties, such as width/height, background color, color and more.",
      additionalProperties: {
        type: "string",
      },
    },
    DisplayerSettings: {
      type: "object",
      properties: {
        lookup: {
          $ref: "#/definitions/DataSetLookup",
        },
        filter: {
          $ref: "#/definitions/SettingsFilter",
        },
        refresh: {
          type: "object",
          properties: {
            interval: {
              type: "string",
            },
          },
          required: ["interval"],
          title: "refresh",
        },
        renderer: {
          type: "string",
          enum: ["c3", "echarts"],
          additionalProperties: false,
          title: "renderer",
        },
        selector: {
          type: "object",
          properties: {
            multiple: {
              type: "boolean",
            },
            inputs_show: {
              type: "boolean",
            },
          },
          required: ["multiple", "inputs_show"],
          title: "selector",
        },
        echarts: {
          type: "string",
          title: "echarts",
        },
        general: {
          type: "object",
          properties: {
            title: {
              type: "string",
            },
            visible: {
              type: "boolean",
            },
            mode: {
              $ref: "#/definitions/ColourModes",
            },
            allowEdit: {
              type: "boolean",
              title: "allowEdit",
            },
          },
          required: ["title"],
          title: "general",
        },
        export: {
          type: "object",
          properties: {
            png: {
              type: "boolean",
            },
          },
          required: ["png"],
          title: "export",
        },
        columns: {
          type: "array",
          items: {
            $ref: "#/definitions/SettingsColumn",
          },
        },
        table: {
          $ref: "#/definitions/SettingsTable",
        },
        html: {},
        javascript: {
          type: "string",
        },
        map: {
          type: "object",
          properties: {
            color_scheme: {
              $ref: "#/definitions/MapColorScheme",
            },
          },
          required: ["color_scheme"],
          title: "map",
        },
        meter: {
          $ref: "#/definitions/MeterTypes",
        },
        component: {
          oneOf: [
            {
              type: "string",
            },
            {
              $ref: "#/definitions/SettingsComponent",
            },
          ],
        },
        external: {
          $ref: "#/definitions/SettingsExternal",
        },
        bubble: {
          type: "object",
          properties: {
            minSize: {
              type: "number",
            },
            maxSize: {
              type: "number",
            },
            color: {
              type: "string",
            },
          },
          required: ["minSize", "maxSize", "color"],
          title: "bubble",
        },
        axis: {
          type: "object",
          properties: {
            x: {
              type: "object",
              properties: {
                labels_show: {
                  type: "boolean",
                },
                title: {
                  type: "string",
                },
                labels_angle: {
                  type: "number",
                },
              },
              required: ["title"],
              title: "x",
            },
            y: {
              type: "object",
              properties: {
                labels_show: {
                  type: "boolean",
                },
                title: {
                  type: "string",
                },
                labels_angle: {
                  type: "string",
                },
              },
              required: ["title"],
              title: "y",
            },
          },
        },
        type: {
          $ref: "#/definitions/ChartType",
        },
        chart: {
          $ref: "#/definitions/ChartProperties",
        },
      },
      allOf: [
        {
          if: {
            properties: {
              type: {
                const: "BARCHART",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/BarChartTypes",
              },
            },
          },
        },
        {
          if: {
            properties: {
              type: {
                const: "LINECHART",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/LineChartTypes",
              },
            },
          },
        },
        {
          if: {
            properties: {
              type: {
                const: "AREACHART",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/AreaChartTypes",
              },
            },
          },
        },
        {
          if: {
            properties: {
              type: {
                const: "PIECHART",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/PieChartTypes",
              },
            },
          },
        },
        {
          if: {
            properties: {
              type: {
                const: "SELECTOR",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/SelectorTypes",
              },
            },
          },
        },
        {
          if: {
            properties: {
              type: {
                const: "MAP",
              },
            },
          },
          then: {
            properties: {
              subtype: {
                $ref: "#/definitions/MapTypes",
              },
            },
          },
        },
      ],
      required: ["lookup"],
      title: "DisplayerSettings",
    },
    SettingsExternal: {
      type: "object",
      properties: {
        baseUrl: {
          type: "string",
        },
        width: {},
        height: {},
      },
      title: "SettingsExternal",
    },
    ChartType: {
      type: "string",
      enum: [
        "BARCHART",
        "LINECHART",
        "AREACHART",
        "PIECHART",
        "BUBBLECHART",
        "SCATTERCHART",
        "SELECTOR",
        "METRIC",
        "METERCHART",
        "MAP",
      ],
      additionalProperties: false,
      title: "ChartType",
    },
    BarChartTypes: {
      type: "string",
      enum: ["COLUMN", "BAR", "STACKED", "COLUMN_STACKED", "BAR_STACKED"],
      default: "COLUMN",
      additionalProperties: false,
      title: "BarChartTypes",
    },
    LineChartTypes: {
      type: "string",
      enum: ["LINE", "SMOOTH"],
      default: "LINE",
      additionalProperties: false,
      title: "LineChartTypes",
    },
    AreaChartTypes: {
      type: "string",
      enum: ["AREA", "AREA_STACKED"],
      default: "AREA",
      additionalProperties: false,
      title: "AreaChartChartTypes",
    },
    PieChartTypes: {
      type: "string",
      enum: ["PIE", "DONUT"],
      default: "PIE",
      additionalProperties: false,
      title: "PieChartChartTypes",
    },
    SelectorTypes: {
      type: "string",
      enum: ["SELECTOR_LABELS", "SELECTOR_DROPDOWN", "SELECTOR_SLIDER"],
      additionalProperties: false,
      title: "SelectorTypes",
    },
    MeterTypes: {
      type: "object",
      properties: {
        start: {
          type: "string",
        },
        end: {
          type: "string",
        },
        critical: {
          type: "string",
        },
        warning: {
          type: "string",
        },
      },
    },
    MapColorScheme: {
      type: "string",
      enum: ["red", "green", "blue"],
    },
    MapTypes: {
      type: "string",
      enum: ["MAP_MARKERS", "MAP_REGIONS"],
      additionalProperties: false,
    },
    NavComponentTypes: {
      type: "string",
      enum: ["TILES", "CAROUSEL", "TREE", "MENU", "TABS", "DIV"],
      additionalProperties: false,
    },
    SettingsComponent: {
      type: "string",
      enum: ["table", "echarts", "svg-heatmap", "timeseries", "uniforms"],
    },
    ColourModes: {
      type: "string",
      enum: ["dark", "light"],
      title: "ColourModes",
      additionalProperties: false,
    },
  },
};
