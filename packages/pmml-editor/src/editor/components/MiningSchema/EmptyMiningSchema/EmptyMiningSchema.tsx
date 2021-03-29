import * as React from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { BoxesIcon } from "@patternfly/react-icons/dist/js/icons/boxes-icon";

const EmptyMiningSchema = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={BoxesIcon} />
      <Title headingLevel="h4" size="lg">
        No Mining Fields found
      </Title>
      <EmptyStateBody>
        Add some fields first from the section above. Then you will be able to add further information for each of them.
      </EmptyStateBody>
    </EmptyState>
  );
};

export default EmptyMiningSchema;
