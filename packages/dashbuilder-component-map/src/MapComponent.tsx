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

import { ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import React, { useCallback, useEffect, useState } from "react";
import { MapMarker, MarkType, Props as MapProps, SimpleReactMap } from "./Map";

const geoUrl = "world.geo.json";

interface Props {
  controller: ComponentController;
}

export const MapComponent = (props: Props) => {
  const [mapProps, setMapProps] = useState<MapProps>({});

  const onDataSet = useCallback(
    (ds: DataSet, params: Map<string, any>) => {
      const columns = ds.columns;
      const type = params.get("type");
      let markType: MarkType | undefined;

      if (type) {
        markType = "bubble" === type.toLowerCase() ? MarkType.BUBBLE : MarkType.FILL;
      }

      props.controller.configurationOk();

      // name, value
      if (columns.length == 2 && columns[0].type === "LABEL" && columns[1].type === "NUMBER") {
        // build
        setMapProps((previous) => ({
          ...previous,
          markers: ds.data.map((line) => {
            return {
              name: line[0],
              value: +line[1],
            };
          }),
          markType: markType || MarkType.FILL,
        }));
      }
      // lat, long, value
      else if (
        columns.length === 3 &&
        columns[0].type === "NUMBER" &&
        columns[1].type === "NUMBER" &&
        columns[2].type === "NUMBER"
      ) {
        setMapProps((previous) => ({
          ...previous,
          markers: ds.data.map((line) => {
            return {
              lat: +line[0],
              long: +line[1],
              value: +line[2],
            };
          }),
          markType: markType || MarkType.FILL,
        }));
      } else {
        props.controller.requireConfigurationFix(
          "You need to provide at least 2 columns containing the geo name or id (LABEL) and a value (NUMBER) or 3-4 columns containing latitude (NUMBER), longitude(NUMBER), the value(NUMBER) and an optional name (LABEL)"
        );
      }
    },
    [props.controller]
  );
  const onInit = useCallback((params: Map<string, any>) => {
    setMapProps((previous) => ({
      ...previous,
      // map
      projection: params.get("projection"),
      background: params.get("background"),

      // Zoom related properties
      enableZoom: params.get("enableZoom") === "true",
      zoom: +(params.get("zoom") || 1),
      maxZoom: +params.get("maxZoom"),
      minZoom: +params.get("minZoom"),
      zoomCenterLong: +(params.get("zoomCenterLong") || 0),
      zoomCenterLat: +(params.get("zoomCenterLat") || 0),

      // style
      fill: params.get("fill"),
      stroke: params.get("stroke"),
      strokeWidth: +params.get("strokeWidth"),
      hover: params.get("hover"),
      hoverStrokeWidth: +params.get("hoverStrokeWidth"),
      hoverStrokeFill: params.get("hoverStrokeFill"),

      // custom Geo
      geoUrl: params.get("geoUrl"),

      // bubble specific configuration
      bubbleFill: params.get("bubbleFill"),
      bubbleStroke: params.get("bubbleStroke"),
      bubbleStrokeWidth: +params.get("bubbleStrokeWidth"),
      bubbleOpacity: +params.get("bubbleOpacity"),
      maxBubbleSize: +params.get("maxBubbleSize"),
      minBubbleSize: +params.get("minBubbleSize"),

      // fill map specific params
      fillBegin: params.get("fillBegin"),
      fillEnd: params.get("fillEnd"),
      geoKey: params.get("geoKey"),
    }));
  }, []);

  useEffect(() => {
    props.controller.setOnInit(onInit);
    props.controller.setOnDataSet(onDataSet);
  }, [props.controller, onDataSet, onInit]);

  return <SimpleReactMap {...mapProps} />;
};
