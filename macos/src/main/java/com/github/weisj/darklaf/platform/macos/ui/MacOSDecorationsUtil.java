/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.weisj.darklaf.platform.macos.ui;

import com.github.weisj.darklaf.platform.macos.JNIDecorationsMacOS;
import com.github.weisj.darklaf.util.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class MacOSDecorationsUtil {

    private static final String FULL_WINDOW_CONTENT_KEY = "apple.awt.fullWindowContent";
    private static final String TRANSPARENT_TITLE_BAR_KEY = "apple.awt.transparentTitleBar";

    protected static CompletableFuture<DecorationInformation> installDecorations(final JRootPane rootPane) {
        if (rootPane == null) return null;
        Window window = SwingUtilities.getWindowAncestor(rootPane);
        long windowHandle = JNIDecorationsMacOS.getComponentPointer(window);
        if (windowHandle == 0) {
            DecorationInformation information = new DecorationInformation(0, false, false,
                                                                          false, rootPane, false, 0, 0);
            return CompletableFuture.completedFuture(information);
        }
        JNIDecorationsMacOS.retainWindow(windowHandle);
        boolean fullWindowContent = isFullWindowContentEnabled(rootPane);
        boolean transparentTitleBar = isTransparentTitleBarEnabled(rootPane);
        float titleFontSize = (float) JNIDecorationsMacOS.getTitleFontSize(windowHandle);
        int titleBarHeight = (int) JNIDecorationsMacOS.getTitleBarHeight(windowHandle);

        boolean jniInstall = !SystemInfo.isJavaVersionAtLeast("12");
        if (!jniInstall) {
            setTransparentTitleBarEnabled(rootPane, true);
            setFullWindowContentEnabled(rootPane, true);
        } else {
            JNIDecorationsMacOS.installDecorations(windowHandle);
        }
        boolean titleVisible = SystemInfo.isMacOSMojave;
        JNIDecorationsMacOS.setTitleEnabled(windowHandle, titleVisible);
        if (titleVisible) {
            boolean isDarkTheme = UIManager.getBoolean("Theme.dark");
            JNIDecorationsMacOS.setDarkTheme(windowHandle, isDarkTheme);
        }
        DecorationInformation information = new DecorationInformation(windowHandle, fullWindowContent,
                                                                      transparentTitleBar, jniInstall,
                                                                      rootPane, titleVisible,
                                                                      titleBarHeight, titleFontSize);
        return processCocoaEvents().thenApply((v) -> information);
    }

    protected static CompletableFuture<Void> uninstallDecorations(final DecorationInformation information) {
        if (information == null || information.windowHandle == 0) return CompletableFuture.completedFuture(null);
        if (information.jniInstalled) {
            JNIDecorationsMacOS.uninstallDecorations(information.windowHandle);
        } else {
            setFullWindowContentEnabled(information.rootPane, information.fullWindowContentEnabled);
            setTransparentTitleBarEnabled(information.rootPane, information.transparentTitleBarEnabled);
        }
        JNIDecorationsMacOS.setTitleEnabled(information.windowHandle, true);
        JNIDecorationsMacOS.releaseWindow(information.windowHandle);
        return processCocoaEvents();
    }

    private static boolean isFullWindowContentEnabled(final JRootPane rootPane) {
        return Boolean.TRUE.equals(rootPane.getClientProperty(FULL_WINDOW_CONTENT_KEY));
    }

    private static boolean isTransparentTitleBarEnabled(final JRootPane rootPane) {
        return Boolean.TRUE.equals(rootPane.getClientProperty(TRANSPARENT_TITLE_BAR_KEY));
    }

    private static void setFullWindowContentEnabled(final JRootPane rootPane, final boolean enabled) {
        rootPane.putClientProperty(FULL_WINDOW_CONTENT_KEY, enabled);
    }

    private static void setTransparentTitleBarEnabled(final JRootPane rootPane, final boolean enabled) {
        rootPane.putClientProperty(TRANSPARENT_TITLE_BAR_KEY, enabled);
    }

    private static CompletableFuture<Void> processCocoaEvents() {
        CompletableFuture<Void> res = new CompletableFuture<>();
        JNIDecorationsMacOS.queueNotify(() -> res.complete(null));
        return res;
    }
}
