import java.util.*;

/**
 * Performs the required calculations on a list of Cheese objects.
 */
public class CheeseAnalyzer {

    private final List<Cheese> cheeses;

    public CheeseAnalyzer(List<Cheese> cheeses) {
        this.cheeses = cheeses;
    }

    // -------------------------------------------------------------------------
    // Calculation 1: Pasteurized vs Raw milk counts
    // -------------------------------------------------------------------------

    /**
     * Counts cheeses whose MilkTreatmentTypeEn contains "Pasteurized" (case-insensitive).
     */
    public long countPasteurized() {
        return cheeses.stream()
                .filter(c -> c.getMilkTreatmentTypeEn().toLowerCase().contains("pasteurized"))
                .count();
    }

    /**
     * Counts cheeses whose MilkTreatmentTypeEn contains "Raw" (case-insensitive).
     */
    public long countRawMilk() {
        return cheeses.stream()
                .filter(c -> c.getMilkTreatmentTypeEn().toLowerCase().contains("raw"))
                .count();
    }

    // -------------------------------------------------------------------------
    // Calculation 2: Organic cheeses with moisture > 41.0%
    // -------------------------------------------------------------------------

    /**
     * Counts cheeses that are organic (Organic == 1) AND have MoisturePercent > 41.0.
     */
    public long countOrganicHighMoisture() {
        return cheeses.stream()
                .filter(c -> c.getOrganic() == 1 && c.getMoisturePercent() > 41.0)
                .count();
    }

    // -------------------------------------------------------------------------
    // Calculation 3: Most common animal milk type in Canada
    // -------------------------------------------------------------------------

    /**
     * Returns the milk type (from MilkTypeEn) that appears most frequently.
     * If multiple types tie for first, the one that appears first alphabetically is returned.
     * Returns "Unknown" if the list is empty.
     *
     * The four expected values are: Cow, Goat, Ewe, Buffalo
     */
    public String mostCommonMilkType() {
        Map<String, Long> counts = new TreeMap<>(); // TreeMap for consistent ordering on ties

        for (Cheese c : cheeses) {
            String milkType = c.getMilkTypeEn();
            if (!milkType.isEmpty()) {
                // Normalize: some rows contain comma-separated multi-types ("Cow, Goat")
                // Count each type mentioned in the field
                String[] types = milkType.split(",");
                for (String t : types) {
                    String normalized = t.trim();
                    if (!normalized.isEmpty()) {
                        counts.merge(normalized, 1L, Long::sum);
                    }
                }
            }
        }

        if (counts.isEmpty()) return "Unknown";

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    /**
     * Returns a full breakdown of milk type counts (useful for the output file).
     */
    public Map<String, Long> milkTypeCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Cheese c : cheeses) {
            String milkType = c.getMilkTypeEn();
            if (!milkType.isEmpty()) {
                String[] types = milkType.split(",");
                for (String t : types) {
                    String normalized = t.trim();
                    if (!normalized.isEmpty()) {
                        counts.merge(normalized, 1L, Long::sum);
                    }
                }
            }
        }
        // Sort by count descending
        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        Map<String, Long> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Long> e : entries) sorted.put(e.getKey(), e.getValue());
        return sorted;
    }
}
