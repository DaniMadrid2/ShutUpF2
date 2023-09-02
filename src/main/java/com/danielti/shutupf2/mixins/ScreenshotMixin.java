package com.danielti.shutupf2.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;


@Mixin(Screenshot.class)
public class ScreenshotMixin {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");


    /**
     * @author danielti
     * @reason remove the saved screenshot message
     */
    @Overwrite
    private static void _grab(File p_92306_, @Nullable String p_92307_, RenderTarget p_92310_, Consumer<Component> p_92311_) {
        NativeImage nativeimage = Screenshot.takeScreenshot(p_92310_);
        File file1 = new File(p_92306_, "screenshots");
        file1.mkdir();
        File file2;
        if (p_92307_ == null) {
            String s = DATE_FORMAT.format(new Date());
            int i = 1;

            while(true) {
                File file = new File(file1, s + (i == 1 ? "" : "_" + i) + ".png");
                if (!file.exists()) {
                    file2=file;
                    break;
                }

                ++i;
            }
        } else {
            file2 = new File(file1, p_92307_);
        }

        net.minecraftforge.client.event.ScreenshotEvent event = net.minecraftforge.client.ForgeHooksClient.onScreenshot(nativeimage, file2);
        if (event.isCanceled()) {
            p_92311_.accept(event.getCancelMessage());
            return;
        }
        final File target = event.getScreenshotFile();

        Util.ioPool().execute(() -> {
            try {
                nativeimage.writeToFile(target);
                if (event.getResultMessage() != null&&!event.getResultMessage().equals(Component.EMPTY))
                    p_92311_.accept(event.getResultMessage());
            } catch (Exception exception) {
                LogUtils.getLogger().warn("Couldn't save screenshot", (Throwable)exception);
                //p_92311_.accept(new TranslatableComponent("screenshot.failure", exception.getMessage()));
            } finally {
                nativeimage.close();
            }

        });
    }
}
