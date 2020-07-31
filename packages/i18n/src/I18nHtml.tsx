import * as React from "react";
import { FunctionComponent } from "react";

interface Props {
  children: string | string[];
}

export const I18nHtml: FunctionComponent<Props> = ({ children }) => {
  let htmlText = children
  if (Array.isArray(htmlText)) {
    htmlText = htmlText.join("")
  }
  return <p style={{ display: "inline" }} dangerouslySetInnerHTML={{ __html: htmlText }} />;
};
