import React from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";

export type ResponsiveDropdownModalProps = {
  isOpen?: boolean;
  className?: string;
  title?: string;
  onClose?: () => void;
};

export const ResponsiveDropdownModal: React.FunctionComponent<ResponsiveDropdownModalProps> = ({
  isOpen,
  onClose,
  title,
  children,
}) => {
  return (
    <Modal
      isOpen={isOpen ?? false}
      onClose={onClose}
      hasNoBodyWrapper={true}
      variant={ModalVariant.small}
      title={title}
      className="kogito--editor__responsive-dropdown-modal"
    >
      <div
        style={{ margin: "var(--pf-global--spacer--lg) 0 0 0" }}
        className="kogito--editor__responsive-dropdown-container"
      >
        {children}
      </div>
    </Modal>
  );
};
