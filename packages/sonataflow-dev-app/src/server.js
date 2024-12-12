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
// HTTP SERVER
const express = require("express");
const rateLimit = require("express-rate-limit");
const swaggerUi = require("swagger-ui-express");
const swaggerApiDoc = require("./MockData/openAPI/openapi.json");
var cors = require("cors");
const app = express();
const { ApolloServer } = require("apollo-server-express");
var bodyParser = require("body-parser");
// GraphQL - Apollo
const { GraphQLScalarType } = require("graphql");
const _ = require("lodash");
// Config
const config = require("./config");

//Mock data
const data = require("./MockData/graphql");
const controller = require("./MockData/controllers");
const typeDefs = require("./MockData/types");

const DEFAULT_DELAY = Number(process.env.SONATAFLOW_DEV_APP_DELAY || 2000);

const swaggerOptions = {
  swaggerOptions: {
    url: "/q/openapi.json",
  },
};

// set up rate limiter: maximum of 100 requests per minute to fix error "Missing rate limiting"
var limiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100,
});

function setPort(port = 4000) {
  app.set("port", parseInt(port, 10));
}

function listen() {
  const port = app.get("port") || config.port;
  app.listen(port, () => {
    console.log(`The server is running and listening at http://localhost:${port}`);
  });
}
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }));

// parse application/json
app.use(bodyParser.json());
app.use(
  cors({
    origin: config.corsDomain, // Be sure to switch to your production domain
    optionsSuccessStatus: 200,
  })
);

// handle empty GraphQL queries
app.use((req, res, next) => {
  if (req.body && req.body.query === "") {
    return res.status(200).send();
  }
  next();
});

app.use(limiter);

app.post("/", controller.triggerCloudEvent);
app.put("/", controller.triggerCloudEvent);

app.get("/q/openapi.json", (req, res) => res.json(swaggerApiDoc));
app.use("/docs", swaggerUi.serveFiles(null, swaggerOptions), swaggerUi.setup(null, swaggerOptions));

app.get("/forms/list", controller.getForms);
app.get("/customDashboard/list", controller.getCustomDashboards);
app.get("/customDashboard/:name", controller.getCustomDashboardContent);
app.get("/forms/:formName", controller.getFormContent);
app.post("/forms/:formName", controller.saveFormContent);

app.post(/^\/(service|hello|systout|jsongreet|order|yamlgreet)$/, controller.startProcessInstance);

function timeout(ms = DEFAULT_DELAY) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function paginatedResult(arr, offset, limit) {
  let paginatedArray = arr.slice(offset, offset + limit);
  console.log("offset : ", offset);
  console.log("limit : ", limit);
  if (offset > arr.length && paginatedArray.length === 0) {
    let prevData = arr.slice(offset - limit, limit);
    return prevData;
  }
  return paginatedArray;
}
// Provide resolver functions for your schema fields
const resolvers = {
  Mutation: {
    JobExecute: async (_parent, args) => {
      const job = data.JobsData.find((data) => {
        return data.id === args["id"];
      });
      if (!job) return;
      job.expirationTime = null;
      job.status = "EXECUTED";
    },
  },
  Query: {
    ProcessInstances: async (parent, args) => {
      let result = data.ProcessInstanceData.filter((datum) => {
        console.log("args", args["where"]);
        if (!args["where"]) {
          return true;
        } else if (args["where"].id && args["where"].id.equal) {
          return datum.id == args["where"].id.equal;
        } else if (args["where"].id && args["where"].id.in) {
          return args["where"].id.in.includes(datum.id);
        } else if (args["where"].rootProcessInstanceId && args["where"].rootProcessInstanceId.equal) {
          return datum.rootProcessInstanceId == args["where"].rootProcessInstanceId.equal;
        } else if (args["where"].parentProcessInstanceId && args["where"].parentProcessInstanceId.equal) {
          return datum.parentProcessInstanceId == args["where"].parentProcessInstanceId.equal;
        } else if (args["where"].parentProcessInstanceId && args["where"].parentProcessInstanceId.isNull) {
          if (args["where"].or === undefined || (args["where"].or && args["where"].or.length === 0)) {
            return datum.parentProcessInstanceId == null && args["where"].state.in.includes(datum.state);
          } else {
            if (
              datum.parentProcessInstanceId === null &&
              args["where"].state.in.includes(datum.state) &&
              datum.businessKey !== null
            ) {
              for (let i = 0; i < args["where"].or.length; i++) {
                if (
                  datum.businessKey &&
                  datum.businessKey.toLowerCase().indexOf(args["where"].or[i].businessKey.like.toLowerCase()) > -1
                ) {
                  return true;
                }
              }
              return false;
            }
          }
        } else {
          return false;
        }
      });
      if (args["orderBy"]) {
        console.log("orderBy args: ", args["orderBy"]);
        result = _.orderBy(
          result,
          _.keys(args["orderBy"]).map((key) => key),
          _.values(args["orderBy"]).map((value) => value.toLowerCase())
        );
      }
      await timeout();
      if (args["pagination"]) {
        result = paginatedResult(result, args["pagination"].offset, args["pagination"].limit);
      }
      console.log("result length: " + result.length);
      return result;
    },
    ProcessDefinitions: async () => {
      await timeout();
      return data.ProcessDefinitionData;
    },
    Jobs: async (parent, args) =>
      data.JobsData.filter((job) => {
        if (!args["where"]) {
          return true;
        } else if (args["where"].processInstanceId && args["where"].processInstanceId.equal) {
          return job.processInstanceId == args["where"].processInstanceId.equal;
        } else {
          return false;
        }
      }),
  },

  DateTime: new GraphQLScalarType({
    name: "DateTime",
    description: "DateTime custom scalar type",
    parseValue(value) {
      return value;
    },
    serialize(value) {
      return value;
    },
    parseLiteral(ast) {
      return null;
    },
  }),
};

const mocks = {
  DateTime: () => new Date().toUTCString(),
};

const server = new ApolloServer({
  typeDefs,
  resolvers,
  mocks,
  mockEntireSchema: false,
  introspection: true,
  playground: true,
});

server.start().then(() => {
  server.applyMiddleware({ app });
});

module.exports = {
  getApp: () => app,
  setPort,
  listen,
};
