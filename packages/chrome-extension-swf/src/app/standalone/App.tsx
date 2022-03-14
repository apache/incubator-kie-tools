import * as React from "react";
import { Main } from "../components/common/Main";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { Logger } from "../../Logger";
import { ResourceContentServiceFactory } from "../components/common/ChromeResourceContentService";
import { RoutesSwitch } from "../navigation/RoutesSwitch";

const imageUris = {
  kie: "/resources/kie_icon_rgb_fullcolor_default.svg",
  serverlessWorkflow: "/resources/sw-logo-transparent.png",
};

const editorEnvelopeLocator = new EditorEnvelopeLocator(window.location.origin, [
  new EnvelopeMapping("sw", "**/*.sw.+(json|yml|yaml)", `envelope/`, `envelope/index.html`),
]);

const resourceContentServiceFactory = new ResourceContentServiceFactory();

const logger = new Logger("standalone-chrome-extension-swf");

export const App = () => (
  <Main
    id={"standalone"}
    editorEnvelopeLocator={editorEnvelopeLocator}
    logger={logger}
    resourceContentServiceFactory={resourceContentServiceFactory}
    imageUris={imageUris}
  >
    <RoutesSwitch />
  </Main>
);
