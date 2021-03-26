/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import {
  Chart,
  validateNonTransposedDataset,
  validateTransposedDataset,
  NOT_ENOUGH_COLUMNS_MSG_TRANSPOSED,
  NOT_ENOUGH_COLUMNS_MSG_NON_TRANSPOSED,
  SECOND_COLUMN_INVALID_MSG_TRANSPOSED,
  THIRD_COLUMN_INVALID_MSG_TRANSPOSED,
  CHARTNAME_VALIDATION,
  getSeriesforNonTransposedDataset,
  getSeriesforTransposedDataset,
  getOptions,
  validateChartName
} from "../Chart";
import { ColumnType, ComponentApi } from "@dashbuilder-js/component-api";
import { shallow } from "enzyme";
import { configure } from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import "jest-enzyme";

configure({ adapter: new Adapter() });

const api = new ComponentApi();
const settings = { columnId: "", columnName: "", valueExpression: "", emptyTemplate: "" };

it("should test Chart component", () => {
  const wrapper = shallow(<Chart controller={api.getComponentController()} />);
  expect(wrapper).toMatchSnapshot();
});

describe("should test validateTransposedDataset", () => {
  it("should return not enough columns when columns are not enough", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series",
          type: ColumnType.LABEL,
          settings
        }
      ],
      data: [[]]
    };
    const result = validateTransposedDataset(ds);
    expect(result).toBe(NOT_ENOUGH_COLUMNS_MSG_TRANSPOSED);
  });

  it("should test type of second column", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series",
          type: ColumnType.NUMBER,
          settings
        },
        {
          name: "series",
          type: ColumnType.NUMBER,
          settings
        }
      ],
      data: [[]]
    };
    const result = validateTransposedDataset(ds);
    expect(result).toBe(SECOND_COLUMN_INVALID_MSG_TRANSPOSED);
  });

  it("should test type of third column", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series",
          type: ColumnType.TEXT,
          settings
        }
      ],
      data: [[]]
    };
    const result = validateTransposedDataset(ds);
    expect(result).toBe(THIRD_COLUMN_INVALID_MSG_TRANSPOSED);
  });
});

describe("should test validateNonTransposedDataset", () => {
  it("should test number of columns of validateNonTransposedDataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        }
      ],
      data: [[]]
    };
    const result = validateNonTransposedDataset(ds);
    expect(result).toBe(NOT_ENOUGH_COLUMNS_MSG_NON_TRANSPOSED);
  });

  it("should test type of second column of validateNonTransposedDataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series-1",
          type: ColumnType.TEXT,
          settings
        }
      ],
      data: [[]]
    };
    const result = validateNonTransposedDataset(ds);
    expect(result).toBe("Wrong type for column 2, it should be NUMBER");
  });
});

describe("should test series and options for Transposed dataset", () => {
  it("should test getSeriesforTransposedDataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.LABEL,
          settings
        },
        {
          name: "series",
          type: ColumnType.LABEL,
          settings
        },
        {
          name: "values",
          type: ColumnType.NUMBER,
          settings
        }
      ],
      data: [
        ["2000", "series-1", "2"],
        ["2000", "series-2", "78"],
        ["2000", "series-3", "200"],
        ["2001", "series-1", "89"],
        ["2001", "series-2", "23"],
        ["2001", "series-3", "110"],
        ["2002", "series-1", "167"],
        ["2002", "series-2", "110"],
        ["2002", "series-3", "11"]
      ]
    };
    const expectedResult = [
      {
        name: "series-1",
        data: [2, 89, 167]
      },
      {
        name: "series-2",
        data: [78, 23, 110]
      },
      {
        name: "series-3",
        data: [200, 110, 11]
      }
    ];
    const result = getSeriesforTransposedDataset(ds);
    expect(result).toStrictEqual(expectedResult);
  });

  it("should test getOptions for transposed dataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.LABEL,
          settings
        },
        {
          name: "series",
          type: ColumnType.LABEL,
          settings
        },
        {
          name: "values",
          type: ColumnType.NUMBER,
          settings
        }
      ],
      data: [
        ["2000", "series-1", "2"],
        ["2000", "series-2", "78"],
        ["2000", "series-3", "200"],
        ["2001", "series-1", "89"],
        ["2001", "series-2", "23"],
        ["2001", "series-3", "110"],
        ["2002", "series-1", "167"],
        ["2002", "series-2", "110"],
        ["2002", "series-3", "11"]
      ]
    };
    const expectedResult = {
      chart: {
        id: "new",
        zoom: {
          type: "x",
          enabled: false,
          autoScaleYaxis: false
        },
        toolbar: {
          show: false,
          autoSelected: "zoom"
        }
      },
      title: {
        text: "new",
        align: "left"
      },
      xaxis: {
        type: "datetime",
        categories: ["2000", "2001", "2002"]
      },
      dataLabels: { enabled: false }
    };
    const result = getOptions(ds, true, "new", "x", false, false, "new", "left", false, "zoom", "datetime");
    expect(result).toStrictEqual(expectedResult);
  });
});

describe("should test series and options for NonTransposed dataset", () => {
  it("should test getSeriesforNonTransposedDataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series-1",
          type: ColumnType.TEXT,
          settings
        }
      ],
      data: [
        ["2000", "2"],
        ["2001", "78"],
        ["2002", "200"]
      ]
    };
    const expectedResult = [
      {
        name: "series-1",
        data: [2, 78, 200]
      }
    ];
    const result = getSeriesforNonTransposedDataset(ds);
    expect(result).toStrictEqual(expectedResult);
  });

  it("should test getOptions for non-transposed dataset", () => {
    const ds = {
      columns: [
        {
          name: "category",
          type: ColumnType.TEXT,
          settings
        },
        {
          name: "series-1",
          type: ColumnType.TEXT,
          settings
        }
      ],
      data: [
        ["2000", "2"],
        ["2001", "78"],
        ["2002", "200"]
      ]
    };
    const expectedResult = {
      chart: {
        id: "new",
        zoom: {
          type: "x",
          enabled: false,
          autoScaleYaxis: false
        },
        toolbar: {
          show: false,
          autoSelected: "zoom"
        }
      },
      title: {
        text: "new",
        align: "left"
      },
      xaxis: {
        type: "category",
        categories: ["2000", "2001", "2002"]
      },
      dataLabels: { enabled: false }
    };
    const result = getOptions(ds, false, "new", "x", false, false, "new", "left", false, "zoom", "category");
    expect(result).toStrictEqual(expectedResult);
  });
});

describe("should test validateChartName", () => {
  it("should test special characters", () => {
    const result = validateChartName("foo+bar");
    expect(result).toBe(CHARTNAME_VALIDATION);
  });
  it("should test spaces", () => {
    const result = validateChartName("foo bar");
    expect(result).toBe(CHARTNAME_VALIDATION);
  });
  it("should test string without special characters", () => {
    const result = validateChartName("foobar");
    expect(result).toBe("");
  });
});
