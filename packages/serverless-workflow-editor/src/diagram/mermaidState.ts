/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Specification } from "@severlessworkflow/sdk-typescript";

/**
 * Determine if the provided value is an object or a primitive type
 * @param value The data
 * @returns {boolean} True if the provided value is an object
 */
export const isObject = (value: any): boolean => {
  if (!value) return false;
  const type = typeof value;
  return type === "object";
};

export class MermaidState {
  constructor(
    private state: {
      name?: string;
      type?: string;
      transition?: string | Specification.Transition;
      end?: boolean | Specification.End;
      compensatedBy?: string;
      onErrors?: Specification.Error[];
      usedForCompensation?: boolean;
    },
    private isFirstState: boolean = false
  ) {}

  sourceCode() {
    const stateDefinition = this.definitions();
    const stateTransitions = this.transitions();

    const stateDescription = stateTransitions.reduce((p, c) => {
      return p + "\n" + c;
    }, stateDefinition);

    return stateDescription;
  }

  private definitions(): string {
    return (
      this.definitionName() +
      "\n" +
      this.definitionType() +
      (this.definitionDetails() !== undefined ? "\n" + this.definitionDetails() : "")
    );
  }

  private transitions(): string[] {
    const transitions: string[] = [];

    transitions.push(...this.startTransition());
    transitions.push(...this.dataConditionsTransitions());
    transitions.push(...this.eventConditionsTransition());
    transitions.push(...this.errorTransitions());
    transitions.push(...this.naturalTransition(this.stateKeyDiagram(this.state.name), this.state.transition));
    transitions.push(...this.compensatedByTransition());
    transitions.push(...this.endTransition());

    return transitions;
  }

  private stateKeyDiagram(name: string | undefined) {
    return name?.replace(/ /g, "_");
  }

  private startTransition() {
    const transitions: string[] = [];
    if (this.isFirstState) {
      const stateName = this.stateKeyDiagram(this.state.name);
      transitions.push(this.transitionDescription("[*]", stateName));
    }
    return transitions;
  }

  private dataConditionsTransitions() {
    const transitions: string[] = [];

    const dataBasedSwitchState = this.state as Specification.Databasedswitchstate;
    if (dataBasedSwitchState.dataConditions) {
      const stateName = this.state.name;
      dataBasedSwitchState.dataConditions.forEach((dataCondition) => {
        const transitionDataCondition = dataCondition as Specification.Transitiondatacondition;

        transitions.push(
          ...this.naturalTransition(stateName, transitionDataCondition.transition, transitionDataCondition.condition)
        );

        const endDataCondition = dataCondition as Specification.Enddatacondition;
        if (endDataCondition.end) {
          transitions.push(this.transitionDescription(stateName, "[*]", transitionDataCondition.condition));
        }
      });

      transitions.push(...this.defaultConditionTransition(dataBasedSwitchState));
    }
    return transitions;
  }

  private eventConditionsTransition() {
    const transitions: string[] = [];

    const eventBasedSwitchState = this.state as Specification.Eventbasedswitchstate;
    if (eventBasedSwitchState.eventConditions) {
      const stateName = this.state.name;
      eventBasedSwitchState.eventConditions.forEach((eventCondition) => {
        const transitionEventCondition = eventCondition as Specification.Transitioneventcondition;

        transitions.push(
          ...this.naturalTransition(stateName, transitionEventCondition.transition, transitionEventCondition.eventRef)
        );

        const endEventCondition = eventCondition as Specification.Enddeventcondition;
        if (endEventCondition.end) {
          transitions.push(this.transitionDescription(stateName, "[*]"));
        }
      });

      transitions.push(...this.defaultConditionTransition(eventBasedSwitchState));
    }
    return transitions;
  }

  private defaultConditionTransition(state: { defaultCondition?: Specification.Defaultconditiondef }) {
    const transitions: string[] = [];

    if (state.defaultCondition) {
      transitions.push(...this.naturalTransition(this.state.name, state.defaultCondition.transition, "default"));
    }
    return transitions;
  }

  private endTransition() {
    const transitions: string[] = [];

    if (this.state.end) {
      const stateName = this.state.name;
      let transitionLabel = undefined;

      if (isObject(this.state.end)) {
        const end = this.state.end as Specification.End;

        if (end.produceEvents) {
          transitionLabel = "Produced event = [" + end.produceEvents!.map((pe) => pe.eventRef).join(",") + "]";
        }
      }

      transitions.push(this.transitionDescription(stateName, "[*]", transitionLabel));
    }
    return transitions;
  }

  private naturalTransition(
    source?: string,
    target?: string | Specification.Transition,
    label: string | undefined = undefined
  ) {
    const transitions: string[] = [];

    if (target) {
      let descTransition = "";
      if (isObject(target)) {
        descTransition = (target as Specification.Transition).nextState;
      } else if (typeof target === "string") {
        descTransition = target;
      }
      transitions.push(this.transitionDescription(source, descTransition, label ? label : undefined));
    }
    return transitions;
  }

  private errorTransitions() {
    const transitions: string[] = [];

    if (this.state.onErrors) {
      this.state.onErrors.forEach((error) => {
        transitions.push(
          ...this.naturalTransition(this.stateKeyDiagram(this.state.name), error.transition, error.errorRef)
        );
      });
    }
    return transitions;
  }

  private compensatedByTransition() {
    const transitions: string[] = [];

    if (this.state.compensatedBy) {
      transitions.push(...this.naturalTransition(this.state.name, this.state.compensatedBy, "compensated by"));
    }
    return transitions;
  }

  private definitionDetails() {
    let definition: string | undefined;

    switch (this.state.type) {
      case "sleep":
        definition = this.sleepStateDetails();
        break;
      case "event":
        // NOTHING
        break;
      case "operation":
        definition = this.operationStateDetails();
        break;
      case "parallel":
        definition = this.parallelStateDetails();
        break;
      case "switch":
        const switchState: any = this.state;
        if (switchState.dataConditions) {
          definition = this.dataBasedSwitchStateDetails();
          break;
        }
        if (switchState.eventConditions) {
          definition = this.eventBasedSwitchStateDetails();
          break;
        }
        throw new Error(`Unexpected switch type; \n state value= ${JSON.stringify(this.state, null, 4)}`);
      case "inject":
        // NOTHING
        break;
      case "foreach":
        definition = this.foreachStateDetails();
        break;
      case "callback":
        definition = this.callbackStateDetails();
        break;
      default:
        throw new Error(`Unexpected type= ${this.state.type}; \n state value= ${JSON.stringify(this.state, null, 4)}`);
    }

    if (this.state.usedForCompensation) {
      definition = definition ? definition : "";
      definition = this.stateDescription(this.stateKeyDiagram(this.state.name), "usedForCompensation\n") + definition;
    }

    return definition ? definition : undefined;
  }

  private definitionType() {
    const type = this.state.type;
    return this.stateDescription(
      this.stateKeyDiagram(this.state.name),
      "type",
      type!.charAt(0).toUpperCase() + type!.slice(1) + " State"
    );
  }

  private parallelStateDetails(): string | undefined {
    const parallelState = this.state as Specification.Parallelstate;

    const descriptions: string[] = [];

    if (parallelState.completionType) {
      descriptions.push(
        this.stateDescription(this.stateKeyDiagram(this.state.name), "Completion type", parallelState.completionType)
      );
    }

    if (parallelState.branches) {
      descriptions.push(
        this.stateDescription(
          this.stateKeyDiagram(this.state.name),
          "Num. of branches",
          parallelState.branches?.length + ""
        )
      );
    }

    return descriptions.length > 0
      ? descriptions.reduce((p, c) => {
          return p + "\n" + c;
        })
      : undefined;
  }

  private eventBasedSwitchStateDetails() {
    return this.stateDescription(this.stateKeyDiagram(this.state.name), `Condition type`, `event-based`);
  }

  private dataBasedSwitchStateDetails() {
    return this.stateDescription(this.stateKeyDiagram(this.state.name), `Condition type`, `data-based`);
  }

  private operationStateDetails() {
    const state = this.state as Specification.Operationstate;

    const descriptions: string[] = [];

    if (state.actionMode) {
      descriptions.push(this.stateDescription(this.stateKeyDiagram(this.state.name), "Action mode", state.actionMode));
    }

    if (state.actions) {
      descriptions.push(
        this.stateDescription(this.stateKeyDiagram(this.state.name), "Num. of actions", state.actions?.length + "")
      );
    }

    return descriptions.length > 0
      ? descriptions.reduce((p, c) => {
          return p + "\n" + c;
        })
      : undefined;
  }

  private sleepStateDetails() {
    const state = this.state as Specification.Sleepstate;
    if (state.duration) {
      return this.stateDescription(this.stateKeyDiagram(this.state.name), "Duration", state.duration);
    }

    return undefined;
  }

  private foreachStateDetails() {
    const state = this.state as Specification.Foreachstate;

    const descriptions: string[] = [];

    if (state.inputCollection) {
      descriptions.push(
        this.stateDescription(this.stateKeyDiagram(this.state.name), "Input collection", state.inputCollection)
      );
    }

    if (state.actions) {
      descriptions.push(
        this.stateDescription(this.stateKeyDiagram(this.state.name), "Num. of actions", state.actions?.length + "")
      );
    }

    return descriptions.length > 0
      ? descriptions.reduce((p, c) => {
          return p + "\n" + c;
        })
      : undefined;
  }

  private callbackStateDetails() {
    const state = this.state as Specification.Callbackstate;

    const descriptions: string[] = [];

    if (state.action && state.action.functionRef) {
      const functionRef = state.action.functionRef;
      let functionRefDescription = "";
      if (isObject(functionRef)) {
        functionRefDescription = (functionRef as Specification.Functionref).refName;
      } else if (typeof functionRef === "string") {
        functionRefDescription = functionRef as string;
      }
      descriptions.push(
        this.stateDescription(this.stateKeyDiagram(this.state.name), "Callback function", functionRefDescription)
      );
    }

    if (state.eventRef) {
      descriptions.push(this.stateDescription(this.stateKeyDiagram(this.state.name), "Callback event", state.eventRef));
    }

    return descriptions.length > 0
      ? descriptions.reduce((p, c) => {
          return p + "\n" + c;
        })
      : undefined;
  }

  private definitionName() {
    return this.stateKeyDiagram(this.state.name) + " : " + this.state.name;
  }

  private transitionDescription(
    source: string | undefined,
    target: string | undefined,
    label: string | undefined = undefined
  ) {
    return this.stateKeyDiagram(source) + " --> " + this.stateKeyDiagram(target) + (label ? " : " + label : "");
  }

  private stateDescription(stateName: string | undefined, description: string, value?: string) {
    return stateName + ` : ${description}${value !== undefined ? " = " + value : ""}`;
  }
}
