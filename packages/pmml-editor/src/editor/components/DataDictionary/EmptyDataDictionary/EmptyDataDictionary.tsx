import * as React from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { BoxesIcon } from "@patternfly/react-icons/dist/js/icons/boxes-icon";

const EmptyDataDictionary = () => {
  return (
    <EmptyState variant={EmptyStateVariant.large}>
      <EmptyStateIcon icon={BoxesIcon} />
      <Title headingLevel="h4" size="lg" data-ouia-component-id="no-data-fields-title">
        No Data Fields Defined
      </Title>
      <EmptyStateBody>It looks empty here. Start adding fields using the buttons above.</EmptyStateBody>
    </EmptyState>
  );
};

export default EmptyDataDictionary;
