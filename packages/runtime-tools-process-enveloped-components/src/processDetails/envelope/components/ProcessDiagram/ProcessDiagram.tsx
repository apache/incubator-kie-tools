/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect, useRef, useState } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { UncontrolledReactSVGPanZoom } from "react-svg-pan-zoom";
import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from "react-svg-pan-zoom-loader";

interface SvgType {
  src: string;
}
interface SvgProp {
  props: SvgType;
}
interface IOwnProps {
  svg: SvgProp;
  width?: number;
  height?: number;
}

const ProcessDiagram: React.FC<IOwnProps> = ({ svg, width, height }) => {
  const reactSvgPanZoomLoaderRefContainer = useRef<HTMLDivElement>(null);
  const [containerWidth, setContainerWidth] = useState<number>(width || 1000);

  useEffect(() => {
    if (reactSvgPanZoomLoaderRefContainer.current) {
      const observer = new ResizeObserver((entries) => {
        for (const entry of entries) {
          setContainerWidth(entry.contentRect.width);
        }
      });

      observer.observe(reactSvgPanZoomLoaderRefContainer.current);

      return () => {
        observer.disconnect();
      };
    }
  }, []);

  return (
    <>
      <Card className="process-diagram">
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Diagram
          </Title>
        </CardHeader>
        <CardBody>
          <div style={{ width: "100%" }} ref={reactSvgPanZoomLoaderRefContainer}>
            <ReactSvgPanZoomLoader
              src={svg.props.src}
              width={containerWidth}
              height={height ?? 400}
              proxy={
                <>
                  <SvgLoaderSelectElement />
                </>
              }
              render={() => (
                <UncontrolledReactSVGPanZoom
                  width={containerWidth}
                  height={height ?? 400}
                  detectAutoPan={false}
                  background="#fff"
                >
                  <svg width={containerWidth} height={height ?? 400}>
                    {svg}
                  </svg>
                </UncontrolledReactSVGPanZoom>
              )}
            />
          </div>
        </CardBody>
      </Card>
    </>
  );
};

export default ProcessDiagram;
