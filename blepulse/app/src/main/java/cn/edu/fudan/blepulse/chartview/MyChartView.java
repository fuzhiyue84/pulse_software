package cn.edu.fudan.blepulse.chartview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by dell on 2016/10/13.
 */
public class MyChartView {

    private Context context;
    private GraphicalView chartView;
    private XYMultipleSeriesRenderer render;
    private XYSeries series;
    private XYMultipleSeriesDataset dataset;
    private XYSeriesRenderer r;
    private String title = "脉形图";
    private int xPoint = 0;

    public MyChartView(Context context) {

        this.context = context;

        series = new XYSeries(title);

        series.add(-1, 0);

        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        int color = Color.GREEN;
        PointStyle style = PointStyle.CIRCLE;
        render = buildRenderer(color, style, true);
        setChartSetting(render, "采样序号", "H", 0, 600, 0, 255, Color.BLACK, Color.BLACK);

        chartView = ChartFactory.getLineChartView(context, dataset, render);
    }

    public GraphicalView getChartView() {

        return chartView;
    }

    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);

        return renderer;
    }

    public void setChartSetting(XYMultipleSeriesRenderer render, String xTitle, String yTitle, double xMin, double xMax,
                                double yMin, double yMax, int axesColor, int labelsColor) {

        render.setBackgroundColor(Color.parseColor("#ffffff"));
        render.setApplyBackgroundColor(true);
        render.setMarginsColor(Color.WHITE);
        render.setMargins(new int[]{90, 90, 90, 90});
        render.setChartTitle(title);
        render.setChartTitleTextSize(50);
        render.setXTitle(xTitle);
        render.setYTitle(yTitle);
        render.setAxisTitleTextSize(40);
        render.setXAxisMin(xMin);
        render.setXAxisMax(xMax);
        render.setYAxisMin(yMin);
        render.setYAxisMax(yMax);
        render.setShowGrid(true);
        render.setGridColor(Color.GRAY);
        render.setAxesColor(axesColor);
        render.setLabelsColor(labelsColor);
        render.setLabelsTextSize(30);
        render.setXLabels(20);
        render.setYLabels(17);
        render.setYLabelsAlign(Paint.Align.RIGHT);
        render.setPointSize(3);
        render.setShowLegend(false);
        render.setExternalZoomEnabled(false);
        render.setPanEnabled(false, false);
    }


    public void updateView(int yPoint) {

        series.add(xPoint, yPoint);
        xPoint++;
        if (xPoint == 600) {
            series.clear();
            xPoint = 0;
        }
        chartView.invalidate();
    }

    public void changeSeriesColor(int i) {
        if (i == 0)
            r.setColor(Color.RED);
        else
            r.setColor(Color.GREEN);
    }
}
