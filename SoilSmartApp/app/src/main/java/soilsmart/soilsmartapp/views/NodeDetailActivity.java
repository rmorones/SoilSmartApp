package soilsmart.soilsmartapp.views;

/**
 * created by jesus on 3/3/16.
 */

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.UserLocalStore;
import soilsmart.soilsmartapp.SoilSmartNode;

// Imports required for the graphing API
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

public class NodeDetailActivity extends BaseMenuActivity {

    private UserLocalStore userLocalStore;
    private SoilSmartNode node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_detail);
        // Extract the node we are graphing for
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            node = (SoilSmartNode) extras.getSerializable("node");
            //specific text for this node
            TextView tv1 = (TextView)findViewById(R.id.nodeID);
            tv1.setText("Node: " + node.getId());
            TextView tv2 = (TextView)findViewById(R.id.LatLong);
            tv2.setText("Latitude: " + String.format("%.6f", node.getLat()) + "  Longitude:" + String.format("%.6f",node.getLon()));
        }

        if (savedInstanceState == null) {
            PlaceholderFragment frag = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable("node", node);
            frag.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.container, frag).commit();
        }

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }
        //button code that allows user to select what type of data to show
        Button buttonClick = (Button)findViewById(R.id.button);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceholderFragment frag = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putSerializable("node", node);
                args.putString("button", "day");
                frag.setArguments(args);

                getSupportFragmentManager().beginTransaction().add(R.id.container, frag).commit();
            }
        });
        Button buttonClick2 = (Button)findViewById(R.id.button2);
        buttonClick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceholderFragment frag = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putSerializable("node", node);
                args.putString("button", "week");
                frag.setArguments(args);

                getSupportFragmentManager().beginTransaction().add(R.id.container, frag).commit();
            }
        });
        Button buttonClick3 = (Button)findViewById(R.id.button3);
        buttonClick3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceholderFragment frag = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putSerializable("node", node);
                args.putString("button", "month");
                frag.setArguments(args);

                getSupportFragmentManager().beginTransaction().add(R.id.container, frag).commit();
            }
        });

        userLocalStore = new UserLocalStore(this);
        Snackbar.make(findViewById(R.id.container), R.string.snackbarMsg,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!userLocalStore.getUserLoggedIn()) {
            launchActivity(LoginActivity.class);
        }
    }

    /**
     * A fragment containing a line chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private SoilSmartNode node;

        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 3;
        private int maxNumberOfLines = 3;
        private int numberOfPoints = 10;

        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            //node sent from NodeDetailActivity
            Bundle bundle = getArguments();
            node = (SoilSmartNode) bundle.get("node");
            //figure out what button was pressed so that the graph can be redrawn
            String button_selection = (String) bundle.get("button");
            if(button_selection != null){
                Toast.makeText(getActivity(), "Displaying: " + button_selection,Toast.LENGTH_SHORT).show();
            }

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            // Generate lines.
            generateData();

            // Disable viewpirt recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);

            resetViewport();

            return rootView;
        }

        //Used to reset the graph if needed
        private void reset() {
            numberOfLines = 3;

            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = true;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;

            chart.setValueSelectionEnabled(hasLabelForSelected);
            resetViewport();
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 1;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {
            //values for the lines we are going to plot
            double[] points1 = node.getValuesLvl1();
            double[] points2 = node.getValuesLvl2();
            double[] points3 = node.getValuesLvl3();

            List<Line> lines = new ArrayList<Line>();

                List<PointValue> values = new ArrayList<PointValue>();
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
                    line.setPointColor(ChartUtils.COLORS[(0 + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(0,line);

            List<PointValue> values2 = new ArrayList<PointValue>();
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

            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Time");
                    axisY.setName("Moisture Level");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        /**
         * Adds lines to data, after that data should be set again with
         * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
         */
        private void addLineToData() {
            if (data.getLines().size() >= maxNumberOfLines) {
                Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                ++numberOfLines;
            }

            generateData();
        }

        private void toggleLines() {
            hasLines = !hasLines;

            generateData();
        }

        private void togglePoints() {
            hasPoints = !hasPoints;

            generateData();
        }

        private void toggleCubic() {
            isCubic = !isCubic;

            generateData();

            if (isCubic) {
                // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
                // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
                // parameter or just set top and bottom values manually.
                // In this example I know that Y values are within (0,100) range so I set viewport height range manually
                // to (-5, 105).
                // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
                // modifying viewport.
                // Remember to set viewport after you call setLineChartData().
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = -5;
                v.top = 105;
                // You have to set max and current viewports separately.
                chart.setMaximumViewport(v);
                // I changing current viewport with animation in this case.
                chart.setCurrentViewportWithAnimation(v);
            } else {
                // If not cubic restore viewport to (0,100) range.
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = 0;
                v.top = 100;

                // You have to set max and current viewports separately.
                // In this case, if I want animation I have to set current viewport first and use animation listener.
                // Max viewport will be set in onAnimationFinished method.
                chart.setViewportAnimationListener(new ChartAnimationListener() {

                    @Override
                    public void onAnimationStarted() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationFinished() {
                        // Set max viewpirt and remove listener.
                        chart.setMaximumViewport(v);
                        chart.setViewportAnimationListener(null);

                    }
                });
                // Set current viewpirt with animation;
                chart.setCurrentViewportWithAnimation(v);
            }

        }

        private void toggleFilled() {
            isFilled = !isFilled;

            generateData();
        }

        private void togglePointColor() {
            pointsHaveDifferentColor = !pointsHaveDifferentColor;

            generateData();
        }

        private void setCircles() {
            shape = ValueShape.CIRCLE;

            generateData();
        }

        private void setSquares() {
            shape = ValueShape.SQUARE;

            generateData();
        }

        private void setDiamonds() {
            shape = ValueShape.DIAMOND;

            generateData();
        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
            }

            generateData();
        }

        private void toggleAxes() {
            hasAxes = !hasAxes;

            generateData();
        }

        private void toggleAxesNames() {
            hasAxesNames = !hasAxesNames;

            generateData();
        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
         * {@link LineChartView#setLineChartData(LineChartData)} again.
         */
        private void prepareDataAnimation() {
            for (Line line : data.getLines()) {
                for (PointValue value : line.getValues()) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.getX(), (float) Math.random() * 100);
                }
            }
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }


}
