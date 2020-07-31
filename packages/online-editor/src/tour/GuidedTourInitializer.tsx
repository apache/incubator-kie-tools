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

import { Title, Text, Button, List, ListItem, Divider } from "@patternfly/react-core";
import { BookOpenIcon, TrophyIcon } from "@patternfly/react-icons";

import { File, EditorType } from "@kogito-tooling/embedded-editor";
import { KogitoGuidedTour, DemoMode, SubTutorialMode, Tutorial } from "@kogito-tooling/guided-tour";
import { OnlineI18n, useOnlineI18n } from "../common/i18n";
import { I18nHtml } from "@kogito-tooling/i18n";

export function useDmnTour(isEditorReady: boolean, file: File) {
  const { i18n } = useOnlineI18n();

  useEffect(() => {
    const guidedTour = KogitoGuidedTour.getInstance();
    guidedTour.setup();
    return () => guidedTour.teardown();
  }, []);

  useEffect(() => {
    if (isEditorReady && file.editorType === EditorType.DMN) {
      const guidedTour = KogitoGuidedTour.getInstance();
      const tutorial = getOnlineEditorTutorial(i18n);

      guidedTour.registerTutorial(tutorial);
      guidedTour.start(tutorial.label);
    }
  }, [isEditorReady, file]);
}

function getOnlineEditorTutorial(i18n: OnlineI18n) {
  return new Tutorial("DMN Online Editor Tutorial", [
    {
      position: "center",
      mode: new DemoMode(),
      content: (props: any) => (
        <div className="pf-c-content kgt-slide--with-accent">
          <BookOpenIcon size="xl" className="kgt-icon--with-accent" />
          <Title headingLevel="h3" size="xl">
            <I18nHtml>{i18n.guidedTour.init.title}</I18nHtml>
          </Title>
          <Text>{i18n.guidedTour.init.learnMore}</Text>
          <Button onClick={props.nextStep} variant="primary">
            <I18nHtml>{i18n.guidedTour.init.letsGo}</I18nHtml>
          </Button>
          <Text>{"  "}</Text>
          <Button onClick={props.dismiss} variant="link">
            <I18nHtml>{i18n.terms.dismiss}</I18nHtml>
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
            <I18nHtml>{i18n.guidedTour.end.title}</I18nHtml>
          </Title>
          <TrophyIcon size="xl" className="kgt-icon--with-accent" />
          <Text>{i18n.guidedTour.end.motivational}!</Text>
          <Divider />
          <Text className="pf-c-content--align-left">{i18n.guidedTour.end.nextSteps.title}:</Text>
          <List className="pf-c-content--align-left">
            <ListItem>{i18n.guidedTour.end.nextSteps.firstStep};</ListItem>
            <ListItem>{i18n.guidedTour.end.nextSteps.secondStep};</ListItem>
            <ListItem>{i18n.guidedTour.end.nextSteps.thirdStep}.</ListItem>
          </List>
          <Text className="pf-c-content--align-left">
            <I18nHtml>{`${i18n.guidedTour.end.findUsefulInfo} `}</I18nHtml>
            <a target="_blank" href="http://learn-dmn-in-15-minutes.com">
              <I18nHtml>{i18n.guidedTour.end.learnDMN}</I18nHtml>
            </a>{" "}
            <I18nHtml>{`${i18n.guidedTour.end.courseOr} `}</I18nHtml>
            <a
              target="_blank"
              href="https://docs.jboss.org/kogito/release/latest/html_single/#_using_dmn_models_in_kogito_services"
            >
              <I18nHtml>{i18n.guidedTour.end.kogitoDoc}</I18nHtml>
            </a>{" "}
            :-)
          </Text>
          <Button onClick={props.dismiss} variant="primary">
            <I18nHtml>{i18n.guidedTour.end.finish}</I18nHtml>
          </Button>
        </div>
      )
    }
  ]);
}
