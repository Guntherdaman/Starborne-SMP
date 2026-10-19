
package com.starborne.rituals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LegendaryIngredientChecker {

    public boolean hasIngredients(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return false;
        }

        AllocationResult result = calculateAllocation(player, recipe);
        return result != null
                && result.totalAllocated == result.totalRequired;
    }

    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            Material material
    ) {
        if (player == null || recipe == null || material == null) {
            return 0;
        }

        AllocationResult result = calculateAllocation(player, recipe);
        if (result == null) {
            return 0;
        }

        int missing = 0;
        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        for (int r = 0; r < requirements.size(); r++) {
            if (requirements.get(r).getMaterial() == material) {
                missing += requirements.get(r).getAmount()
                        - result.allocatedByRequirement[r];
            }
        }

        return missing;
    }

    public int getMissingAmount(
            Player player,
            LegendaryRecipe recipe,
            IngredientRequirement target
    ) {
        if (player == null || recipe == null || target == null) {
            return 0;
        }

        AllocationResult result = calculateAllocation(player, recipe);
        if (result == null) {
            return 0;
        }

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        for (int r = 0; r < requirements.size(); r++) {
            if (requirements.get(r) == target) {
                return requirements.get(r).getAmount()
                        - result.allocatedByRequirement[r];
            }
        }

        return 0;
    }

    /**
     * Returns a complete allocation, or null if the recipe cannot
     * be fulfilled. Package-private for LegendaryIngredientConsumer.
     */
    int[][] createAllocation(
            Player player,
            LegendaryRecipe recipe
    ) {
        AllocationResult result = calculateAllocation(player, recipe);

        if (result == null
                || result.totalAllocated != result.totalRequired) {
            return null;
        }

        return result.allocation;
    }

    private AllocationResult calculateAllocation(
            Player player,
            LegendaryRecipe recipe
    ) {
        if (player == null || recipe == null) {
            return null;
        }

        ItemStack[] contents =
                player.getInventory().getStorageContents();

        List<IngredientRequirement> requirements =
                recipe.getRequirements();

        int requirementCount = requirements.size();
        int slotCount = contents.length;

        int source = 0;
        int requirementStart = 1;
        int slotStart = requirementStart + requirementCount;
        int sink = slotStart + slotCount;
        int nodeCount = sink + 1;

        List<FlowEdge>[] graph = new List[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            graph[i] = new ArrayList<>();
        }

        FlowEdge[][] requirementSlotEdges =
                new FlowEdge[requirementCount][slotCount];

        int totalRequired = 0;

        for (int r = 0; r < requirementCount; r++) {
            IngredientRequirement requirement = requirements.get(r);
            totalRequired += requirement.getAmount();

            addEdge(
                    graph,
                    source,
                    requirementStart + r,
                    requirement.getAmount()
            );

            for (int slot = 0; slot < slotCount; slot++) {
                ItemStack item = contents[slot];

                if (item == null
                        || item.getAmount() <= 0
                        || !requirement.matches(item)) {
                    continue;
                }

                requirementSlotEdges[r][slot] = addEdge(
                        graph,
                        requirementStart + r,
                        slotStart + slot,
                        requirement.getAmount()
                );
            }
        }

        for (int slot = 0; slot < slotCount; slot++) {
            ItemStack item = contents[slot];

            if (item != null && item.getAmount() > 0) {
                addEdge(
                        graph,
                        slotStart + slot,
                        sink,
                        item.getAmount()
                );
            }
        }

        int totalAllocated = maxFlow(graph, source, sink);
        int[][] allocation =
                new int[requirementCount][slotCount];
        int[] allocatedByRequirement = new int[requirementCount];

        for (int r = 0; r < requirementCount; r++) {
            for (int slot = 0; slot < slotCount; slot++) {
                FlowEdge edge = requirementSlotEdges[r][slot];

                if (edge == null) {
                    continue;
                }

                int allocated = edge.originalCapacity - edge.capacity;
                allocation[r][slot] = allocated;
                allocatedByRequirement[r] += allocated;
            }
        }

        return new AllocationResult(
                allocation,
                allocatedByRequirement,
                totalAllocated,
                totalRequired
        );
    }

    private FlowEdge addEdge(
            List<FlowEdge>[] graph,
            int from,
            int to,
            int capacity
    ) {
        FlowEdge forward = new FlowEdge(to, graph[to].size(), capacity);
        FlowEdge reverse = new FlowEdge(from, graph[from].size(), 0);

        graph[from].add(forward);
        graph[to].add(reverse);

        return forward;
    }

    private int maxFlow(
            List<FlowEdge>[] graph,
            int source,
            int sink
    ) {
        int totalFlow = 0;
        int nodeCount = graph.length;

        while (true) {
            int[] parentNode = new int[nodeCount];
            int[] parentEdge = new int[nodeCount];
            Arrays.fill(parentNode, -1);

            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);
            parentNode[source] = source;

            while (!queue.isEmpty() && parentNode[sink] == -1) {
                int current = queue.remove();

                for (int i = 0; i < graph[current].size(); i++) {
                    FlowEdge edge = graph[current].get(i);

                    if (edge.capacity <= 0
                            || parentNode[edge.to] != -1) {
                        continue;
                    }

                    parentNode[edge.to] = current;
                    parentEdge[edge.to] = i;
                    queue.add(edge.to);

                    if (edge.to == sink) {
                        break;
                    }
                }
            }

            if (parentNode[sink] == -1) {
                break;
            }

            int amount = Integer.MAX_VALUE;

            for (int node = sink; node != source;
                    node = parentNode[node]) {
                FlowEdge edge = graph[parentNode[node]]
                        .get(parentEdge[node]);
                amount = Math.min(amount, edge.capacity);
            }

            for (int node = sink; node != source;
                    node = parentNode[node]) {
                int previous = parentNode[node];
                FlowEdge edge = graph[previous].get(parentEdge[node]);

                edge.capacity -= amount;
                graph[node].get(edge.reverseIndex).capacity += amount;
            }

            totalFlow += amount;
        }

        return totalFlow;
    }

    private static final class FlowEdge {
        private final int to;
        private final int reverseIndex;
        private final int originalCapacity;
        private int capacity;

        private FlowEdge(int to, int reverseIndex, int capacity) {
            this.to = to;
            this.reverseIndex = reverseIndex;
            this.capacity = capacity;
            this.originalCapacity = capacity;
        }
    }

    private static final class AllocationResult {
        private final int[][] allocation;
        private final int[] allocatedByRequirement;
        private final int totalAllocated;
        private final int totalRequired;

        private AllocationResult(
                int[][] allocation,
                int[] allocatedByRequirement,
                int totalAllocated,
                int totalRequired
        ) {
            this.allocation = allocation;
            this.allocatedByRequirement = allocatedByRequirement;
            this.totalAllocated = totalAllocated;
            this.totalRequired = totalRequired;
        }
    }
}
