import * as React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";

export function DataTypesEmptyState({ onAdd }: { onAdd: () => void }) {
  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateIcon icon={CubesIcon} />
        <Title size={"lg"} headingLevel={"h4"}>
          {`No custom data types have been defined.`}
        </Title>
        <EmptyStateBody>
          {`Data types are referenced in the input and output values for decision tables. Custom data types allow you to reference more complex data types, beyond the simple "default" types.`}
        </EmptyStateBody>
        <br />
        <EmptyStatePrimary>
          <Button variant={ButtonVariant.primary} onClick={onAdd}>
            Create a custom data type
          </Button>
        </EmptyStatePrimary>
      </EmptyState>
    </Flex>
  );
}
