package com.tanishisherewith.dynamichud.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.MathAnimations;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.BooleanOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.ColorOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.DoubleOption;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import com.twelvemonkeys.lang.Validate;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

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
    private final float[] dataPoints;
    private int head = 0;
    private int maxDataPoints;
    private float minValue, prevMinValue;
    private float maxValue, prevMaxValue;
    private Color graphColor;
    private Color backgroundColor;
    private float lineThickness;
    private boolean showGrid;
    private int gridLines;
    private float width;
    private float height;
    private String label;
    /// Automatically update the min and max of the graph
    private boolean autoUpdateRange = false;

    public GraphWidget(String registryID, String registryKey, String modId, Anchor anchor, float width, float height, int maxDataPoints, float minValue, float maxValue, Color graphColor, Color backgroundColor, float lineThickness, boolean showGrid, int gridLines, String label) {
        super(DATA, modId, anchor, registryID, registryKey);
        Validate.isTrue(maxDataPoints > 2, "MaxDataPoints should be more than 2.");
        this.dataPoints = new float[maxDataPoints];
        this.width = width;
        this.height = height;
        this.maxDataPoints = maxDataPoints;
        this.setMinValue(minValue);
        this.setMaxValue(maxValue);
        this.graphColor = graphColor;
        this.backgroundColor = backgroundColor;
        this.lineThickness = lineThickness;
        this.showGrid = showGrid;
        this.gridLines = gridLines;
        this.label = label.trim();
        this.widgetBox = new WidgetBox(x, y, (int) width, (int) height);
        setTooltipText(Text.of("Graph displaying: " + label));
        createMenu();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    public GraphWidget() {
        this(DynamicValueRegistry.GLOBAL_ID, "unknown", "unknown", Anchor.CENTER, 0, 0, 10, 0, 10, Color.RED, Color.GREEN, 0, false, 0, "empty");
    }

    @Override
    public void init() {
        // Initialize the buffer with minValue, mimicking ArrayList behavior
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
        dataPoints[index] = MathHelper.clamp(value, minValue, maxValue);
        head = (head + 1) % maxDataPoints; // Buffer full, overwrite oldest and move head
    }

    private List<float[]> getInterpolatedPoints() {
        List<float[]> points = new ArrayList<>();
        if (dataPoints.length < 2) return points;

        float xStep = width / (dataPoints.length - 1);
        for (int i = 0; i < dataPoints.length - 1; i++) {
            int index1 = (head + i) % dataPoints.length;
            int index2 = (head + i + 1) % dataPoints.length;

            float x1 = x + i * xStep;
            float y1 = y + height - ((dataPoints[index1] - minValue) / (maxValue - minValue) * height);
            float x2 = x + (i + 1) * xStep;
            float y2 = y + height - ((dataPoints[index2] - minValue) / (maxValue - minValue) * height);

            // Add interpolated points using hermite spline (simplified)
            for (float t = 0; t <= 1; t += 0.03f) {
                float t2 = t * t;
                float t3 = t2 * t;
                float h00 = 2 * t3 - 3 * t2 + 1;
                float h10 = t3 - 2 * t2 + t;
                float h01 = -2 * t3 + 3 * t2;

                float px = h00 * x1 + h10 * xStep + h01 * x2;
                float py = h00 * y1 + h10 * (y2 - y1) + h01 * y2;
                points.add(new float[]{px, py});
            }
        }
        return points;
    }

    // draw a continuous interpolated curve
    private void drawInterpolatedCurve(Matrix4f matrix, List<float[]> points, int color, float thickness) {
        if (points.size() < 2) return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < points.size(); i++) {
            float[] point = points.get(i);
            float x = point[0];
            float y = point[1];

            // Create a thick line by offsetting vertices perpendicular to the curve
            float dx = (i < points.size() - 1) ? points.get(i + 1)[0] - x : x - points.get(i - 1)[0];
            float dy = (i < points.size() - 1) ? points.get(i + 1)[1] - y : y - points.get(i - 1)[1];
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length == 0) continue;

            float offsetX = (thickness * 0.5f * dy) / length;
            float offsetY = (thickness * 0.5f * -dx) / length;

            bufferBuilder.vertex(matrix, x + offsetX, y + offsetY, 0).color(color);
            bufferBuilder.vertex(matrix, x - offsetX, y - offsetY, 0).color(color);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    // draw a gradient shadow under the curve
    private void drawGradientShadow(Matrix4f matrix, List<float[]> points, float bottomY, int startColor, int endColor) {
        if (points.size() < 2) return;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        for (float[] point : points) {
            float x = point[0];
            float y = point[1];

            bufferBuilder.vertex(matrix, x, y, 0).color(startColor);
            bufferBuilder.vertex(matrix, x, bottomY, 0).color(endColor);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        if (valueSupplier != null) {
            addDataPoint(getValue());
        }

        // Apply pulse1 animation to background alpha
        float animatedAlpha = MathHelper.clamp(MathAnimations.pulse1(backgroundColor.getAlpha() / 255.0f, 0.2f, 0.001f), 0f, 1.0f);
        Color animatedBackgroundColor = ColorHelper.changeAlpha(backgroundColor, (int) (animatedAlpha * 255));
        Color gradientColor = ColorHelper.changeAlpha(backgroundColor, (int) (animatedAlpha * 255 * 0.7f));

        DrawHelper.enableScissor(widgetBox);

        // Draw gradient background with rounded corners
        if (!isInEditor) {
            DrawHelper.drawRoundedGradientRectangle(
                    matrix,
                    animatedBackgroundColor,
                    gradientColor,
                    gradientColor,
                    animatedBackgroundColor,
                    x, y, width, height, 5,
                    false, true, false, false
            );
        }

        // Draw grid lines and value markings
        if (showGrid) {
            float stepY = height / (gridLines + 1);
            float valueStep = (maxValue - minValue) / (gridLines + 1);

            //TODO: The scale is too small for grid lines for than 21 (20 is the barely visible threshold)
            float scale = (float) MathHelper.clamp((stepY / 9.5), 0.0f, 1.0f);

            for (int i = 1; i <= gridLines; i++) {
                float yPos = y + stepY * i;
                DrawHelper.drawHorizontalLine(matrix, x, width, yPos, 0.5f, 0x4DFFFFFF); // Semi-transparent white

                // Draw value labels on the left axis
                float value = maxValue - (i * valueStep);
                String valueText = formatValue(value);

                //Scale the text to its proper position and size with grid lines
                DrawHelper.scaleAndPosition(context.getMatrices(), x - 2, yPos, scale);
                context.drawText(mc.textRenderer, valueText, x - mc.textRenderer.getWidth(valueText), (int) (yPos - (mc.textRenderer.fontHeight * scale) / 2.0f), 0xFFFFFFFF, true);
                DrawHelper.stopScaling(context.getMatrices());
            }

            // Draw vertical grid lines (time axis)
            float stepX = width / 5; // 5 vertical lines
            for (int i = 1; i < 5; i++) {
                float xPos = x + stepX * i;
                DrawHelper.drawVerticalLine(matrix, xPos, y, height, 0.5f, 0x4DFFFFFF);
            }
        }
        // Draw interpolated graph curve
        List<float[]> points = getInterpolatedPoints();
        drawInterpolatedCurve(matrix, points, graphColor.getRGB(), lineThickness);

        // Draw shadow effect under the graph
        drawGradientShadow(
                matrix, points, y + height,
                ColorHelper.changeAlpha(graphColor, 50).getRGB(),
                0x00000000
        );

        DrawHelper.drawChromaText(
                context, label,
                x + 5, y + 5,
                1.0f, 0.8f, 1.0f, 0.05f, true
        );

        // Draw axes
        DrawHelper.drawHorizontalLine(matrix, x, width, y + height, 1.0f, 0xFFFFFFFF); // X-axis
        DrawHelper.drawVerticalLine(matrix, x, y, height, 1.0f, 0xFFFFFFFF); // Y-axis

        // Draw min and max value labels with formatted values
        /*
        DrawHelper.scaleAndPosition(context.getMatrices(),x - 5,y,0.5f);
        String formattedMaxVal = formatValue(maxValue);
        context.drawText(mc.textRenderer, formattedMaxVal, x - 5 - mc.textRenderer.getWidth(formattedMaxVal), y - 4, 0xFFFFFFFF, true);
        DrawHelper.stopScaling(context.getMatrices());

         */

        DrawHelper.scaleAndPosition(context.getMatrices(), x - 5, y + height, 0.5f);
        String formattedMinVal = formatValue(minValue);
        context.drawText(mc.textRenderer, formattedMinVal, x - mc.textRenderer.getWidth(formattedMinVal), (int) (y + height - 4), 0xFFFFFFFF, true);
        DrawHelper.stopScaling(context.getMatrices());

        this.widgetBox.setDimensions(x, y, width, height,shouldScale, GlobalConfig.get().getScale());
        DrawHelper.disableScissor();

        if (menu != null) menu.set(getX(), getY(), (int) Math.ceil(getHeight()));
    }

    // format large values (like: 1000 -> 1K, 1000000 -> 1M)
    private String formatValue(float value) {
        if (Math.abs(value) >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000);
        } else if (Math.abs(value) >= 1_000) {
            return String.format("%.1fK", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        menu.toggleDisplay(widgetBox, mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void createMenu() {
        ContextMenuProperties properties = ContextMenuProperties.builder().build();
        menu = new ContextMenu<>(getX(), (int) (getY() + widgetBox.getHeight()), properties);

        menu.addOption(new BooleanOption(Text.of("Show Grid"),
                () -> this.showGrid, value -> this.showGrid = value,
                BooleanOption.BooleanType.YES_NO)
                .description(Text.of("Shows a grid and Y axis values"))
        );
        menu.addOption(new DoubleOption(Text.of("Number of Grid Lines"),
                1, 25, 1,
                () -> (double) this.gridLines, value -> this.gridLines = value.intValue(), menu)
                .renderWhen(() -> this.showGrid)
        );
        menu.addOption(new ColorOption(Text.of("Graph Line Color"),
                () -> this.graphColor, value -> this.graphColor = value, menu)
                .description(Text.of("Specify the color you want for the graph's lines"))
        );
        menu.addOption(new ColorOption(Text.of("Graph Background Color"),
                () -> this.backgroundColor, value -> this.backgroundColor = value, menu)
                .description(Text.of("Specify the color you want for the graph's background"))
        );
        menu.addOption(new DoubleOption(Text.of("Line Thickness"),
                0.5f, 5.0f, 0.1f,
                () -> (double) this.lineThickness, value -> this.lineThickness = value.floatValue(), menu)
        );
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        this.width = tag.getFloat("width");
        this.height = tag.getFloat("height");
        this.maxDataPoints = tag.getInt("maxDataPoints");
        this.minValue = tag.getFloat("minValue");
        this.maxValue = tag.getFloat("maxValue");
        this.graphColor = new Color(tag.getInt("graphColor"));
        this.backgroundColor = new Color(tag.getInt("backgroundColor"));
        this.lineThickness = tag.getFloat("lineThickness");
        this.showGrid = tag.getBoolean("showGrid");
        this.gridLines = tag.getInt("gridLines");
        this.label = tag.getString("label");
        this.widgetBox = new WidgetBox(x, y, (int) width, (int) height);

        createMenu();
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.prevMinValue = this.minValue;
        this.minValue = minValue;
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

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getGridLines() {
        return gridLines;
    }

    public void setGridLines(int gridLines) {
        this.gridLines = gridLines;
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
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putFloat("width", width);
        tag.putFloat("height", height);
        tag.putInt("maxDataPoints", maxDataPoints);
        tag.putFloat("minValue", minValue);
        tag.putFloat("maxValue", maxValue);
        tag.putInt("graphColor", graphColor.getRGB());
        tag.putInt("backgroundColor", backgroundColor.getRGB());
        tag.putFloat("lineThickness", lineThickness);
        tag.putBoolean("showGrid", showGrid);
        tag.putInt("gridLines", gridLines);
        tag.putString("label", label);
    }

    @Override
    public ContextMenu<?> getContextMenu() {
        return menu;
    }

    public static class GraphWidgetBuilder extends DynamicValueWidgetBuilder<GraphWidgetBuilder, GraphWidget> {
        private Anchor anchor = Anchor.CENTER;
        private float width = 100;
        private float height = 50;
        private int maxDataPoints = 50;
        private float minValue = 0;
        private float maxValue = 100;
        private Color graphColor = new Color(0xFF00FF00);
        private Color backgroundColor = new Color(0x80000000);
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

        public GraphWidgetBuilder anchor(Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public GraphWidgetBuilder width(float width) {
            this.width = width;
            return this;
        }

        public GraphWidgetBuilder height(float height) {
            this.height = height;
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
            GraphWidget widget = new GraphWidget(registryID, registryKey, modID, anchor, width, height, maxDataPoints, minValue, maxValue, graphColor, backgroundColor, lineThickness, showGrid, gridLines, label);
            widget.setPosition(x, y);
            widget.setDraggable(isDraggable);
            widget.setShouldScale(shouldScale);
            widget.isVisible = display;
            return widget;
        }
    }
}