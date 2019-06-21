/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import * as AppFormer from "appformer-js";
import * as Service from "./service";
import { Popup } from "./Popup";
import { NotificationEvent } from "./NotificationEvent";
import { NotificationType } from "./NotificationType";

interface Props {
  onClose: () => void;
}

interface State {
  name: string;
  errorMessages: string[];
  displayedErrorMessages: string[];
}

export class NewSpacePopup extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { name: "", errorMessages: [], displayedErrorMessages: [] };
  }

  private async add() {
    const newSpace = {
      name: this.state.name,
      groupId: `com.${this.state.name.toLowerCase()}`
    };

    const emptyName = Promise.resolve().then(() => {
      if (!newSpace.name || newSpace.name.trim() === "") {
        this.addErrorMessage(AppFormer.translate("EmptyFieldValidation", ["Name"]));
        return Promise.reject();
      } else {
        return Promise.resolve();
      }
    });

    const duplicatedName = Promise.resolve()
      .then(() => Service.fetchIsDuplicatedSpaceName(newSpace.name))
      .then(spaceAlreadyExists => {
        if (spaceAlreadyExists) {
          this.addErrorMessage(AppFormer.translate("DuplicatedOrganizationalUnitValidation", [
              AppFormer.translate("OrganizationalUnitDefaultAliasInSingular", []).toLowerCase()
          ]));
          return Promise.reject();
        } else {
          return Promise.resolve();
        }
      });

    const validGroupId = Promise.resolve(true)
      .then(() => Service.fetchIsValidGroupIdName(newSpace.groupId))
      .then(isValidGroupId => {
        if (!isValidGroupId) {
          this.addErrorMessage(AppFormer.translate("InvalidSpaceName", []));
          return Promise.reject();
        } else {
          return Promise.resolve();
        }
      });

    this.setState({ errorMessages: [] }, () =>
      Promise.resolve()
        .then(() => Promise.all([emptyName, duplicatedName, validGroupId]))
        .then(() => Service.createSpace(newSpace))
        .then(i => {
          AppFormer.fireEvent(
            new NotificationEvent({
              type: NotificationType.SUCCESS,
              notification: AppFormer.translate("OrganizationalUnitSaveSuccess", [
                  AppFormer.translate("OrganizationalUnitDefaultAliasInSingular", []).toLowerCase()
              ])
            })
          );
          return this.props.onClose();
        })
        .catch(() => this.setState(prevState => ({ displayedErrorMessages: prevState.errorMessages })))
    );
  }

  private addErrorMessage(msg: string) {
    this.setState(prevState => ({ errorMessages: [...prevState.errorMessages, msg] }));
  }

  public render() {
    return (
      <>
        <Popup onClose={() => this.props.onClose()}>
          <div className={"modal-content"}>
            <div className={"modal-header"}>
              <button type="button" className={"close"} data-dismiss="modal" onClick={() => this.props.onClose()}>
                ×
              </button>
              <h4 className={"modal-title"}>Add Space</h4>
            </div>
            <div className={"modal-body"}>
              {this.state.displayedErrorMessages.map(errorMessage => (
                <div key={errorMessage} className={"alert alert-danger alert-dismissable"}>
                  <span className={"pficon pficon-error-circle-o"} />
                  <span>{errorMessage}</span>
                </div>
              ))}
              <label className={"form-control-label required"}>Name</label>
              <div className={"form-group"}>
                <input
                  type={"text"}
                  className={"form-control"}
                  onInput={(e: any) => this.setState({ name: e.target.value })}
                />
              </div>
            </div>
            <div className={"modal-footer"}>
              <button className={"btn btn-default"} onClick={() => this.props.onClose()}>
                {AppFormer.translate("Cancel", [])}
              </button>
              <button className={"btn btn-primary"} onClick={() => this.add()}>
                {AppFormer.translate("Add", [])}
              </button>
            </div>
          </div>
        </Popup>
      </>
    );
  }
}
