import * as React from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { BoxesIcon } from "@patternfly/react-icons/dist/js/icons/boxes-icon";

const NoMiningSchemaFieldsOptions = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={BoxesIcon} />
      <Title headingLevel="h4" size="lg">
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
