package org.iceui.controls.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseButtonListener;

public abstract class XColorPaletteTab extends Element implements XColorSelector.ColorTabPanel {

    private final static int LIGHTNESS_STEPS = 14;
    private int[] rowSizes;
    private List<ColorRGBA> palette = new ArrayList<ColorRGBA>();
    private final Element hex;
    private final Vector2f cellSize;
    private final float cellIndent;
    private final Element lightness;
    private ColorRGBA color = ColorRGBA.White;

    private void rebuildLightness() {
        // Lightness bar
        lightness.removeAllChildren();
        float[] hsv = getHSV(color);
        for (int i = 0; i < LIGHTNESS_STEPS; i++) {
            Color c = new Color(Color.HSBtoRGB(hsv[0], hsv[1], (float) i / (float) LIGHTNESS_STEPS));
            ColorRGBA col = colorToColorRGBA(c);
            ColorCell cell = new ColorCell(screen, col) {
                @Override
                public void onMouseLeftReleased(MouseButtonEvent evt) {
                    XColorPaletteTab.this.color = col;
                    XColorPaletteTab.this.onChange(color);
                    rebuildHex();
                }
            };
            lightness.addChild(cell);
        }
    }

    private void rebuildHex() {
        float[] hsv = getHSV(color);
        hex.removeAllChildren();
        Iterator<ColorRGBA> rgbIt = palette.iterator();
        float y = 0;
        for (int rs : rowSizes) {
            if (!rgbIt.hasNext()) {
                break;
            }

            float x = (hex.getWidth() - (rs * cellSize.x)) / 2f;

            for (int j = 0; j < rs; j++) {
                if (!rgbIt.hasNext()) {
                    break;
                }
                final ColorRGBA col = rgbIt.next();
                float[] chsv = getHSV(col);
                ColorCell cell = new ColorCell(screen, colorToColorRGBA(new Color(Color.HSBtoRGB(chsv[0], chsv[1], hsv[2])))) {
                    public void onMouseLeftReleased(MouseButtonEvent evt) {
                        XColorPaletteTab.this.color = col;
                        XColorPaletteTab.this.onChange(color);
                        rebuildLightness();
                    }
                };
                cell.setPosition(x, y);
                hex.addChild(cell);
                x += cellSize.x;
            }
            y += cellSize.y - cellIndent;
        }
    }

    private float[] getHSV(ColorRGBA c) {
        float[] hsv = new float[3];
        hsv = Color.RGBtoHSB((int) (c.getRed() * 255f), (int) (c.getGreen() * 255f), (int) (c.getBlue() * 255f), hsv);
        return hsv;
    }

    private ColorRGBA colorToColorRGBA(Color c) {
        ColorRGBA col = new ColorRGBA((float) c.getRed() / 255f, (float) c.getGreen() / 255f, (float) c.getBlue() / 255f, 1f);
        return col;
    }

    abstract class ColorCell extends Element implements MouseButtonListener {

        protected final ColorRGBA col;

        ColorCell(ElementManager screen, ColorRGBA col) {
            super(screen, UIDUtil.getUID(), cellSize, Vector4f.ZERO, screen.getStyle("ColorPalette").getString("cellImg"));
            getElementMaterial().setColor("Color", col);
            setIgnoreMouse(false);
            this.col = col;
        }

        public void onMouseLeftPressed(MouseButtonEvent evt) {
        }

        public void onMouseRightPressed(MouseButtonEvent evt) {
        }

        public void onMouseRightReleased(MouseButtonEvent evt) {
        }
    }

    public XColorPaletteTab(ElementManager screen) {
        this(screen, 7);
    }

    public XColorPaletteTab(ElementManager screen, int cells) {
        super(screen);
        setLayoutManager(new MigLayout(screen, "wrap 1, fill", "[align center]", "[align center][align center]"));

        int rows = (cells * 2) - 1;
        rowSizes = new int[rows];
        int rowCount = cells;
        for (int i = 0; i < rows; i++) {
            rowSizes[i] = rowCount;
            if (i < (cells - 1)) {
                rowCount++;
            } else {
                rowCount--;
            }
        }
        int maxw = (cells * 2) - 1;

        // Generate color hex wheel thing
        ColorRGBA startStart = new ColorRGBA(0, 0, 1, 1);
        ColorRGBA startEnd = new ColorRGBA(1, 0, 1, 1);
        ColorRGBA midStart = new ColorRGBA(0, 1, 1, 1);
        ColorRGBA midEnd = new ColorRGBA(1, 0, 0, 1);
        ColorRGBA endStart = new ColorRGBA(0, 1, 0, 1);
        ColorRGBA endEnd = new ColorRGBA(1, 1, 0, 1);

        ColorRGBA start;
        ColorRGBA end;
        ColorRGBA mid;
        int hw = (int) Math.ceil((double) rowSizes.length / 2d);
        for (int i = 0; i < rowSizes.length; i++) {
            int r = rowSizes[i];

            final float progress = ((float) i / ((float) hw - 1));

            mid = ColorRGBA.White.clone();
            if (i < hw) {
                start = startStart.clone();
                start.interpolate(midStart, progress);

                end = startEnd.clone();
                end.interpolate(midEnd, progress);

                mid = start.clone();
                mid.interpolate(end, 0.5f);
                mid.interpolate(ColorRGBA.White, progress);
            } else {
                start = midStart.clone();
                start.interpolate(endStart, progress - 1f);

                end = midEnd.clone();
                end.interpolate(endEnd, progress - 1f);

                mid = start.clone();
                mid.interpolate(end, 0.5f);
                mid.interpolate(ColorRGBA.White, 1f - (progress - 1f));
            }

            ColorRGBA row;
            float rowHw = (float) r / 2f;
            int ahw = (int) (rowHw + 0.5f);
            int rightCells = r - ahw;
            for (int j = 0; j < r; j++) {
                if (j >= ahw) {
                    row = mid.clone();
                    float p = (float) (j + 1 - ahw) / (float) rightCells;
                    row.interpolate(end, p);
                } else {
                    float p = (float) j / (float) Math.floor(rowHw - 0.5);
                    row = start.clone();
                    row.interpolate(mid, p);
                }
                palette.add(row);
            }

        }

        cellIndent = screen.getStyle("ColorPalette").getFloat("cellIndent");
        cellSize = screen.getStyle("ColorPalette").getVector2f("cellSize");

        // Hex
        hex = new Element(screen, new Vector2f(maxw * cellSize.x, (rowSizes.length * (cellSize.y - cellIndent)) + cellIndent));
        addChild(hex);

        // Lightness
        lightness = new Element(screen);
        lightness.setLayoutManager(new FlowLayout(0, BitmapFont.Align.Center));
        addChild(lightness);


        //
        rebuild();
    }

    public void setPalette(List<ColorRGBA> palette) {
        this.palette = palette;
        rebuild();
    }

    private void rebuild() {
        rebuildLightness();
        rebuildHex();
    }

    public void setColor(ColorRGBA color) {
        this.color = color == null ? null : color.clone();
        rebuild();
    }
}
