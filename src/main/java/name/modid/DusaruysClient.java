package name.modid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DusaruysClient implements ModInitializer, ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("dusaruysclient");

    // ==================== GUI ====================
    public static class ClickGuiScreen extends Screen {
        private static final int WINDOW_WIDTH = 277;
        private static final int WINDOW_HEIGHT = 573;
        private static final int GAP = 18;
        private static final int CORNER_RADIUS = 23;
        private static final int BG_COLOR = 0xCC1A2A1A;

        private float animationProgress = 0f;
        private long startTime = 0;

        public ClickGuiScreen() {
            super(Text.literal("ClickGUI"));
        }

        @Override
        protected void init() {
            super.init();
            animationProgress = 0f;
            startTime = System.currentTimeMillis();
        }

        @Override
        public void tick() {
            super.tick();
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < 300) {
                animationProgress = MathHelper.clamp((float) elapsed / 300f, 0f, 1f);
            } else {
                animationProgress = 1f;
            }
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);

            context.fill(0, 0, this.width, this.height, 0x66000000);

            int totalWidth = WINDOW_WIDTH * 5 + GAP * 4;
            int startX = (this.width - totalWidth) / 2;
            int startY = (this.height - WINDOW_HEIGHT) / 2;

            float scale = 0.8f + 0.2f * animationProgress;
            int offsetX = (int) ((1 - scale) * WINDOW_WIDTH / 2);
            int offsetY = (int) ((1 - scale) * WINDOW_HEIGHT / 2);

            for (int i = 0; i < 5; i++) {
                int x = startX + i * (WINDOW_WIDTH + GAP) + offsetX;
                int y = startY + offsetY;
                int w = (int) (WINDOW_WIDTH * scale);
                int h = (int) (WINDOW_HEIGHT * scale);

                context.fillRounded(x, y, x + w, y + h, CORNER_RADIUS, BG_COLOR);
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 256) {
                this.close();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }

    // ==================== Entrypoints ====================
    @Override
    public void onInitialize() {
        LOGGER.info("DusaruysClient loaded!");
    }

    @Override
    public void onInitializeClient() {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.clickgui.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.clickgui"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.wasPressed()) {
                if (client.currentScreen instanceof ClickGuiScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });
    }
}
