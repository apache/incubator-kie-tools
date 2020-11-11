import * as React from "react";
import { EmptyState, EmptyStateVariant, EmptyStateIcon, EmptyStateBody, Title } from "@patternfly/react-core";
import { BoxesIcon } from "@patternfly/react-icons";

const EmptyDataDictionary = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={BoxesIcon} />
      <Title headingLevel="h4" size="lg">
        No Data Fields Defined
      </Title>
      <EmptyStateBody>It looks empty here. Start adding fields using the buttons above.</EmptyStateBody>
    </EmptyState>
  );
};

export default EmptyDataDictionary;
