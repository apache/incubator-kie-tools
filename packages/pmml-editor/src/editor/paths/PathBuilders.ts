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

export interface Path {
  readonly path: string;
}

class Builders {
  constructor(public readonly builders: BaseBuilder[]) {}

  public add(builder: BaseBuilder) {
    this.builders.push(builder);
  }
}

abstract class BaseBuilder {
  constructor(protected readonly builders: Builders) {}

  protected abstract segment(): string;

  public build(): Path {
    const path = this.builders.builders
      .map((builder) => builder.segment())
      .filter((segment) => segment !== "")
      .join(".");
    return { path: path };
  }
}

export const Builder = (): PMMLBuilder => {
  return new PMMLBuilder();
};

class PMMLBuilder extends BaseBuilder {
  constructor() {
    super(new Builders([]));
    this.builders.add(this);
  }

  public forHeader = () => {
    return new HeaderBuilder(this.builders);
  };

  public forDataDictionary = () => {
    return new DataDictionaryBuilder(this.builders);
  };

  public forModel = (modelIndex?: number): ModelBuilder => {
    return new ModelBuilder(this.builders, modelIndex);
  };

  protected segment(): string {
    return "";
  }
}

class ModelBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly modelIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forBaselineScore = () => {
    return new BaselineScoreBuilder(this.builders);
  };

  public forUseReasonCodes = () => {
    return new UseReasonCodesBuilder(this.builders);
  };

  public forCharacteristics = () => {
    return new CharacteristicsBuilder(this.builders);
  };

  public forMiningSchema = () => {
    return new MiningSchemaBuilder(this.builders);
  };

  public forOutput = () => {
    return new OutputBuilder(this.builders);
  };

  protected segment(): string {
    return this.modelIndex !== undefined ? `models[${this.modelIndex}]` : `models`;
  }
}

class HeaderBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `Header`;
  }
}

class DataDictionaryBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  public forDataField = (dataFieldIndex?: number) => {
    return new DataFieldBuilder(this.builders, dataFieldIndex);
  };

  protected segment(): string {
    return `DataDictionary`;
  }
}

class DataFieldBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly dataFieldIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forInterval = (intervalIndex?: number) => {
    return new IntervalBuilder(this.builders, intervalIndex);
  };

  public forValue = (valueIndex?: number) => {
    return new ValueBuilder(this.builders, valueIndex);
  };

  protected segment(): string {
    return this.dataFieldIndex !== undefined ? `DataField[${this.dataFieldIndex}]` : `DataField`;
  }
}

class IntervalBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly intervalIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return this.intervalIndex !== undefined ? `Interval[${this.intervalIndex}]` : `Interval`;
  }
}

class ValueBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly valueIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return this.valueIndex !== undefined ? `Value[${this.valueIndex}]` : `Value`;
  }
}

class CharacteristicsBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  public forCharacteristic = (characteristicIndex?: number) => {
    return new CharacteristicBuilder(this.builders, characteristicIndex);
  };

  protected segment(): string {
    return `Characteristics`;
  }
}

class CharacteristicBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly characteristicIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forReasonCode = () => {
    return new ReasonCodeBuilder(this.builders);
  };

  public forBaselineScore = () => {
    return new BaselineScoreBuilder(this.builders);
  };

  public forAttribute = (attributeIndex?: number) => {
    return new AttributeBuilder(this.builders, attributeIndex);
  };

  protected segment(): string {
    return this.characteristicIndex !== undefined ? `Characteristic[${this.characteristicIndex}]` : `Characteristic`;
  }
}

class ReasonCodeBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `reasonCode`;
  }
}

class BaselineScoreBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `baselineScore`;
  }
}

class UseReasonCodesBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `useReasonCodes`;
  }
}

class AttributeBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly attributeIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forPredicate = (predicateIndex?: number) => {
    return new PredicateBuilder(this.builders, predicateIndex);
  };

  public forReasonCode = () => {
    return new ReasonCodeBuilder(this.builders);
  };

  public forPartialScore = () => {
    return new PartialScoreBuilder(this.builders);
  };

  protected segment(): string {
    return this.attributeIndex !== undefined ? `Attribute[${this.attributeIndex}]` : `Attribute`;
  }
}

class PartialScoreBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `partialScore`;
  }
}

class PredicateBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly predicateIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forFieldName = () => {
    return new FieldNameBuilder(this.builders);
  };

  protected segment(): string {
    return this.predicateIndex !== undefined ? `predicate[${this.predicateIndex}]` : `predicate`;
  }
}

class FieldNameBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `fieldName`;
  }
}

class MiningSchemaBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  public forMiningField = (miningFieldIndex?: number) => {
    return new MiningFieldBuilder(this.builders, miningFieldIndex);
  };

  protected segment(): string {
    return `MiningSchema`;
  }
}

class MiningFieldBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly miningFieldIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forImportance = () => {
    return new MiningFieldImportanceBuilder(this.builders);
  };

  public forLowValue = () => {
    return new MiningFieldLowValueBuilder(this.builders);
  };

  public forHighValue = () => {
    return new MiningFieldHighValueBuilder(this.builders);
  };

  public forMissingValueReplacement = () => {
    return new MiningFieldMissingValueReplacementBuilder(this.builders);
  };

  public forInvalidValueReplacement = () => {
    return new MiningFieldInvalidValueReplacementBuilder(this.builders);
  };

  public forDataFieldMissing = () => {
    return new MiningFieldDataFieldMissingBuilder(this.builders);
  };

  protected segment(): string {
    return this.miningFieldIndex !== undefined ? `MiningField[${this.miningFieldIndex}]` : `MiningField`;
  }
}

class MiningFieldImportanceBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `importance`;
  }
}

class MiningFieldLowValueBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `lowValue`;
  }
}

class MiningFieldHighValueBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `highValue`;
  }
}

class MiningFieldMissingValueReplacementBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `missingValueReplacement`;
  }
}

class MiningFieldInvalidValueReplacementBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `invalidValueReplacement`;
  }
}

class MiningFieldDataFieldMissingBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `dataFieldMissing`;
  }
}

class OutputBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  public forOutputField = (outputFieldIndex?: number) => {
    return new OutputFieldBuilder(this.builders, outputFieldIndex);
  };

  protected segment(): string {
    return `Output`;
  }
}

class OutputFieldBuilder extends BaseBuilder {
  constructor(protected builders: Builders, private readonly outputFieldIndex?: number) {
    super(builders);
    this.builders.add(this);
  }

  public forTargetField = () => {
    return new OutputFieldTargetFieldBuilder(this.builders);
  };

  protected segment(): string {
    return this.outputFieldIndex !== undefined ? `OutputField[${this.outputFieldIndex}]` : `OutputField`;
  }
}

class OutputFieldTargetFieldBuilder extends BaseBuilder {
  constructor(protected builders: Builders) {
    super(builders);
    this.builders.add(this);
  }

  protected segment(): string {
    return `targetField`;
  }
}
