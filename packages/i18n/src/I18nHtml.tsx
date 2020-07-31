import * as React from "react";
import { FunctionComponent } from "react";

interface Props {
  children: string;
}

export const I18nHtml: FunctionComponent<Props> = ({ children }) => {
  return <p style={{ display: "inline" }} dangerouslySetInnerHTML={{ __html: children }} />;
};
