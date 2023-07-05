package draylar.tiered.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import draylar.tiered.api.BorderTemplate;
import draylar.tiered.config.ConfigInit;
import draylar.tiered.mixin.client.DrawableHelperAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class TieredTooltip {

    public static void renderTieredTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, int width, int height, BorderTemplate borderTemplate,
            TextRenderer textRenderer, ItemRenderer itemRenderer) {
        TooltipComponent tooltipComponent2;
        int t;
        int k;
        if (components.isEmpty()) {
            return;
        }
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components) {

            if (tooltipComponent == null) {
                continue;
            }

            k = tooltipComponent.getWidth(textRenderer);
            if (k > i) {
                i = k;
            }
            j += tooltipComponent.getHeight();
        }
        if (i < 64)
            i = 64;
        if (j < 16)
            j = 16;

        int l = x + 12;
        int m = y - 12;
        k = i;
        int n = j;
        if (l + i > width) {
            l -= 28 + i;
        }
        if (m + n + 6 > height) {
            m = height - n - 6;
        }
        matrices.push();
        float f = itemRenderer.zOffset;
        itemRenderer.zOffset = 400.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int backgroundColor = borderTemplate.getBackgroundGradient();
        // background
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + k + 3, m - 3, 400, backgroundColor, backgroundColor);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m + n + 3, l + k + 3, m + n + 4, 400, backgroundColor, backgroundColor);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m + n + 3, 400, backgroundColor, backgroundColor);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + n + 3, 400, backgroundColor, backgroundColor);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l + k + 3, m - 3, l + k + 4, m + n + 3, 400, backgroundColor, backgroundColor);

        int colorStart = borderTemplate.getStartGradient();
        int colorEnd = borderTemplate.getEndGradient();

        // border
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + n + 3 - 1, 400, colorStart, colorEnd);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l + k + 2, m - 3 + 1, l + k + 3, m + n + 3 - 1, 400, colorStart, colorEnd);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m - 3 + 1, 400, colorStart, colorStart);
        DrawableHelperAccessor.callFillGradient(matrix4f, bufferBuilder, l - 3, m + n + 2, l + k + 3, m + n + 3, 400, colorEnd, colorEnd);

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        int s = m;
        for (t = 0; t < components.size(); ++t) {
            tooltipComponent2 = components.get(t);

            if (tooltipComponent2 == null) {
                continue;
            }

            int nameCentering = 0;
            if (t == 0 && ConfigInit.CONFIG.centerName)
                nameCentering = i / 2 - tooltipComponent2.getWidth(textRenderer) / 2;
            tooltipComponent2.drawText(textRenderer, l + nameCentering, s, matrix4f, immediate);
            s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
        }
        immediate.draw();
        matrices.pop();
        s = m;
        for (t = 0; t < components.size(); ++t) {
            tooltipComponent2 = components.get(t);

            if (tooltipComponent2 == null) {
                continue;
            }

            tooltipComponent2.drawItems(textRenderer, l, s, matrices, itemRenderer, 400);
            s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
        }
        itemRenderer.zOffset = f;

        int border = borderTemplate.getIndex();
        int secondHalf = border > 7 ? 1 : 0;
        if (border > 7)
            border -= 8;

        matrices.push();
        RenderSystem.setShaderTexture(0, borderTemplate.getIdentifier());
        // left top corner
        DrawableHelper.drawTexture(matrices, l - 6, m - 6, 0 + secondHalf * 64, 0 + border * 16, 8, 8, 128, 128);
        // right top corner
        DrawableHelper.drawTexture(matrices, l + k - 2, m - 6, 56 + secondHalf * 64, 0 + border * 16, 8, 8, 128, 128);

        // left down corner
        DrawableHelper.drawTexture(matrices, l - 6, m + n - 2, 0 + secondHalf * 64, 8 + border * 16, 8, 8, 128, 128);
        // right down corner
        DrawableHelper.drawTexture(matrices, l + k - 2, m + n - 2, 56 + secondHalf * 64, 8 + border * 16, 8, 8, 128, 128);

        // middle header
        DrawableHelper.drawTexture(matrices, (l - 6 + l + k + 6) / 2 - 24, m - 9, 8 + secondHalf * 64, 0 + border * 16, 48, 8, 128, 128);
        // bottom footer
        DrawableHelper.drawTexture(matrices, (l - 6 + l + k + 6) / 2 - 24, m + n + 1, 8 + secondHalf * 64, 8 + border * 16, 48, 8, 128, 128);

        matrices.pop();
    }
}
