import * as React from "react";
import { EmptyState, EmptyStateVariant, EmptyStateIcon, EmptyStateBody, Title } from "@patternfly/react-core";
import { OutlinedMehIcon } from "@patternfly/react-icons";

const NoMiningSchemaFieldsOptions = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={OutlinedMehIcon} />
      <Title headingLevel="h3" size="md">
        No Data Fields defined
      </Title>
      <EmptyStateBody>
        Mining schema can only include fields from the Data Dictionary. It seems there are none yet. Go to the Data
        Dictionary and come back after creating them.
      </EmptyStateBody>
    </EmptyState>
  );
};

export default NoMiningSchemaFieldsOptions;
