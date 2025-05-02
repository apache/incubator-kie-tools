/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useState } from "react";
import { Dropdown, DropdownToggle, DropdownItem } from "@patternfly/react-core/deprecated";
import { UserIcon } from "@patternfly/react-icons/dist/js/icons/user-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { useDevUIAppContext } from "../../../contexts/DevUIAppContext";
import "../../../styles.css";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface IOwnProps {
  user: string;
}

const TaskListSwitchUser: React.FC<IOwnProps & OUIAProps> = ({ user, ouiaId, ouiaSafe }) => {
  const appContext = useDevUIAppContext();
  const [isDropDownOpen, setIsDropDownOpen] = useState(false);
  const [currentUser, setCurrentUser] = useState(user);
  const allUsers = appContext.getAllUsers();

  const onSelect = (event): void => {
    const selectedUser = event.target.innerHTML;
    appContext.switchUser(selectedUser);
    setCurrentUser(selectedUser);
    setIsDropDownOpen(!isDropDownOpen);
  };

  const dropdownItems = (): JSX.Element[] => {
    const userIds = [];
    allUsers.forEach((userObj) => {
      userIds.push(<DropdownItem key={userObj.id}>{userObj.id}</DropdownItem>);
    });
    return userIds;
  };

  const onToggle = (isOpen): void => {
    setIsDropDownOpen(isOpen);
  };

  return (
    <Dropdown
      onSelect={onSelect}
      toggle={
        <DropdownToggle
          onToggle={(_event, isOpen) => onToggle(isOpen)}
          aria-label="Applications"
          id="toggle-id-7"
          toggleIndicator={CaretDownIcon}
          icon={<UserIcon />}
        >
          {currentUser}
        </DropdownToggle>
      }
      isOpen={isDropDownOpen}
      isPlain
      dropdownItems={dropdownItems()}
      className="DevUI-switchUser-dropdown-styling"
      {...componentOuiaProps(ouiaId, "tasks-switch-user", ouiaSafe)}
    />
  );
};

export default TaskListSwitchUser;
