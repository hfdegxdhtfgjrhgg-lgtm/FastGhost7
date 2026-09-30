package com.fast.ghost;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FastGhost extends JavaPlugin {
    private static FastGhost instance;
    private GhostHitManager ghostHitManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.ghostHitManager = new GhostHitManager(this);
        Stats.init(this);

        getServer().getPluginManager().registerEvents(new GhostHitListener(this), this);
        getCommand("fastghost").setExecutor(new FastGhostCommand(this));

        // الاستماع لـ Packets الهجوم المباشرة من العميل
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                
                // تشغيل المعالجة في المين ثريد (Main Thread)
                getServer().getScheduler().runTask(FastGhost.getInstance(), () -> {
                    LivingEntity target = ghostHitManager.findTargetEntity(player, 4.0);
                    if (target != null) {
                        ghostHitManager.processAndFixGhostHit(player, target, 1.0);
                    }
                });
            }
        });
    }

    public static FastGhost getInstance() { return instance; }
    public GhostHitManager getGhostHitManager() { return ghostHitManager; }
}
