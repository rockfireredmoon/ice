package org.iceui.controls;

import icetone.controls.buttons.Button;
import icetone.core.BaseElement;
import icetone.core.Element;
import icetone.core.layout.Border;

/**
 * Helpers to add common styles to elements (as defined in the 'UI' theme
 * extensions).
 */
public class ElementStyle {

	public static Element altColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-alt");
		return label;
	}

	public static Element negativeColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-negative");
		return label;
	}

	public static Element positiveColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-postive");
		return label;
	}

	public static Element successColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-success");
		return label;
	}

	public static Element errorColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-error");
		return label;
	}

	public static Element warningColor(Element label) {
		normalColor(label);
		label.addStyleClass("color-warning");
		return label;
	}

	public static Element normalColor(Element label) {
		return label.removeStyleClass("color-alt").removeStyleClass("color-positive").removeStyleClass("color-error")
				.removeStyleClass("color-warning").removeStyleClass("color-negative")
				.removeStyleClass("color-positive");
	}

	public static Element tiny(Element label) {
		label.addStyleClass("fnt-tiny");
		return label;
	}

	public static Element mediumOutline(Element label) {
		label.addStyleClass("fnt-medium-outline");
		return label;
	}

	public static Element medium(Element label, boolean bold, boolean italic) {
		if (bold) {
			if (italic) {
				label.addStyleClass("fnt-medium-strong-italic");
			} else {
				label.addStyleClass("fnt-medium-strong");
			}
		} else {
			if (italic) {
				label.addStyleClass("fnt-medium-italic");
			} else {
				label.addStyleClass("fnt-medium");
			}
		}
		return label;
	}

	public static Element medium(Element label) {
		medium(label, false, false);
		return label;
	}

	public static Element normal(Element label, boolean bold, boolean italic) {
		return normal(label, bold, italic, false);
	}

	public static Element normal(Element label, boolean bold, boolean italic, boolean outline) {
		if (outline) {
			label.addStyleClass("fnt-default-outline");
		} else {
			if (bold) {
				if (italic) {
					label.addStyleClass("fnt-strong-italic");
				} else {
					label.addStyleClass("fnt-strong");
				}
			} else {
				if (italic) {
					label.addStyleClass("fnt-italic");
				} else {
					label.addStyleClass("fnt-default");
				}
			}
		}
		return label;
	}

	public static Element large(Element label) {
		label.addStyleClass("fnt-large");
		return label;
	}

	public static BaseElement normal(Element label) {
		normal(label, false, false);
		return label;
	}

	public static Button arrowButton(Button button, Border direction) {
		button.getButtonIcon().removeStyleClass("button-icon icon-up icon-down icon-back icon-forward");
		switch (direction) {
		case NORTH:
			button.getButtonIcon().addStyleClass("button-icon icon-up");
			break;
		case SOUTH:
			button.getButtonIcon().addStyleClass("button-icon icon-down");
			break;
		case WEST:
			button.getButtonIcon().addStyleClass("button-icon icon-back");
			break;
		case EAST:
			button.getButtonIcon().addStyleClass("button-icon icon-forward");
			break;
		default:
			break;
		}
		return button;
	}
}
