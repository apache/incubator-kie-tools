import * as React from "react";
import { AutoForm, QuickForm, ValidatedForm } from "uniforms/esm";
import { BaseRow } from "./BaseRow";

function Auto(parent: any): any {
  class _ extends AutoForm.Auto(ValidatedForm.Validated(QuickForm.Quick(parent))) {
    static Auto = Auto;
  }
  return _;
}

export const AutoRow = Auto(BaseRow);
