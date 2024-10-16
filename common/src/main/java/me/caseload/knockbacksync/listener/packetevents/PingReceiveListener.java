package me.caseload.knockbacksync.listener.packetevents;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import me.caseload.knockbacksync.KnockbackSyncBase;
import me.caseload.knockbacksync.manager.PlayerDataManager;
import me.caseload.knockbacksync.player.PlayerData;

public class PingReceiveListener extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!KnockbackSyncBase.INSTANCE.getConfigManager().isToggled())
            return;

        if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE)
            return;

        WrapperPlayClientKeepAlive keepAlive = new WrapperPlayClientKeepAlive(event);
        PlayerData playerData = PlayerDataManager.getPlayerData(event.getUser().getUUID());
        if (playerData == null) return;

        long sendTime = keepAlive.getId();
        long ping = System.currentTimeMillis() - sendTime;
        playerData.setPreviousPing(playerData.getPing());
        playerData.setPing(ping);

        playerData.getJitterCalculator().addPing(ping);
        double jitter = playerData.getJitterCalculator().calculateJitter();
        playerData.setJitter(jitter);
    }
}