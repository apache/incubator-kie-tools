import React, { useCallback, useMemo, useState } from "react";
import { ResponsiveDropdown } from "../../../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../../../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useEditorToolbarContext, useEditorToolbarDispatchContext } from "../EditorToolbarContextProvider";
import CaretDownIcon from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import {
  useApplyAccelerators,
  useAvailableAccelerators,
  useCurrentAccelerator,
} from "../../../accelerators/AcceleratorsContext";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ProjectAccelerator } from "../../../accelerators/AcceleratorsApi";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { useOnlineI18n } from "../../../i18n";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";

type Props = {
  workspaceFile: WorkspaceFile;
};

export function AcceleratorsDropdown(props: Props) {
  const { i18n } = useOnlineI18n();
  const { isAcceleratorsDropdownOpen, workspace } = useEditorToolbarContext();
  const { setAcceleratorsDropdownOpen } = useEditorToolbarDispatchContext();
  const accelerators = useAvailableAccelerators();
  const applyAcceleratorToWorkspace = useApplyAccelerators(workspace);
  const [isAcceleratorDetailsModalOpen, setAcceleratorDetailsModalOpen] = useState(false);
  const [isConfirmModalOpen, setConfirmModalOpen] = useState(false);
  const [selectedAccelerator, setSelectedAccelerator] = useState<ProjectAccelerator | undefined>();

  const currentAccelerator = useCurrentAccelerator(props.workspaceFile.workspaceId);

  const onOpenConfirmAccelerator = useCallback(
    (accelerator: ProjectAccelerator) => {
      setAcceleratorsDropdownOpen(false);
      setSelectedAccelerator(accelerator);
      setConfirmModalOpen(true);
    },
    [setAcceleratorsDropdownOpen]
  );

  const onApplyAccelerator = useCallback(() => {
    if (selectedAccelerator) {
      applyAcceleratorToWorkspace(selectedAccelerator, props.workspaceFile);
    }
    setSelectedAccelerator(undefined);
    setConfirmModalOpen(false);
  }, [applyAcceleratorToWorkspace, props.workspaceFile, selectedAccelerator]);

  const acceleratorModalContent = useMemo(() => {
    const isConfirming = !!selectedAccelerator;
    const accelerator = selectedAccelerator ?? currentAccelerator;

    if (!accelerator) {
      return;
    }

    return (
      <>
        <Title headingLevel="h1">
          Accelerator: &nbsp;
          {accelerator?.iconUrl && (
            <>
              <Icon isInline style={{ verticalAlign: "middle" }}>
                <img src={accelerator?.iconUrl} />
              </Icon>
              &nbsp;
            </>
          )}
          {accelerator.name}
        </Title>
        <Grid style={{ margin: "1rem 0" }} hasGutter>
          {isConfirming && (
            <GridItem span={12}>
              <i>{i18n.accelerators.acceleratorDescription}</i>
            </GridItem>
          )}
          <GridItem span={12}>
            {i18n.accelerators.acceleratorDetails}
            &nbsp;
            <a href={accelerator.gitRepositoryUrl} target="__blank">
              {accelerator.gitRepositoryUrl}
            </a>
            &nbsp; ref:{accelerator.gitRepositoryGitRef}
          </GridItem>
          <GridItem span={6}>
            {isConfirming ? i18n.accelerators.dmnFilesMove : i18n.accelerators.dmnFilesLocation}
          </GridItem>
          <GridItem span={6}>
            <pre>{accelerator.dmnDestinationFolder}</pre>
          </GridItem>
          <GridItem span={6}>
            {isConfirming ? i18n.accelerators.bpmnFilesMove : i18n.accelerators.bpmnFilesLocation}
          </GridItem>
          <GridItem span={6}>
            <pre>{accelerator.bpmnDestinationFolder}</pre>
          </GridItem>
          <GridItem span={6}>
            {isConfirming ? i18n.accelerators.otherFilesMove : i18n.accelerators.otherFilesLocation}
          </GridItem>
          <GridItem span={6}>
            <pre>{accelerator.otherFilesDestinationFolder}</pre>
          </GridItem>
        </Grid>
        {isConfirming && <b>{i18n.accelerators.applyConfirmMessage}</b>}
      </>
    );
  }, [currentAccelerator, i18n.accelerators, selectedAccelerator]);

  return currentAccelerator ? (
    <>
      <Button variant={ButtonVariant.secondary} onClick={() => setAcceleratorDetailsModalOpen(true)}>
        <TextContent>
          <Text component="p">
            Accelerator: &nbsp;
            {currentAccelerator?.iconUrl && (
              <>
                <Icon isInline style={{ verticalAlign: "middle" }}>
                  <img src={currentAccelerator?.iconUrl} />
                </Icon>
                &nbsp;
              </>
            )}
            {currentAccelerator.name}
          </Text>
        </TextContent>
      </Button>
      <Modal
        isOpen={isAcceleratorDetailsModalOpen}
        onClose={() => setAcceleratorDetailsModalOpen(false)}
        aria-label="Accelerator"
        variant={ModalVariant.medium}
      >
        {acceleratorModalContent}
      </Modal>
    </>
  ) : (
    <>
      <ResponsiveDropdown
        title="Accelerators"
        onClose={() => setAcceleratorsDropdownOpen(false)}
        position={"right"}
        isOpen={isAcceleratorsDropdownOpen}
        toggle={
          <ResponsiveDropdownToggle
            onToggle={() => setAcceleratorsDropdownOpen((prev) => !prev)}
            toggleIndicator={CaretDownIcon}
          >
            Add Accelerator
          </ResponsiveDropdownToggle>
        }
        dropdownItems={accelerators.map((accelerator) => (
          <DropdownItem
            key={accelerator.name}
            icon={<img src={accelerator.iconUrl} />}
            onClick={() => onOpenConfirmAccelerator(accelerator)}
          >
            {accelerator.name}
          </DropdownItem>
        ))}
      />
      <Modal
        isOpen={isConfirmModalOpen}
        onClose={() => setConfirmModalOpen(false)}
        aria-label="Confirm accelerator"
        variant={ModalVariant.medium}
        actions={[
          <Button key="confirm" variant="primary" onClick={onApplyAccelerator}>
            {i18n.terms.apply}
          </Button>,
          <Button key="cancel" variant="link" onClick={() => setConfirmModalOpen(false)}>
            {i18n.terms.cancel}
          </Button>,
        ]}
      >
        {acceleratorModalContent}
      </Modal>
    </>
  );
}
