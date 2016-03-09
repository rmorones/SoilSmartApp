package soilsmart.soilsmartapp.views;

/**
 * Created by jesus on 3/4/16.
 */
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.ValueShape;
import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.UserLocalStore;
import soilsmart.soilsmartapp.SoilSmartNode;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


public class AllNodesActivity extends BaseMenuActivity {
    private UserLocalStore userLocalStore;
    private List<SoilSmartNode> tempNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_nodes);

        if (savedInstanceState == null) {
            //populate nodes list with "random" data
            tempNodes = NodeLocationsActivity.GetRandomNodes();
            PlaceholderFragment frag = new PlaceholderFragment();
            frag.setNodes(tempNodes);
            getSupportFragmentManager().beginTransaction().add(R.id.container, frag).commit();
        }

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }

        userLocalStore = new UserLocalStore(this);
        Snackbar.make(findViewById(R.id.container), R.string.snackbarMsg,
                Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chartTop;
        private ColumnChartView chartBottom;

        private LineChartData lineData;
        private ColumnChartData columnData;

        private List<SoilSmartNode> tempnodes;

        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = true;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor = false;

        public PlaceholderFragment() {
        }

        public void setNodes(List<SoilSmartNode> nodes) {
            this.tempnodes = nodes;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_line_column_dependency, container, false);

            // *** TOP LINE CHART ***
            chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
        }

        private void generateColumnData() {

            int numSubcolumns = 1;
            int numColumns = tempnodes.size();

            List<AxisValue> axisValues = new ArrayList<>();
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float) tempnodes.get(i).getValuesLvl1Avg(), ChartUtils.COLORS[(j) % ChartUtils.COLORS.length]));
                    values.add(new SubcolumnValue((float) tempnodes.get(i).getValuesLvl2Avg(), ChartUtils.COLORS[(j + 1) % ChartUtils.COLORS.length]));
                    values.add(new SubcolumnValue((float) tempnodes.get(i).getValuesLvl3Avg(), ChartUtils.COLORS[(j + 2) % ChartUtils.COLORS.length]));
                }

                axisValues.add(new AxisValue(i).setLabel(tempnodes.get(i).getId()));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
            }

            columnData = new ColumnChartData(columns);

            //stack the bars
            columnData.setStacked(true);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true).setName("Node ID"));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName("Moisture Level (AVG)"));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL);

            // chartBottom.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // SelectedValue sv = chartBottom.getSelectedValue();
            // if (!sv.isSet()) {
            // generateInitialLineData();
            // }
            //
            // }
            // });

        }

        /**
         * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
         * will select value on column chart.
         */
        private void generateInitialLineData() {
            int numValues = tempnodes.get(0).getValuesLvl1().length;

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                //axisValues.add(new AxisValue(i).setLabel("day"));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<>();
            lines.add(line);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);

            lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true).setName("Previous Days"));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName("Moisture"));

            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewport- remember to set viewports after data.
            Viewport v = new Viewport(0, 100, 6, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        private void generateLineData(int color, float range, int nodeIndex) {
            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            double[] points1 = tempnodes.get(nodeIndex).getValuesLvl1();
            double[] points2 = tempnodes.get(nodeIndex).getValuesLvl2();
            double[] points3 = tempnodes.get(nodeIndex).getValuesLvl3();

            List<Line> lines = new ArrayList<>();

            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < points1.length; ++j) {
                values.add(new PointValue(j, (float) points1[j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[0]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                line.setPointColor(ChartUtils.COLORS[(1) % ChartUtils.COLORS.length]);
            }
            lines.add(0,line);

            List<PointValue> values2 = new ArrayList<>();
            for (int j = 0; j < points2.length; ++j) {
                values2.add(new PointValue(j, (float) points2[j]));
            }
            Line line2 = new Line(values2);
            line2.setColor(ChartUtils.COLORS[1]);
            line2.setShape(shape);
            line2.setCubic(isCubic);
            line2.setFilled(isFilled);
            line2.setHasLabels(hasLabels);
            line2.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line2.setHasLines(hasLines);
            line2.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                line2.setPointColor(ChartUtils.COLORS[(1 + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(1,line2);

            List<PointValue> values3 = new ArrayList<PointValue>();
            for (int j = 0; j < points3.length; ++j) {
                values3.add(new PointValue(j, (float) points3[j]));
            }
            Line line3 = new Line(values3);
            line3.setColor(ChartUtils.COLORS[2]);
            line3.setShape(shape);
            line3.setCubic(isCubic);
            line3.setFilled(isFilled);
            line3.setHasLabels(hasLabels);
            line3.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line3.setHasLines(hasLines);
            line3.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor){
                line3.setPointColor(ChartUtils.COLORS[(2 + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(2, line3);

            lineData = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis().setHasLines(true);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Previous Days (Days Ago)");
                    axisY.setName("Moisture");
                }
                lineData.setAxisXBottom(axisX);
                lineData.setAxisYLeft(axisY);
            } else {
                lineData.setAxisXBottom(null);
                lineData.setAxisYLeft(null);
            }
            chartTop.setLineChartData(lineData);

            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                generateLineData(value.getColor(), 1, columnIndex);
            }

            @Override
            public void onValueDeselected() {

                generateInitialLineData();

            }
        }
    }

}
