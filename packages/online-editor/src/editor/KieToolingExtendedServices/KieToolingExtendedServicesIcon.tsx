import { Dropdown, DropdownPosition, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";
import { ConnectedIcon } from "@patternfly/react-icons/dist/js/icons/connected-icon";
import { DisconnectedIcon } from "@patternfly/react-icons/dist/js/icons/disconnected-icon";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { useOnlineI18n } from "../../common/i18n";

export function KieToolingExtendedServicesIcon() {
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const { i18n } = useOnlineI18n();

  const onToggleKieToolingExtendedServices = useCallback(() => {
    if (!kieToolingExtendedServices.outdated) {
      return;
    }
    kieToolingExtendedServices.setModalOpen(true);
  }, [kieToolingExtendedServices]);

  const dropdownToggleIcon = useMemo(
    () => (
      <>
        {kieToolingExtendedServices.outdated && (
          <Tooltip
            className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
            key={"outdated"}
            content={i18n.kieToolingExtendedServices.dropdown.tooltip.outdated}
            flipBehavior={["left"]}
            distance={20}
          >
            <ExclamationTriangleIcon
              data-testid="outdated-icon"
              className="kogito--editor__kie-tooling-extended-services-dropdown-icon-outdated static-opacity"
              id="kie-tooling-extended-services-outdated-icon"
            />
          </Tooltip>
        )}
        {!kieToolingExtendedServices.outdated && (
          <>
            {kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING ? (
              <Tooltip
                className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                key={"connected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.connected(kieToolingExtendedServices.port)}
                flipBehavior={["left"]}
                distance={20}
              >
                <ConnectedIcon
                  data-testid="connected-icon"
                  className="kogito--editor__kie-tooling-extended-services-dropdown-icon-connected blink-opacity"
                  id="kie-tooling-extended-services-connected-icon"
                />
              </Tooltip>
            ) : (
              <Tooltip
                className="kogito--editor__kie-tooling-extended-services-dropdown-tooltip"
                key={"disconnected"}
                content={i18n.kieToolingExtendedServices.dropdown.tooltip.disconnected}
                flipBehavior={["left"]}
                distance={20}
              >
                <DisconnectedIcon
                  data-testid="disconnected-icon"
                  className="static-opacity"
                  id="kie-tooling-extended-services-disconnected-icon"
                />
              </Tooltip>
            )}
          </>
        )}
      </>
    ),
    [i18n, kieToolingExtendedServices.outdated, kieToolingExtendedServices.port, kieToolingExtendedServices.status]
  );

  return (
    <Dropdown
      toggle={
        <DropdownToggle
          id="kie-tooling-extended-services-button"
          toggleIndicator={null}
          onToggle={onToggleKieToolingExtendedServices}
          className="kogito--kie-tooling-extended-services-button"
          data-testid="kie-tooling-extended-services-button"
        >
          {dropdownToggleIcon}
        </DropdownToggle>
      }
      isPlain={true}
      className={"kogito--editor__toolbar dropdown"}
      position={DropdownPosition.right}
      style={
        kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING
          ? { marginRight: "2px", borderBottom: "solid transparent 2px", paddingBottom: 0 }
          : { marginRight: "2px", borderBottom: "solid var(--pf-global--palette--red-300) 2px", paddingBottom: 0 }
      }
    />
  );
}
