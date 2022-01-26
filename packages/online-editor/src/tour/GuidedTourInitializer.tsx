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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { BookOpenIcon } from "@patternfly/react-icons/dist/js/icons/book-open-icon";
import { TrophyIcon } from "@patternfly/react-icons/dist/js/icons/trophy-icon";
import { KogitoGuidedTour } from "@kie-tools-core/guided-tour/dist/channel";
import { DemoMode, SubTutorialMode, Tutorial } from "@kie-tools-core/guided-tour/dist/api";
import { OnlineI18n, useOnlineI18n } from "../i18n";
import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";

export function useDmnTour(shouldShow: boolean) {
  const { i18n } = useOnlineI18n();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  useEffect(() => {
    if (!settings.general.guidedTour.isEnabled || shouldShow) {
      return;
    }

    const guidedTour = KogitoGuidedTour.getInstance();
    guidedTour.setup(() => settingsDispatch.general.guidedTour.setEnabled(false));
  }, [shouldShow, settings, settingsDispatch]);

  useEffect(() => {
    if (shouldShow && settings.general.guidedTour.isEnabled) {
      const guidedTour = KogitoGuidedTour.getInstance();
      const tutorial = getOnlineEditorTutorial(i18n);

      guidedTour.registerTutorial(tutorial);
      guidedTour.start(tutorial.label);
    }
  }, [shouldShow, i18n, settings]);
}

function getOnlineEditorTutorial(i18n: OnlineI18n) {
  function dismissAndStartDmnRunner(props: any) {
    props.dismiss();
    (document.getElementById("dmn-runner-button") as HTMLButtonElement)?.click();
  }

  return new Tutorial("DMN Online Editor Tutorial", [
    {
      position: "center",
      mode: new DemoMode(),
      content: (props: any) => (
        <div className="pf-c-content kgt-slide--with-accent">
          <BookOpenIcon size="xl" className="kgt-icon--with-accent" />
          <Title headingLevel="h3" size="xl">
            {i18n.guidedTour.init.title}
          </Title>
          <Text>{i18n.guidedTour.init.learnMore}</Text>
          <Text>{i18n.guidedTour.init.dmnRunnerIntro}</Text>

          <Text>{"  "}</Text>
          <Button
            ouiaId="dmn-guided-tour-skip-runner-start-button"
            onClick={() => dismissAndStartDmnRunner(props)}
            variant="link"
          >
            {i18n.guidedTour.init.skipTourAndUseDmnRunner}
          </Button>
          <br />
          <Button onClick={props.dismiss} variant="link">
            {i18n.guidedTour.init.skipTour}
          </Button>
          <Text>{"  "}</Text>
          <Button onClick={props.nextStep} variant="primary">
            {i18n.guidedTour.init.takeTour}
          </Button>
        </div>
      ),
    },
    {
      mode: new SubTutorialMode("DMN 101 Tutorial"),
    },
    {
      position: "center",
      mode: new DemoMode(),
      content: (props: any) => (
        <div className="pf-c-content kgt-slide--with-accent">
          <Title headingLevel="h3" size="xl">
            {i18n.guidedTour.end.title}
          </Title>
          <TrophyIcon size="xl" className="kgt-icon--with-accent" />
          <Text>{i18n.guidedTour.end.motivational}</Text>
          <Divider />
          <Text className="pf-c-content--align-left">{i18n.guidedTour.end.nextSteps.title}:</Text>
          <List className="pf-c-content--align-left">
            <ListItem>
              <I18nHtml>{i18n.guidedTour.end.nextSteps.firstStep}</I18nHtml>
            </ListItem>
            <ListItem>
              <I18nHtml>{i18n.guidedTour.end.nextSteps.secondStep}</I18nHtml>
            </ListItem>
            <ListItem>
              {i18n.guidedTour.end.nextSteps.thirdStep}{" "}
              <Button isInline={true} onClick={() => dismissAndStartDmnRunner(props)} variant="link">
                {i18n.guidedTour.end.nextSteps.startDmnRunner}
              </Button>
            </ListItem>
          </List>
          <Text className="pf-c-content--align-left">
            {`${i18n.guidedTour.end.findUsefulInfo} `}
            <a target="_blank" href="http://learn-dmn-in-15-minutes.com">
              {i18n.guidedTour.end.learnDMN}
            </a>{" "}
            {`${i18n.guidedTour.end.courseOr} `}
            <a
              target="_blank"
              href="https://docs.jboss.org/kogito/release/latest/html_single/#_using_dmn_models_in_kogito_services"
            >
              {i18n.guidedTour.end.kogitoDoc}
            </a>{" "}
            :-)
          </Text>
          <Button onClick={props.dismiss} variant="primary">
            {i18n.guidedTour.end.finish}
          </Button>
        </div>
      ),
    },
  ]);
}
