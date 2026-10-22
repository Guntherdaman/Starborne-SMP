
package com.starborne.rituals;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LegendaryRitualVisual {

    private final JavaPlugin plugin;
    private final Map<UUID, ItemDisplay> displays =
            new HashMap<>();

    private BukkitRunnable animation;

    public LegendaryRitualVisual(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(
            UUID ritualId,
            Location ritualLocation,
            ItemStack previewItem
    ) {
        if (ritualId == null
                || ritualLocation == null
                || ritualLocation.getWorld() == null
                || previewItem == null
                || previewItem.getType().isAir()) {
            return;
        }

        remove(ritualId);

        Location displayLocation = ritualLocation.clone()
                .add(0.5, 1.8, 0.5);

        World world = displayLocation.getWorld();

        ItemDisplay display = world.spawn(
                displayLocation,
                ItemDisplay.class,
                entity -> {
                    entity.setItemStack(previewItem.clone());
                    entity.setItemDisplayTransform(
                            ItemDisplayTransform.FIXED
                    );
                    entity.setPersistent(false);
                    entity.setInvulnerable(true);
                    entity.setGravity(false);
                    entity.setTransformation(
                            new Transformation(
                                    new Vector3f(0, 0, 0),
                                    new AxisAngle4f(
                                            0, 0, 1, 0
                                    ),
                                    new Vector3f(1.0f, 1.0f, 1.0f),
                                    new AxisAngle4f(
                                            0, 0, 1, 0
                                    )
                            )
                    );
                }
        );

        displays.put(ritualId, display);

        if (animation == null) {
            animation = new BukkitRunnable() {
                private float rotation = 0.0f;
                private float phase = 0.0f;

                @Override
                public void run() {
                    if (displays.isEmpty()) {
                        cancel();
                        animation = null;
                        return;
                    }

                    rotation += 0.06f;
                    phase += 0.08f;

                    for (ItemDisplay activeDisplay
                            : displays.values()) {
                        if (!activeDisplay.isValid()) {
                            continue;
                        }

                        Location base = activeDisplay.getLocation();
                        base.setYaw((float) Math.toDegrees(rotation));
                        base.setPitch(0);

                        activeDisplay.teleport(
                                base.add(
                                        0,
                                        Math.sin(phase) * 0.015,
                                        0
                                )
                        );

                        World activeWorld = activeDisplay.getWorld();
                        Location particleLocation =
                                activeDisplay.getLocation()
                                        .add(0, -0.2, 0);

                        activeWorld.spawnParticle(
                                Particle.END_ROD,
                                particleLocation,
                                2,
                                0.18, 0.18, 0.18,
                                0.005
                        );

                        activeWorld.spawnParticle(
                                Particle.PORTAL,
                                particleLocation,
                                3,
                                0.25, 0.25, 0.25,
                                0.05
                        );
                    }
                }
            };

            animation.runTaskTimer(plugin, 1L, 2L);
        }
    }

    public void remove(UUID ritualId) {
        ItemDisplay display = displays.remove(ritualId);

        if (display != null && display.isValid()) {
            display.remove();
        }
    }

    public void removeAll() {
        for (ItemDisplay display : displays.values()) {
            if (display != null && display.isValid()) {
                display.remove();
            }
        }

        displays.clear();

        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
