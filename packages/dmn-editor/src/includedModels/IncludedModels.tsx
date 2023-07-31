import * as React from "react";
import { useCallback, useMemo } from "react";

import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useDmnEditorStore } from "../store/Store";

export function IncludedModels() {
  const { dmn } = useDmnEditorStore();

  const imports = useMemo(() => {
    return dmn.model.definitions.import ?? [];
  }, [dmn.model.definitions.import]);

  const includeModel = useCallback(() => {
    // TODO: Implement
  }, []);

  return (
    <>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.p}>
            Included models are externally defined models that have been added to this DMN file. External DMN models
            have their decision requirements diagram or graph components available to this DMN file. External PMML
            models can be invoked through DMN functions.
          </Text>
        </TextContent>
      </PageSection>
      {imports.length > 0 && (
        <PageSection>
          <Button onClick={includeModel} variant={ButtonVariant.primary}>
            Include model
          </Button>
          <br />
          <br />
          <>Included model list...</>
        </PageSection>
      )}
      {imports.length <= 0 && (
        <PageSection>
          <EmptyState>
            <EmptyStateIcon icon={PlusCircleIcon} />
            <Title headingLevel="h4" size="lg">
              No external models have been included.
            </Title>
            <EmptyStateBody>{`Select "Include model" to start.`}</EmptyStateBody>
            <Button onClick={includeModel} variant={ButtonVariant.primary}>
              Include model
            </Button>
          </EmptyState>
        </PageSection>
      )}
    </>
  );
}
