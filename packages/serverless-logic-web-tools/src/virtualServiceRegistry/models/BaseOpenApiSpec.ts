/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

export function generateOpenApiSpec(functionName: string) {
  return `
    openapi: 3.0.3
    info:
      title: serverless-workflow-base API
      version: 0.0.0
    tags:
      - name: ${functionName}
        description: Workflow
    paths:
      /:
        post:
          requestBody:
            content:
              "*/*":
                schema:
                  $ref: "#/components/schemas/CloudEvent"
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    $ref: "#/components/schemas/Response"
      /${functionName}:
        get:
          operationId: ${functionName}
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    type: array
                    items:
                      $ref: "#/components/schemas/${functionName}ModelOutput"
        post:
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          parameters:
            - name: businessKey
              in: query
              schema:
                default: ""
                type: string
          requestBody:
            content:
              application/json:
                schema:
                  $ref: "#/components/schemas/${functionName}ModelInput"
          responses:
            "200":
              description: OK
      /${functionName}/schema:
        get:
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    type: object
                    additionalProperties:
                      type: object
      "/${functionName}/{id}":
        get:
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          parameters:
            - name: id
              in: path
              required: true
              schema:
                type: string
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    $ref: "#/components/schemas/${functionName}ModelOutput"
        put:
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          parameters:
            - name: id
              in: path
              required: true
              schema:
                type: string
          requestBody:
            content:
              application/json:
                schema:
                  $ref: "#/components/schemas/${functionName}Model"
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    $ref: "#/components/schemas/${functionName}ModelOutput"
        delete:
          tags:
            - ${functionName}
          summary: ${functionName}
          description: ""
          parameters:
            - name: id
              in: path
              required: true
              schema:
                type: string
          responses:
            "200":
              description: OK
              content:
                application/json:
                  schema:
                    $ref: "#/components/schemas/${functionName}ModelOutput"
    components:
      schemas:
        CloudEvent:
          type: object
          properties:
            specVersion:
              $ref: "#/components/schemas/SpecVersion"
            id:
              type: string
            type:
              type: string
            source:
              format: uri
              type: string
            dataContentType:
              type: string
            dataSchema:
              format: uri
              type: string
            subject:
              type: string
            time:
              format: date-time
              type: string
            attributeNames:
              uniqueItems: true
              type: array
              items:
                type: string
            extensionNames:
              uniqueItems: true
              type: array
              items:
                type: string
            data:
              $ref: "#/components/schemas/CloudEventData"
        CloudEventData:
          type: object
        EntityTag:
          type: object
          properties:
            value:
              type: string
            weak:
              type: boolean
        Family:
          enum:
            - INFORMATIONAL
            - SUCCESSFUL
            - REDIRECTION
            - CLIENT_ERROR
            - SERVER_ERROR
            - OTHER
          type: string
        ${functionName}Model:
          type: object
          properties:
            id:
              type: string
            workflowdata:
              type: object
        ${functionName}ModelInput:
          type: object
          properties:
            workflowdata:
              type: object
        ${functionName}ModelOutput:
          type: object
          properties:
            id:
              type: string
            workflowdata:
              type: object
        Link:
          type: object
          properties:
            uri:
              format: uri
              type: string
            uriBuilder:
              $ref: "#/components/schemas/UriBuilder"
            rel:
              type: string
            rels:
              type: array
              items:
                type: string
            title:
              type: string
            type:
              type: string
            params:
              type: object
              additionalProperties:
                type: string
        Locale:
          type: object
          properties:
            language:
              type: string
            script:
              type: string
            country:
              type: string
            variant:
              type: string
            extensionKeys:
              uniqueItems: true
              type: array
              items:
                format: byte
                type: string
            unicodeLocaleAttributes:
              uniqueItems: true
              type: array
              items:
                type: string
            unicodeLocaleKeys:
              uniqueItems: true
              type: array
              items:
                type: string
            iSO3Language:
              type: string
            iSO3Country:
              type: string
            displayLanguage:
              type: string
            displayScript:
              type: string
            displayCountry:
              type: string
            displayVariant:
              type: string
            displayName:
              type: string
        MediaType:
          type: object
          properties:
            type:
              type: string
            subtype:
              type: string
            parameters:
              type: object
              additionalProperties:
                type: string
            wildcardType:
              type: boolean
            wildcardSubtype:
              type: boolean
        MultivaluedMapStringObject:
          type: object
          additionalProperties:
            type: array
            items:
              type: object
        MultivaluedMapStringString:
          type: object
          additionalProperties:
            type: array
            items:
              type: string
        NewCookie:
          type: object
          properties:
            name:
              type: string
            value:
              type: string
            version:
              format: int32
              type: integer
            path:
              type: string
            domain:
              type: string
            comment:
              type: string
            maxAge:
              format: int32
              type: integer
            expiry:
              format: date
              type: string
            secure:
              type: boolean
            httpOnly:
              type: boolean
        Response:
          type: object
          properties:
            status:
              format: int32
              type: integer
            statusInfo:
              $ref: "#/components/schemas/StatusType"
            entity:
              type: object
            mediaType:
              $ref: "#/components/schemas/MediaType"
            language:
              $ref: "#/components/schemas/Locale"
            length:
              format: int32
              type: integer
            allowedMethods:
              uniqueItems: true
              type: array
              items:
                type: string
            cookies:
              type: object
              additionalProperties:
                $ref: "#/components/schemas/NewCookie"
            entityTag:
              $ref: "#/components/schemas/EntityTag"
            date:
              format: date
              type: string
            lastModified:
              format: date
              type: string
            location:
              format: uri
              type: string
            links:
              uniqueItems: true
              type: array
              items:
                $ref: "#/components/schemas/Link"
            metadata:
              $ref: "#/components/schemas/MultivaluedMapStringObject"
            headers:
              $ref: "#/components/schemas/MultivaluedMapStringObject"
            stringHeaders:
              $ref: "#/components/schemas/MultivaluedMapStringString"
        SpecVersion:
          enum:
            - V03
            - V1
          type: string
        StatusType:
          type: object
          properties:
            statusCode:
              format: int32
              type: integer
            family:
              $ref: "#/components/schemas/Family"
            reasonPhrase:
              type: string
        TaskModel:
          type: object
          properties:
            id:
              type: string
            name:
              type: string
            parameters:
              type: object
            phase:
              type: string
            phaseStatus:
              type: string
            results:
              type: object
            state:
              format: int32
              type: integer
        UriBuilder:
          type: object
  `;
}
