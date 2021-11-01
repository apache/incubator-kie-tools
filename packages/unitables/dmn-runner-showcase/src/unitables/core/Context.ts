import { Context } from "uniforms";
import { createContext } from "react";
import { Grid } from "./Grid";

export type TableContext<Model> = Context<Model> & {
  grid: Grid;
};

export const tableContext = createContext<TableContext<any> | null>(null);
