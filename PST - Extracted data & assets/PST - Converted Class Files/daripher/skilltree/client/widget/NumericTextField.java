/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.widget.TextField;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class NumericTextField
extends TextField {
    private static final Predicate<String> DEFAULT_FILTER = NumericTextField::isNumericString;
    @Nullable
    private Consumer<Double> numericResponder;
    private double defaultValue;

    public NumericTextField(int x, int y, int width, int height, double defaultValue) {
        super(x, y, width, height, NumericTextField.formatDefaultValue(defaultValue));
        this.setDefaultValue(defaultValue);
        this.setSoftFilter(DEFAULT_FILTER);
    }

    public void m_94164_(@NotNull String text) {
        super.m_94164_(text);
        this.onNumericValueChange();
    }

    public void m_94199_(int length) {
        super.m_94199_(length);
        this.onNumericValueChange();
    }

    public void m_94144_(@NotNull String text) {
        super.m_94144_(text);
        this.onNumericValueChange();
    }

    @Override
    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        boolean pressed = super.m_7933_(keyCode, scanCode, modifiers);
        this.onNumericValueChange();
        return pressed;
    }

    public NumericTextField setNumericFilter(Predicate<Double> filter) {
        this.setSoftFilter(DEFAULT_FILTER.and(this.createNumericFilter(filter)));
        return this;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getNumericValue() {
        try {
            return Double.parseDouble(this.m_94155_());
        }
        catch (NumberFormatException exception) {
            return this.defaultValue;
        }
    }

    public void setNumericResponder(@Nullable Consumer<Double> numericResponder) {
        this.numericResponder = numericResponder;
    }

    private void onNumericValueChange() {
        if (this.numericResponder != null) {
            this.numericResponder.accept(this.getNumericValue());
        }
    }

    private Predicate<String> createNumericFilter(Predicate<Double> filter) {
        return s -> filter.test(Double.parseDouble(s));
    }

    private static String formatDefaultValue(double defaultValue) {
        String formatted = String.format(Locale.ENGLISH, "%.3f", defaultValue);
        while (formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }

    private static boolean isNumericString(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}

