import * as React from "react";
import { Main } from "./common/Main";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { ResourceContentServiceFactory } from "./common/AppResourceContentService";
import { RoutesSwitch } from "./navigation/RoutesSwitch";
import { Global } from "./common/Global";

const imagesUriPath = "/images/";
const resourcesUriPath = "/resources/";

// TODO: Replace it by EditorEnvelopeLocatorContext
export const editorEnvelopeLocator = new EditorEnvelopeLocator(window.location.origin, [
  new EnvelopeMapping("sw", "**/*.sw.+(json|yml|yaml)", "", "swf-envelope.html"),
]);

const resourceContentServiceFactory = new ResourceContentServiceFactory();

export const App = () => (
  <Global
    id={"standalone"}
    editorEnvelopeLocator={editorEnvelopeLocator}
    resourceContentServiceFactory={resourceContentServiceFactory}
    imagesUriPath={imagesUriPath}
    resourcesUriPath={resourcesUriPath}
  >
    <Main>
      <RoutesSwitch />
    </Main>
  </Global>
);
