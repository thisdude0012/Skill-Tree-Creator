/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.util.FormattedCharSequence
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package daripher.skilltree.mixin;

import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EditBox.class})
public interface EditBoxAccessor {
    @Accessor
    @Nullable
    public String getSuggestion();

    @Accessor
    public int getDisplayPos();

    @Accessor
    public int getHighlightPos();

    @Accessor
    public int getFrame();

    @Accessor
    public int getMaxLength();

    @Accessor
    public BiFunction<String, Integer, FormattedCharSequence> getFormatter();

    @Invoker
    public void invokeRenderHighlight(GuiGraphics var1, int var2, int var3, int var4, int var5);
}

