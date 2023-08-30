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
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import React, { useCallback, useEffect } from "react";
import { When } from "react-if";
import { SampleAuthor } from "./types";

export function ExpandableAuthor(props: { author: SampleAuthor }) {
  const { author } = props;
  const [isExpanded, setIsExpanded] = React.useState(false);

  const getSocialLink = useCallback((network: string, id: string) => {
    switch (network.toLowerCase()) {
      case "twitter":
        return `https://twitter.com/${id}`;
      case "linkedin":
        return `https://www.linkedin.com/in/${id}`;
      case "facebook":
        return `https://www.facebook.com/${id}`;
      default:
        return "";
    }
  }, []);

  useEffect(() => {
    setIsExpanded(false);
  }, [author]);

  return (
    <ExpandableSection
      toggleText={author.name}
      onToggle={() => setIsExpanded(!isExpanded)}
      isExpanded={isExpanded}
      isIndented
    >
      <When condition={author.email}>
        <div>
          <strong>Email:</strong>{" "}
          <a href={`mailto:${author.email}`} rel="noopener noreferrer">
            {author.email}
          </a>
        </div>
      </When>
      <When condition={author.github}>
        <div>
          <strong>Github:</strong>{" "}
          <a href={author.github} target="_blank" rel="noopener noreferrer">
            {author.github}
          </a>
        </div>
      </When>
      {author.social &&
        author.social.map((social, index) => (
          <div key={index}>
            <b style={{ textTransform: "capitalize" }}>{social.network}</b>:{" "}
            <a href={getSocialLink(social.network, social.id)} target="_blank" rel="noopener noreferrer">
              {social.id}
            </a>
          </div>
        ))}
    </ExpandableSection>
  );
}
