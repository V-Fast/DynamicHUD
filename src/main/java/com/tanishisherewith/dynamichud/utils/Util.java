package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;
import org.apache.commons.text.similarity.FuzzyScore;

import java.util.*;

public class Util {
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
    public static List<Option<?>> getSearchResults(String query, int minimumScore, List<Option<?>> options) {
        if(options.isEmpty()) return new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(options);
        }

        FuzzyScore FUZZY_SCORE = new FuzzyScore(Locale.ENGLISH);

        String lowerQuery = query.toLowerCase().trim();
        Map<Option<?>, Integer> scoreMap = new HashMap<>();

        List<Option<?>> allOptions = Skin.flattenOptions(options);

        for (Option<?> opt : allOptions) {
            if (!opt.shouldRender()) continue;
            String name = opt.getName().getString();
            String desc = opt.getDescription().getString();
            int nameScore = FUZZY_SCORE.fuzzyScore(name, lowerQuery);
            int descScore = FUZZY_SCORE.fuzzyScore(desc, lowerQuery);
            int best = Math.max(nameScore, descScore);
            scoreMap.put(opt, best);
        }


        if (scoreMap.isEmpty()) return new ArrayList<>();

        //Allow 2 typos for a min score of -1 using the query length
        int threshold = (minimumScore == -1) ? lowerQuery.length() - 2: minimumScore;

        return filterAndSortOptions(options, threshold, scoreMap);
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
                int groupScore = scoreMap.getOrDefault(group, 0);
                boolean groupMatches = groupScore >= threshold;

                if (groupMatches || !filteredChildren.isEmpty()) {
                    OptionGroup newGroup = new OptionGroup(group.name);
                    newGroup.setExpanded(true);
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


    public static boolean isSafeToContinue() {
        return DynamicHUD.MC.getWindow() != null && DynamicHUD.MC.font != null;
    }
}
