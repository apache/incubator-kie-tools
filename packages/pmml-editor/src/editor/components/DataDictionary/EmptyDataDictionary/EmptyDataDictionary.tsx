import * as React from "react";
import { EmptyState, EmptyStateVariant, EmptyStateIcon, EmptyStateBody, Title } from "@patternfly/react-core";
import { OutlinedMehIcon } from "@patternfly/react-icons";

const EmptyDataDictionary = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={OutlinedMehIcon} />
      <Title headingLevel="h3" size="md">
        No Data Fields Defined
      </Title>
      <EmptyStateBody>It looks empty here. Start adding fields using the buttons above.</EmptyStateBody>
    </EmptyState>
  );
};

export default EmptyDataDictionary;
