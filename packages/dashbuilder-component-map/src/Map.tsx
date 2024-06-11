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

import { ComponentController } from "@kie-tools/dashbuilder-component-api";
import React from "react";
import { ComposableMap, Geographies, Geography, Marker, ZoomableGroup } from "react-simple-maps";
import { scaleLinear } from "d3-scale";

const DEFAULT_GEO = "world.geo.json";
const DEFAULT_BACKGROUND = "lightblue";
const DEFAULT_STROKE = "darkgray";
const DEFAULT_FILL = "#F2D0A9";
const DEFAULT_DISABLED = "lightgray";
const DEFAULT_STROKE_WIDTH = 1.0;
const DEFAULT_PROJECTION = "geoEqualEarth";
const DEFAULT_BUBBLE_FILL = "#E26D5C";
const DEFAULT_BUBBLE_STROKE = "lightgray";
const DEFAULT_MAX_BUBBLE_SIZE = 30;
const DEFAULT_MIN_BUBBLE_SIZE = 1;
const DEFAULT_BUBBLE_STROKE_WIDTH = 0.5;
const DEFAULT_BUBBLE_OPACITY = 0.8;
const DEFAULT_FILL_BEGIN = "#ffbaba";
const DEFAULT_FILL_END = "#a70000";

export interface Props {
  background?: string;
  projection?: string;
  geoUrl?: string;

  // zoom properties
  enableZoom?: boolean;
  zoom?: number;
  zoomCenterLong?: number;
  zoomCenterLat?: number;
  maxZoom?: number;
  minZoom?: number;

  // Style
  fill?: string;
  stroke?: string;
  strokeWidth?: number;
  hover?: string;
  markers?: MapMarker[];
  markType?: MarkType;
  hoverStrokeWidth?: number;
  hoverStrokeFill?: string;

  // fill specific configuration
  geoKey?: string;
  fillBegin?: string;
  fillEnd?: string;

  // bubble specific configuration
  bubbleFill?: string;
  bubbleStroke?: string;
  bubbleStrokeWidth?: number;
  maxBubbleSize?: number;
  minBubbleSize?: number;
  bubbleOpacity?: number;
}

export enum MarkType {
  FILL = "Fill",
  BUBBLE = "Bubble",
}

export interface MapMarker {
  lat?: number;
  long?: number;
  name?: string;
  value: number;
}

export const SimpleReactMap = (props: Props) => {
  const geoUrl = props.geoUrl || DEFAULT_GEO;
  let fillCallback = (geo: any) => props.fill || DEFAULT_FILL;
  let markers: JSX.Element[] = [];

  if (props.markers && props.markers.length > 0) {
    const min = props.markers.map((m) => m.value).reduce((a, b) => (a <= b ? a : b));
    const max = props.markers.map((m) => m.value).reduce((a, b) => (a >= b ? a : b));
    if (MarkType.FILL === props.markType) {
      const colorScale = scaleLinear<string, string>()
        .domain([min, max])
        .range([props.fillBegin || DEFAULT_FILL_BEGIN, props.fillEnd || DEFAULT_FILL_END]);
      fillCallback = (geo: any) => {
        const marker = props.markers?.find(
          (m) =>
            m.name === geo.properties.name ||
            m.name === geo.properties.id ||
            (props.geoKey && m.name == geo.properties[props.geoKey])
        );
        return marker ? colorScale(marker.value) : props.fill || DEFAULT_DISABLED;
      };
    } else {
      // create markers for bubble
      const radiusScale = scaleLinear<number, number>()
        .domain([min, max])
        .range([props.minBubbleSize || DEFAULT_MIN_BUBBLE_SIZE, props.maxBubbleSize || DEFAULT_MAX_BUBBLE_SIZE]);

      markers = props.markers.map((marker, i) => (
        <Marker key={i} coordinates={[marker.long || 0, marker.lat || 0]}>
          <circle
            r={radiusScale(marker.value)}
            fill={props.bubbleFill || DEFAULT_BUBBLE_FILL}
            stroke={props.bubbleStroke || DEFAULT_BUBBLE_STROKE}
            strokeWidth={props.bubbleStrokeWidth || DEFAULT_BUBBLE_STROKE_WIDTH}
            opacity={props.bubbleOpacity || DEFAULT_BUBBLE_OPACITY}
          />
        </Marker>
      ));
    }
  }
  const geoComponent = (
    <>
      <Geographies geography={geoUrl}>
        {({ geographies }) =>
          geographies.map((geo) => {
            return (
              <Geography
                key={geo.rsmKey}
                geography={geo}
                strokeWidth={props.strokeWidth || DEFAULT_STROKE_WIDTH}
                stroke={props.stroke || DEFAULT_STROKE}
                style={{
                  default: {
                    fill: fillCallback(geo),
                    outline: "none",
                    pointerEvents: props.hover ? "all" : "none",
                  },
                  hover: {
                    fill: props.hover,
                    strokeWidth: props.hoverStrokeWidth,
                    stroke: props.hoverStrokeFill,
                  },
                }}
              />
            );
          })
        }
      </Geographies>
      {markers}
    </>
  );

  return (
    <ComposableMap
      projection={props.projection || DEFAULT_PROJECTION}
      style={{ backgroundColor: props.background || DEFAULT_BACKGROUND }}
    >
      {props.enableZoom ? (
        <ZoomableGroup
          center={[props.zoomCenterLong || 0, props.zoomCenterLat || 0]}
          zoom={props.zoom || 1}
          maxZoom={props.maxZoom || 8}
          minZoom={props.minZoom || 1}
        >
          {geoComponent}
        </ZoomableGroup>
      ) : (
        <>{geoComponent}</>
      )}
    </ComposableMap>
  );
};
