package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.text.similarity.FuzzyScore;

import java.util.*;

public class Util {
    public static final FuzzyScore FUZZY_SCORE = new FuzzyScore(Locale.ENGLISH);

    public static Quadrant getQuadrant(int x, int y) {
        int screenWidth = DynamicHUD.MC.getWindow().getGuiScaledWidth();
        int screenHeight = DynamicHUD.MC.getWindow().getGuiScaledHeight();

        if (x < screenWidth / 2) {
            if (y < screenHeight / 2) {
                return Quadrant.UPPER_LEFT;
            } else {
                return Quadrant.BOTTOM_LEFT;
            }
        } else {
            if (y < screenHeight / 2) {
                return Quadrant.UPPER_RIGHT;
            } else {
                return Quadrant.BOTTOM_RIGHT;
            }
        }
    }

    public enum Quadrant {
        UPPER_LEFT, UPPER_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static boolean errorIfTrue(boolean expression, String message, Object... objects) {
        if (!expression) DynamicHUD.logger.error(message, objects);
        return expression;
    }

    public static boolean warnIfTrue(boolean expression, String message, Object... objects) {
        if (expression) DynamicHUD.logger.warn(message, objects);
        return expression;
    }

    /**
     * Returns a list of options sorted by higher fuzzy score from the query string.
     * If minimumScore is -1, then two typos from query string will be tolerated.
     */
    public static List<Option<?>> getSearchResults(String query, int minimumScore, List<Option<?>> options, boolean flatten) {
        if(options.isEmpty()) return new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(options);
        }

        String lowerQuery = query.toLowerCase().trim();

        //Allow 2 typos for a min score of -1 using the query length
        int threshold = (minimumScore == -1) ? Math.max(1, lowerQuery.length() - 2) : minimumScore;

        Map<Option<?>, Integer> scoreMap = new HashMap<>();
        List<Option<?>> matched = new ArrayList<>();

        for (Option<?> opt : Skin.flattenOptions(options)) {
            if (!opt.shouldRender()) continue;

            String name = opt.getName().getString().toLowerCase();
            String desc = opt.getDescription() != null ? opt.getDescription().getString().toLowerCase() : "";

            int score = calculateWeightedScore(lowerQuery, name, desc);

            if (score >= threshold) {
                scoreMap.put(opt, score);
                matched.add(opt);
            }
        }


        if (scoreMap.isEmpty()) return new ArrayList<>();

        if (flatten) {
            matched.sort((a, b) -> Integer.compare(scoreMap.get(b), scoreMap.get(a)));
            return matched;
        }

        return filterAndSortOptions(options, threshold, scoreMap);
    }

    /**
     * Calculates a weighted score for exact and prefix matches of options
     */
    public static int calculateWeightedScore(String query, String name, String desc) {
        int nameScore = FUZZY_SCORE.fuzzyScore(name, query);
        int descScore = FUZZY_SCORE.fuzzyScore(desc, query);

        // add score heavily if the name contains or starts with the exact query
        if (name.equalsIgnoreCase(query)) {
            nameScore += 100; // exact match
        } else if (name.startsWith(query)) {
            nameScore += 50;  // starts with
        } else if (name.contains(query)) {
            nameScore += 25;  // substring match
        }

        // higher score to option names (twice as much)
        return Math.max(nameScore * 2, descScore);
    }

    /**
     * Recursively processes a list of options, returning a filtered and sorted copy.
     * Groups are re‑created with only matching children, and are expanded.
     */
    private static List<Option<?>> filterAndSortOptions(List<Option<?>> source, int threshold, Map<Option<?>, Integer> scoreMap) {
        List<Option<?>> result = new ArrayList<>();
        for (Option<?> opt : source) {
            if (opt instanceof OptionGroup group) {
                // Process children first
                List<Option<?>> filteredChildren = filterAndSortOptions(group.getGroupOptions(), threshold, scoreMap);

                if (!filteredChildren.isEmpty()) {
                    OptionGroup newGroup = new OptionGroup(group.getName());
                    newGroup.setExpanded(true);

                    // sort children inside group by score
                    filteredChildren.sort((a, b) -> Integer.compare(
                            getEffectiveScore(b, scoreMap),
                            getEffectiveScore(a, scoreMap)
                    ));

                    for (Option<?> child : filteredChildren) {
                        newGroup.addOption(child);
                    }
                    result.add(newGroup);
                }
            } else if (scoreMap.getOrDefault(opt, 0) >= threshold) {
                result.add(opt);
            }
        }

        // sort  by score descending
        result.sort((a, b) -> {
            int sa = getEffectiveScore(a, scoreMap);
            int sb = getEffectiveScore(b, scoreMap);
            return Integer.compare(sb, sa);
        });
        return result;
    }

    /**
     * Returns the highest score among all options inside a group (or the group's own score).
     */
    private static int getEffectiveScore(Option<?> opt, Map<Option<?>, Integer> scoreMap) {
        if (opt instanceof OptionGroup group) {
            int max = scoreMap.getOrDefault(group, 0);
            for (Option<?> child : group.getGroupOptions()) {
                max = Math.max(max, getEffectiveScore(child, scoreMap));
            }
            return max;
        }
        return scoreMap.getOrDefault(opt, 0);
    }

    public static MutableComponent getTruncatedName(Component name, int maxTextWidth) {
        String raw = name.getString();
        if (DynamicHUD.MC.font.width(raw) > maxTextWidth) {
            String truncated = DynamicHUD.MC.font.plainSubstrByWidth(raw, maxTextWidth - DynamicHUD.MC.font.width("...")) + "...";
            return Component.literal(truncated);
        }
        return name.copy();
    }
}
