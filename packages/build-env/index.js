const dotenv = require("dotenv");
dotenv.config();

export function get(envVariableName) {
  const value = process.env[envVariableName];
  if (value !== undefined) {
    return value;
  }

  console.error(`Variable '${envVariableName}' is not set.`);
  process.exit(1);
}
