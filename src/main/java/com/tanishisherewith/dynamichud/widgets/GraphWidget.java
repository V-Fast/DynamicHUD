package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.renderstates.GradientShadowRenderState;
import com.tanishisherewith.dynamichud.renderstates.InterpolatedCurveRenderState;
import com.tanishisherewith.dynamichud.utils.CustomRenderLayers;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.BooleanOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.ColorOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.DoubleOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import com.twelvemonkeys.lang.Validate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Graph widget to draw a simple but detailed graph.
 * You need to use DynamicValueRegistry to pass a value to the graph.
 * You can use null values to signify the graph should update with a new value yet.
 */
public class GraphWidget extends DynamicValueWidget implements ContextMenuProvider {
    public static WidgetData<GraphWidget> DATA = new WidgetData<>("GraphWidget", "Show graph", GraphWidget::new);

    private ContextMenu<?> menu;
    private float[] dataPoints;
    private int head = 0;
    private int maxDataPoints;
    private float minValue, prevMinValue;
    private float maxValue, prevMaxValue;
    private Color graphColor;
    private boolean graphColorRainbow;
    private Color backgroundColor;
    private float lineThickness;
    private boolean showGrid;
    private int gridLines;
    private float gWidth;
    private float gHeight;
    private String label;
    /// Automatically update the min and max of the graph
    private boolean autoUpdateRange = false;
    private float stepY;
    private float valueStep;
    private float valueScale;
    int offset = -2;

    public GraphWidget(String registryID, String registryKey, String modId, Anchor anchor, float gWidth, float gHeight, int maxDataPoints, float minValue, float maxValue, Color graphColor, Color backgroundColor, float lineThickness, boolean showGrid, int gridLines, String label) {
        super(DATA, modId, anchor, registryID, registryKey);
        this.gWidth = gWidth;
        this.gHeight = gHeight;
        this.maxDataPoints = maxDataPoints;
        this.graphColor = graphColor;
        this.backgroundColor = backgroundColor;
        this.lineThickness = lineThickness;
        this.showGrid = showGrid;
        this.gridLines = gridLines;
        this.label = label;
        this.setMinValue(minValue);
        this.setMaxValue(maxValue);

        internal_init();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    private void internal_init() {
        Validate.isTrue(maxDataPoints > 2, "MaxDataPoints should be more than 2.");
        this.dataPoints = new float[maxDataPoints];
        this.label = label.trim();
        this.widgetBox = new WidgetBox(x, y, (int) gWidth, (int) gHeight);
        this.stepY = gHeight / (gridLines + 1);
        this.valueStep = (maxValue - minValue) / (gridLines + 1);
        this.valueScale = (float) Math.clamp((stepY / 9.5), 0.0f, 1.0f);

        computeOffset();

        setTooltipText(Component.literal("Graph displaying: " + label));
        createMenu();
    }

    public GraphWidget() {
        this(DynamicValueRegistry.GLOBAL_ID, "unknown", "unknown", Anchor._default(), 0, 0, 5, 0, 10, Color.RED, Color.GREEN, 0, false, 0, "empty");
    }

    @Override
    public void init() {
        // init the buffer with minValue
        for (int i = 0; i < maxDataPoints; i++) {
            dataPoints[i] = minValue;
        }
    }

    /// Automatically update the min and max of the graph
    public GraphWidget autoUpdateRange() {
        this.autoUpdateRange = true;
        return this;
    }

    public void addDataPoint(Float value) {
        if (value == null) return;
        if (autoUpdateRange) {
            if (getMaxValue() < value) {
                setMaxValue(value + 10);
                float diff = getMaxValue() - getPrevMaxValue();
                setMinValue(getMinValue() + diff);
            }
            if (getMinValue() > value) {
                setMinValue(value - 10);
                float diff = getPrevMinValue() - getMinValue();
                setMaxValue(getMaxValue() - diff);
            }
        }

        int index = (head) % maxDataPoints;
        dataPoints[index] = Math.clamp(value, minValue, maxValue);
        head = (head + 1) % maxDataPoints; // Buffer full, overwrite oldest and move head
    }

    private List<float[]> getInterpolatedPoints() {
        List<float[]> points = new ArrayList<>();
        if (dataPoints.length < 2) return points;

        float xStep = gWidth / (dataPoints.length - 1);
        float range = Math.max(maxValue - minValue, 0.0001f);

        float[] yVals = new float[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            int index = (head + i) % dataPoints.length;
            yVals[i] = y + gHeight - ((dataPoints[index] - minValue) / range * gHeight);
        }

        // Calculate Monotone Cubic Spline Tangents
        float[] m = new float[dataPoints.length];
        for (int i = 0; i < dataPoints.length; i++) {
            if (i == 0) {
                m[i] = yVals[1] - yVals[0];
            } else if (i == dataPoints.length - 1) {
                m[i] = yVals[dataPoints.length - 1] - yVals[dataPoints.length - 2];
            } else {
                float sPrev = yVals[i] - yVals[i - 1];
                float sNext = yVals[i + 1] - yVals[i];

                if (sPrev * sNext <= 0) {
                    m[i] = 0;
                } else {
                    m[i] = (sPrev + sNext) / 2.0f;
                    float maxMag = 3.0f * Math.min(Math.abs(sPrev), Math.abs(sNext));
                    if (Math.abs(m[i]) > maxMag) {
                        m[i] = Math.signum(m[i]) * maxMag;
                    }
                }
            }
        }

        int stepsPerSegment = 32;
        float stepSize = 1.0f / stepsPerSegment;

        for (int i = 0; i < dataPoints.length - 1; i++) {
            float y0 = yVals[i];
            float y1 = yVals[i + 1];
            float m0 = m[i];
            float m1 = m[i + 1];
            float x0 = x + i * xStep;

            for (int step = 0; step <= stepsPerSegment; step++) {
                // Avoid redundant overlapping nodes between adjoining segments
                if (step == stepsPerSegment && i < dataPoints.length - 2) continue;

                float t = step * stepSize;
                float t2 = t * t;
                float t3 = t2 * t;

                // Cubic Hermite Spline formulation
                float h00 = 2 * t3 - 3 * t2 + 1;
                float h10 = t3 - 2 * t2 + t;
                float h01 = -2 * t3 + 3 * t2;
                float h11 = t3 - t2;

                float px = x0 + t * xStep;
                float py = h00 * y0 + h10 * m0 + h01 * y1 + h11 * m1;

                py = Math.clamp(py, y, y + gHeight);

                if (points.isEmpty()) {
                    points.add(new float[]{px, py});
                } else {
                    float[] lastP = points.getLast();
                    float distanceSq = (px - lastP[0]) * (px - lastP[0]) + (py - lastP[1]) * (py - lastP[1]);
                    if (distanceSq > 0.005f) {
                        points.add(new float[]{px, py});
                    }
                }
            }
        }
        return points;
    }

    // draw a continuous interpolated curve
    private void drawInterpolatedCurve(GuiGraphics graphics, List<float[]> points, int color, float thickness) {
        if (points.size() < 2) return;

        graphics.guiRenderState.submitGuiElement(
                new InterpolatedCurveRenderState(points, thickness, color, new Matrix3x2f(graphics.pose()), CustomRenderLayers.QUADS_CUSTOM_BLEND, (int) gWidth, (int) gHeight, graphics.scissorStack.peek())
        );
    }

    // draw a gradient shadow under the curve
    private void drawGradientShadow(GuiGraphics graphics, List<float[]> points, float bottomY, int startColor, int endColor) {
        if (points.size() < 2) return;

       graphics.guiRenderState.submitGuiElement(
                new GradientShadowRenderState(points,bottomY, startColor, endColor, new Matrix3x2f(graphics.pose()), RenderPipelines.DEBUG_QUADS, (int) gWidth, (int) gHeight, graphics.scissorStack.peek())
        );
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY) {
        if (valueSupplier != null) {
            addDataPoint(getValue());
        }

        // Safety check. Happens on startup
        if(offset < 0) computeOffset();

        if(graphColorRainbow) graphColor = ColorHelper.getRainbowColor(100);

   //     DrawHelper.enableScissor(widgetBox);

        // Draw gradient background with rounded corners
        if (!isInEditor) {
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + offset,
                    y,
                    false,
                    true,
                    false,
                    false,
                    gWidth,
                    gHeight,
                    4,
                    backgroundColor.getRGB()
            );
        }

        // Draw grid lines and value markings
        if (showGrid) {
            //TODO: The valueScale is too small when no. of grid lines is greater than 21 (20 is the barely visible threshold)

            for (int i = 1; i <= gridLines; i++) {
                float yPos = y + stepY * i;
                DrawHelper.drawHorizontalLine(graphics, x + offset, gWidth, yPos, 0.5f, 0x4DFFFFFF); // Semi-transparent white

                // Draw value labels on the left axis
                float value = maxValue - (i * valueStep);
                String valueText = formatValue(value);

                float texWidth = mc.font.width(valueText);

                //Scale the Component to its proper position and size with grid lines
                DrawHelper.scaleAndPosition(graphics.pose(), x + offset - texWidth/2.0f, yPos - (mc.font.lineHeight * valueScale) / 2.0f,texWidth,mc.font.lineHeight * valueScale, valueScale);
                graphics.drawString(mc.font, valueText, Math.round(x + offset - texWidth), (int) (yPos - (mc.font.lineHeight * valueScale) / 2), 0xFFFFFFFF, true);
                DrawHelper.stopScaling(graphics.pose());
            }

            x += offset;

            // Draw vertical grid lines (time axis)
            float stepX = gWidth / 5; // 5 vertical lines
            for (int i = 1; i < 5; i++) {
                float xPos = x + stepX * i;
                DrawHelper.drawVerticalLine(graphics, xPos, y, gHeight, 0.5f, 0x4DFFFFFF);
            }
        }

        List<float[]> points = getInterpolatedPoints();


        // Draw shadow effect under the graph
        drawGradientShadow(
                graphics, points, y + gHeight,
                ColorHelper.changeAlpha(graphColor, 60).getRGB(),
                0x00000000
        );

        drawInterpolatedCurve(graphics, points, graphColor.getRGB(), lineThickness);

        DrawHelper.drawChromaText(
                graphics, label,
                x + 5, y + 5,
                1.0f, 0.8f, 1.0f, 0.05f, true
        );

        if (!points.isEmpty()) {
            float[] livePoint = points.getLast();

            DrawHelper.drawFilledCircle(graphics, livePoint[0], livePoint[1], 0.9f, 0x26FFFFFF);
        }


        // Draw axes
        DrawHelper.drawHorizontalLine(graphics, x, gWidth, y + gHeight - 1, 1.0f, 0xFFFFFFFF); // X-axis
        DrawHelper.drawVerticalLine(graphics, x, y, gHeight, 1.0f, 0xFFFFFFFF); // Y-axis

        // Draw min and max value labels with formatted values
        /*
        DrawHelper.scaleAndPosition(context.getMatrices(),x - 5,y,0.5f);
        String formattedMaxVal = formatValue(maxValue);
        context.drawText(mc.font, formattedMaxVal, x - 5 - mc.font.gWidth(formattedMaxVal), y - 4, 0xFFFFFFFF, true);
        DrawHelper.stopScaling(context.getMatrices());

         */

        String formattedMinVal = formatValue(minValue);

        DrawHelper.scaleAndPosition(graphics.pose(), x - mc.font.width(formattedMinVal)/2.0f, y + gHeight,mc.font.width(formattedMinVal),mc.font.lineHeight * 0.5f, 0.5f);
        graphics.drawString(mc.font, formattedMinVal, x - mc.font.width(formattedMinVal), (int) (y + gHeight - 1), 0xFFFFFFFF, true);
        DrawHelper.stopScaling(graphics.pose());

        if(showGrid) x -= offset;

        this.widgetBox.setDimensions(x, y, gWidth + offset, gHeight, canScale);
     //   DrawHelper.disableScissor();

        if (menu != null) menu.set(getX(), getY(), (int) Math.ceil(getHeight()));
    }

    // format large values (like: 1000 -> 1K, 1000000 -> 1M)
    private String formatValue(float value) {
        float absVal = Math.abs(value);

        if (absVal >= 1_000_000) {
            long rounded = Math.round(value / 1_000_000f);
            return String.format("%dM", rounded);
        } else if (absVal >= 1_000) {
            long rounded = Math.round(value / 1_000f);
            return String.format("%dK", rounded);
        } else {
            return String.format("%d", (int) value);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return menu.toggleDisplay(widgetBox, mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    public void createMenu() {
        ContextMenuProperties properties = ContextMenuProperties.builder().skin(new MinecraftSkin(MinecraftSkin.PanelColor.FOREST_GREEN)).build();
        menu = new ContextMenu<>(getX(), (int) (getY() + widgetBox.getHeight()), properties);

        menu.addOption(new BooleanOption(Component.literal("Show Grid"),
                () -> this.showGrid, value -> {
                        this.showGrid = value;
                        this.computeOffset();
                },
                BooleanOption.BooleanType.YES_NO)
                .description(Component.literal("Shows a grid and Y axis values"))
        );
        menu.addOption(new DoubleOption(Component.literal("Number of Grid Lines"),
                1, 25, 1,
                () -> (double) this.gridLines, value -> {
                       this.setGridLines(value.intValue());
                       this.computeOffset();
                }, menu)
                .renderWhen(() -> this.showGrid)
        );
        menu.addOption(new ColorOption(Component.literal("Graph Line Color"),
                () -> this.graphColor, value -> this.graphColor = value, menu)
                .description(Component.literal("Specify the color you want for the graph's lines"))
        );
        menu.addOption(new BooleanOption(Component.literal("Rainbow Graph Line Color"),
                () -> this.graphColorRainbow, value -> this.graphColorRainbow = value)
                .description(Component.literal("Color your graph line with funny rainbow"))
                .withComplexity(Option.Complexity.Pro)
        );
        menu.addOption(new ColorOption(Component.literal("Graph Background Color"),
                () -> this.backgroundColor, value -> this.backgroundColor = value, menu)
                .description(Component.literal("Specify the color you want for the graph's background"))
        );
        menu.addOption(new DoubleOption(Component.literal("Line Thickness"),
                0.5f, 1f, 0.01f,
                () -> (double) this.lineThickness, value -> this.lineThickness = value.floatValue(), menu)
        );
    }

    private void computeOffset(){
        if(!showGrid) {
            offset = 0;
            return;
        }

        // The first Component is usually the largest but a negative value may occupy more gWidth so we check the first and last Component.
        // Idk how this will break.
        String firstText = formatValue(maxValue - valueStep);
        String lastText = formatValue(maxValue - (gridLines * valueStep));

        offset = Math.max(
                (int) Math.ceil(mc.font.width(firstText) * this.valueScale),
                (int) Math.ceil(mc.font.width(lastText) * this.valueScale)
        );
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.prevMinValue = this.minValue;
        this.minValue = minValue;
        this.valueStep = (maxValue - minValue) / (gridLines + 1);
    }

    public float getPrevMinValue() {
        return prevMinValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.prevMaxValue = this.maxValue;
        this.maxValue = maxValue;
        this.valueStep = (maxValue - minValue) / (gridLines + 1);
    }

    public float getPrevMaxValue() {
        return prevMaxValue;
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getGridLines() {
        return gridLines;
    }

    public void setGridLines(int gridLines) {
        this.gridLines = gridLines;
        this.stepY = gHeight / (gridLines + 1);
        this.valueStep = (maxValue - minValue) / (gridLines + 1);
        this.valueScale = (float) Math.clamp((stepY / 9.5), 0.0f, 1.0f);
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public Color getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(Color graphColor) {
        this.graphColor = graphColor;
    }

    @Override
    public Float getValue() {
        if (valueSupplier.get() instanceof Number number) {
            return number.floatValue();
        }
        return null;
    }

    @Override
    public void onClose() {
        super.onClose();
        menu.close();
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        super.writeToTag(tag);
        tag.putFloat("gWidth", gWidth);
        tag.putFloat("gHeight", gHeight);
        tag.putInt("maxDataPoints", maxDataPoints);
        tag.putFloat("minValue", minValue);
        tag.putFloat("maxValue", maxValue);
        tag.putInt("graphColor", graphColor.getRGB());
        tag.putInt("backgroundColor", backgroundColor.getRGB());
        tag.putFloat("lineThickness", lineThickness);
        tag.putBoolean("showGrid", showGrid);
        tag.putInt("gridLines", gridLines);
        tag.putString("label", label);
        tag.putBoolean("autoUpdateRange", autoUpdateRange);
        tag.putBoolean("graphColorRainbow", graphColorRainbow);
    }

    @Override
    public void readFromTag(CompoundTag tag) {
        super.readFromTag(tag);
        this.gWidth = tag.getFloat("gWidth").orElse(100f);
        this.gHeight = tag.getFloat("gHeight").orElse(50f);
        this.maxDataPoints = tag.getInt("maxDataPoints").orElse(100);
        this.minValue = tag.getFloat("minValue").orElse(0f);
        this.maxValue = tag.getFloat("maxValue").orElse(1f);
        this.graphColor = new Color(tag.getInt("graphColor").orElse(0xFF00FF00), true); // default green
        this.backgroundColor = new Color(tag.getInt("backgroundColor").orElse(0xFF000000), true); // default black
        this.lineThickness = tag.getFloat("lineThickness").orElse(1.0f);
        this.showGrid = tag.getBoolean("showGrid").orElse(true);
        this.gridLines = tag.getInt("gridLines").orElse(5);
        this.label = tag.getString("label").orElse("Graph");
        this.autoUpdateRange = tag.getBoolean("autoUpdateRange").orElse(false);
        this.graphColorRainbow = tag.getBoolean("graphColorRainbow").orElse(false);

        this.setMinValue(minValue);
        this.setMaxValue(maxValue);

        internal_init();
    }

    @Override
    public ContextMenu<?> getContextMenu() {
        return menu;
    }

    public static class GraphWidgetBuilder extends DynamicValueWidgetBuilder<GraphWidgetBuilder, GraphWidget> {
        private float gWidth = 100;
        private float gHeight = 50;
        private int maxDataPoints = 50;
        private float minValue = 0;
        private float maxValue = 100;
        private Color graphColor = new Color(0xFF00FF00, true);
        private Color backgroundColor = new Color(0x80000000, true);
        private float lineThickness = 1.0f;
        private boolean showGrid = true;
        private int gridLines = 4;
        private String label = "Graph";

        public GraphWidgetBuilder() {
        }

        public GraphWidgetBuilder label(String label) {
            this.label = label;
            return this;
        }

        public GraphWidgetBuilder gWidth(float gWidth) {
            this.gWidth = gWidth;
            return this;
        }

        public GraphWidgetBuilder gHeight(float gHeight) {
            this.gHeight = gHeight;
            return this;
        }

        public GraphWidgetBuilder maxDataPoints(int maxDataPoints) {
            this.maxDataPoints = maxDataPoints;
            return this;
        }

        public GraphWidgetBuilder minValue(float minValue) {
            this.minValue = minValue;
            return this;
        }

        public GraphWidgetBuilder maxValue(float maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public GraphWidgetBuilder graphColor(Color graphColor) {
            this.graphColor = graphColor;
            return this;
        }

        public GraphWidgetBuilder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public GraphWidgetBuilder lineThickness(float lineThickness) {
            this.lineThickness = lineThickness;
            return this;
        }

        public GraphWidgetBuilder showGrid(boolean showGrid) {
            this.showGrid = showGrid;
            return this;
        }

        public GraphWidgetBuilder gridLines(int no_of_gridLines) {
            this.gridLines = no_of_gridLines;
            return this;
        }

        @Override
        protected GraphWidgetBuilder self() {
            return this;
        }

        @Override
        public GraphWidget build() {
            GraphWidget widget = new GraphWidget(registryID, registryKey, modID, anchor, gWidth, gHeight, maxDataPoints, minValue, maxValue, graphColor, backgroundColor, lineThickness, showGrid, gridLines, label);
            widget.setPosition(x, y);
            widget.setDraggable(isDraggable);
            widget.setCanScale(shouldScale);
            widget.isVisible = isVisible;
            return widget;
        }
    }
}