import * as React from "react";
import { Main } from "../common/Main";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { Logger } from "../../Logger";
import { ResourceContentServiceFactory } from "../common/ChromeResourceContentService";
import { RoutesSwitch } from "../navigation/RoutesSwitch";
import { Global } from "../common/Global";

const imagesUriPath = "/images/";
const resourcesUriPath = "/resources/";

export const editorEnvelopeLocator = new EditorEnvelopeLocator(window.location.origin, [
  new EnvelopeMapping("sw", "**/*.sw.+(json|yml|yaml)", "", "envelope.html"),
]);

const resourceContentServiceFactory = new ResourceContentServiceFactory();

const logger = new Logger("standalone-chrome-extension-swf");

export const App = () => (
  <Global
    id={"standalone"}
    editorEnvelopeLocator={editorEnvelopeLocator}
    logger={logger}
    resourceContentServiceFactory={resourceContentServiceFactory}
    imagesUriPath={imagesUriPath}
    resourcesUriPath={resourcesUriPath}
  >
    <Main>
      <RoutesSwitch />
    </Main>
  </Global>
);
