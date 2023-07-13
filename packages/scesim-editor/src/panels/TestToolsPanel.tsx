import * as React from "react";

import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { DrawerHead, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

export function TestToolsPanel() {
  return (
    <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"500px"}>
      <DrawerHead>
        <TextContent>
          <Text component={TextVariants.h4}>
            <>Test Tools</>
          </Text>
          <Divider />
          <Text component={TextVariants.p}>
            To create a test scenario, define the "Given" and "Expect" columns by using the expression editor below.
          </Text>
        </TextContent>
      </DrawerHead>
    </DrawerPanelContent>
  );
}
