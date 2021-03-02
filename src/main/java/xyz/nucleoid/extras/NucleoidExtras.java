package xyz.nucleoid.extras;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.command.NucleoidCommandAliases;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;

public final class NucleoidExtras implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(NucleoidExtras.class);

    @Override
    public void onInitialize() {
        NucleoidIntegrations.get();

        NucleoidCommandAliases.register();
        ScheduledStop.register();

        ServerTickEvents.END_SERVER_TICK.register(NucleoidExtras::onServerTick);
        PlayerSendChatEvent.EVENT.register((player, content) -> {
            System.out.println("relay: " + content);
        });
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTicks();
        if (ticks % 20 == 0) {
            NucleoidExtrasConfig config = NucleoidExtrasConfig.get();
            if (config.isSidebarEnabled()) {
                NucleoidSidebar.get(server).update();
            }
        }

        NucleoidIntegrations integrations = NucleoidIntegrations.get();
        if (integrations != null) {
            integrations.tick();
        }
    }
}
