package me.msmaciek.cleantablist;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class EmptyPlayerTablist extends JavaPlugin implements PacketListener {
    PacketListenerCommon listener;

    @Override
    public void onLoad() {
        if(PacketEvents.getAPI().isLoaded())
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        PacketEvents.getAPI().getSettings()
            .reEncodeByDefault(true)
            .checkForUpdates(false)
            .bStats(false);

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        listener = PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.LOW);
        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().getEventManager().unregisterListener(listener);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPacketType() != PacketType.Play.Server.PLAYER_INFO_UPDATE)
            return;

        var wrappedPacket = new WrapperPlayServerPlayerInfoUpdate(event);

        for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo pi : wrappedPacket.getEntries()) {
            pi.setListed(false);
        }
    }
}
