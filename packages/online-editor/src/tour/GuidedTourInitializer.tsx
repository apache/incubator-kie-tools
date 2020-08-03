/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useEffect } from "react";

import { Button, Divider, List, ListItem, Text, Title } from "@patternfly/react-core";
import { BookOpenIcon, TrophyIcon } from "@patternfly/react-icons";

import { File } from "@kogito-tooling/editor/dist/embedded";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour/dist/channel";
import { DemoMode, SubTutorialMode, Tutorial } from "@kogito-tooling/guided-tour/dist/api";

export function useDmnTour(isEditorReady: boolean, file: File) {
  useEffect(() => {
    const guidedTour = KogitoGuidedTour.getInstance();
    guidedTour.setup();
    return () => guidedTour.teardown();
  }, []);

  useEffect(() => {
    if (isEditorReady && file.fileExtension === "dmn") {
      const guidedTour = KogitoGuidedTour.getInstance();
      const tutorial = getOnlineEditorTutorial();

      guidedTour.registerTutorial(tutorial);
      guidedTour.start(tutorial.label);
    }
  }, [isEditorReady, file]);
}

function getOnlineEditorTutorial() {
  return new Tutorial("DMN Online Editor Tutorial", [
    {
      position: "center",
      mode: new DemoMode(),
      content: (props: any) => (
        <div className="pf-c-content kgt-slide--with-accent">
          <BookOpenIcon size="xl" className="kgt-icon--with-accent" />
          <Title headingLevel="h3" size="xl">
            Welcome to this 5 minutes tour
          </Title>
          <Text>Learn more about the DMN online editor by taking this brief and interactive tour.</Text>
          <Button onClick={props.nextStep} variant="primary">
            Let's go
          </Button>
          <Text>{"  "}</Text>
          <Button onClick={props.dismiss} variant="link">
            Dismiss
          </Button>
        </div>
      )
    },
    {
      mode: new SubTutorialMode("DMN 101 Tutorial")
    },
    {
      position: "center",
      mode: new DemoMode(),
      content: (props: any) => (
        <div className="pf-c-content kgt-slide--with-accent">
          <Title headingLevel="h3" size="xl">
            Congratulations
          </Title>
          <TrophyIcon size="xl" className="kgt-icon--with-accent" />
          <Text>Now you know how each part of the DMN editor works, and you're empowered to go ahead and explore!</Text>
          <Divider />
          <Text className="pf-c-content--align-left">As next steps, you can try to:</Text>
          <List className="pf-c-content--align-left">
            <ListItem>
              Connect the <b>Age</b> input with the <b>Can drive?</b> decision;
            </ListItem>
            <ListItem>
              Define the decision logic into the <b>Can drive?</b> node, to return <b>true</b> when <b>Age</b> is
              greater <b>21</b>, otherwise <b>false</b>;
            </ListItem>
            <ListItem>Execute the model.</ListItem>
          </List>
          <Text className="pf-c-content--align-left">
            You can find useful information at the{" "}
            <a target="_blank" href="http://learn-dmn-in-15-minutes.com">
              Learn DMN in 15 minutes
            </a>{" "}
            course or at the{" "}
            <a
              target="_blank"
              href="https://docs.jboss.org/kogito/release/latest/html_single/#_using_dmn_models_in_kogito_services"
            >
              Kogito documentation
            </a>{" "}
            :-)
          </Text>
          <Button onClick={props.dismiss} variant="primary">
            Finish the Tour
          </Button>
        </div>
      )
    }
  ]);
}
