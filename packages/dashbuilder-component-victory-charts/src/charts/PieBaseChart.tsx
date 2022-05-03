import { BaseChart, PieChartSerie } from "./BaseChart";

export type PieLegendPositionType = "bottom" | "right";

export abstract class PieBaseChart extends BaseChart {
  categories(): string[] {
    const data = this.props.dataSet.data;
    const categories: string[] = [];
    for (let i = 0; i < data.length; i++) {
      categories.push(data[i][0]);
    }
    return categories;
  }

  dataSetToPieChart(): PieChartSerie[] {
    const ds = this.props.dataSet;
    const rows = this.props.dataSet.data.length;
    const series: PieChartSerie[] = [];

    for (let i = 0; i < rows; i++) {
      series.push({
        name: ds.data[i][0],
        y: +ds.data[i][1],
      });
    }
    return series;
  }

  pieLegendPosition(): PieLegendPositionType {
    const currentPos = this.props.legendPosition;
    if (currentPos === "bottom" || currentPos) {
      return currentPos as PieLegendPositionType;
    }

    return "right";
  }
}
