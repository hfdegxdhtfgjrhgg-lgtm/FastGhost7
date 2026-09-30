package com.fast.ghost;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GhostHitListener implements Listener {
    private final FastGhost plugin;

    public GhostHitListener(FastGhost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) return;

        Player attacker = (Player) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();
        Stats.addTotalHit();

        // تحويل أي ضربة ملغاة إلى ضربة حقيقية
        if (event.isCancelled()) {
            event.setCancelled(false);
            double damage = event.getDamage() > 0 ? event.getDamage() : 1.0;
            plugin.getGhostHitManager().processAndFixGhostHit(attacker, victim, damage);
        }
    }
}
