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

export interface Options {
  chart: {
    id: string;
    zoom: {
      type: string;
      enabled: boolean;
      autoScaleYaxis: boolean;
    };
    toolbar: {
      show: boolean;
      autoSelected: string;
    };
  };
  title: {
    text: string;
    align: string;
  };
  xaxis: {
    type: string;
    categories: Array<string | number>;
  };
  dataLabels: {
    enabled: boolean;
  };
}

export interface SingleSeries {
  name: string;
  data: Array<string | number>;
}
