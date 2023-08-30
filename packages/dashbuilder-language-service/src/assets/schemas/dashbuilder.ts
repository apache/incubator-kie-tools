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
      description: "Datasets are used to declare source of data",
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
          description: "The dashboard style",
          $ref: "#/definitions/ColourModes",
        },
        allowUrlProperties: {
          type: ["boolean"],
        },
        displayer: {
          description: "A global configuration for all displayers. It does not support lookup.",
          $ref: "#/definitions/DisplayerSettings",
        },
        dataset: {
          description: "A global dataset definition. It is not recommended to set a global uuid.",
          $ref: "#/definitions/Dataset",
        },
      },
      title: "Global Properties",
    },
    Page: {
      type: "object",
      properties: {
        name: {
          description: "The page name. If not set then a name is generated.",
          type: "string",
        },
        components: {
          type: "array",
          description:
            "A list of components for this page. If rows are not used then the components are organized in a list of rows with single column each.",
          items: {
            $ref: "#/definitions/PageComponent",
          },
        },
        rows: {
          type: "array",
          description: "List of pages rows. It should be used only if components is not used",
          items: {
            $ref: "#/definitions/Row",
          },
        },
        properties: {
          description: "CSS properties for the whole page",
          $ref: "#/definitions/CustomProperties",
        },
      },
      title: "Page",
    },
    PageComponent: {
      type: "object",
      description: "A page component that can be dynamic (dataset information) or static (static content)",
      properties: {
        html: {
          description: "A component that renders HTML content.",
          type: "string",
        },
        markdown: {
          description: "A component that renders markdown content.",
          type: "string",
        },
        panel: {
          description: "A panel to embed other pages. The panel is collapsible and the title is the page name.",
          type: "string",
        },
        screen: {
          description: "A component that embed other page",
          type: "string",
        },
        displayer: {
          description: "A page block to show a part of a dataset",
          $ref: "#/definitions/DisplayerSettings",
        },
      },
      title: "PageComponent",
    },
    Row: {
      type: "object",
      description: "A page row",
      properties: {
        columns: {
          type: "array",
          description: "List of columns for this row",
          items: {
            $ref: "#/definitions/RowColumn",
          },
        },
        properties: {
          description: "CSS properties applied for this row.",
          $ref: "#/definitions/CustomProperties",
        },
      },
      required: ["columns"],
      title: "Row",
    },
    RowColumn: {
      type: "object",
      description: "A column which lives in a row.",
      properties: {
        span: {
          description:
            "The amount of spaces occupied by this column. The max value is 12 and the sum of all columns in a rfow must be 12.",
          type: ["integer"],
        },
        components: {
          type: "array",
          description: "A list of components for this column",
          items: {
            $ref: "#/definitions/ColumnComponent",
          },
        },
        rows: {
          type: "array",
          description: "A row for the column, used for more complex layouts. Use it or components",
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
      $ref: "#/definitions/PageComponent",
    },
    Dataset: {
      type: "object",
      properties: {
        uuid: {
          description: "An unique identifier for the dataset",
          type: "string",
        },
        url: {
          description: "The dataset JSON content URL",
          type: "string",
          format: "uri",
        },
        type: {
          description: "The dataset type",
          $ref: "#/definitions/typeEnum",
        },
        content: {
          description: "Local JSON Array used as the dataset content",
          type: "string",
        },
        columns: {
          type: "array",
          description: "A list of  columns for the dataset",
          items: {
            $ref: "#/definitions/DatasetColumn",
          },
        },
        expression: {
          description: "A JSONAta expression to handle the dataset content and parse to a JSON 2d Array",
          type: "string",
          minLength: 1,
        },
        cacheEnabled: {
          description: "Enables cache for the dataset. If no refreshTime is provided, then it the cache won't expire.",
          type: ["boolean"],
        },
        refreshTime: {
          description: "The cache expiration time. Its syntax is {number}{unit}, example: 10minute, 10second, 10hour",
          type: "string",
        },
        maxCacheRows: {
          description: "The max of rows cached by the dataset or with accumulated datasets. Default is 1000.",
          type: "integer",
        },
        path: {
          description: "Additional path added to the dataset URL",
          type: "string",
        },
        method: {
          description: "HTTP Method used by the the dataset request. By default it is GET",
          type: "string",
        },
        accumulate: {
          description: "If true then previous calls to the dataset are kept in memory and not discarded.",
          type: "boolean",
        },
        headers: {
          description: "HTTP headers sent with the request.",
          $ref: "#/definitions/DatasetHeaders",
        },
        query: {
          description: "Query parameters added to the dataset URL.",
          $ref: "#/definitions/DatasetQuery",
        },
        form: {
          description: "Form parameters sent when the HTTP method is POST.",
          $ref: "#/definitions/DatasetForm",
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
            join: {
              type: "array",
            },
          },
          required: ["content"],
          not: {
            required: ["url", "join"],
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
            join: {
              type: "array",
            },
          },
          required: ["url"],
          not: {
            required: ["content", "join"],
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
            join: {
              type: "array",
            },
          },
          required: ["join"],
          not: {
            required: ["content", "url"],
          },
        },
      ],
      required: ["uuid"],
      title: "Dataset",
    },
    typeEnum: {
      type: "string",
      description: "For datasets that are reading data from Prometheus query response",
      enum: ["prometheus"],
      additionalProperties: false,
      title: "typeEnum",
    },
    DatasetColumn: {
      type: "object",
      properties: {
        id: {
          description: "An unique ID for the column",
          type: "string",
        },
        type: {
          $ref: "#/definitions/DataSetType",
        },
      },
      required: ["id"],
      title: "DatasetColumn",
    },
    DataSetLookup: {
      type: "object",
      description: "Selects a dataset part to be displayed by this component",
      properties: {
        uuid: {
          description: "The dataset uuid.",
          type: "string",
        },
        rowCount: {
          description: "The amount of rows to be returned.",
          type: "integer",
        },
        rowOffset: {
          description: "Rows offset",
          type: "integer",
        },
        sort: {
          type: "array",
          description: "A list of sort operation.",
          items: {
            $ref: "#/definitions/Sort",
          },
        },
        filter: {
          type: "array",
          description: "A list of filter operation.",
          items: {
            $ref: "#/definitions/FilterComponent",
          },
        },
        group: {
          type: "array",
          description: "List of group operations",
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
          type: ["boolean"],
        },
        listening: {
          description: "Enable this so this component can be filtered by others",
          type: ["boolean"],
        },
        notification: {
          description: "Enable this so this component can filter others",
          type: ["boolean"],
        },
        selfapply: {
          description: "Enable this so this component can self apply the filter",
          type: ["boolean"],
        },
      },
      title: "SettingsFilter",
    },
    SettingsColumn: {
      description: "Settings for the columns returned by the dataset lookup.",
      type: "object",
      properties: {
        id: {
          description: "The column identifier.",
          type: "string",
        },
        name: {
          description: "An optional new name for the column.",
          type: "string",
        },
        expression: {
          description: "A javascript expression to transform the column. You can use the variable value.",
          type: "string",
        },
        pattern: {
          description: "A pattern for number columns.",
          type: "string",
        },
      },
      required: ["id"],
      title: "SettingsColumn",
    },
    SettingsHTML: {
      type: "object",
      description: "HTML template for METRICS component",
      properties: {
        html: {
          description: "The HTML template. ",
          type: "string",
        },
        javascript: {
          description: "Javascript for the template",
          type: "string",
        },
      },
      title: "SettingsHTML",
    },
    SettingsTable: {
      type: "object",
      properties: {
        pageSize: {
          description: "The size of a table page.",
          type: "integer",
        },
        show_column_picker: {
          description: "The column picker visible when true",
          type: "boolean",
        },
        sort: {
          $ref: "#/definitions/TableSort",
        },
      },
      title: "SettingsTable",
    },
    TableSort: {
      type: "object",
      description: "Table sort configuration.",
      properties: {
        enabled: {
          type: ["boolean"],
        },
        columnId: {
          type: "string",
        },
        order: {
          $ref: "#/definitions/sortEnum",
        },
      },
      required: ["enabled"],
      title: "TableSort",
    },
    Sort: {
      type: "object",
      properties: {
        column: {
          type: "string",
        },
        order: {
          $ref: "#/definitions/sortEnum",
        },
      },
      required: ["column", "order"],
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
      description: "A Filter operation on the dataset lookup.",
      properties: {
        column: {
          description: "The column to be filtered.",
          type: "string",
        },
        function: {
          description: "The filter function.",
          $ref: "#/definitions/FunctionList",
        },
        args: {
          description: "A list of arguments to be used with the filter function",
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
          description: "A group operation for the filter.",
          type: "object",
          properties: {
            source: {
              description: "The column which will be used for the group",
              type: "string",
            },
          },
          required: ["source"],
        },
        functions: {
          type: "array",
          description: "A list of columns returned by this group operation.",
          items: {
            $ref: "#/definitions/ColumnGroupFunctions",
          },
        },
      },
      title: "DatasetlookupGroup",
    },
    ColumnGroupFunctions: {
      type: "object",
      description: "A function used in the group",
      properties: {
        source: {
          description: "The column which the group will be applied",
          type: "string",
        },
        function: {
          type: "string",
          enum: ["SUM", "MAX", "MIN", "AVERAGE", "COUNT", "MEDIAN", "JOIN", "JOIN_COMMA", "JOIN_HYPHEN"],
          additionalProperties: false,
        },
        column: {
          description: "A name for the result column",
          type: "string",
        },
      },
      required: ["source"],
      title: "ColumnGroupFunctions",
    },
    NavTree: {
      type: "object",
      description: "The pages navigation configuration.",
      properties: {
        root_items: {
          description: "List of the navigation groups/items",
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
      description: "A navigation item. Can be a group of pages.",
      properties: {
        type: {
          description: "The item type.",
          $ref: "#/definitions/NavItemEnum",
        },
        id: {
          type: "string",
        },
        name: {
          type: "string",
        },
        children: {
          type: "array",
          description: "The list of child items. Can be other navigation groups or pages",
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
      title: "Child",
    },
    NavItemEnum: {
      type: "string",
      enum: ["ITEM", "GROUP", "DIVIDER"],
      title: "Type",
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
          description: "Chart background color.",
          type: "string",
        },
        width: {
          description: "The chart fixed with. Does not have effect if resizable is true",
          type: ["number"],
        },
        height: {
          description: "The chart fixed height. Does not have effect if resizable is true",
          type: ["number"],
        },
        zoom: {
          description: "Enables zoom on the chart.",
          type: ["boolean"],
        },
        margin: {
          type: "object",
          description: "A margin for the chart",
          properties: {
            right: {
              type: ["number"],
            },
            top: {
              type: ["number"],
            },
            bottom: {
              type: ["number"],
            },
            left: {
              type: ["number"],
            },
          },
        },
        resizable: {
          description: "Makes the chart responsible",
          type: ["boolean"],
        },
        legend: {
          type: "object",
          description: "Configuration for the chart legend.",
          properties: {
            show: {
              description: "If true the legend is displayed. Default is false.",
              type: ["boolean"],
            },
            position: {
              type: "string",
              description: "The legend position.",
              enum: ["in", "right", "bottom"],
            },
          },
        },
        grid: {
          type: "object",
          description: "Show/hide the XY charts grid.",
          properties: {
            x: {
              type: ["boolean"],
            },
            y: {
              type: ["boolean"],
            },
          },
        },
      },
    },
    CustomProperties: {
      type: "object",
      description: "The properties can be CSS properties, such as width/height, background color, color and more.",
      additionalProperties: {},
    },
    DatasetHeaders: {
      type: "object",
      description: "Additional headers sent to a dataset HTTP Request",
      additionalProperties: {},
    },
    DatasetQuery: {
      type: "object",
      description: "Additional query parameter sent to a dataset HTTP Request",
      additionalProperties: {},
    },
    DatasetForm: {
      type: "object",
      description: "Form Parameters used in the request body when the HTTP method is POST",
      additionalProperties: {},
    },
    DisplayerSettings: {
      type: "object",
      properties: {
        lookup: {
          description: "Configures the source of data for this displayer",
          $ref: "#/definitions/DataSetLookup",
        },
        dataSet: {
          description: "A local dataset declaration which should be a string with a JSON array",
          type: "string",
        },
        filter: {
          $ref: "#/definitions/SettingsFilter",
        },
        refresh: {
          type: "object",
          description: "Configure an auto refresh feature for the displayer",
          properties: {
            interval: {
              description: "The value in seconds. Use -1 for no update.",
              type: "number",
            },
          },
          required: ["interval"],
          title: "refresh",
        },
        renderer: {
          type: "string",
          description: "The underlying library used by Dashbuilder.",
          enum: ["c3", "echarts"],
          additionalProperties: false,
          title: "renderer",
        },
        extraConfiguration: {
          type: "string",
          description:
            "An extra configuration for the chart. It depends on the underlying chart library, for example, you can provide a JSON object for the echarts renderer which will be merged with the default configuration",
        },
        selector: {
          type: "object",
          properties: {
            multiple: {
              type: ["boolean"],
            },
            inputs_show: {
              type: ["boolean"],
            },
          },
          required: ["multiple"],
          title: "selector",
        },
        general: {
          type: "object",
          properties: {
            visible: {
              description: "Enable/disable the title. Default is true.",
              type: ["boolean"],
            },
            title: {
              description: "A title for the chart.",
              type: "string",
            },
            subtitle: {
              description: "A subtitle for the chart.",
              type: "string",
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
          description: "Export configuration for the displayer",
          properties: {
            png: {
              description: "Enables PNG download",
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
        html: {
          $ref: "#/definitions/SettingsHTML",
        },
        map: {
          type: "object",
          properties: {
            color_scheme: {
              $ref: "#/definitions/MapColorScheme",
            },
          },
          title: "map",
        },
        meter: {
          $ref: "#/definitions/MeterTypes",
        },
        component: {
          anyOf: [
            {
              type: "string",
            },
            {
              $ref: "#/definitions/SettingsComponent",
            },
          ],
        },
        external: {
          description: "Specific configuration for the external component.",
          $ref: "#/definitions/SettingsExternal",
        },
        bubble: {
          type: "object",
          description: "Bubble chart specific configuration",
          properties: {
            minSize: {
              description: "The mininum size for the bubble",
              type: "number",
            },
            maxSize: {
              description: "The max size for the bubble",
              type: "number",
            },
            color: {
              description: "Bubble color",
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
              description: "Specific X axis configuration.",
              properties: {
                labels_show: {
                  description: "Shows or hide the axis X. Default is false.",
                  type: ["boolean"],
                },
                title: {
                  description: "A title for the axis X.",
                  type: "string",
                },
                labels_angle: {
                  description: "X Labels angle",
                  type: ["number"],
                },
              },
              title: "x",
            },
            y: {
              type: "object",
              properties: {
                labels_show: {
                  description: "Shows or hide the axis Y. Default is false.",
                  type: ["boolean"],
                },
                title: {
                  description: "A title for the axis Y.",
                  type: "string",
                },
                labels_angle: {
                  description: "Y Labels angle",
                  type: ["number"],
                },
              },
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
        "TABLE",
        "TIMESERIES",
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
