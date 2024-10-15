package me.caseload.knockbacksync.manager;

import io.github.retrooper.packetevents.util.GeyserUtil;
import me.caseload.knockbacksync.util.FloodgateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private static final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public static PlayerData getPlayerData(@NotNull UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public static void addPlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
        if (!shouldExempt(uuid))
            playerDataMap.put(uuid, playerData);
    }

    public static void removePlayerData(@NotNull UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public static boolean containsPlayerData(@NotNull UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    public static boolean shouldExempt(@NotNull UUID uuid) {
        // Geyser players don't have Java movement
        return GeyserUtil.isGeyserPlayer(uuid)
                // Floodgate is the authentication system for Geyser on servers that use Geyser as a proxy instead of installing it as a plugin directly on the server
                || FloodgateUtil.isFloodgatePlayer(uuid)
                // Geyser formatted player string
                // This will never happen for Java players, as the first character in the 3rd group is always 4 (xxxxxxxx-xxxx-4xxx-xxxx-xxxxxxxxxxxx)
                || uuid.toString().startsWith("00000000-0000-0000-0009");
    }
}
