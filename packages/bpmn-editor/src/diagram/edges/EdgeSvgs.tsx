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

export const SequenceFlowPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const AssociationPath = React.memo(
  (__props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const strokeWidth = __props.strokeWidth ?? 1.5;
    const { svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          strokeWidth={strokeWidth}
          strokeLinecap="butt"
          strokeLinejoin="round"
          style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
          {...props}
        />
      </>
    );
  }
);
