import { create } from "@storybook/theming/create";
import brandImage from "./static/logo.svg";

export const KieToolsTheme = create({
  base: "light",
  brandTitle: "KIE Tools",
  brandImage,
  brandTarget: "_self",
});
