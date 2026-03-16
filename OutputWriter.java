import java.io.*;
import java.util.Map;

/**
 * Writes the analysis results to output.txt.
 */
public class OutputWriter {

    private final String outputPath;

    public OutputWriter(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Writes all three calculation results to the output file.
     */
    public void write(CheeseAnalyzer analyzer) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {

            pw.println("=== Canadian Cheese Dataset Analysis ===");
            pw.println();

            // --- Calculation 1 ---
            pw.println("--- Calculation 1: Milk Treatment Type ---");
            pw.println("Number of cheeses using Pasteurized milk : " + analyzer.countPasteurized());
            pw.println("Number of cheeses using Raw milk          : " + analyzer.countRawMilk());
            pw.println();

            // --- Calculation 2 ---
            pw.println("--- Calculation 2: Organic Cheeses with Moisture > 41.0% ---");
            pw.println("Count : " + analyzer.countOrganicHighMoisture());
            pw.println();

            // --- Calculation 3 ---
            pw.println("--- Calculation 3: Most Common Animal Milk Type in Canada ---");
            Map<String, Long> breakdown = analyzer.milkTypeCounts();
            pw.println("Most common type : " + analyzer.mostCommonMilkType());
            pw.println("Full breakdown:");
            for (Map.Entry<String, Long> entry : breakdown.entrySet()) {
                pw.printf("  %-20s : %d%n", entry.getKey(), entry.getValue());
            }
            pw.println();

            pw.println("=== End of Report ===");
        }

        System.out.println("Results written to: " + outputPath);
    }
}
