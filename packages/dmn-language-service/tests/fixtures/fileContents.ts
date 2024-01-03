import { readFileSync } from "fs";
import * as __path from "path";

export const model = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "model.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const nested = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "nested.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const recursive = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "recursive.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example1 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example1.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const example2 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "example2.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const decisions = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "decisions.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};

export const simple15 = () => {
  const fsFileAbsolutePath = __path.resolve(__dirname, "simple-1.5.dmn".split("/").join(__path.sep));
  return readFileSync(fsFileAbsolutePath, "utf8");
};
