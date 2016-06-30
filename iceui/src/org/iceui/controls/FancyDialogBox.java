package org.iceui.controls;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;

public abstract class FancyDialogBox extends FancyAlertBox {

    private ButtonAdapter btnCancel;


    /**
     * Creates a new instance of the DialogBox control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     */
    public FancyDialogBox(ElementManager screen, Size size, boolean closeable) {
        this(screen, Vector2f.ZERO, size, closeable);
    }

    /**
     * Creates a new instance of the DialogBox control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     */
    public FancyDialogBox(ElementManager screen, Vector2f position, Size size, boolean closeable) {
        this(screen, UIDUtil.getUID(), position,
                screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
                screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
                screen.getStyle(size.toStyleName()).getString("defaultImg"),
                size,
                closeable);
    }

    /**
     * Creates a new instance of the AlertBox control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     */
    public FancyDialogBox(ElementManager screen, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
        this(screen, UIDUtil.getUID(), position, dimensions,
                screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
                screen.getStyle(size.toStyleName()).getString("defaultImg"),
                size,
                closeable);
    }

    /**
     * Creates a new instance of the Dialog control
     *
     * @param screen The screen control the Element is to be added to
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     * @param resizeBorders A Vector4f containg the border information used when resizing
     * the default image (x = N, y = W, z = E, w = S)
     * @param defaultImg The default image to use for the DialogBox window
     */
    public FancyDialogBox(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Size size, boolean closeable) {
        this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg,
                size,
                closeable);
    }

    /**
     * Creates a new instance of the DialogBox control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     */
    public FancyDialogBox(ElementManager screen, String UID, Vector2f position, Size size, boolean closeable) {
        this(screen, UID, position,
                screen.getStyle(size.toStyleName()).getVector2f("defaultSize"),
                screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
                screen.getStyle(size.toStyleName()).getString("defaultImg"),
                size,
                closeable);
    }

    /**
     * Creates a new instance of the AlertBox control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     */
    public FancyDialogBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
        this(screen, UID, position, dimensions,
                screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
                screen.getStyle(size.toStyleName()).getString("defaultImg"),
                size,
                closeable);
    }

    /**
     * Creates a new instance of the Dialog control
     *
     * @param screen The screen control the Element is to be added to
     * @param UID A unique String identifier for the Element
     * @param position A Vector2f containing the x/y position of the Element
     * @param dimensions A Vector2f containing the width/height dimensions of the Element
     * @param resizeBorders A Vector4f containg the border information used when resizing
     * the default image (x = N, y = W, z = E, w = S)
     * @param defaultImg The default image to use for the DialogBox window
     */
    public FancyDialogBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Size size, boolean closeable) {
        super(screen, UID, position, dimensions, resizeBorders, defaultImg, size, closeable);
    }

    /**
     * Sets the text of the Cancel button
     *
     * @param text String
     */
    public void setButtonCancelText(String text) {
        btnCancel.setText(text);
    }

    /**
     * Abstract method for handling Cancel button click event
     *
     * @param evt MouseButtonEvent
     * @param toggled boolean
     */
    public abstract void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled);

    /**
     * Sets the tooltip text to display when mouse hovers over the Cancel button
     *
     * @param tip String
     */
    public void setToolTipCancelButton(String tip) {
        this.btnCancel.setToolTipText(tip);
    }

    @Override
    public void createButtons(Element buttons) {
        super.createButtons(buttons);
        btnCancel = new CancelButton(screen, getUID() + ":btnCancel") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                onButtonCancelPressed(evt, toggled);
            }
        };
        btnCancel.setText("Cancel");
        buttons.addChild(btnCancel);
        form.addFormElement(btnCancel);
    }
}
