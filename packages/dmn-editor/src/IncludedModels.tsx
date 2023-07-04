import * as React from "react";
import { TextContent, Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { useCallback, useMemo } from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon, Title } from "@patternfly/react-core/dist/esm/components";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";

export function IncludedModels({
  dmn,
  setDmn,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
}) {
  const imports = useMemo(() => {
    return dmn.definitions.import ?? [];
  }, [dmn.definitions.import]);

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
