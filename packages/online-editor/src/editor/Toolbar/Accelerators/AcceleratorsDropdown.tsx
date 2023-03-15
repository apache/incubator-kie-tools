import React, { useCallback, useState } from "react";
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
import { AcceleratorConfig } from "../../../accelerators/AcceleratorsApi";
import { useOnlineI18n } from "../../../i18n";
import { AcceleratorModal } from "./AcceleratorModal";
import { AcceleratorIcon } from "./AcceleratorIcon";

type Props = {
  workspaceFile: WorkspaceFile;
};

export function AcceleratorsDropdown(props: Props) {
  const { i18n } = useOnlineI18n();
  const { isAcceleratorsDropdownOpen, workspace } = useEditorToolbarContext();
  const { setAcceleratorsDropdownOpen } = useEditorToolbarDispatchContext();
  const accelerators = useAvailableAccelerators();
  const applyAcceleratorToWorkspace = useApplyAccelerators(workspace);
  const [isApplyModalOpen, setApplyModalOpen] = useState(false);
  const [selectedAccelerator, setSelectedAccelerator] = useState<AcceleratorConfig | undefined>();

  const currentAccelerator = useCurrentAccelerator(props.workspaceFile.workspaceId);

  const onOpenApplyAccelerator = useCallback(
    (accelerator: AcceleratorConfig) => {
      setAcceleratorsDropdownOpen(false);
      setSelectedAccelerator(accelerator);
      setApplyModalOpen(true);
    },
    [setAcceleratorsDropdownOpen]
  );

  const onApplyAccelerator = useCallback(() => {
    if (selectedAccelerator) {
      applyAcceleratorToWorkspace(selectedAccelerator, props.workspaceFile);
    }
    setSelectedAccelerator(undefined);
    setApplyModalOpen(false);
  }, [applyAcceleratorToWorkspace, props.workspaceFile, selectedAccelerator]);

  if (currentAccelerator) {
    return <></>;
  }

  return (
    <>
      <ResponsiveDropdown
        title="Accelerators"
        className={"kie-tools--masthead-hoverable"}
        isPlain={true}
        onClose={() => setAcceleratorsDropdownOpen(false)}
        position={"right"}
        isOpen={isAcceleratorsDropdownOpen}
        toggle={
          <ResponsiveDropdownToggle
            onToggle={() => setAcceleratorsDropdownOpen((prev) => !prev)}
            toggleIndicator={CaretDownIcon}
            icon={<i>🚀</i>}
          >
            {i18n.accelerators.applyAccelerator}
          </ResponsiveDropdownToggle>
        }
        dropdownItems={accelerators.map((accelerator) => (
          <DropdownItem
            key={accelerator.name}
            icon={<AcceleratorIcon iconUrl={accelerator.iconUrl} />}
            onClick={() => onOpenApplyAccelerator(accelerator)}
          >
            {accelerator.name}
          </DropdownItem>
        ))}
      />
      {selectedAccelerator && (
        <AcceleratorModal
          isOpen={isApplyModalOpen}
          onClose={() => setApplyModalOpen(false)}
          onApplyAccelerator={onApplyAccelerator}
          accelerator={selectedAccelerator}
          isApplying={true}
        />
      )}
    </>
  );
}
