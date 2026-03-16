import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for CheeseAnalyzer.
 *
 * These tests use no external framework — just plain Java assertions.
 * Run with:
 *   javac CheeseTests.java Cheese.java CheeseAnalyzer.java
 *   java CheeseTests
 */
public class CheeseTests {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static int passed = 0;
    private static int failed = 0;

    private static void assertEqual(String testName, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("  PASS  " + testName);
            passed++;
        } else {
            System.out.println("  FAIL  " + testName
                    + " | expected: " + expected + " | got: " + actual);
            failed++;
        }
    }

    // -------------------------------------------------------------------------
    // Sample data shared across tests
    // -------------------------------------------------------------------------

    /**
     * Returns a small, known dataset:
     *   [0] Pasteurized, not organic, moisture 35.0, Cow
     *   [1] Raw Milk,    not organic, moisture 42.0, Goat
     *   [2] Pasteurized, organic,     moisture 45.0, Cow
     *   [3] Pasteurized, organic,     moisture 38.0, Ewe
     *   [4] Raw Milk,    organic,     moisture 50.0, Buffalo
     *   [5] Pasteurized, organic,     moisture 41.0, Goat   <- moisture NOT > 41
     *   [6] Pasteurized, not organic, moisture 55.0, Cow
     */
    private static List<Cheese> sampleData() {
        return Arrays.asList(
            new Cheese("Pasteurized",           0, 35.0, "Cow"),
            new Cheese("Raw Milk",              0, 42.0, "Goat"),
            new Cheese("Pasteurized",           1, 45.0, "Cow"),
            new Cheese("Pasteurized",           1, 38.0, "Ewe"),
            new Cheese("Raw Milk",              1, 50.0, "Buffalo"),
            new Cheese("Pasteurized",           1, 41.0, "Goat"),   // exactly 41 — NOT > 41
            new Cheese("Pasteurized",           0, 55.0, "Cow")
        );
    }

    // -------------------------------------------------------------------------
    // Calculation 1 Tests — Pasteurized vs Raw
    // -------------------------------------------------------------------------

    private static void testCountPasteurized() {
        CheeseAnalyzer a = new CheeseAnalyzer(sampleData());
        assertEqual("countPasteurized returns 5", 5L, a.countPasteurized());
    }

    private static void testCountRawMilk() {
        CheeseAnalyzer a = new CheeseAnalyzer(sampleData());
        assertEqual("countRawMilk returns 2", 2L, a.countRawMilk());
    }

    private static void testCountPasteurizedCaseInsensitive() {
        List<Cheese> data = Arrays.asList(
            new Cheese("PASTEURIZED", 0, 30.0, "Cow"),
            new Cheese("pasteurized", 0, 30.0, "Cow"),
            new Cheese("Pasteurized Milk", 0, 30.0, "Cow")
        );
        CheeseAnalyzer a = new CheeseAnalyzer(data);
        assertEqual("countPasteurized is case-insensitive", 3L, a.countPasteurized());
    }

    private static void testCountsEmptyList() {
        CheeseAnalyzer a = new CheeseAnalyzer(Arrays.asList());
        assertEqual("countPasteurized on empty list", 0L, a.countPasteurized());
        assertEqual("countRawMilk on empty list",     0L, a.countRawMilk());
    }

    // -------------------------------------------------------------------------
    // Calculation 2 Tests — Organic + Moisture > 41%
    // -------------------------------------------------------------------------

    private static void testCountOrganicHighMoisture() {
        CheeseAnalyzer a = new CheeseAnalyzer(sampleData());
        // Organic cheeses: indices 2,3,4,5
        // Moisture > 41: index 2 (45.0), index 4 (50.0)
        // Index 3 moisture is 38.0 — excluded; index 5 moisture is exactly 41.0 — excluded
        assertEqual("countOrganicHighMoisture returns 2", 2L, a.countOrganicHighMoisture());
    }

    private static void testOrganicBoundaryExactly41() {
        List<Cheese> data = Arrays.asList(
            new Cheese("Pasteurized", 1, 41.0, "Cow"),   // exactly 41 — NOT > 41
            new Cheese("Pasteurized", 1, 41.1, "Cow")    // just above — IS > 41
        );
        CheeseAnalyzer a = new CheeseAnalyzer(data);
        assertEqual("moisture exactly 41.0 is NOT > 41", 1L, a.countOrganicHighMoisture());
    }

    private static void testOrganicZeroNotCounted() {
        List<Cheese> data = Arrays.asList(
            new Cheese("Pasteurized", 0, 99.0, "Cow")  // high moisture but NOT organic
        );
        CheeseAnalyzer a = new CheeseAnalyzer(data);
        assertEqual("non-organic with high moisture not counted", 0L, a.countOrganicHighMoisture());
    }

    // -------------------------------------------------------------------------
    // Calculation 3 Tests — Most Common Milk Type
    // -------------------------------------------------------------------------

    private static void testMostCommonMilkType() {
        CheeseAnalyzer a = new CheeseAnalyzer(sampleData());
        // Cow appears 3 times (indices 0,2,6), Goat 2 times (1,5), Ewe 1, Buffalo 1
        assertEqual("mostCommonMilkType returns Cow", "Cow", a.mostCommonMilkType());
    }

    private static void testMostCommonMilkTypeEmptyList() {
        CheeseAnalyzer a = new CheeseAnalyzer(Arrays.asList());
        assertEqual("mostCommonMilkType on empty list returns Unknown", "Unknown", a.mostCommonMilkType());
    }

    private static void testMilkTypeCounts() {
        CheeseAnalyzer a = new CheeseAnalyzer(sampleData());
        Map<String, Long> counts = a.milkTypeCounts();
        assertEqual("milkTypeCounts Cow = 3",     3L, counts.get("Cow"));
        assertEqual("milkTypeCounts Goat = 2",    2L, counts.get("Goat"));
        assertEqual("milkTypeCounts Ewe = 1",     1L, counts.get("Ewe"));
        assertEqual("milkTypeCounts Buffalo = 1", 1L, counts.get("Buffalo"));
    }

    // -------------------------------------------------------------------------
    // CSV parser tests — quoted fields containing commas
    // -------------------------------------------------------------------------

    private static void testParseRowSimple() {
        List<String> result = CheeseCSVReader.parseRow("Cow,Pasteurized,1,42.0");
        assertEqual("parseRow simple field count", 4, result.size());
        assertEqual("parseRow field 0", "Cow", result.get(0));
    }

    private static void testParseRowQuotedComma() {
        // "Sharp, lactic" must stay as one field
        List<String> result = CheeseCSVReader.parseRow("228,NB,47.0,\"Sharp, lactic\",Uncooked,Ewe,Raw Milk");
        assertEqual("parseRow quoted comma field count", 7, result.size());
        assertEqual("parseRow quoted field value", "Sharp, lactic", result.get(3));
        assertEqual("parseRow field after quoted", "Uncooked", result.get(4));
    }

    private static void testParseRowEscapedQuote() {
        List<String> result = CheeseCSVReader.parseRow("\"He said \"\"hello\"\"\",plain");
        assertEqual("parseRow escaped quote field count", 2, result.size());
        assertEqual("parseRow escaped quote value", "He said \"hello\"", result.get(0));
    }

    // -------------------------------------------------------------------------
    // Cheese model test
    // -------------------------------------------------------------------------

    private static void testCheeseGetters() {
        Cheese c = new Cheese("  Raw Milk  ", 1, 47.5, "  Goat  ");
        assertEqual("Cheese milkTreatment trimmed", "Raw Milk", c.getMilkTreatmentTypeEn());
        assertEqual("Cheese organic",               1,          c.getOrganic());
        assertEqual("Cheese moisture",              47.5,       c.getMoisturePercent());
        assertEqual("Cheese milkType trimmed",      "Goat",     c.getMilkTypeEn());
    }

    // -------------------------------------------------------------------------
    // Main — run all tests
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("Running CheeseAnalyzer tests...\n");

        // CSV parser
        testParseRowSimple();
        testParseRowQuotedComma();
        testParseRowEscapedQuote();

        // Cheese model
        testCheeseGetters();

        // Calculation 1
        testCountPasteurized();
        testCountRawMilk();
        testCountPasteurizedCaseInsensitive();
        testCountsEmptyList();

        // Calculation 2
        testCountOrganicHighMoisture();
        testOrganicBoundaryExactly41();
        testOrganicZeroNotCounted();

        // Calculation 3
        testMostCommonMilkType();
        testMostCommonMilkTypeEmptyList();
        testMilkTypeCounts();

        // Summary
        System.out.println();
        System.out.println("Results: " + passed + " passed, " + failed + " failed out of " + (passed + failed) + " tests.");
        if (failed > 0) System.exit(1);
    }
}
