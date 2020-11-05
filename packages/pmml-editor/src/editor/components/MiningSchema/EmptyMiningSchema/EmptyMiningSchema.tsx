import * as React from "react";
import { EmptyState, EmptyStateVariant, EmptyStateIcon, EmptyStateBody, Title } from "@patternfly/react-core";
import { OutlinedMehIcon } from "@patternfly/react-icons";

const EmptyMiningSchema = () => {
  return (
    <EmptyState variant={EmptyStateVariant.small}>
      <EmptyStateIcon icon={OutlinedMehIcon} />
      <Title headingLevel="h3" size="md">
        No fields added
      </Title>
      <EmptyStateBody>
        Add some fields first from the section above. Then you will be able to add further information for each of them.
      </EmptyStateBody>
    </EmptyState>
  );
};

export default EmptyMiningSchema;
