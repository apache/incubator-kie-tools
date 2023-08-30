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

/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 2.24.612 on 2020-06-26 13:44:41.

export class PMMLObject {
  constructor(data: PMMLObject) {}
}

export class Expression extends PMMLObject {
  constructor(data: Expression) {
    super(data);
  }
}

export class Aggregate extends Expression {
  Extension?: Extension[];
  field: string;
  function: Function;
  groupField?: string;
  sqlWhere?: string;

  constructor(data: Aggregate) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.function = data.function;
    this.groupField = data.groupField;
    this.sqlWhere = data.sqlWhere;
  }
}

export class Annotation extends PMMLObject {
  Extension?: any[];

  constructor(data: Annotation) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class Anova extends PMMLObject {
  Extension?: Extension[];
  AnovaRow: AnovaRow[];
  target?: string;

  constructor(data: Anova) {
    super(data);
    this.Extension = data.Extension;
    this.AnovaRow = data.AnovaRow;
    this.target = data.target;
  }
}

export class AnovaRow extends PMMLObject {
  Extension?: Extension[];
  type: AnovaRowType;
  sumOfSquares: number;
  degreesOfFreedom: number;
  meanOfSquares?: number;
  fValue?: number;
  pValue?: number;

  constructor(data: AnovaRow) {
    super(data);
    this.Extension = data.Extension;
    this.type = data.type;
    this.sumOfSquares = data.sumOfSquares;
    this.degreesOfFreedom = data.degreesOfFreedom;
    this.meanOfSquares = data.meanOfSquares;
    this.fValue = data.fValue;
    this.pValue = data.pValue;
  }
}

export class Distribution extends PMMLObject {
  constructor(data: Distribution) {
    super(data);
  }
}

export class ContinuousDistribution extends Distribution {
  constructor(data: ContinuousDistribution) {
    super(data);
  }
}

export class AnyDistribution extends ContinuousDistribution {
  Extension?: Extension[];
  mean: number;
  variance: number;

  constructor(data: AnyDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.mean = data.mean;
    this.variance = data.variance;
  }
}

export class Application extends PMMLObject {
  Extension?: Extension[];
  name: string;
  version?: string;

  constructor(data: Application) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.version = data.version;
  }
}

export class Apply extends Expression {
  Extension?: Extension[];
  expressions?: Expression[];
  function: string;
  mapMissingTo?: string;
  defaultValue?: string;
  invalidValueTreatment?: InvalidValueTreatmentMethod;

  constructor(data: Apply) {
    super(data);
    this.Extension = data.Extension;
    this.expressions = data.expressions;
    this.function = data.function;
    this.mapMissingTo = data.mapMissingTo;
    this.defaultValue = data.defaultValue;
    this.invalidValueTreatment = data.invalidValueTreatment;
  }
}

export class Array extends PMMLObject {
  value?: any;
  n?: number;
  type: ArrayType;

  constructor(data: Array) {
    super(data);
    this.value = data.value;
    this.n = data.n;
    this.type = data.type;
  }
}

export class Measure extends PMMLObject {
  constructor(data: Measure) {
    super(data);
  }
}

export class Similarity extends Measure {
  constructor(data: Similarity) {
    super(data);
  }
}

export class BinarySimilarity extends Similarity {
  Extension?: Extension[];
  "c00-parameter": number;
  "c01-parameter": number;
  "c10-parameter": number;
  "c11-parameter": number;
  "d00-parameter": number;
  "d01-parameter": number;
  "d10-parameter": number;
  "d11-parameter": number;

  constructor(data: BinarySimilarity) {
    super(data);
    this.Extension = data.Extension;
    this["c00-parameter"] = data["c00-parameter"];
    this["c01-parameter"] = data["c01-parameter"];
    this["c10-parameter"] = data["c10-parameter"];
    this["c11-parameter"] = data["c11-parameter"];
    this["d00-parameter"] = data["d00-parameter"];
    this["d01-parameter"] = data["d01-parameter"];
    this["d10-parameter"] = data["d10-parameter"];
    this["d11-parameter"] = data["d11-parameter"];
  }
}

export class BlockIndicator extends PMMLObject {
  Extension?: Extension[];
  field: string;

  constructor(data: BlockIndicator) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
  }
}

export class BoundaryValueMeans extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: BoundaryValueMeans) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class BoundaryValues extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: BoundaryValues) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class Cell extends PMMLObject {
  value?: any;

  constructor(data: Cell) {
    super(data);
    this.value = data.value;
  }
}

export class Distance extends Measure {
  constructor(data: Distance) {
    super(data);
  }
}

export class Chebychev extends Distance {
  Extension?: Extension[];

  constructor(data: Chebychev) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class ChildParent extends PMMLObject {
  Extension?: Extension[];
  FieldColumnPair?: FieldColumnPair[];
  TableLocator?: TableLocator;
  InlineTable?: InlineTable;
  childField: string;
  parentField: string;
  parentLevelField?: string;
  isRecursive?: Recursive;

  constructor(data: ChildParent) {
    super(data);
    this.Extension = data.Extension;
    this.FieldColumnPair = data.FieldColumnPair;
    this.TableLocator = data.TableLocator;
    this.InlineTable = data.InlineTable;
    this.childField = data.childField;
    this.parentField = data.parentField;
    this.parentLevelField = data.parentLevelField;
    this.isRecursive = data.isRecursive;
  }
}

export class CityBlock extends Distance {
  Extension?: Extension[];

  constructor(data: CityBlock) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class ClassLabels extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: ClassLabels) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class ClusteringModelQuality extends PMMLObject {
  Extension?: Extension[];
  dataName?: string;
  SSE?: number;
  SSB?: number;

  constructor(data: ClusteringModelQuality) {
    super(data);
    this.Extension = data.Extension;
    this.dataName = data.dataName;
    this.SSE = data.SSE;
    this.SSB = data.SSB;
  }
}

export class ComparisonField<E> extends PMMLObject {
  constructor(data: ComparisonField<E>) {
    super(data);
  }
}

export class ComparisonMeasure extends PMMLObject {
  Extension?: Extension[];
  measure?: Measure;
  kind: ComparisonMeasureKind;
  compareFunction?: CompareFunction;
  minimum?: number;
  maximum?: number;

  constructor(data: ComparisonMeasure) {
    super(data);
    this.Extension = data.Extension;
    this.measure = data.measure;
    this.kind = data.kind;
    this.compareFunction = data.compareFunction;
    this.minimum = data.minimum;
    this.maximum = data.maximum;
  }
}

export class ComplexArray extends Array {
  constructor(data: ComplexArray) {
    super(data);
  }
}

export interface ComplexValue {}

export class Predicate extends PMMLObject {
  constructor(data: Predicate) {
    super(data);
  }
}

export class CompoundPredicate extends Predicate {
  Extension?: Extension[];
  predicates?: Predicate[];
  booleanOperator: CompoundPredicateBooleanOperator;

  constructor(data: CompoundPredicate) {
    super(data);
    this.Extension = data.Extension;
    this.predicates = data.predicates;
    this.booleanOperator = data.booleanOperator;
  }
}

export class ConfusionMatrix extends PMMLObject {
  Extension?: Extension[];
  ClassLabels: ClassLabels;
  Matrix: Matrix;

  constructor(data: ConfusionMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.ClassLabels = data.ClassLabels;
    this.Matrix = data.Matrix;
  }
}

export class Constant extends Expression {
  value?: any;
  dataType?: DataType;
  missing?: boolean;

  constructor(data: Constant) {
    super(data);
    this.value = data.value;
    this.dataType = data.dataType;
    this.missing = data.missing;
  }
}

export class ContStats extends PMMLObject {
  Extension?: Extension[];
  Interval?: Interval[];
  Array?: Array[];
  totalValuesSum?: number;
  totalSquaresSum?: number;

  constructor(data: ContStats) {
    super(data);
    this.Extension = data.Extension;
    this.Interval = data.Interval;
    this.Array = data.Array;
    this.totalValuesSum = data.totalValuesSum;
    this.totalSquaresSum = data.totalSquaresSum;
  }
}

export class CorrelationFields extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: CorrelationFields) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class CorrelationMethods extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: CorrelationMethods) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class CorrelationValues extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: CorrelationValues) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class Correlations extends PMMLObject {
  Extension?: Extension[];
  CorrelationFields: CorrelationFields;
  CorrelationValues: CorrelationValues;
  CorrelationMethods?: CorrelationMethods;

  constructor(data: Correlations) {
    super(data);
    this.Extension = data.Extension;
    this.CorrelationFields = data.CorrelationFields;
    this.CorrelationValues = data.CorrelationValues;
    this.CorrelationMethods = data.CorrelationMethods;
  }
}

export class Counts extends PMMLObject {
  Extension?: Extension[];
  totalFreq: number;
  missingFreq?: number;
  invalidFreq?: number;
  cardinality?: number;

  constructor(data: Counts) {
    super(data);
    this.Extension = data.Extension;
    this.totalFreq = data.totalFreq;
    this.missingFreq = data.missingFreq;
    this.invalidFreq = data.invalidFreq;
    this.cardinality = data.cardinality;
  }
}

export class DataDictionary extends PMMLObject {
  Extension?: Extension[];
  DataField: DataField[];
  Taxonomy?: Taxonomy[];
  numberOfFields?: number;

  constructor(data: DataDictionary) {
    super(data);
    this.Extension = data.Extension;
    this.DataField = data.DataField;
    this.Taxonomy = data.Taxonomy;
    this.numberOfFields = data.numberOfFields;
  }
}

export class Field<E> extends PMMLObject {
  constructor(data: Field<E>) {
    super(data);
  }
}

export class DataField extends Field<DataField> {
  Extension?: Extension[];
  Interval?: Interval[];
  Value?: Value[];
  name: string;
  displayName?: string;
  optype: OpType;
  dataType: DataType;
  taxonomy?: string;
  isCyclic?: Cyclic;

  constructor(data: DataField) {
    super(data);
    this.Extension = data.Extension;
    this.Interval = data.Interval;
    this.Value = data.Value;
    this.name = data.name;
    this.displayName = data.displayName;
    this.optype = data.optype;
    this.dataType = data.dataType;
    this.taxonomy = data.taxonomy;
    this.isCyclic = data.isCyclic;
  }
}

export class Decision extends PMMLObject {
  Extension?: Extension[];
  value: string;
  displayValue?: string;
  description?: string;

  constructor(data: Decision) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.displayValue = data.displayValue;
    this.description = data.description;
  }
}

export class Decisions extends PMMLObject {
  Extension?: Extension[];
  Decision: Decision[];
  businessProblem?: string;
  description?: string;

  constructor(data: Decisions) {
    super(data);
    this.Extension = data.Extension;
    this.Decision = data.Decision;
    this.businessProblem = data.businessProblem;
    this.description = data.description;
  }
}

export class DefineFunction extends PMMLObject {
  Extension?: Extension[];
  ParameterField: ParameterField[];
  expression?: Expression;
  name: string;
  optype: OpType;
  dataType?: DataType;

  constructor(data: DefineFunction) {
    super(data);
    this.Extension = data.Extension;
    this.ParameterField = data.ParameterField;
    this.expression = data.expression;
    this.name = data.name;
    this.optype = data.optype;
    this.dataType = data.dataType;
  }
}

export class DerivedField extends Field<DerivedField> {
  Extension?: Extension[];
  expression?: Expression;
  Interval?: Interval[];
  Value?: Value[];
  name?: string;
  displayName?: string;
  optype: OpType;
  dataType: DataType;

  constructor(data: DerivedField) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
    this.Interval = data.Interval;
    this.Value = data.Value;
    this.name = data.name;
    this.displayName = data.displayName;
    this.optype = data.optype;
    this.dataType = data.dataType;
  }
}

export class DiscrStats extends PMMLObject {
  Extension?: Extension[];
  Array?: Array[];
  modalValue?: string;

  constructor(data: DiscrStats) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.modalValue = data.modalValue;
  }
}

export class DiscreteDistribution extends Distribution {
  constructor(data: DiscreteDistribution) {
    super(data);
  }
}

export class Discretize extends Expression {
  Extension?: Extension[];
  DiscretizeBin?: DiscretizeBin[];
  field: string;
  mapMissingTo?: any;
  defaultValue?: any;
  dataType?: DataType;

  constructor(data: Discretize) {
    super(data);
    this.Extension = data.Extension;
    this.DiscretizeBin = data.DiscretizeBin;
    this.field = data.field;
    this.mapMissingTo = data.mapMissingTo;
    this.defaultValue = data.defaultValue;
    this.dataType = data.dataType;
  }
}

export class DiscretizeBin extends PMMLObject {
  Extension?: Extension[];
  Interval: Interval;
  binValue: any;

  constructor(data: DiscretizeBin) {
    super(data);
    this.Extension = data.Extension;
    this.Interval = data.Interval;
    this.binValue = data.binValue;
  }
}

export class EmbeddedModel extends PMMLObject {
  constructor(data: EmbeddedModel) {
    super(data);
  }
}

export class Entity<V> extends PMMLObject {
  constructor(data: Entity<V>) {
    super(data);
  }
}

export class Euclidean extends Distance {
  Extension?: Extension[];

  constructor(data: Euclidean) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class Extension extends PMMLObject {
  content?: any[];
  extender?: string;
  name?: string;
  value?: string;

  constructor(data: Extension) {
    super(data);
    this.content = data.content;
    this.extender = data.extender;
    this.name = data.name;
    this.value = data.value;
  }
}

export class False extends Predicate {
  Extension?: Extension[];

  constructor(data: False) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class FieldColumnPair extends PMMLObject {
  Extension?: Extension[];
  field: string;
  column: string;

  constructor(data: FieldColumnPair) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.column = data.column;
  }
}
export class FieldRef extends Expression {
  Extension?: Extension[];
  field: string;
  mapMissingTo?: string;

  constructor(data: FieldRef) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.mapMissingTo = data.mapMissingTo;
  }
}

export class GaussianDistribution extends ContinuousDistribution {
  Extension?: Extension[];
  mean: number;
  variance: number;

  constructor(data: GaussianDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.mean = data.mean;
    this.variance = data.variance;
  }
}

export class Header extends PMMLObject {
  Extension?: Extension[];
  Application?: Application;
  Annotation?: Annotation[];
  Timestamp?: Timestamp;
  copyright?: string;
  description?: string;
  modelVersion?: string;

  constructor(data: Header) {
    super(data);
    this.Extension = data.Extension;
    this.Application = data.Application;
    this.Annotation = data.Annotation;
    this.Timestamp = data.Timestamp;
    this.copyright = data.copyright;
    this.description = data.description;
    this.modelVersion = data.modelVersion;
  }
}

export class InlineTable extends PMMLObject {
  Extension?: Extension[];
  row?: Row[];

  constructor(data: InlineTable) {
    super(data);
    this.Extension = data.Extension;
    this.row = data.row;
  }
}

export class SparseArray<E> extends PMMLObject {
  constructor(data: SparseArray<E>) {
    super(data);
  }
}

export class IntSparseArray extends SparseArray<number> {
  Indices?: number[];
  "INT-Entries"?: number[];
  n?: number;
  defaultValue?: number;

  constructor(data: IntSparseArray) {
    super(data);
    this.Indices = data.Indices;
    this["INT-Entries"] = data["INT-Entries"];
    this.n = data.n;
    this.defaultValue = data.defaultValue;
  }
}

export class Interval extends PMMLObject {
  Extension?: Extension[];
  closure: Closure;
  leftMargin?: number;
  rightMargin?: number;

  constructor(data: Interval) {
    super(data);
    this.Extension = data.Extension;
    this.closure = data.closure;
    this.leftMargin = data.leftMargin;
    this.rightMargin = data.rightMargin;
  }
}

export class Jaccard extends Similarity {
  Extension?: Extension[];

  constructor(data: Jaccard) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class Lag extends Expression {
  Extension?: Extension[];
  BlockIndicator?: BlockIndicator[];
  field: string;
  n?: number;
  aggregate?: string;

  constructor(data: Lag) {
    super(data);
    this.Extension = data.Extension;
    this.BlockIndicator = data.BlockIndicator;
    this.field = data.field;
    this.n = data.n;
    this.aggregate = data.aggregate;
  }
}

export class LiftData extends PMMLObject {
  Extension?: Extension[];
  ModelLiftGraph: ModelLiftGraph;
  OptimumLiftGraph?: OptimumLiftGraph;
  RandomLiftGraph?: RandomLiftGraph;
  targetFieldValue?: string;
  targetFieldDisplayValue?: string;
  rankingQuality?: number;

  constructor(data: LiftData) {
    super(data);
    this.Extension = data.Extension;
    this.ModelLiftGraph = data.ModelLiftGraph;
    this.OptimumLiftGraph = data.OptimumLiftGraph;
    this.RandomLiftGraph = data.RandomLiftGraph;
    this.targetFieldValue = data.targetFieldValue;
    this.targetFieldDisplayValue = data.targetFieldDisplayValue;
    this.rankingQuality = data.rankingQuality;
  }
}

export class LiftGraph extends PMMLObject {
  Extension?: Extension[];
  XCoordinates: XCoordinates;
  YCoordinates: YCoordinates;
  BoundaryValues?: BoundaryValues;
  BoundaryValueMeans?: BoundaryValueMeans;

  constructor(data: LiftGraph) {
    super(data);
    this.Extension = data.Extension;
    this.XCoordinates = data.XCoordinates;
    this.YCoordinates = data.YCoordinates;
    this.BoundaryValues = data.BoundaryValues;
    this.BoundaryValueMeans = data.BoundaryValueMeans;
  }
}

export class LinearNorm extends PMMLObject {
  Extension?: Extension[];
  orig: number;
  norm: number;

  constructor(data: LinearNorm) {
    super(data);
    this.Extension = data.Extension;
    this.orig = data.orig;
    this.norm = data.norm;
  }
}

export class LocalTransformations extends PMMLObject {
  Extension?: Extension[];
  DerivedField?: DerivedField[];

  constructor(data: LocalTransformations) {
    super(data);
    this.Extension = data.Extension;
    this.DerivedField = data.DerivedField;
  }
}

export class MapValues extends Expression {
  Extension?: Extension[];
  FieldColumnPair?: FieldColumnPair[];
  TableLocator?: TableLocator;
  InlineTable?: InlineTable;
  mapMissingTo?: any;
  defaultValue?: any;
  outputColumn: string;
  dataType?: DataType;

  constructor(data: MapValues) {
    super(data);
    this.Extension = data.Extension;
    this.FieldColumnPair = data.FieldColumnPair;
    this.TableLocator = data.TableLocator;
    this.InlineTable = data.InlineTable;
    this.mapMissingTo = data.mapMissingTo;
    this.defaultValue = data.defaultValue;
    this.outputColumn = data.outputColumn;
    this.dataType = data.dataType;
  }
}

export class MatCell extends PMMLObject {
  value?: any;
  row: number;
  col: number;

  constructor(data: MatCell) {
    super(data);
    this.value = data.value;
    this.row = data.row;
    this.col = data.col;
  }
}

export class Matrix extends PMMLObject {
  Array?: Array[];
  MatCell?: MatCell[];
  kind?: MatrixKind;
  nbRows?: number;
  nbCols?: number;
  diagDefault?: number;
  offDiagDefault?: number;

  constructor(data: Matrix) {
    super(data);
    this.Array = data.Array;
    this.MatCell = data.MatCell;
    this.kind = data.kind;
    this.nbRows = data.nbRows;
    this.nbCols = data.nbCols;
    this.diagDefault = data.diagDefault;
    this.offDiagDefault = data.offDiagDefault;
  }
}

export class MiningBuildTask extends PMMLObject {
  Extension?: Extension[];

  constructor(data: MiningBuildTask) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class MiningField extends PMMLObject {
  Extension?: Extension[];
  name: string;
  usageType?: UsageType;
  optype?: OpType;
  importance?: number;
  outliers?: OutlierTreatmentMethod;
  lowValue?: number;
  highValue?: number;
  missingValueReplacement?: any;
  missingValueTreatment?: MissingValueTreatmentMethod;
  invalidValueTreatment?: InvalidValueTreatmentMethod;
  invalidValueReplacement?: any;

  constructor(data: MiningField) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.usageType = data.usageType;
    this.optype = data.optype;
    this.importance = data.importance;
    this.outliers = data.outliers;
    this.lowValue = data.lowValue;
    this.highValue = data.highValue;
    this.missingValueReplacement = data.missingValueReplacement;
    this.missingValueTreatment = data.missingValueTreatment;
    this.invalidValueTreatment = data.invalidValueTreatment;
    this.invalidValueReplacement = data.invalidValueReplacement;
  }
}

export class MiningSchema extends PMMLObject {
  Extension?: Extension[];
  MiningField: MiningField[];

  constructor(data: MiningSchema) {
    super(data);
    this.Extension = data.Extension;
    this.MiningField = data.MiningField;
  }
}

export class Minkowski extends Distance {
  Extension?: Extension[];
  "p-parameter": number;

  constructor(data: Minkowski) {
    super(data);
    this.Extension = data.Extension;
    this["p-parameter"] = data["p-parameter"];
  }
}

export class Model extends PMMLObject {
  constructor(data: Model) {
    super(data);
  }
}

export class ModelExplanation extends PMMLObject {
  Extension?: Extension[];
  PredictiveModelQuality?: PredictiveModelQuality[];
  ClusteringModelQuality?: ClusteringModelQuality[];
  Correlations?: Correlations;

  constructor(data: ModelExplanation) {
    super(data);
    this.Extension = data.Extension;
    this.PredictiveModelQuality = data.PredictiveModelQuality;
    this.ClusteringModelQuality = data.ClusteringModelQuality;
    this.Correlations = data.Correlations;
  }
}

export class ModelLiftGraph extends PMMLObject {
  Extension?: Extension[];
  LiftGraph: LiftGraph;

  constructor(data: ModelLiftGraph) {
    super(data);
    this.Extension = data.Extension;
    this.LiftGraph = data.LiftGraph;
  }
}

export class ModelStats extends PMMLObject {
  Extension?: Extension[];
  UnivariateStats?: UnivariateStats[];
  MultivariateStats?: MultivariateStats[];

  constructor(data: ModelStats) {
    super(data);
    this.Extension = data.Extension;
    this.UnivariateStats = data.UnivariateStats;
    this.MultivariateStats = data.MultivariateStats;
  }
}

export class ModelVerification extends PMMLObject {
  Extension?: Extension[];
  VerificationFields: VerificationFields;
  InlineTable: InlineTable;
  recordCount?: number;
  fieldCount?: number;

  constructor(data: ModelVerification) {
    super(data);
    this.Extension = data.Extension;
    this.VerificationFields = data.VerificationFields;
    this.InlineTable = data.InlineTable;
    this.recordCount = data.recordCount;
    this.fieldCount = data.fieldCount;
  }
}

export class MultivariateStat extends PMMLObject {
  Extension?: Extension[];
  name?: string;
  category?: any;
  exponent?: number;
  isIntercept?: boolean;
  importance?: number;
  stdError?: number;
  tValue?: number;
  chiSquareValue?: number;
  fStatistic?: number;
  dF?: number;
  pValueAlpha?: number;
  pValueInitial?: number;
  pValueFinal?: number;
  confidenceLevel?: number;
  confidenceLowerBound?: number;
  confidenceUpperBound?: number;

  constructor(data: MultivariateStat) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.category = data.category;
    this.exponent = data.exponent;
    this.isIntercept = data.isIntercept;
    this.importance = data.importance;
    this.stdError = data.stdError;
    this.tValue = data.tValue;
    this.chiSquareValue = data.chiSquareValue;
    this.fStatistic = data.fStatistic;
    this.dF = data.dF;
    this.pValueAlpha = data.pValueAlpha;
    this.pValueInitial = data.pValueInitial;
    this.pValueFinal = data.pValueFinal;
    this.confidenceLevel = data.confidenceLevel;
    this.confidenceLowerBound = data.confidenceLowerBound;
    this.confidenceUpperBound = data.confidenceUpperBound;
  }
}

export class MultivariateStats extends PMMLObject {
  Extension?: Extension[];
  MultivariateStat: MultivariateStat[];
  targetCategory?: any;

  constructor(data: MultivariateStats) {
    super(data);
    this.Extension = data.Extension;
    this.MultivariateStat = data.MultivariateStat;
    this.targetCategory = data.targetCategory;
  }
}

export class NormContinuous extends Expression {
  Extension?: Extension[];
  LinearNorm: LinearNorm[];
  mapMissingTo?: number;
  field: string;
  outliers?: OutlierTreatmentMethod;

  constructor(data: NormContinuous) {
    super(data);
    this.Extension = data.Extension;
    this.LinearNorm = data.LinearNorm;
    this.mapMissingTo = data.mapMissingTo;
    this.field = data.field;
    this.outliers = data.outliers;
  }
}

export class NormDiscrete extends Expression {
  Extension?: Extension[];
  field: string;
  method?: Method;
  value: any;
  mapMissingTo?: number;

  constructor(data: NormDiscrete) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.method = data.method;
    this.value = data.value;
    this.mapMissingTo = data.mapMissingTo;
  }
}

export class NumericInfo extends PMMLObject {
  Extension?: Extension[];
  Quantile?: Quantile[];
  minimum?: number;
  maximum?: number;
  mean?: number;
  standardDeviation?: number;
  median?: number;
  interQuartileRange?: number;

  constructor(data: NumericInfo) {
    super(data);
    this.Extension = data.Extension;
    this.Quantile = data.Quantile;
    this.minimum = data.minimum;
    this.maximum = data.maximum;
    this.mean = data.mean;
    this.standardDeviation = data.standardDeviation;
    this.median = data.median;
    this.interQuartileRange = data.interQuartileRange;
  }
}

export class OptimumLiftGraph extends PMMLObject {
  Extension?: Extension[];
  LiftGraph: LiftGraph;

  constructor(data: OptimumLiftGraph) {
    super(data);
    this.Extension = data.Extension;
    this.LiftGraph = data.LiftGraph;
  }
}

export class Output extends PMMLObject {
  Extension?: Extension[];
  OutputField: OutputField[];

  constructor(data: Output) {
    super(data);
    this.Extension = data.Extension;
    this.OutputField = data.OutputField;
  }
}

export class OutputField extends Field<OutputField> {
  Extension?: Extension[];
  Decisions?: Decisions;
  expression?: Expression;
  Value?: Value[];
  name: string;
  displayName?: string;
  optype?: OpType;
  dataType: DataType;
  targetField?: string;
  "x-reportField"?: string;
  feature?: ResultFeature;
  value?: any;
  ruleFeature?: RuleFeature;
  algorithm?: OutputFieldAlgorithm;
  rank?: number;
  rankBasis?: RankBasis;
  rankOrder?: RankOrder;
  isMultiValued?: string;
  segmentId?: string;
  isFinalResult?: boolean;

  constructor(data: OutputField) {
    super(data);
    this.Extension = data.Extension;
    this.Decisions = data.Decisions;
    this.expression = data.expression;
    this.Value = data.Value;
    this.name = data.name;
    this.displayName = data.displayName;
    this.optype = data.optype;
    this.dataType = data.dataType;
    this.targetField = data.targetField;
    this["x-reportField"] = data["x-reportField"];
    this.feature = data.feature;
    this.value = data.value;
    this.ruleFeature = data.ruleFeature;
    this.algorithm = data.algorithm;
    this.rank = data.rank;
    this.rankBasis = data.rankBasis;
    this.rankOrder = data.rankOrder;
    this.isMultiValued = data.isMultiValued;
    this.segmentId = data.segmentId;
    this.isFinalResult = data.isFinalResult;
  }
}

export class PMML extends PMMLObject {
  Header: Header;
  MiningBuildTask?: MiningBuildTask;
  DataDictionary: DataDictionary;
  TransformationDictionary?: TransformationDictionary;
  models?: Model[];
  Extension?: Extension[];
  version: string;
  "x-baseVersion"?: string;

  constructor(data: PMML) {
    super(data);
    this.Header = data.Header;
    this.MiningBuildTask = data.MiningBuildTask;
    this.DataDictionary = data.DataDictionary;
    this.TransformationDictionary = data.TransformationDictionary;
    this.models = data.models;
    this.Extension = data.Extension;
    this.version = data.version;
    this["x-baseVersion"] = data["x-baseVersion"];
  }
}

export interface PMMLFunctions {}

export class ParameterField extends Field<ParameterField> {
  name: string;
  optype?: OpType;
  dataType?: DataType;
  displayName?: string;

  constructor(data: ParameterField) {
    super(data);
    this.name = data.name;
    this.optype = data.optype;
    this.dataType = data.dataType;
    this.displayName = data.displayName;
  }
}

export class Partition extends PMMLObject {
  Extension?: Extension[];
  PartitionFieldStats?: PartitionFieldStats[];
  name: string;
  size?: number;

  constructor(data: Partition) {
    super(data);
    this.Extension = data.Extension;
    this.PartitionFieldStats = data.PartitionFieldStats;
    this.name = data.name;
    this.size = data.size;
  }
}

export class PartitionFieldStats extends PMMLObject {
  Extension?: Extension[];
  Counts?: Counts;
  NumericInfo?: NumericInfo;
  Array?: Array[];
  field: string;
  weighted?: PartitionFieldStatsWeighted;

  constructor(data: PartitionFieldStats) {
    super(data);
    this.Extension = data.Extension;
    this.Counts = data.Counts;
    this.NumericInfo = data.NumericInfo;
    this.Array = data.Array;
    this.field = data.field;
    this.weighted = data.weighted;
  }
}

export class PoissonDistribution extends ContinuousDistribution {
  Extension?: Extension[];
  mean: number;

  constructor(data: PoissonDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.mean = data.mean;
  }
}

export class PredictiveModelQuality extends PMMLObject {
  Extension?: Extension[];
  ConfusionMatrix?: ConfusionMatrix;
  LiftData?: LiftData[];
  ROC?: ROC;
  targetField: string;
  dataName?: string;
  dataUsage?: DataUsage;
  meanError?: number;
  meanAbsoluteError?: number;
  meanSquaredError?: number;
  rootMeanSquaredError?: number;
  "r-squared"?: number;
  "adj-r-squared"?: number;
  sumSquaredError?: number;
  sumSquaredRegression?: number;
  numOfRecords?: number;
  numOfRecordsWeighted?: number;
  numOfPredictors?: number;
  degreesOfFreedom?: number;
  fStatistic?: number;
  AIC?: number;
  BIC?: number;
  AICc?: number;

  constructor(data: PredictiveModelQuality) {
    super(data);
    this.Extension = data.Extension;
    this.ConfusionMatrix = data.ConfusionMatrix;
    this.LiftData = data.LiftData;
    this.ROC = data.ROC;
    this.targetField = data.targetField;
    this.dataName = data.dataName;
    this.dataUsage = data.dataUsage;
    this.meanError = data.meanError;
    this.meanAbsoluteError = data.meanAbsoluteError;
    this.meanSquaredError = data.meanSquaredError;
    this.rootMeanSquaredError = data.rootMeanSquaredError;
    this["r-squared"] = data["r-squared"];
    this["adj-r-squared"] = data["adj-r-squared"];
    this.sumSquaredError = data.sumSquaredError;
    this.sumSquaredRegression = data.sumSquaredRegression;
    this.numOfRecords = data.numOfRecords;
    this.numOfRecordsWeighted = data.numOfRecordsWeighted;
    this.numOfPredictors = data.numOfPredictors;
    this.degreesOfFreedom = data.degreesOfFreedom;
    this.fStatistic = data.fStatistic;
    this.AIC = data.AIC;
    this.BIC = data.BIC;
    this.AICc = data.AICc;
  }
}

export class Quantile extends PMMLObject {
  Extension?: Extension[];
  quantileLimit: number;
  quantileValue: number;

  constructor(data: Quantile) {
    super(data);
    this.Extension = data.Extension;
    this.quantileLimit = data.quantileLimit;
    this.quantileValue = data.quantileValue;
  }
}

export class ROC extends PMMLObject {
  Extension?: Extension[];
  ROCGraph: ROCGraph;
  positiveTargetFieldValue: string;
  positiveTargetFieldDisplayValue?: string;
  negativeTargetFieldValue?: string;
  negativeTargetFieldDisplayValue?: string;

  constructor(data: ROC) {
    super(data);
    this.Extension = data.Extension;
    this.ROCGraph = data.ROCGraph;
    this.positiveTargetFieldValue = data.positiveTargetFieldValue;
    this.positiveTargetFieldDisplayValue = data.positiveTargetFieldDisplayValue;
    this.negativeTargetFieldValue = data.negativeTargetFieldValue;
    this.negativeTargetFieldDisplayValue = data.negativeTargetFieldDisplayValue;
  }
}

export class ROCGraph extends PMMLObject {
  Extension?: Extension[];
  XCoordinates: XCoordinates;
  YCoordinates: YCoordinates;
  BoundaryValues?: BoundaryValues;

  constructor(data: ROCGraph) {
    super(data);
    this.Extension = data.Extension;
    this.XCoordinates = data.XCoordinates;
    this.YCoordinates = data.YCoordinates;
    this.BoundaryValues = data.BoundaryValues;
  }
}

export class RandomLiftGraph extends PMMLObject {
  Extension?: Extension[];
  LiftGraph: LiftGraph;

  constructor(data: RandomLiftGraph) {
    super(data);
    this.Extension = data.Extension;
    this.LiftGraph = data.LiftGraph;
  }
}

export class RealSparseArray extends SparseArray<number> {
  Indices?: number[];
  "REAL-Entries"?: number[];
  n?: number;
  defaultValue?: number;

  constructor(data: RealSparseArray) {
    super(data);
    this.Indices = data.Indices;
    this["REAL-Entries"] = data["REAL-Entries"];
    this.n = data.n;
    this.defaultValue = data.defaultValue;
  }
}

export class ResultField extends Field<ResultField> {
  Extension?: Extension[];
  name: string;
  displayName?: string;
  optype?: OpType;
  dataType?: DataType;
  feature?: ResultFeature;
  value?: any;

  constructor(data: ResultField) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.displayName = data.displayName;
    this.optype = data.optype;
    this.dataType = data.dataType;
    this.feature = data.feature;
    this.value = data.value;
  }
}

export class Row extends PMMLObject {
  content?: any[];

  constructor(data: Row) {
    super(data);
    this.content = data.content;
  }
}

export class ScoreDistribution extends PMMLObject {
  Extension?: Extension[];
  value: any;
  recordCount: number;
  confidence?: number;
  probability?: number;

  constructor(data: ScoreDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.recordCount = data.recordCount;
    this.confidence = data.confidence;
    this.probability = data.probability;
  }
}

export class SimpleMatching extends Similarity {
  Extension?: Extension[];

  constructor(data: SimpleMatching) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class SimplePredicate extends Predicate {
  Extension?: Extension[];
  field: string;
  operator: SimplePredicateOperator;
  value?: any;

  constructor(data: SimplePredicate) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.operator = data.operator;
    this.value = data.value;
  }
}

export class SimpleSetPredicate extends Predicate {
  Extension?: Extension[];
  Array: Array;
  field: string;
  booleanOperator: SimpleSetPredicate$BooleanOperator;

  constructor(data: SimpleSetPredicate) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.field = data.field;
    this.booleanOperator = data.booleanOperator;
  }
}

export class SquaredEuclidean extends Distance {
  Extension?: Extension[];

  constructor(data: SquaredEuclidean) {
    super(data);
    this.Extension = data.Extension;
  }
}

export interface StringValue<E> {}

export class TableLocator extends PMMLObject {
  Extension?: Extension[];

  constructor(data: TableLocator) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class Tanimoto extends Similarity {
  Extension?: Extension[];

  constructor(data: Tanimoto) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class Target extends PMMLObject {
  Extension?: Extension[];
  TargetValue?: TargetValue[];
  field?: string;
  optype?: OpType;
  castInteger?: CastInteger;
  min?: number;
  max?: number;
  rescaleConstant?: number;
  rescaleFactor?: number;

  constructor(data: Target) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValue = data.TargetValue;
    this.field = data.field;
    this.optype = data.optype;
    this.castInteger = data.castInteger;
    this.min = data.min;
    this.max = data.max;
    this.rescaleConstant = data.rescaleConstant;
    this.rescaleFactor = data.rescaleFactor;
  }
}

export class TargetValue extends PMMLObject {
  Extension?: Extension[];
  Partition?: Partition;
  value?: any;
  displayValue?: string;
  priorProbability?: number;
  defaultValue?: number;

  constructor(data: TargetValue) {
    super(data);
    this.Extension = data.Extension;
    this.Partition = data.Partition;
    this.value = data.value;
    this.displayValue = data.displayValue;
    this.priorProbability = data.priorProbability;
    this.defaultValue = data.defaultValue;
  }
}

export class Targets extends PMMLObject {
  Extension?: Extension[];
  Target: Target[];

  constructor(data: Targets) {
    super(data);
    this.Extension = data.Extension;
    this.Target = data.Target;
  }
}

export class Taxonomy extends PMMLObject {
  Extension?: Extension[];
  ChildParent: ChildParent[];
  name: string;

  constructor(data: Taxonomy) {
    super(data);
    this.Extension = data.Extension;
    this.ChildParent = data.ChildParent;
    this.name = data.name;
  }
}

export class TextIndex extends Expression {
  Extension?: Extension[];
  TextIndexNormalization?: TextIndexNormalization[];
  expression?: Expression;
  textField: string;
  localTermWeights?: TextIndexLocalTermWeights;
  isCaseSensitive?: boolean;
  maxLevenshteinDistance?: number;
  countHits?: CountHits;
  wordSeparatorCharacterRE?: string;
  tokenize?: boolean;

  constructor(data: TextIndex) {
    super(data);
    this.Extension = data.Extension;
    this.TextIndexNormalization = data.TextIndexNormalization;
    this.expression = data.expression;
    this.textField = data.textField;
    this.localTermWeights = data.localTermWeights;
    this.isCaseSensitive = data.isCaseSensitive;
    this.maxLevenshteinDistance = data.maxLevenshteinDistance;
    this.countHits = data.countHits;
    this.wordSeparatorCharacterRE = data.wordSeparatorCharacterRE;
    this.tokenize = data.tokenize;
  }
}

export class TextIndexNormalization extends PMMLObject {
  Extension?: Extension[];
  TableLocator?: TableLocator;
  InlineTable?: InlineTable;
  inField?: string;
  outField?: string;
  regexField?: string;
  recursive?: boolean;
  isCaseSensitive?: boolean;
  maxLevenshteinDistance?: number;
  wordSeparatorCharacterRE?: string;
  tokenize?: boolean;

  constructor(data: TextIndexNormalization) {
    super(data);
    this.Extension = data.Extension;
    this.TableLocator = data.TableLocator;
    this.InlineTable = data.InlineTable;
    this.inField = data.inField;
    this.outField = data.outField;
    this.regexField = data.regexField;
    this.recursive = data.recursive;
    this.isCaseSensitive = data.isCaseSensitive;
    this.maxLevenshteinDistance = data.maxLevenshteinDistance;
    this.wordSeparatorCharacterRE = data.wordSeparatorCharacterRE;
    this.tokenize = data.tokenize;
  }
}

export class Timestamp extends PMMLObject {
  Extension?: any[];

  constructor(data: Timestamp) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class TransformationDictionary extends PMMLObject {
  Extension?: Extension[];
  DefineFunction?: DefineFunction[];
  DerivedField?: DerivedField[];

  constructor(data: TransformationDictionary) {
    super(data);
    this.Extension = data.Extension;
    this.DefineFunction = data.DefineFunction;
    this.DerivedField = data.DerivedField;
  }
}

export class True extends Predicate {
  Extension?: Extension[];

  constructor(data: True) {
    super(data);
    this.Extension = data.Extension;
  }
}

export class UniformDistribution extends ContinuousDistribution {
  Extension?: Extension[];
  lower: number;
  upper: number;

  constructor(data: UniformDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.lower = data.lower;
    this.upper = data.upper;
  }
}

export class UnivariateStats extends PMMLObject {
  Extension?: Extension[];
  Counts?: Counts;
  NumericInfo?: NumericInfo;
  DiscrStats?: DiscrStats;
  ContStats?: ContStats;
  Anova?: Anova;
  field?: string;
  weighted?: UnivariateStatsWeighted;

  constructor(data: UnivariateStats) {
    super(data);
    this.Extension = data.Extension;
    this.Counts = data.Counts;
    this.NumericInfo = data.NumericInfo;
    this.DiscrStats = data.DiscrStats;
    this.ContStats = data.ContStats;
    this.Anova = data.Anova;
    this.field = data.field;
    this.weighted = data.weighted;
  }
}

export class Value extends PMMLObject {
  Extension?: Extension[];
  value: any;
  displayValue?: string;
  property?: Property;

  constructor(data: Value) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.displayValue = data.displayValue;
    this.property = data.property;
  }
}

export class VerificationField extends PMMLObject {
  Extension?: Extension[];
  field: string;
  column?: string;
  precision?: number;
  zeroThreshold?: number;

  constructor(data: VerificationField) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.column = data.column;
    this.precision = data.precision;
    this.zeroThreshold = data.zeroThreshold;
  }
}

export class VerificationFields extends PMMLObject {
  Extension?: Extension[];
  VerificationField: VerificationField[];

  constructor(data: VerificationFields) {
    super(data);
    this.Extension = data.Extension;
    this.VerificationField = data.VerificationField;
  }
}

export class XCoordinates extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: XCoordinates) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class YCoordinates extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: YCoordinates) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class AnomalyDetectionModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  LocalTransformations?: LocalTransformations;
  ModelVerification?: ModelVerification;
  model?: Model;
  MeanClusterDistances?: MeanClusterDistances;
  modelName?: string;
  algorithmName?: string;
  functionName: MiningFunction;
  algorithmType: string;
  sampleDataSize?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: AnomalyDetectionModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.LocalTransformations = data.LocalTransformations;
    this.ModelVerification = data.ModelVerification;
    this.model = data.model;
    this.MeanClusterDistances = data.MeanClusterDistances;
    this.modelName = data.modelName;
    this.algorithmName = data.algorithmName;
    this.functionName = data.functionName;
    this.algorithmType = data.algorithmType;
    this.sampleDataSize = data.sampleDataSize;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class MeanClusterDistances extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: MeanClusterDistances) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class AssociationModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  LocalTransformations?: LocalTransformations;
  Item?: Item[];
  Itemset?: Itemset[];
  AssociationRule?: AssociationRule[];
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  numberOfTransactions: number;
  maxNumberOfItemsPerTA?: number;
  avgNumberOfItemsPerTA?: number;
  minimumSupport: number;
  minimumConfidence: number;
  lengthLimit?: number;
  numberOfItems: number;
  numberOfItemsets: number;
  numberOfRules: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: AssociationModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.LocalTransformations = data.LocalTransformations;
    this.Item = data.Item;
    this.Itemset = data.Itemset;
    this.AssociationRule = data.AssociationRule;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.numberOfTransactions = data.numberOfTransactions;
    this.maxNumberOfItemsPerTA = data.maxNumberOfItemsPerTA;
    this.avgNumberOfItemsPerTA = data.avgNumberOfItemsPerTA;
    this.minimumSupport = data.minimumSupport;
    this.minimumConfidence = data.minimumConfidence;
    this.lengthLimit = data.lengthLimit;
    this.numberOfItems = data.numberOfItems;
    this.numberOfItemsets = data.numberOfItemsets;
    this.numberOfRules = data.numberOfRules;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class AssociationRule extends Entity<string> {
  Extension?: Extension[];
  antecedent: string;
  consequent: string;
  support: number;
  confidence: number;
  lift?: number;
  leverage?: number;
  affinity?: number;
  id?: string;

  constructor(data: AssociationRule) {
    super(data);
    this.Extension = data.Extension;
    this.antecedent = data.antecedent;
    this.consequent = data.consequent;
    this.support = data.support;
    this.confidence = data.confidence;
    this.lift = data.lift;
    this.leverage = data.leverage;
    this.affinity = data.affinity;
    this.id = data.id;
  }
}

export class Item extends PMMLObject {
  Extension?: Extension[];
  id: string;
  value: string;
  field?: string;
  category?: string;
  mappedValue?: string;
  weight?: number;

  constructor(data: Item) {
    super(data);
    this.Extension = data.Extension;
    this.id = data.id;
    this.value = data.value;
    this.field = data.field;
    this.category = data.category;
    this.mappedValue = data.mappedValue;
    this.weight = data.weight;
  }
}

export class ItemRef extends PMMLObject {
  Extension?: Extension[];
  itemRef: string;

  constructor(data: ItemRef) {
    super(data);
    this.Extension = data.Extension;
    this.itemRef = data.itemRef;
  }
}

export class Itemset extends PMMLObject {
  Extension?: Extension[];
  ItemRef?: ItemRef[];
  id: string;
  support?: number;
  numberOfItems?: number;

  constructor(data: Itemset) {
    super(data);
    this.Extension = data.Extension;
    this.ItemRef = data.ItemRef;
    this.id = data.id;
    this.support = data.support;
    this.numberOfItems = data.numberOfItems;
  }
}

export class Alternate extends PMMLObject {
  continuousDistribution?: ContinuousDistribution;

  constructor(data: Alternate) {
    super(data);
    this.continuousDistribution = data.continuousDistribution;
  }
}

export class Baseline extends PMMLObject {
  continuousDistribution?: ContinuousDistribution;
  CountTable?: CountTable;
  NormalizedCountTable?: CountTable;
  FieldRef?: FieldRef[];

  constructor(data: Baseline) {
    super(data);
    this.continuousDistribution = data.continuousDistribution;
    this.CountTable = data.CountTable;
    this.NormalizedCountTable = data.NormalizedCountTable;
    this.FieldRef = data.FieldRef;
  }
}

export class BaselineModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  TestDistributions: TestDistributions;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: BaselineModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.TestDistributions = data.TestDistributions;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class CountTable extends DiscreteDistribution {
  Extension?: Extension[];
  FieldValue?: FieldValue[];
  FieldValueCount?: FieldValueCount[];
  sample?: number;

  constructor(data: CountTable) {
    super(data);
    this.Extension = data.Extension;
    this.FieldValue = data.FieldValue;
    this.FieldValueCount = data.FieldValueCount;
    this.sample = data.sample;
  }
}

export class FieldValue extends PMMLObject {
  Extension?: Extension[];
  FieldValue?: FieldValue[];
  FieldValueCount?: FieldValueCount[];
  field: string;
  value: any;

  constructor(data: FieldValue) {
    super(data);
    this.Extension = data.Extension;
    this.FieldValue = data.FieldValue;
    this.FieldValueCount = data.FieldValueCount;
    this.field = data.field;
    this.value = data.value;
  }
}

export class FieldValueCount extends PMMLObject {
  Extension?: Extension[];
  field: string;
  value: any;
  count: number;

  constructor(data: FieldValueCount) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.value = data.value;
    this.count = data.count;
  }
}

export class TestDistributions extends PMMLObject {
  Extension?: Extension[];
  Baseline: Baseline;
  Alternate?: Alternate;
  field: string;
  testStatistic: TestStatistic;
  resetValue?: number;
  windowSize?: number;
  weightField?: string;
  normalizationScheme?: string;

  constructor(data: TestDistributions) {
    super(data);
    this.Extension = data.Extension;
    this.Baseline = data.Baseline;
    this.Alternate = data.Alternate;
    this.field = data.field;
    this.testStatistic = data.testStatistic;
    this.resetValue = data.resetValue;
    this.windowSize = data.windowSize;
    this.weightField = data.weightField;
    this.normalizationScheme = data.normalizationScheme;
  }
}

export class BayesianNetworkModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  BayesianNetworkNodes: BayesianNetworkNodes;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  modelType?: string;
  inferenceMethod?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: BayesianNetworkModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.BayesianNetworkNodes = data.BayesianNetworkNodes;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.modelType = data.modelType;
    this.inferenceMethod = data.inferenceMethod;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class BayesianNetworkNodes extends PMMLObject {
  Extension?: Extension[];
  content?: PMMLObject[];

  constructor(data: BayesianNetworkNodes) {
    super(data);
    this.Extension = data.Extension;
    this.content = data.content;
  }
}

export class ContinuousConditionalProbability extends PMMLObject {
  Extension?: Extension[];
  ParentValue?: ParentValue[];
  ContinuousDistribution: BayesianContinuousDistribution[];
  count?: number;

  constructor(data: ContinuousConditionalProbability) {
    super(data);
    this.Extension = data.Extension;
    this.ParentValue = data.ParentValue;
    this.ContinuousDistribution = data.ContinuousDistribution;
    this.count = data.count;
  }
}

export class BayesianContinuousDistribution extends PMMLObject {
  Extension?: Extension[];
  TriangularDistributionForBN?: TriangularDistribution;
  NormalDistributionForBN?: NormalDistribution;
  LognormalDistributionForBN?: LognormalDistribution;
  UniformDistributionForBN?: BayesianUniformDistribution;

  constructor(data: BayesianContinuousDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.TriangularDistributionForBN = data.TriangularDistributionForBN;
    this.NormalDistributionForBN = data.NormalDistributionForBN;
    this.LognormalDistributionForBN = data.LognormalDistributionForBN;
    this.UniformDistributionForBN = data.UniformDistributionForBN;
  }
}

export class ContinuousNode extends PMMLObject {
  Extension?: Extension[];
  DerivedField?: DerivedField[];
  content?: PMMLObject[];
  name: string;
  count?: number;

  constructor(data: ContinuousNode) {
    super(data);
    this.Extension = data.Extension;
    this.DerivedField = data.DerivedField;
    this.content = data.content;
    this.name = data.name;
    this.count = data.count;
  }
}

export class DiscreteConditionalProbability extends PMMLObject {
  Extension?: Extension[];
  ParentValue: ParentValue[];
  ValueProbability: ValueProbability[];
  count?: number;

  constructor(data: DiscreteConditionalProbability) {
    super(data);
    this.Extension = data.Extension;
    this.ParentValue = data.ParentValue;
    this.ValueProbability = data.ValueProbability;
    this.count = data.count;
  }
}

export class DiscreteNode extends PMMLObject {
  Extension?: Extension[];
  DerivedField?: DerivedField[];
  content?: PMMLObject[];
  name: string;
  count?: number;

  constructor(data: DiscreteNode) {
    super(data);
    this.Extension = data.Extension;
    this.DerivedField = data.DerivedField;
    this.content = data.content;
    this.name = data.name;
    this.count = data.count;
  }
}

export class LognormalDistribution extends PMMLObject {
  Extension?: Extension[];
  Mean: Mean;
  Variance: Variance;

  constructor(data: LognormalDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.Mean = data.Mean;
    this.Variance = data.Variance;
  }
}

export class Lower extends PMMLObject {
  Extension?: Extension[];
  expression?: Expression;

  constructor(data: Lower) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
  }
}

export class Mean extends PMMLObject {
  Extension?: Extension[];
  expression?: Expression;

  constructor(data: Mean) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
  }
}

export class NormalDistribution extends PMMLObject {
  Extension?: Extension[];
  Mean: Mean;
  Variance: Variance;

  constructor(data: NormalDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.Mean = data.Mean;
    this.Variance = data.Variance;
  }
}

export class ParentValue extends PMMLObject {
  Extension?: Extension[];
  parent: string;
  value: any;

  constructor(data: ParentValue) {
    super(data);
    this.Extension = data.Extension;
    this.parent = data.parent;
    this.value = data.value;
  }
}

export class TriangularDistribution extends PMMLObject {
  Extension?: Extension[];
  Mean: Mean;
  Lower: Lower;
  Upper: Upper;

  constructor(data: TriangularDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.Mean = data.Mean;
    this.Lower = data.Lower;
    this.Upper = data.Upper;
  }
}

export class BayesianUniformDistribution extends PMMLObject {
  Extension?: Extension[];
  Lower: Lower;
  Upper: Upper;

  constructor(data: BayesianUniformDistribution) {
    super(data);
    this.Extension = data.Extension;
    this.Lower = data.Lower;
    this.Upper = data.Upper;
  }
}

export class Upper extends PMMLObject {
  Extension?: Extension[];
  expression?: Expression;

  constructor(data: Upper) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
  }
}

export class ValueProbability extends PMMLObject {
  Extension?: Extension[];
  value: any;
  probability: number;

  constructor(data: ValueProbability) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.probability = data.probability;
  }
}

export class Variance extends PMMLObject {
  Extension?: Extension[];
  expression?: Expression;

  constructor(data: Variance) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
  }
}

export class CenterFields extends PMMLObject {
  DerivedField: DerivedField[];

  constructor(data: CenterFields) {
    super(data);
    this.DerivedField = data.DerivedField;
  }
}

export class Cluster extends Entity<string> {
  Extension?: Extension[];
  KohonenMap?: KohonenMap;
  Array?: Array;
  Partition?: Partition;
  Covariances?: Covariances;
  id?: string;
  name?: string;
  size?: number;

  constructor(data: Cluster) {
    super(data);
    this.Extension = data.Extension;
    this.KohonenMap = data.KohonenMap;
    this.Array = data.Array;
    this.Partition = data.Partition;
    this.Covariances = data.Covariances;
    this.id = data.id;
    this.name = data.name;
    this.size = data.size;
  }
}

export class ClusteringField extends ComparisonField<ClusteringField> {
  Extension?: Extension[];
  Comparisons?: Comparisons;
  field: string;
  isCenterField?: CenterField;
  fieldWeight?: number;
  similarityScale?: number;
  compareFunction?: CompareFunction;

  constructor(data: ClusteringField) {
    super(data);
    this.Extension = data.Extension;
    this.Comparisons = data.Comparisons;
    this.field = data.field;
    this.isCenterField = data.isCenterField;
    this.fieldWeight = data.fieldWeight;
    this.similarityScale = data.similarityScale;
    this.compareFunction = data.compareFunction;
  }
}

export class ClusteringModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  LocalTransformations?: LocalTransformations;
  ComparisonMeasure: ComparisonMeasure;
  ClusteringField: ClusteringField[];
  CenterFields?: CenterFields;
  MissingValueWeights?: MissingValueWeights;
  Cluster: Cluster[];
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  modelClass: ModelClass;
  numberOfClusters: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: ClusteringModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.LocalTransformations = data.LocalTransformations;
    this.ComparisonMeasure = data.ComparisonMeasure;
    this.ClusteringField = data.ClusteringField;
    this.CenterFields = data.CenterFields;
    this.MissingValueWeights = data.MissingValueWeights;
    this.Cluster = data.Cluster;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.modelClass = data.modelClass;
    this.numberOfClusters = data.numberOfClusters;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class Comparisons extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: Comparisons) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class Covariances extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: Covariances) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class KohonenMap extends PMMLObject {
  Extension?: Extension[];
  coord1?: number;
  coord2?: number;
  coord3?: number;

  constructor(data: KohonenMap) {
    super(data);
    this.Extension = data.Extension;
    this.coord1 = data.coord1;
    this.coord2 = data.coord2;
    this.coord3 = data.coord3;
  }
}

export class MissingValueWeights extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: MissingValueWeights) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class ARDSquaredExponentialKernel extends PMMLObject {
  Extension?: Extension[];
  Lambda?: Lambda[];
  description?: string;
  gamma?: number;
  noiseVariance?: number;

  constructor(data: ARDSquaredExponentialKernel) {
    super(data);
    this.Extension = data.Extension;
    this.Lambda = data.Lambda;
    this.description = data.description;
    this.gamma = data.gamma;
    this.noiseVariance = data.noiseVariance;
  }
}

export class AbsoluteExponentialKernel extends PMMLObject {
  Extension?: Extension[];
  Lambda?: Lambda[];
  description?: string;
  gamma?: number;
  noiseVariance?: number;

  constructor(data: AbsoluteExponentialKernel) {
    super(data);
    this.Extension = data.Extension;
    this.Lambda = data.Lambda;
    this.description = data.description;
    this.gamma = data.gamma;
    this.noiseVariance = data.noiseVariance;
  }
}

export class GaussianProcessModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  RadialBasisKernel?: GaussianRadialBasisKernel;
  ARDSquaredExponentialKernel?: ARDSquaredExponentialKernel;
  AbsoluteExponentialKernel?: AbsoluteExponentialKernel;
  GeneralizedExponentialKernel?: GeneralizedExponentialKernel;
  TrainingInstances: TrainingInstances;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  optimizer?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: GaussianProcessModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.RadialBasisKernel = data.RadialBasisKernel;
    this.ARDSquaredExponentialKernel = data.ARDSquaredExponentialKernel;
    this.AbsoluteExponentialKernel = data.AbsoluteExponentialKernel;
    this.GeneralizedExponentialKernel = data.GeneralizedExponentialKernel;
    this.TrainingInstances = data.TrainingInstances;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.optimizer = data.optimizer;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class GeneralizedExponentialKernel extends PMMLObject {
  Extension?: Extension[];
  Lambda?: Lambda[];
  description?: string;
  gamma?: number;
  noiseVariance?: number;
  degree?: number;

  constructor(data: GeneralizedExponentialKernel) {
    super(data);
    this.Extension = data.Extension;
    this.Lambda = data.Lambda;
    this.description = data.description;
    this.gamma = data.gamma;
    this.noiseVariance = data.noiseVariance;
    this.degree = data.degree;
  }
}

export class Lambda extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: Lambda) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class GaussianRadialBasisKernel extends PMMLObject {
  Extension?: Extension[];
  description?: string;
  gamma?: number;
  noiseVariance?: number;
  lambda?: number;

  constructor(data: GaussianRadialBasisKernel) {
    super(data);
    this.Extension = data.Extension;
    this.description = data.description;
    this.gamma = data.gamma;
    this.noiseVariance = data.noiseVariance;
    this.lambda = data.lambda;
  }
}

export class BaseCumHazardTables extends PMMLObject {
  Extension?: Extension[];
  BaselineStratum?: BaselineStratum[];
  BaselineCell?: BaselineCell[];
  maxTime?: number;

  constructor(data: BaseCumHazardTables) {
    super(data);
    this.Extension = data.Extension;
    this.BaselineStratum = data.BaselineStratum;
    this.BaselineCell = data.BaselineCell;
    this.maxTime = data.maxTime;
  }
}

export class BaselineCell extends PMMLObject {
  Extension?: Extension[];
  time: number;
  cumHazard: number;

  constructor(data: BaselineCell) {
    super(data);
    this.Extension = data.Extension;
    this.time = data.time;
    this.cumHazard = data.cumHazard;
  }
}

export class BaselineStratum extends PMMLObject {
  Extension?: Extension[];
  BaselineCell?: BaselineCell[];
  value: any;
  label?: string;
  maxTime: number;

  constructor(data: BaselineStratum) {
    super(data);
    this.Extension = data.Extension;
    this.BaselineCell = data.BaselineCell;
    this.value = data.value;
    this.label = data.label;
    this.maxTime = data.maxTime;
  }
}

export class Categories extends PMMLObject {
  Extension?: Extension[];
  Category: Category[];

  constructor(data: Categories) {
    super(data);
    this.Extension = data.Extension;
    this.Category = data.Category;
  }
}

export class Category extends PMMLObject {
  Extension?: Extension[];
  value: any;

  constructor(data: Category) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
  }
}

export class PredictorList extends PMMLObject {
  constructor(data: PredictorList) {
    super(data);
  }
}

export class CovariateList extends PredictorList {
  Extension?: Extension[];
  Predictor?: Predictor[];

  constructor(data: CovariateList) {
    super(data);
    this.Extension = data.Extension;
    this.Predictor = data.Predictor;
  }
}

export class EventValues extends PMMLObject {
  Extension?: Extension[];
  Value?: Value[];
  Interval?: Interval[];

  constructor(data: EventValues) {
    super(data);
    this.Extension = data.Extension;
    this.Value = data.Value;
    this.Interval = data.Interval;
  }
}

export class FactorList extends PredictorList {
  Extension?: Extension[];
  Predictor?: Predictor[];

  constructor(data: FactorList) {
    super(data);
    this.Extension = data.Extension;
    this.Predictor = data.Predictor;
  }
}

export class GeneralRegressionModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  ParameterList: ParameterList;
  FactorList?: FactorList;
  CovariateList?: CovariateList;
  PPMatrix: PPMatrix;
  PCovMatrix?: PCovMatrix;
  ParamMatrix: ParamMatrix;
  EventValues?: EventValues;
  BaseCumHazardTables?: BaseCumHazardTables;
  ModelVerification?: ModelVerification;
  targetVariableName?: string;
  modelType: GeneralRegressionModelType;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  targetReferenceCategory?: any;
  cumulativeLink?: CumulativeLinkFunction;
  linkFunction?: LinkFunction;
  linkParameter?: number;
  trialsVariable?: string;
  trialsValue?: number;
  distribution?: GeneralRegressionModelDistribution;
  distParameter?: number;
  offsetVariable?: string;
  offsetValue?: number;
  modelDF?: number;
  endTimeVariable?: string;
  startTimeVariable?: string;
  subjectIDVariable?: string;
  statusVariable?: string;
  baselineStrataVariable?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: GeneralRegressionModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.ParameterList = data.ParameterList;
    this.FactorList = data.FactorList;
    this.CovariateList = data.CovariateList;
    this.PPMatrix = data.PPMatrix;
    this.PCovMatrix = data.PCovMatrix;
    this.ParamMatrix = data.ParamMatrix;
    this.EventValues = data.EventValues;
    this.BaseCumHazardTables = data.BaseCumHazardTables;
    this.ModelVerification = data.ModelVerification;
    this.targetVariableName = data.targetVariableName;
    this.modelType = data.modelType;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.targetReferenceCategory = data.targetReferenceCategory;
    this.cumulativeLink = data.cumulativeLink;
    this.linkFunction = data.linkFunction;
    this.linkParameter = data.linkParameter;
    this.trialsVariable = data.trialsVariable;
    this.trialsValue = data.trialsValue;
    this.distribution = data.distribution;
    this.distParameter = data.distParameter;
    this.offsetVariable = data.offsetVariable;
    this.offsetValue = data.offsetValue;
    this.modelDF = data.modelDF;
    this.endTimeVariable = data.endTimeVariable;
    this.startTimeVariable = data.startTimeVariable;
    this.subjectIDVariable = data.subjectIDVariable;
    this.statusVariable = data.statusVariable;
    this.baselineStrataVariable = data.baselineStrataVariable;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class ParameterCell extends PMMLObject {
  constructor(data: ParameterCell) {
    super(data);
  }
}

export class PCell extends ParameterCell {
  Extension?: Extension[];
  targetCategory?: any;
  parameterName: string;
  beta: number;
  df?: number;

  constructor(data: PCell) {
    super(data);
    this.Extension = data.Extension;
    this.targetCategory = data.targetCategory;
    this.parameterName = data.parameterName;
    this.beta = data.beta;
    this.df = data.df;
  }
}

export class PCovCell extends PMMLObject {
  Extension?: Extension[];
  pRow: string;
  pCol: string;
  tRow?: string;
  tCol?: string;
  value: number;
  targetCategory?: any;

  constructor(data: PCovCell) {
    super(data);
    this.Extension = data.Extension;
    this.pRow = data.pRow;
    this.pCol = data.pCol;
    this.tRow = data.tRow;
    this.tCol = data.tCol;
    this.value = data.value;
    this.targetCategory = data.targetCategory;
  }
}

export class PCovMatrix extends PMMLObject {
  Extension?: Extension[];
  PCovCell: PCovCell[];
  type?: PCovMatrixType;

  constructor(data: PCovMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.PCovCell = data.PCovCell;
    this.type = data.type;
  }
}

export class PPCell extends ParameterCell {
  Extension?: Extension[];
  value: any;
  predictorName: string;
  parameterName: string;
  targetCategory?: any;

  constructor(data: PPCell) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.predictorName = data.predictorName;
    this.parameterName = data.parameterName;
    this.targetCategory = data.targetCategory;
  }
}

export class PPMatrix extends PMMLObject {
  Extension?: Extension[];
  PPCell?: PPCell[];

  constructor(data: PPMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.PPCell = data.PPCell;
  }
}

export class ParamMatrix extends PMMLObject {
  Extension?: Extension[];
  PCell?: PCell[];

  constructor(data: ParamMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.PCell = data.PCell;
  }
}

export class Parameter extends PMMLObject {
  Extension?: Extension[];
  name: string;
  label?: string;
  referencePoint?: number;

  constructor(data: Parameter) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.label = data.label;
    this.referencePoint = data.referencePoint;
  }
}

export class ParameterList extends PMMLObject {
  Extension?: Extension[];
  Parameter?: Parameter[];

  constructor(data: ParameterList) {
    super(data);
    this.Extension = data.Extension;
    this.Parameter = data.Parameter;
  }
}

export class Predictor extends PMMLObject {
  Extension?: Extension[];
  Categories?: Categories;
  Matrix?: Matrix;
  name: string;
  contrastMatrixType?: string;

  constructor(data: Predictor) {
    super(data);
    this.Extension = data.Extension;
    this.Categories = data.Categories;
    this.Matrix = data.Matrix;
    this.name = data.name;
    this.contrastMatrixType = data.contrastMatrixType;
  }
}

export class MiningModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  embeddedModels?: EmbeddedModel[];
  Segmentation?: Segmentation;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: MiningModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.embeddedModels = data.embeddedModels;
    this.Segmentation = data.Segmentation;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class Segment extends Entity<string> {
  Extension?: Extension[];
  predicate?: Predicate;
  model?: Model;
  VariableWeight?: VariableWeight;
  id?: string;
  weight?: number;

  constructor(data: Segment) {
    super(data);
    this.Extension = data.Extension;
    this.predicate = data.predicate;
    this.model = data.model;
    this.VariableWeight = data.VariableWeight;
    this.id = data.id;
    this.weight = data.weight;
  }
}

export class Segmentation extends PMMLObject {
  Extension?: Extension[];
  LocalTransformations?: LocalTransformations;
  Segment: Segment[];
  multipleModelMethod: MultipleModelMethod;
  missingPredictionTreatment?: MissingPredictionTreatment;
  missingThreshold?: number;

  constructor(data: Segmentation) {
    super(data);
    this.Extension = data.Extension;
    this.LocalTransformations = data.LocalTransformations;
    this.Segment = data.Segment;
    this.multipleModelMethod = data.multipleModelMethod;
    this.missingPredictionTreatment = data.missingPredictionTreatment;
    this.missingThreshold = data.missingThreshold;
  }
}

export class VariableWeight extends PMMLObject {
  Extension?: Extension[];
  field: string;

  constructor(data: VariableWeight) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
  }
}

export class BayesInput extends PMMLObject {
  Extension?: Extension[];
  TargetValueStats?: TargetValueStats;
  DerivedField?: DerivedField;
  PairCounts?: PairCounts[];
  fieldName: string;

  constructor(data: BayesInput) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValueStats = data.TargetValueStats;
    this.DerivedField = data.DerivedField;
    this.PairCounts = data.PairCounts;
    this.fieldName = data.fieldName;
  }
}

export class BayesInputs extends PMMLObject {
  Extension?: Extension[];
  BayesInput: BayesInput[];

  constructor(data: BayesInputs) {
    super(data);
    this.Extension = data.Extension;
    this.BayesInput = data.BayesInput;
  }
}

export class BayesOutput extends PMMLObject {
  Extension?: Extension[];
  TargetValueCounts: TargetValueCounts;
  fieldName: string;

  constructor(data: BayesOutput) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValueCounts = data.TargetValueCounts;
    this.fieldName = data.fieldName;
  }
}

export class NaiveBayesModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  BayesInputs: BayesInputs;
  BayesOutput: BayesOutput;
  ModelVerification?: ModelVerification;
  modelName?: string;
  threshold: number;
  functionName: MiningFunction;
  algorithmName?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: NaiveBayesModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.BayesInputs = data.BayesInputs;
    this.BayesOutput = data.BayesOutput;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.threshold = data.threshold;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class PairCounts extends PMMLObject {
  Extension?: Extension[];
  TargetValueCounts: TargetValueCounts;
  value: any;

  constructor(data: PairCounts) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValueCounts = data.TargetValueCounts;
    this.value = data.value;
  }
}

export class TargetValueCount extends PMMLObject {
  Extension?: Extension[];
  value: any;
  count: number;

  constructor(data: TargetValueCount) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
    this.count = data.count;
  }
}

export class TargetValueCounts extends PMMLObject {
  Extension?: Extension[];
  TargetValueCount: TargetValueCount[];

  constructor(data: TargetValueCounts) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValueCount = data.TargetValueCount;
  }
}

export class TargetValueStat extends PMMLObject {
  Extension?: Extension[];
  continuousDistribution?: ContinuousDistribution;
  value: any;

  constructor(data: TargetValueStat) {
    super(data);
    this.Extension = data.Extension;
    this.continuousDistribution = data.continuousDistribution;
    this.value = data.value;
  }
}

export class TargetValueStats extends PMMLObject {
  Extension?: Extension[];
  TargetValueStat: TargetValueStat[];

  constructor(data: TargetValueStats) {
    super(data);
    this.Extension = data.Extension;
    this.TargetValueStat = data.TargetValueStat;
  }
}

export class InstanceField extends PMMLObject {
  Extension?: Extension[];
  field: string;
  column?: string;

  constructor(data: InstanceField) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.column = data.column;
  }
}

export class InstanceFields extends PMMLObject {
  Extension?: Extension[];
  InstanceField: InstanceField[];

  constructor(data: InstanceFields) {
    super(data);
    this.Extension = data.Extension;
    this.InstanceField = data.InstanceField;
  }
}

export class KNNInput extends ComparisonField<KNNInput> {
  Extension?: Extension[];
  field: string;
  fieldWeight?: number;
  compareFunction?: CompareFunction;

  constructor(data: KNNInput) {
    super(data);
    this.Extension = data.Extension;
    this.field = data.field;
    this.fieldWeight = data.fieldWeight;
    this.compareFunction = data.compareFunction;
  }
}

export class KNNInputs extends PMMLObject {
  Extension?: Extension[];
  KNNInput: KNNInput[];

  constructor(data: KNNInputs) {
    super(data);
    this.Extension = data.Extension;
    this.KNNInput = data.KNNInput;
  }
}

export class NearestNeighborModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  TrainingInstances: TrainingInstances;
  ComparisonMeasure: ComparisonMeasure;
  KNNInputs: KNNInputs;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  numberOfNeighbors: number;
  continuousScoringMethod?: ContinuousScoringMethod;
  categoricalScoringMethod?: CategoricalScoringMethod;
  instanceIdVariable?: string;
  threshold?: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: NearestNeighborModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.TrainingInstances = data.TrainingInstances;
    this.ComparisonMeasure = data.ComparisonMeasure;
    this.KNNInputs = data.KNNInputs;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.numberOfNeighbors = data.numberOfNeighbors;
    this.continuousScoringMethod = data.continuousScoringMethod;
    this.categoricalScoringMethod = data.categoricalScoringMethod;
    this.instanceIdVariable = data.instanceIdVariable;
    this.threshold = data.threshold;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class TrainingInstances extends PMMLObject {
  Extension?: Extension[];
  InstanceFields: InstanceFields;
  TableLocator?: TableLocator;
  InlineTable?: InlineTable;
  isTransformed?: boolean;
  recordCount?: number;
  fieldCount?: number;

  constructor(data: TrainingInstances) {
    super(data);
    this.Extension = data.Extension;
    this.InstanceFields = data.InstanceFields;
    this.TableLocator = data.TableLocator;
    this.InlineTable = data.InlineTable;
    this.isTransformed = data.isTransformed;
    this.recordCount = data.recordCount;
    this.fieldCount = data.fieldCount;
  }
}

export class Connection extends PMMLObject {
  Extension?: Extension[];
  from: string;
  weight: number;

  constructor(data: Connection) {
    super(data);
    this.Extension = data.Extension;
    this.from = data.from;
    this.weight = data.weight;
  }
}

export class NeuralEntity extends Entity<string> {
  constructor(data: NeuralEntity) {
    super(data);
  }
}

export class NeuralInput extends NeuralEntity {
  Extension?: Extension[];
  DerivedField: DerivedField;
  id: string;

  constructor(data: NeuralInput) {
    super(data);
    this.Extension = data.Extension;
    this.DerivedField = data.DerivedField;
    this.id = data.id;
  }
}

export class NeuralInputs extends PMMLObject {
  Extension?: Extension[];
  NeuralInput: NeuralInput[];
  numberOfInputs?: number;

  constructor(data: NeuralInputs) {
    super(data);
    this.Extension = data.Extension;
    this.NeuralInput = data.NeuralInput;
    this.numberOfInputs = data.numberOfInputs;
  }
}

export class NeuralLayer extends PMMLObject {
  Extension?: Extension[];
  Neuron: Neuron[];
  numberOfNeurons?: number;
  activationFunction?: ActivationFunction;
  threshold?: number;
  width?: number;
  altitude?: number;
  normalizationMethod?: NeuralNetworkNormalizationMethod;

  constructor(data: NeuralLayer) {
    super(data);
    this.Extension = data.Extension;
    this.Neuron = data.Neuron;
    this.numberOfNeurons = data.numberOfNeurons;
    this.activationFunction = data.activationFunction;
    this.threshold = data.threshold;
    this.width = data.width;
    this.altitude = data.altitude;
    this.normalizationMethod = data.normalizationMethod;
  }
}

export class NeuralNetwork extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  NeuralInputs: NeuralInputs;
  NeuralLayer: NeuralLayer[];
  NeuralOutputs?: NeuralOutputs;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  activationFunction: ActivationFunction;
  normalizationMethod?: NeuralNetworkNormalizationMethod;
  threshold?: number;
  width?: number;
  altitude?: number;
  numberOfLayers?: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: NeuralNetwork) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.NeuralInputs = data.NeuralInputs;
    this.NeuralLayer = data.NeuralLayer;
    this.NeuralOutputs = data.NeuralOutputs;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.activationFunction = data.activationFunction;
    this.normalizationMethod = data.normalizationMethod;
    this.threshold = data.threshold;
    this.width = data.width;
    this.altitude = data.altitude;
    this.numberOfLayers = data.numberOfLayers;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class NeuralOutput extends PMMLObject {
  Extension?: Extension[];
  DerivedField: DerivedField;
  outputNeuron: string;

  constructor(data: NeuralOutput) {
    super(data);
    this.Extension = data.Extension;
    this.DerivedField = data.DerivedField;
    this.outputNeuron = data.outputNeuron;
  }
}

export class NeuralOutputs extends PMMLObject {
  Extension?: Extension[];
  NeuralOutput: NeuralOutput[];
  numberOfOutputs?: number;

  constructor(data: NeuralOutputs) {
    super(data);
    this.Extension = data.Extension;
    this.NeuralOutput = data.NeuralOutput;
    this.numberOfOutputs = data.numberOfOutputs;
  }
}

export class Neuron extends NeuralEntity {
  Extension?: Extension[];
  Con: Connection[];
  id: string;
  bias?: number;
  width?: number;
  altitude?: number;

  constructor(data: Neuron) {
    super(data);
    this.Extension = data.Extension;
    this.Con = data.Con;
    this.id = data.id;
    this.bias = data.bias;
    this.width = data.width;
    this.altitude = data.altitude;
  }
}

export class Term extends PMMLObject {
  constructor(data: Term) {
    super(data);
  }
}

export class CategoricalPredictor extends Term {
  Extension?: Extension[];
  name: string;
  value: any;
  coefficient: number;

  constructor(data: CategoricalPredictor) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.value = data.value;
    this.coefficient = data.coefficient;
  }
}

export class NumericPredictor extends Term {
  Extension?: Extension[];
  name: string;
  exponent?: number;
  coefficient: number;

  constructor(data: NumericPredictor) {
    super(data);
    this.Extension = data.Extension;
    this.name = data.name;
    this.exponent = data.exponent;
    this.coefficient = data.coefficient;
  }
}

export class PredictorTerm extends Term {
  Extension?: Extension[];
  FieldRef: FieldRef[];
  name?: string;
  coefficient: number;

  constructor(data: PredictorTerm) {
    super(data);
    this.Extension = data.Extension;
    this.FieldRef = data.FieldRef;
    this.name = data.name;
    this.coefficient = data.coefficient;
  }
}

export class Regression extends EmbeddedModel {
  Extension?: Extension[];
  Output?: Output;
  ModelStats?: ModelStats;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  ResultField?: ResultField[];
  RegressionTable: RegressionTable[];
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  normalizationMethod?: RegressionNormalizationMethod;

  constructor(data: Regression) {
    super(data);
    this.Extension = data.Extension;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.ResultField = data.ResultField;
    this.RegressionTable = data.RegressionTable;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.normalizationMethod = data.normalizationMethod;
  }
}

export class RegressionModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  RegressionTable: RegressionTable[];
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  modelType?: RegressionModelType;
  targetFieldName?: string;
  normalizationMethod?: RegressionModelNormalizationMethod;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: RegressionModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.RegressionTable = data.RegressionTable;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.modelType = data.modelType;
    this.targetFieldName = data.targetFieldName;
    this.normalizationMethod = data.normalizationMethod;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class RegressionTable extends PMMLObject {
  Extension?: Extension[];
  NumericPredictor?: NumericPredictor[];
  CategoricalPredictor?: CategoricalPredictor[];
  PredictorTerm?: PredictorTerm[];
  intercept: number;
  targetCategory?: any;

  constructor(data: RegressionTable) {
    super(data);
    this.Extension = data.Extension;
    this.NumericPredictor = data.NumericPredictor;
    this.CategoricalPredictor = data.CategoricalPredictor;
    this.PredictorTerm = data.PredictorTerm;
    this.intercept = data.intercept;
    this.targetCategory = data.targetCategory;
  }
}

export class Rule extends Entity<string> {
  constructor(data: Rule) {
    super(data);
  }
}

export class CompoundRule extends Rule {
  Extension?: Extension[];
  predicate?: Predicate;
  rules?: Rule[];

  constructor(data: CompoundRule) {
    super(data);
    this.Extension = data.Extension;
    this.predicate = data.predicate;
    this.rules = data.rules;
  }
}

export class RuleSelectionMethod extends PMMLObject {
  Extension?: Extension[];
  criterion: Criterion;

  constructor(data: RuleSelectionMethod) {
    super(data);
    this.Extension = data.Extension;
    this.criterion = data.criterion;
  }
}

export class RuleSet extends PMMLObject {
  Extension?: Extension[];
  RuleSelectionMethod: RuleSelectionMethod[];
  ScoreDistribution?: ScoreDistribution[];
  rules?: Rule[];
  recordCount?: number;
  nbCorrect?: number;
  defaultScore?: any;
  defaultConfidence?: number;

  constructor(data: RuleSet) {
    super(data);
    this.Extension = data.Extension;
    this.RuleSelectionMethod = data.RuleSelectionMethod;
    this.ScoreDistribution = data.ScoreDistribution;
    this.rules = data.rules;
    this.recordCount = data.recordCount;
    this.nbCorrect = data.nbCorrect;
    this.defaultScore = data.defaultScore;
    this.defaultConfidence = data.defaultConfidence;
  }
}

export class RuleSetModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  RuleSet: RuleSet;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: RuleSetModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.RuleSet = data.RuleSet;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class SimpleRule extends Rule {
  Extension?: Extension[];
  predicate?: Predicate;
  ScoreDistribution?: ScoreDistribution[];
  id?: string;
  score: any;
  recordCount?: number;
  nbCorrect?: number;
  confidence?: number;
  weight?: number;

  constructor(data: SimpleRule) {
    super(data);
    this.Extension = data.Extension;
    this.predicate = data.predicate;
    this.ScoreDistribution = data.ScoreDistribution;
    this.id = data.id;
    this.score = data.score;
    this.recordCount = data.recordCount;
    this.nbCorrect = data.nbCorrect;
    this.confidence = data.confidence;
    this.weight = data.weight;
  }
}

export class Attribute extends PMMLObject {
  Extension?: Extension[];
  predicate?: Predicate;
  ComplexPartialScore?: ComplexPartialScore;
  reasonCode?: string;
  partialScore?: number;

  constructor(data: Attribute) {
    super(data);
    this.Extension = data.Extension;
    this.predicate = data.predicate;
    this.ComplexPartialScore = data.ComplexPartialScore;
    this.reasonCode = data.reasonCode;
    this.partialScore = data.partialScore;
  }
}

export class Characteristic extends PMMLObject {
  Extension?: Extension[];
  Attribute: Attribute[];
  name?: string;
  reasonCode?: string;
  baselineScore?: number;

  constructor(data: Characteristic) {
    super(data);
    this.Extension = data.Extension;
    this.Attribute = data.Attribute;
    this.name = data.name;
    this.reasonCode = data.reasonCode;
    this.baselineScore = data.baselineScore;
  }
}

export class Characteristics extends PMMLObject {
  Extension?: Extension[];
  Characteristic: Characteristic[];

  constructor(data: Characteristics) {
    super(data);
    this.Extension = data.Extension;
    this.Characteristic = data.Characteristic;
  }
}

export class ComplexPartialScore extends PMMLObject {
  Extension?: Extension[];
  expression?: Expression;

  constructor(data: ComplexPartialScore) {
    super(data);
    this.Extension = data.Extension;
    this.expression = data.expression;
  }
}

export class Scorecard extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  Characteristics: Characteristics;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  initialScore?: number;
  useReasonCodes?: boolean;
  reasonCodeAlgorithm?: ReasonCodeAlgorithm;
  baselineScore?: number;
  baselineMethod?: BaselineMethod;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: Scorecard) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.Characteristics = data.Characteristics;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.initialScore = data.initialScore;
    this.useReasonCodes = data.useReasonCodes;
    this.reasonCodeAlgorithm = data.reasonCodeAlgorithm;
    this.baselineScore = data.baselineScore;
    this.baselineMethod = data.baselineMethod;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class AntecedentSequence extends PMMLObject {
  Extension?: Extension[];
  SequenceReference: SequenceReference;
  Time?: Time;

  constructor(data: AntecedentSequence) {
    super(data);
    this.Extension = data.Extension;
    this.SequenceReference = data.SequenceReference;
    this.Time = data.Time;
  }
}

export class ConsequentSequence extends PMMLObject {
  Extension?: Extension[];
  SequenceReference: SequenceReference;
  Time?: Time;

  constructor(data: ConsequentSequence) {
    super(data);
    this.Extension = data.Extension;
    this.SequenceReference = data.SequenceReference;
    this.Time = data.Time;
  }
}

export class Constraints extends PMMLObject {
  Extension?: Extension[];
  minimumNumberOfItems?: number;
  maximumNumberOfItems?: number;
  minimumNumberOfAntecedentItems?: number;
  maximumNumberOfAntecedentItems?: number;
  minimumNumberOfConsequentItems?: number;
  maximumNumberOfConsequentItems?: number;
  minimumSupport?: number;
  minimumConfidence?: number;
  minimumLift?: number;
  minimumTotalSequenceTime?: number;
  maximumTotalSequenceTime?: number;
  minimumItemsetSeparationTime?: number;
  maximumItemsetSeparationTime?: number;
  minimumAntConsSeparationTime?: number;
  maximumAntConsSeparationTime?: number;

  constructor(data: Constraints) {
    super(data);
    this.Extension = data.Extension;
    this.minimumNumberOfItems = data.minimumNumberOfItems;
    this.maximumNumberOfItems = data.maximumNumberOfItems;
    this.minimumNumberOfAntecedentItems = data.minimumNumberOfAntecedentItems;
    this.maximumNumberOfAntecedentItems = data.maximumNumberOfAntecedentItems;
    this.minimumNumberOfConsequentItems = data.minimumNumberOfConsequentItems;
    this.maximumNumberOfConsequentItems = data.maximumNumberOfConsequentItems;
    this.minimumSupport = data.minimumSupport;
    this.minimumConfidence = data.minimumConfidence;
    this.minimumLift = data.minimumLift;
    this.minimumTotalSequenceTime = data.minimumTotalSequenceTime;
    this.maximumTotalSequenceTime = data.maximumTotalSequenceTime;
    this.minimumItemsetSeparationTime = data.minimumItemsetSeparationTime;
    this.maximumItemsetSeparationTime = data.maximumItemsetSeparationTime;
    this.minimumAntConsSeparationTime = data.minimumAntConsSeparationTime;
    this.maximumAntConsSeparationTime = data.maximumAntConsSeparationTime;
  }
}

export class Delimiter extends PMMLObject {
  Extension?: Extension[];
  delimiter: TimeWindow;
  gap: Gap;

  constructor(data: Delimiter) {
    super(data);
    this.Extension = data.Extension;
    this.delimiter = data.delimiter;
    this.gap = data.gap;
  }
}

export class Sequence extends PMMLObject {
  Extension?: Extension[];
  SetReference: SetReference;
  content?: PMMLObject[];
  Time?: Time;
  id: string;
  numberOfSets?: number;
  occurrence?: number;
  support?: number;

  constructor(data: Sequence) {
    super(data);
    this.Extension = data.Extension;
    this.SetReference = data.SetReference;
    this.content = data.content;
    this.Time = data.Time;
    this.id = data.id;
    this.numberOfSets = data.numberOfSets;
    this.occurrence = data.occurrence;
    this.support = data.support;
  }
}

export class SequenceModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  ModelStats?: ModelStats;
  LocalTransformations?: LocalTransformations;
  Constraints?: Constraints;
  Item?: Item[];
  Itemset?: Itemset[];
  SetPredicate?: SetPredicate[];
  Sequence: Sequence[];
  SequenceRule?: SequenceRule[];
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  numberOfTransactions?: number;
  maxNumberOfItemsPerTransaction?: number;
  avgNumberOfItemsPerTransaction?: number;
  numberOfTransactionGroups?: number;
  maxNumberOfTAsPerTAGroup?: number;
  avgNumberOfTAsPerTAGroup?: number;
  minimumSupport: number;
  minimumConfidence: number;
  lengthLimit?: number;
  numberOfItems: number;
  numberOfSets: number;
  numberOfSequences: number;
  numberOfRules: number;
  timeWindowWidth?: number;
  minimumTime?: number;
  maximumTime?: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: SequenceModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.ModelStats = data.ModelStats;
    this.LocalTransformations = data.LocalTransformations;
    this.Constraints = data.Constraints;
    this.Item = data.Item;
    this.Itemset = data.Itemset;
    this.SetPredicate = data.SetPredicate;
    this.Sequence = data.Sequence;
    this.SequenceRule = data.SequenceRule;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.numberOfTransactions = data.numberOfTransactions;
    this.maxNumberOfItemsPerTransaction = data.maxNumberOfItemsPerTransaction;
    this.avgNumberOfItemsPerTransaction = data.avgNumberOfItemsPerTransaction;
    this.numberOfTransactionGroups = data.numberOfTransactionGroups;
    this.maxNumberOfTAsPerTAGroup = data.maxNumberOfTAsPerTAGroup;
    this.avgNumberOfTAsPerTAGroup = data.avgNumberOfTAsPerTAGroup;
    this.minimumSupport = data.minimumSupport;
    this.minimumConfidence = data.minimumConfidence;
    this.lengthLimit = data.lengthLimit;
    this.numberOfItems = data.numberOfItems;
    this.numberOfSets = data.numberOfSets;
    this.numberOfSequences = data.numberOfSequences;
    this.numberOfRules = data.numberOfRules;
    this.timeWindowWidth = data.timeWindowWidth;
    this.minimumTime = data.minimumTime;
    this.maximumTime = data.maximumTime;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class SequenceReference extends PMMLObject {
  Extension?: Extension[];
  seqId: string;

  constructor(data: SequenceReference) {
    super(data);
    this.Extension = data.Extension;
    this.seqId = data.seqId;
  }
}

export class SequenceRule extends Entity<string> {
  content?: any[];
  id: string;
  numberOfSets: number;
  occurrence: number;
  support: number;
  confidence: number;
  lift?: number;

  constructor(data: SequenceRule) {
    super(data);
    this.content = data.content;
    this.id = data.id;
    this.numberOfSets = data.numberOfSets;
    this.occurrence = data.occurrence;
    this.support = data.support;
    this.confidence = data.confidence;
    this.lift = data.lift;
  }
}

export class SetPredicate extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;
  id: string;
  field: string;
  operator?: SetPredicateOperator;

  constructor(data: SetPredicate) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.id = data.id;
    this.field = data.field;
    this.operator = data.operator;
  }
}

export class SetReference extends PMMLObject {
  Extension?: Extension[];
  setId: string;

  constructor(data: SetReference) {
    super(data);
    this.Extension = data.Extension;
    this.setId = data.setId;
  }
}

export class Time extends PMMLObject {
  Extension?: Extension[];
  min?: number;
  max?: number;
  mean?: number;
  standardDeviation?: number;

  constructor(data: Time) {
    super(data);
    this.Extension = data.Extension;
    this.min = data.min;
    this.max = data.max;
    this.mean = data.mean;
    this.standardDeviation = data.standardDeviation;
  }
}

export class Coefficient extends PMMLObject {
  Extension?: Extension[];
  value?: number;

  constructor(data: Coefficient) {
    super(data);
    this.Extension = data.Extension;
    this.value = data.value;
  }
}

export class Coefficients extends PMMLObject {
  Extension?: Extension[];
  Coefficient: Coefficient[];
  numberOfCoefficients?: number;
  absoluteValue?: number;

  constructor(data: Coefficients) {
    super(data);
    this.Extension = data.Extension;
    this.Coefficient = data.Coefficient;
    this.numberOfCoefficients = data.numberOfCoefficients;
    this.absoluteValue = data.absoluteValue;
  }
}

export class Kernel extends PMMLObject {
  constructor(data: Kernel) {
    super(data);
  }
}

export class LinearKernel extends Kernel {
  Extension?: Extension[];
  description?: string;

  constructor(data: LinearKernel) {
    super(data);
    this.Extension = data.Extension;
    this.description = data.description;
  }
}

export class PolynomialKernel extends Kernel {
  Extension?: Extension[];
  description?: string;
  gamma?: number;
  coef0?: number;
  degree?: number;

  constructor(data: PolynomialKernel) {
    super(data);
    this.Extension = data.Extension;
    this.description = data.description;
    this.gamma = data.gamma;
    this.coef0 = data.coef0;
    this.degree = data.degree;
  }
}

export class VectorMachineRadialBasisKernel extends Kernel {
  Extension?: Extension[];
  description?: string;
  gamma?: number;

  constructor(data: VectorMachineRadialBasisKernel) {
    super(data);
    this.Extension = data.Extension;
    this.description = data.description;
    this.gamma = data.gamma;
  }
}

export class SigmoidKernel extends Kernel {
  Extension?: Extension[];
  description?: string;
  gamma?: number;
  coef0?: number;

  constructor(data: SigmoidKernel) {
    super(data);
    this.Extension = data.Extension;
    this.description = data.description;
    this.gamma = data.gamma;
    this.coef0 = data.coef0;
  }
}

export class SupportVector extends PMMLObject {
  Extension?: Extension[];
  vectorId: string;

  constructor(data: SupportVector) {
    super(data);
    this.Extension = data.Extension;
    this.vectorId = data.vectorId;
  }
}

export class SupportVectorMachine extends PMMLObject {
  Extension?: Extension[];
  SupportVectors?: SupportVectors;
  Coefficients: Coefficients;
  targetCategory?: any;
  alternateTargetCategory?: any;
  threshold?: number;

  constructor(data: SupportVectorMachine) {
    super(data);
    this.Extension = data.Extension;
    this.SupportVectors = data.SupportVectors;
    this.Coefficients = data.Coefficients;
    this.targetCategory = data.targetCategory;
    this.alternateTargetCategory = data.alternateTargetCategory;
    this.threshold = data.threshold;
  }
}

export class SupportVectorMachineModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  kernel?: Kernel;
  VectorDictionary: VectorDictionary;
  SupportVectorMachine: SupportVectorMachine[];
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  threshold?: number;
  svmRepresentation?: Representation;
  alternateBinaryTargetCategory?: any;
  classificationMethod?: ClassificationMethod;
  maxWins?: boolean;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: SupportVectorMachineModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.kernel = data.kernel;
    this.VectorDictionary = data.VectorDictionary;
    this.SupportVectorMachine = data.SupportVectorMachine;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.threshold = data.threshold;
    this.svmRepresentation = data.svmRepresentation;
    this.alternateBinaryTargetCategory = data.alternateBinaryTargetCategory;
    this.classificationMethod = data.classificationMethod;
    this.maxWins = data.maxWins;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class SupportVectors extends PMMLObject {
  Extension?: Extension[];
  SupportVector: SupportVector[];
  numberOfSupportVectors?: number;
  numberOfAttributes?: number;

  constructor(data: SupportVectors) {
    super(data);
    this.Extension = data.Extension;
    this.SupportVector = data.SupportVector;
    this.numberOfSupportVectors = data.numberOfSupportVectors;
    this.numberOfAttributes = data.numberOfAttributes;
  }
}

export class VectorDictionary extends PMMLObject {
  Extension?: Extension[];
  VectorFields: VectorFields;
  VectorInstance?: VectorInstance[];
  numberOfVectors?: number;

  constructor(data: VectorDictionary) {
    super(data);
    this.Extension = data.Extension;
    this.VectorFields = data.VectorFields;
    this.VectorInstance = data.VectorInstance;
    this.numberOfVectors = data.numberOfVectors;
  }
}

export class VectorFields extends PMMLObject {
  Extension?: Extension[];
  content?: PMMLObject[];
  numberOfFields?: number;

  constructor(data: VectorFields) {
    super(data);
    this.Extension = data.Extension;
    this.content = data.content;
    this.numberOfFields = data.numberOfFields;
  }
}

export class VectorInstance extends PMMLObject {
  Extension?: Extension[];
  "REAL-SparseArray"?: RealSparseArray;
  Array?: Array;
  id: string;

  constructor(data: VectorInstance) {
    super(data);
    this.Extension = data.Extension;
    this["REAL-SparseArray"] = data["REAL-SparseArray"];
    this.Array = data.Array;
    this.id = data.id;
  }
}

export class DocumentTermMatrix extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: DocumentTermMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class TextCorpus extends PMMLObject {
  Extension?: Extension[];
  TextDocument?: TextDocument[];

  constructor(data: TextCorpus) {
    super(data);
    this.Extension = data.Extension;
    this.TextDocument = data.TextDocument;
  }
}

export class TextDictionary extends PMMLObject {
  Extension?: Extension[];
  Taxonomy?: Taxonomy;
  Array?: Array;

  constructor(data: TextDictionary) {
    super(data);
    this.Extension = data.Extension;
    this.Taxonomy = data.Taxonomy;
    this.Array = data.Array;
  }
}

export class TextDocument extends PMMLObject {
  Extension?: Extension[];
  id: string;
  name?: string;
  length?: number;
  file?: string;

  constructor(data: TextDocument) {
    super(data);
    this.Extension = data.Extension;
    this.id = data.id;
    this.name = data.name;
    this.length = data.length;
    this.file = data.file;
  }
}

export class TextModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  TextDictionary: TextDictionary;
  TextCorpus: TextCorpus;
  DocumentTermMatrix: DocumentTermMatrix;
  TextModelNormalization?: TextModelNormalization;
  TextModelSimiliarity?: TextModelSimiliarity;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  numberOfTerms: number;
  numberOfDocuments: number;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: TextModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.TextDictionary = data.TextDictionary;
    this.TextCorpus = data.TextCorpus;
    this.DocumentTermMatrix = data.DocumentTermMatrix;
    this.TextModelNormalization = data.TextModelNormalization;
    this.TextModelSimiliarity = data.TextModelSimiliarity;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.numberOfTerms = data.numberOfTerms;
    this.numberOfDocuments = data.numberOfDocuments;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class TextModelNormalization extends PMMLObject {
  Extension?: Extension[];
  localTermWeights?: TextModelNormalizationLocalTermWeights;
  globalTermWeights?: GlobalTermWeights;
  documentNormalization?: DocumentNormalization;

  constructor(data: TextModelNormalization) {
    super(data);
    this.Extension = data.Extension;
    this.localTermWeights = data.localTermWeights;
    this.globalTermWeights = data.globalTermWeights;
    this.documentNormalization = data.documentNormalization;
  }
}

export class TextModelSimiliarity extends PMMLObject {
  Extension?: Extension[];
  similarityType?: SimilarityType;

  constructor(data: TextModelSimiliarity) {
    super(data);
    this.Extension = data.Extension;
    this.similarityType = data.similarityType;
  }
}

export class AR extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: AR) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class TimeSeriesAlgorithm extends PMMLObject {
  constructor(data: TimeSeriesAlgorithm) {
    super(data);
  }
}

export class ARIMA extends TimeSeriesAlgorithm {
  Extension?: Extension[];
  NonseasonalComponent?: NonseasonalComponent;
  SeasonalComponent?: SeasonalComponent;
  DynamicRegressor?: DynamicRegressor[];
  MaximumLikelihoodStat?: MaximumLikelihoodStat;
  OutlierEffect?: OutlierEffect[];
  RMSE?: number;
  transformation?: string;
  constantTerm?: number;
  predictionMethod?: string;

  constructor(data: ARIMA) {
    super(data);
    this.Extension = data.Extension;
    this.NonseasonalComponent = data.NonseasonalComponent;
    this.SeasonalComponent = data.SeasonalComponent;
    this.DynamicRegressor = data.DynamicRegressor;
    this.MaximumLikelihoodStat = data.MaximumLikelihoodStat;
    this.OutlierEffect = data.OutlierEffect;
    this.RMSE = data.RMSE;
    this.transformation = data.transformation;
    this.constantTerm = data.constantTerm;
    this.predictionMethod = data.predictionMethod;
  }
}

export class ARMAPart extends PMMLObject {
  Extension?: Extension[];
  AR: AR;
  MA: MA;
  constant?: number;
  p: number;
  q: number;

  constructor(data: ARMAPart) {
    super(data);
    this.Extension = data.Extension;
    this.AR = data.AR;
    this.MA = data.MA;
    this.constant = data.constant;
    this.p = data.p;
    this.q = data.q;
  }
}

export class Denominator extends PMMLObject {
  Extension?: Extension[];
  NonseasonalFactor?: NonseasonalFactor;
  SeasonalFactor?: SeasonalFactor;

  constructor(data: Denominator) {
    super(data);
    this.Extension = data.Extension;
    this.NonseasonalFactor = data.NonseasonalFactor;
    this.SeasonalFactor = data.SeasonalFactor;
  }
}

export class DynamicRegressor extends PMMLObject {
  Extension?: Extension[];
  Numerator?: Numerator;
  Denominator?: Denominator;
  RegressorValues?: RegressorValues;
  field: string;
  transformation?: string;
  delay?: number;
  futureValuesMethod?: string;
  targetField?: string;

  constructor(data: DynamicRegressor) {
    super(data);
    this.Extension = data.Extension;
    this.Numerator = data.Numerator;
    this.Denominator = data.Denominator;
    this.RegressorValues = data.RegressorValues;
    this.field = data.field;
    this.transformation = data.transformation;
    this.delay = data.delay;
    this.futureValuesMethod = data.futureValuesMethod;
    this.targetField = data.targetField;
  }
}

export class ExponentialSmoothing extends TimeSeriesAlgorithm {
  Level: Level;
  Trend_ExpoSmooth?: TrendExpoSmooth;
  Seasonality_ExpoSmooth?: SeasonalityExpoSmooth;
  TimeValue?: TimeValue[];
  RMSE?: number;
  transformation?: Transformation;

  constructor(data: ExponentialSmoothing) {
    super(data);
    this.Level = data.Level;
    this.Trend_ExpoSmooth = data.Trend_ExpoSmooth;
    this.Seasonality_ExpoSmooth = data.Seasonality_ExpoSmooth;
    this.TimeValue = data.TimeValue;
    this.RMSE = data.RMSE;
    this.transformation = data.transformation;
  }
}

export class FinalNoise extends PMMLObject {
  Array?: Array;

  constructor(data: FinalNoise) {
    super(data);
    this.Array = data.Array;
  }
}

export class FinalNu extends PMMLObject {
  Array?: Array;

  constructor(data: FinalNu) {
    super(data);
    this.Array = data.Array;
  }
}

export class FinalOmega extends PMMLObject {
  Matrix: Matrix;

  constructor(data: FinalOmega) {
    super(data);
    this.Matrix = data.Matrix;
  }
}

export class FinalPredictedNoise extends PMMLObject {
  Array?: Array;

  constructor(data: FinalPredictedNoise) {
    super(data);
    this.Array = data.Array;
  }
}

export class FinalStateVector extends PMMLObject {
  Array?: Array;

  constructor(data: FinalStateVector) {
    super(data);
    this.Array = data.Array;
  }
}

export class FinalTheta extends PMMLObject {
  Theta: Theta[];

  constructor(data: FinalTheta) {
    super(data);
    this.Theta = data.Theta;
  }
}

export class GARCH extends TimeSeriesAlgorithm {
  Extension?: Extension[];
  ARMAPart: ARMAPart;
  GARCHPart: GARCHPart;

  constructor(data: GARCH) {
    super(data);
    this.Extension = data.Extension;
    this.ARMAPart = data.ARMAPart;
    this.GARCHPart = data.GARCHPart;
  }
}

export class GARCHPart extends PMMLObject {
  Extension?: Extension[];
  ResidualSquareCoefficients: ResidualSquareCoefficients;
  VarianceCoefficients: VarianceCoefficients;
  constant?: number;
  gp: number;
  gq: number;

  constructor(data: GARCHPart) {
    super(data);
    this.Extension = data.Extension;
    this.ResidualSquareCoefficients = data.ResidualSquareCoefficients;
    this.VarianceCoefficients = data.VarianceCoefficients;
    this.constant = data.constant;
    this.gp = data.gp;
    this.gq = data.gq;
  }
}

export class HVector extends PMMLObject {
  Array?: Array;

  constructor(data: HVector) {
    super(data);
    this.Array = data.Array;
  }
}

export class KalmanState extends PMMLObject {
  FinalOmega: FinalOmega;
  FinalStateVector: FinalStateVector;
  HVector?: HVector;

  constructor(data: KalmanState) {
    super(data);
    this.FinalOmega = data.FinalOmega;
    this.FinalStateVector = data.FinalStateVector;
    this.HVector = data.HVector;
  }
}

export class Level extends PMMLObject {
  alpha?: number;
  quadraticSmoothedValue?: number;
  cubicSmoothedValue?: number;
  smoothedValue?: number;

  constructor(data: Level) {
    super(data);
    this.alpha = data.alpha;
    this.quadraticSmoothedValue = data.quadraticSmoothedValue;
    this.cubicSmoothedValue = data.cubicSmoothedValue;
    this.smoothedValue = data.smoothedValue;
  }
}

export class MA extends PMMLObject {
  Extension?: Extension[];
  MACoefficients?: MACoefficients;
  Residuals?: Residuals;

  constructor(data: MA) {
    super(data);
    this.Extension = data.Extension;
    this.MACoefficients = data.MACoefficients;
    this.Residuals = data.Residuals;
  }
}

export class MACoefficients extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: MACoefficients) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class MaximumLikelihoodStat extends PMMLObject {
  KalmanState?: KalmanState;
  ThetaRecursionState?: ThetaRecursionState;
  method: string;
  periodDeficit?: number;

  constructor(data: MaximumLikelihoodStat) {
    super(data);
    this.KalmanState = data.KalmanState;
    this.ThetaRecursionState = data.ThetaRecursionState;
    this.method = data.method;
    this.periodDeficit = data.periodDeficit;
  }
}

export class MeasurementMatrix extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: MeasurementMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class NonseasonalComponent extends PMMLObject {
  Extension?: Extension[];
  AR?: AR;
  MA?: MA;
  p?: number;
  d?: number;
  q?: number;

  constructor(data: NonseasonalComponent) {
    super(data);
    this.Extension = data.Extension;
    this.AR = data.AR;
    this.MA = data.MA;
    this.p = data.p;
    this.d = data.d;
    this.q = data.q;
  }
}

export class NonseasonalFactor extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;
  difference?: number;
  maximumOrder?: number;

  constructor(data: NonseasonalFactor) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.difference = data.difference;
    this.maximumOrder = data.maximumOrder;
  }
}

export class Numerator extends PMMLObject {
  Extension?: Extension[];
  NonseasonalFactor?: NonseasonalFactor;
  SeasonalFactor?: SeasonalFactor;

  constructor(data: Numerator) {
    super(data);
    this.Extension = data.Extension;
    this.NonseasonalFactor = data.NonseasonalFactor;
    this.SeasonalFactor = data.SeasonalFactor;
  }
}

export class OutlierEffect extends PMMLObject {
  Extension?: Extension[];
  type: string;
  startTime: number;
  magnitude: number;
  dampingCoefficient?: number;

  constructor(data: OutlierEffect) {
    super(data);
    this.Extension = data.Extension;
    this.type = data.type;
    this.startTime = data.startTime;
    this.magnitude = data.magnitude;
    this.dampingCoefficient = data.dampingCoefficient;
  }
}

export class PastVariances extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: PastVariances) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class PsiVector extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;
  targetField?: string;
  variance?: string;

  constructor(data: PsiVector) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.targetField = data.targetField;
    this.variance = data.variance;
  }
}

export class RegressorValues extends PMMLObject {
  Extension?: Extension[];
  TimeSeries?: TimeSeries;
  TrendCoefficients?: TrendCoefficients;
  TransferFunctionValues?: TransferFunctionValues;

  constructor(data: RegressorValues) {
    super(data);
    this.Extension = data.Extension;
    this.TimeSeries = data.TimeSeries;
    this.TrendCoefficients = data.TrendCoefficients;
    this.TransferFunctionValues = data.TransferFunctionValues;
  }
}

export class ResidualSquareCoefficients extends PMMLObject {
  Extension?: Extension[];
  Residuals?: Residuals;
  MACoefficients?: MACoefficients;

  constructor(data: ResidualSquareCoefficients) {
    super(data);
    this.Extension = data.Extension;
    this.Residuals = data.Residuals;
    this.MACoefficients = data.MACoefficients;
  }
}

export class Residuals extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: Residuals) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class SeasonalComponent extends PMMLObject {
  Extension?: Extension[];
  AR?: AR;
  MA?: MA;
  P?: number;
  D?: number;
  Q?: number;
  period: number;

  constructor(data: SeasonalComponent) {
    super(data);
    this.Extension = data.Extension;
    this.AR = data.AR;
    this.MA = data.MA;
    this.P = data.P;
    this.D = data.D;
    this.Q = data.Q;
    this.period = data.period;
  }
}

export class SeasonalFactor extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;
  difference?: number;
  maximumOrder?: number;

  constructor(data: SeasonalFactor) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
    this.difference = data.difference;
    this.maximumOrder = data.maximumOrder;
  }
}

export class SeasonalTrendDecomposition extends TimeSeriesAlgorithm {
  constructor(data: SeasonalTrendDecomposition) {
    super(data);
  }
}

export class SeasonalityExpoSmooth extends PMMLObject {
  Array?: Array;
  type: SeasonalityExpoSmoothType;
  period: number;
  unit?: string;
  phase?: number;
  delta?: number;

  constructor(data: SeasonalityExpoSmooth) {
    super(data);
    this.Array = data.Array;
    this.type = data.type;
    this.period = data.period;
    this.unit = data.unit;
    this.phase = data.phase;
    this.delta = data.delta;
  }
}

export class SpectralAnalysis extends TimeSeriesAlgorithm {
  constructor(data: SpectralAnalysis) {
    super(data);
  }
}

export class StateSpaceModel extends TimeSeriesAlgorithm {
  Extension?: Extension[];
  StateVector?: StateVector;
  TransitionMatrix?: TransitionMatrix;
  MeasurementMatrix?: MeasurementMatrix;
  PsiVector?: PsiVector;
  DynamicRegressor?: DynamicRegressor[];
  variance?: number;
  period?: any;
  intercept?: number;

  constructor(data: StateSpaceModel) {
    super(data);
    this.Extension = data.Extension;
    this.StateVector = data.StateVector;
    this.TransitionMatrix = data.TransitionMatrix;
    this.MeasurementMatrix = data.MeasurementMatrix;
    this.PsiVector = data.PsiVector;
    this.DynamicRegressor = data.DynamicRegressor;
    this.variance = data.variance;
    this.period = data.period;
    this.intercept = data.intercept;
  }
}

export class StateVector extends PMMLObject {
  Extension?: Extension[];
  Array?: Array;

  constructor(data: StateVector) {
    super(data);
    this.Extension = data.Extension;
    this.Array = data.Array;
  }
}

export class Theta extends PMMLObject {
  i?: number;
  j?: number;
  theta?: number;

  constructor(data: Theta) {
    super(data);
    this.i = data.i;
    this.j = data.j;
    this.theta = data.theta;
  }
}

export class ThetaRecursionState extends PMMLObject {
  FinalNoise: FinalNoise;
  FinalPredictedNoise: FinalPredictedNoise;
  FinalTheta: FinalTheta;
  FinalNu: FinalNu;

  constructor(data: ThetaRecursionState) {
    super(data);
    this.FinalNoise = data.FinalNoise;
    this.FinalPredictedNoise = data.FinalPredictedNoise;
    this.FinalTheta = data.FinalTheta;
    this.FinalNu = data.FinalNu;
  }
}

export class TimeAnchor extends PMMLObject {
  TimeCycle?: TimeCycle[];
  TimeException?: TimeException[];
  type?: TimeAnchorType;
  offset?: number;
  stepsize?: number;
  displayName?: string;

  constructor(data: TimeAnchor) {
    super(data);
    this.TimeCycle = data.TimeCycle;
    this.TimeException = data.TimeException;
    this.type = data.type;
    this.offset = data.offset;
    this.stepsize = data.stepsize;
    this.displayName = data.displayName;
  }
}

export class TimeCycle extends PMMLObject {
  Array?: Array;
  length?: number;
  type?: TimeCycleType;
  displayName?: string;

  constructor(data: TimeCycle) {
    super(data);
    this.Array = data.Array;
    this.length = data.length;
    this.type = data.type;
    this.displayName = data.displayName;
  }
}

export class TimeException extends PMMLObject {
  Array?: Array;
  type?: TimeExceptionType;
  count?: number;

  constructor(data: TimeException) {
    super(data);
    this.Array = data.Array;
    this.type = data.type;
    this.count = data.count;
  }
}

export class TimeSeries extends PMMLObject {
  TimeAnchor?: TimeAnchor;
  TimeException?: TimeException[];
  TimeValue: TimeValue[];
  usage?: Usage;
  startTime?: number;
  endTime?: number;
  interpolationMethod?: InterpolationMethod;
  field?: string;

  constructor(data: TimeSeries) {
    super(data);
    this.TimeAnchor = data.TimeAnchor;
    this.TimeException = data.TimeException;
    this.TimeValue = data.TimeValue;
    this.usage = data.usage;
    this.startTime = data.startTime;
    this.endTime = data.endTime;
    this.interpolationMethod = data.interpolationMethod;
    this.field = data.field;
  }
}

export class TimeSeriesModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  LocalTransformations?: LocalTransformations;
  TimeSeries?: TimeSeries[];
  SpectralAnalysis?: SpectralAnalysis;
  ARIMA?: ARIMA;
  ExponentialSmoothing?: ExponentialSmoothing;
  SeasonalTrendDecomposition?: SeasonalTrendDecomposition;
  StateSpaceModel?: StateSpaceModel;
  GARCH?: GARCH;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  bestFit: TimeSeriesModelAlgorithm;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: TimeSeriesModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.LocalTransformations = data.LocalTransformations;
    this.TimeSeries = data.TimeSeries;
    this.SpectralAnalysis = data.SpectralAnalysis;
    this.ARIMA = data.ARIMA;
    this.ExponentialSmoothing = data.ExponentialSmoothing;
    this.SeasonalTrendDecomposition = data.SeasonalTrendDecomposition;
    this.StateSpaceModel = data.StateSpaceModel;
    this.GARCH = data.GARCH;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.bestFit = data.bestFit;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export class TimeValue extends PMMLObject {
  Timestamp?: Timestamp;
  index?: number;
  time?: number;
  value: number;
  standardError?: number;

  constructor(data: TimeValue) {
    super(data);
    this.Timestamp = data.Timestamp;
    this.index = data.index;
    this.time = data.time;
    this.value = data.value;
    this.standardError = data.standardError;
  }
}

export class TransferFunctionValues extends PMMLObject {
  Array?: Array;

  constructor(data: TransferFunctionValues) {
    super(data);
    this.Array = data.Array;
  }
}

export class TransitionMatrix extends PMMLObject {
  Extension?: Extension[];
  Matrix: Matrix;

  constructor(data: TransitionMatrix) {
    super(data);
    this.Extension = data.Extension;
    this.Matrix = data.Matrix;
  }
}

export class TrendCoefficients extends PMMLObject {
  Extension?: Extension[];
  "REAL-SparseArray"?: RealSparseArray;

  constructor(data: TrendCoefficients) {
    super(data);
    this.Extension = data.Extension;
    this["REAL-SparseArray"] = data["REAL-SparseArray"];
  }
}

export class TrendExpoSmooth extends PMMLObject {
  Array?: Array;
  trend?: Trend;
  gamma?: number;
  phi?: number;
  smoothedValue?: number;

  constructor(data: TrendExpoSmooth) {
    super(data);
    this.Array = data.Array;
    this.trend = data.trend;
    this.gamma = data.gamma;
    this.phi = data.phi;
    this.smoothedValue = data.smoothedValue;
  }
}

export class VarianceCoefficients extends PMMLObject {
  Extension?: Extension[];
  PastVariances?: PastVariances;
  MACoefficients?: MACoefficients;

  constructor(data: VarianceCoefficients) {
    super(data);
    this.Extension = data.Extension;
    this.PastVariances = data.PastVariances;
    this.MACoefficients = data.MACoefficients;
  }
}

export class Node extends Entity<any> {
  constructor(data: Node) {
    super(data);
  }
}

export class SimpleNode extends Node {
  score?: any;
  predicate?: Predicate;

  constructor(data: SimpleNode) {
    super(data);
    this.score = data.score;
    this.predicate = data.predicate;
  }
}

export class BranchNode extends SimpleNode {
  id?: any;
  defaultChild?: any;
  nodes?: Node[];

  constructor(data: BranchNode) {
    super(data);
    this.id = data.id;
    this.defaultChild = data.defaultChild;
    this.nodes = data.nodes;
  }
}

export class ClassifierNode extends SimpleNode {
  id?: any;
  recordCount?: number;
  defaultChild?: any;
  scoreDistributions?: ScoreDistribution[];
  nodes?: Node[];

  constructor(data: ClassifierNode) {
    super(data);
    this.id = data.id;
    this.recordCount = data.recordCount;
    this.defaultChild = data.defaultChild;
    this.scoreDistributions = data.scoreDistributions;
    this.nodes = data.nodes;
  }
}

export class ComplexNode extends Node {
  Extension?: Extension[];
  predicate?: Predicate;
  Partition?: Partition;
  ScoreDistribution?: ScoreDistribution[];
  Node?: Node[];
  embeddedModel?: EmbeddedModel;
  id?: any;
  score?: any;
  recordCount?: number;
  defaultChild?: any;

  constructor(data: ComplexNode) {
    super(data);
    this.Extension = data.Extension;
    this.predicate = data.predicate;
    this.Partition = data.Partition;
    this.ScoreDistribution = data.ScoreDistribution;
    this.Node = data.Node;
    this.embeddedModel = data.embeddedModel;
    this.id = data.id;
    this.score = data.score;
    this.recordCount = data.recordCount;
    this.defaultChild = data.defaultChild;
  }
}

export class CountingBranchNode extends BranchNode {
  recordCount?: number;

  constructor(data: CountingBranchNode) {
    super(data);
    this.recordCount = data.recordCount;
  }
}

export class LeafNode extends SimpleNode {
  id?: any;

  constructor(data: LeafNode) {
    super(data);
    this.id = data.id;
  }
}

export class CountingLeafNode extends LeafNode {
  recordCount?: number;

  constructor(data: CountingLeafNode) {
    super(data);
    this.recordCount = data.recordCount;
  }
}

export class DecisionTree extends EmbeddedModel {
  Extension?: Extension[];
  Output?: Output;
  ModelStats?: ModelStats;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  ResultField?: ResultField[];
  Node: Node;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  missingValueStrategy?: DecisionTreeMissingValueStrategy;
  missingValuePenalty?: number;
  noTrueChildStrategy?: DecisionTreeNoTrueChildStrategy;
  splitCharacteristic?: DecisionTreeSplitCharacteristic;

  constructor(data: DecisionTree) {
    super(data);
    this.Extension = data.Extension;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.ResultField = data.ResultField;
    this.Node = data.Node;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.missingValueStrategy = data.missingValueStrategy;
    this.missingValuePenalty = data.missingValuePenalty;
    this.noTrueChildStrategy = data.noTrueChildStrategy;
    this.splitCharacteristic = data.splitCharacteristic;
  }
}

export interface NodeTransformer {}

export class SimplifyingNodeTransformer implements NodeTransformer {
  constructor(data: SimplifyingNodeTransformer) {}
}

export class TreeModel extends Model {
  Extension?: Extension[];
  MiningSchema: MiningSchema;
  Output?: Output;
  ModelStats?: ModelStats;
  ModelExplanation?: ModelExplanation;
  Targets?: Targets;
  LocalTransformations?: LocalTransformations;
  Node: Node;
  ModelVerification?: ModelVerification;
  modelName?: string;
  functionName: MiningFunction;
  algorithmName?: string;
  missingValueStrategy?: TreeModelMissingValueStrategy;
  missingValuePenalty?: number;
  noTrueChildStrategy?: TreeModelNoTrueChildStrategy;
  splitCharacteristic?: TreeModelSplitCharacteristic;
  isScorable?: boolean;
  "x-mathContext"?: MathContext;

  constructor(data: TreeModel) {
    super(data);
    this.Extension = data.Extension;
    this.MiningSchema = data.MiningSchema;
    this.Output = data.Output;
    this.ModelStats = data.ModelStats;
    this.ModelExplanation = data.ModelExplanation;
    this.Targets = data.Targets;
    this.LocalTransformations = data.LocalTransformations;
    this.Node = data.Node;
    this.ModelVerification = data.ModelVerification;
    this.modelName = data.modelName;
    this.functionName = data.functionName;
    this.algorithmName = data.algorithmName;
    this.missingValueStrategy = data.missingValueStrategy;
    this.missingValuePenalty = data.missingValuePenalty;
    this.noTrueChildStrategy = data.noTrueChildStrategy;
    this.splitCharacteristic = data.splitCharacteristic;
    this.isScorable = data.isScorable;
    this["x-mathContext"] = data["x-mathContext"];
  }
}

export type Function = "count" | "sum" | "average" | "min" | "max" | "multiset";

export type AnovaRowType = "Model" | "Error" | "Total";

export type ArrayType = "int" | "real" | "string";

export type Recursive = "no" | "yes";

export type CompareFunction = "absDiff" | "gaussSim" | "delta" | "equal" | "table";

export type ComparisonMeasureKind = "distance" | "similarity";

export type CompoundPredicateBooleanOperator = "or" | "and" | "xor" | "surrogate";

export type Cyclic = "0" | "1";

export type DataType =
  | "string"
  | "integer"
  | "float"
  | "double"
  | "boolean"
  | "date"
  | "time"
  | "dateTime"
  | "dateDaysSince[0]"
  | "dateDaysSince[1960]"
  | "dateDaysSince[1970]"
  | "dateDaysSince[1980]"
  | "x-dateDaysSince[1990]"
  | "x-dateDaysSince[2000]"
  | "x-dateDaysSince[2010]"
  | "x-dateDaysSince[2020]"
  | "timeSeconds"
  | "dateTimeSecondsSince[0]"
  | "dateTimeSecondsSince[1960]"
  | "dateTimeSecondsSince[1970]"
  | "dateTimeSecondsSince[1980]"
  | "x-dateTimeSecondsSince[1990]"
  | "x-dateTimeSecondsSince[2000]"
  | "x-dateTimeSecondsSince[2010]"
  | "x-dateTimeSecondsSince[2020]";

export type Closure = "openClosed" | "openOpen" | "closedOpen" | "closedClosed";

export type InvalidValueTreatmentMethod = "returnInvalid" | "asIs" | "asMissing" | "asValue";

export type MathContext = "float" | "double";

export type MatrixKind = "diagonal" | "symmetric" | "any";

export type UsageType =
  | "active"
  | "predicted"
  | "target"
  | "supplementary"
  | "group"
  | "order"
  | "frequencyWeight"
  | "analysisWeight";

export type MiningFunction =
  | "associationRules"
  | "sequences"
  | "classification"
  | "regression"
  | "clustering"
  | "timeSeries"
  | "mixed";

export type MissingValueTreatmentMethod = "asIs" | "asMean" | "asMode" | "asMedian" | "asValue" | "returnInvalid";

export type Method = "indicator" | "thermometer";

export type OpType = "categorical" | "ordinal" | "continuous";

export type OutlierTreatmentMethod = "asIs" | "asMissingValues" | "asExtremeValues";

export type OutputFieldAlgorithm = "recommendation" | "exclusiveRecommendation" | "ruleAssociation";

export type RankBasis = "confidence" | "support" | "lift" | "leverage" | "affinity";

export type RankOrder = "descending" | "ascending";

export type RuleFeature =
  | "antecedent"
  | "consequent"
  | "rule"
  | "ruleId"
  | "confidence"
  | "support"
  | "lift"
  | "leverage"
  | "affinity";

export type PartitionFieldStatsWeighted = "0" | "1";

export type DataUsage = "training" | "test" | "validation";

export type ResultFeature =
  | "predictedValue"
  | "predictedDisplayValue"
  | "transformedValue"
  | "decision"
  | "probability"
  | "affinity"
  | "residual"
  | "standardError"
  | "standardDeviation"
  | "clusterId"
  | "clusterAffinity"
  | "entityId"
  | "entityAffinity"
  | "warning"
  | "ruleValue"
  | "reasonCode"
  | "antecedent"
  | "consequent"
  | "rule"
  | "ruleId"
  | "confidence"
  | "support"
  | "lift"
  | "leverage"
  | "x-report";

export type SimplePredicateOperator =
  | "equal"
  | "notEqual"
  | "lessThan"
  | "lessOrEqual"
  | "greaterThan"
  | "greaterOrEqual"
  | "isMissing"
  | "isNotMissing";

export type SimpleSetPredicate$BooleanOperator = "isIn" | "isNotIn";

export type CastInteger = "round" | "ceiling" | "floor";

export type CountHits = "allHits" | "bestHits";

export type TextIndexLocalTermWeights = "termFrequency" | "binary" | "logarithmic" | "augmentedNormalizedTermFrequency";

export type UnivariateStatsWeighted = "0" | "1";

export type Property = "valid" | "invalid" | "missing";

export type Version =
  | "PMML_3_0"
  | "PMML_3_1"
  | "PMML_3_2"
  | "PMML_4_0"
  | "PMML_4_1"
  | "PMML_4_2"
  | "PMML_4_3"
  | "PMML_4_4"
  | "XPMML";

export type TestStatistic = "zValue" | "chiSquareIndependence" | "chiSquareDistribution" | "CUSUM" | "scalarProduct";

export type CenterField = "true" | "false";

export type ModelClass = "centerBased" | "distributionBased";

export type CumulativeLinkFunction = "logit" | "probit" | "cloglog" | "loglog" | "cauchit";

export type GeneralRegressionModelDistribution =
  | "binomial"
  | "gamma"
  | "igauss"
  | "negbin"
  | "normal"
  | "poisson"
  | "tweedie";

export type LinkFunction =
  | "cloglog"
  | "identity"
  | "log"
  | "logc"
  | "logit"
  | "loglog"
  | "negbin"
  | "oddspower"
  | "power"
  | "probit";

export type GeneralRegressionModelType =
  | "regression"
  | "generalLinear"
  | "multinomialLogistic"
  | "ordinalMultinomial"
  | "generalizedLinear"
  | "CoxRegression";

export type PCovMatrixType = "model" | "robust";

export type MissingPredictionTreatment = "returnMissing" | "skipSegment" | "continue";

export type MultipleModelMethod =
  | "majorityVote"
  | "weightedMajorityVote"
  | "average"
  | "weightedAverage"
  | "median"
  | "weightedMedian"
  | "max"
  | "sum"
  | "weightedSum"
  | "selectFirst"
  | "selectAll"
  | "modelChain";

export type CategoricalScoringMethod = "majorityVote" | "weightedMajorityVote";

export type ContinuousScoringMethod = "median" | "average" | "weightedAverage";

export type ActivationFunction =
  | "threshold"
  | "logistic"
  | "tanh"
  | "identity"
  | "exponential"
  | "reciprocal"
  | "square"
  | "Gauss"
  | "sine"
  | "cosine"
  | "Elliott"
  | "arctan"
  | "rectifier"
  | "radialBasis";

export type NeuralNetworkNormalizationMethod = "none" | "simplemax" | "softmax";

export type RegressionNormalizationMethod =
  | "none"
  | "simplemax"
  | "softmax"
  | "logit"
  | "probit"
  | "cloglog"
  | "exp"
  | "loglog"
  | "cauchit";

export type RegressionModelType = "linearRegression" | "stepwisePolynomialRegression" | "logisticRegression";

export type RegressionModelNormalizationMethod =
  | "none"
  | "simplemax"
  | "softmax"
  | "logit"
  | "probit"
  | "cloglog"
  | "exp"
  | "loglog"
  | "cauchit";

export type Criterion = "weightedSum" | "weightedMax" | "firstHit";

export type BaselineMethod = "max" | "min" | "mean" | "neutral" | "other";

export type ReasonCodeAlgorithm = "pointsAbove" | "pointsBelow";

export type Gap = "true" | "false" | "unknown";

export type TimeWindow = "sameTimeWindow" | "acrossTimeWindows";

export type SetPredicateOperator = "supersetOf";

export type ClassificationMethod = "OneAgainstAll" | "OneAgainstOne";

export type Representation = "SupportVectors" | "Coefficients";

export type DocumentNormalization = "none" | "cosine";

export type GlobalTermWeights = "inverseDocumentFrequency" | "none" | "GFIDF" | "normal" | "probabilisticInverse";

export type TextModelNormalizationLocalTermWeights =
  | "termFrequency"
  | "binary"
  | "logarithmic"
  | "augmentedNormalizedTermFrequency";

export type SimilarityType = "euclidean" | "cosine";

export type Transformation = "none" | "logarithmic" | "squareroot";

export type SeasonalityExpoSmoothType = "additive" | "multiplicative";

export type TimeAnchorType =
  | "dateTimeMillisecondsSince[0]"
  | "dateTimeMillisecondsSince[1960]"
  | "dateTimeMillisecondsSince[1970]"
  | "dateTimeMillisecondsSince[1980]"
  | "dateTimeSecondsSince[0]"
  | "dateTimeSecondsSince[1960]"
  | "dateTimeSecondsSince[1970]"
  | "dateTimeSecondsSince[1980]"
  | "dateDaysSince[0]"
  | "dateDaysSince[1960]"
  | "dateDaysSince[1970]"
  | "dateDaysSince[1980]"
  | "dateMonthsSince[0]"
  | "dateMonthsSince[1960]"
  | "dateMonthsSince[1970]"
  | "dateMonthsSince[1980]"
  | "dateYearsSince[0]";

export type TimeCycleType = "includeAll" | "includeFromTo" | "excludeFromTo" | "includeSet" | "excludeSet";

export type TimeExceptionType = "exclude" | "include";

export type InterpolationMethod = "none" | "linear" | "exponentialSpline" | "cubicSpline";

export type Usage = "original" | "logical" | "prediction";

export type TimeSeriesModelAlgorithm =
  | "ARIMA"
  | "ExponentialSmoothing"
  | "SeasonalTrendDecomposition"
  | "SpectralAnalysis"
  | "StateSpaceModel"
  | "GARCH";

export type Trend =
  | "additive"
  | "damped_additive"
  | "multiplicative"
  | "damped_multiplicative"
  | "double_exponential"
  | "polynomial_exponential";

export type DecisionTreeMissingValueStrategy =
  | "lastPrediction"
  | "nullPrediction"
  | "defaultChild"
  | "weightedConfidence"
  | "aggregateNodes"
  | "none";

export type DecisionTreeNoTrueChildStrategy = "returnNullPrediction" | "returnLastPrediction";

export type DecisionTreeSplitCharacteristic = "binarySplit" | "multiSplit";

export type TreeModelMissingValueStrategy =
  | "lastPrediction"
  | "nullPrediction"
  | "defaultChild"
  | "weightedConfidence"
  | "aggregateNodes"
  | "none";

export type TreeModelNoTrueChildStrategy = "returnNullPrediction" | "returnLastPrediction";

export type TreeModelSplitCharacteristic = "binarySplit" | "multiSplit";
