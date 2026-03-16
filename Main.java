import java.io.IOException;
import java.util.List;

/**
 * Main driver program.
 *
 * Usage:
 *   java Main <path-to-cheese-csv>
 *
 * If no argument is supplied the program defaults to "cheese.csv" in the
 * current working directory.
 *
 * Results are written to output.txt in the current working directory.
 */
public class Main {

    public static void main(String[] args) {

        // Determine CSV file path
        String csvPath = (args.length > 0) ? args[0] : "cheese.csv";
        String outputPath = "output.txt";

        System.out.println("Reading cheese data from: " + csvPath);

        // 1. Read CSV
        CheeseCSVReader reader = new CheeseCSVReader();
        List<Cheese> cheeses;

        try {
            cheeses = reader.read(csvPath);
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.err.println("Make sure the file exists at: " + csvPath);
            System.exit(1);
            return;
        }

        System.out.println("Loaded " + cheeses.size() + " cheese records.");

        // 2. Analyse
        CheeseAnalyzer analyzer = new CheeseAnalyzer(cheeses);

        // 3. Print a quick summary to the console
        System.out.println();
        System.out.println("=== Results Preview ===");
        System.out.println("Pasteurized cheeses          : " + analyzer.countPasteurized());
        System.out.println("Raw milk cheeses             : " + analyzer.countRawMilk());
        System.out.println("Organic + moisture > 41%     : " + analyzer.countOrganicHighMoisture());
        System.out.println("Most common milk type        : " + analyzer.mostCommonMilkType());
        System.out.println();

        // 4. Write full report to output.txt
        OutputWriter writer = new OutputWriter(outputPath);
        try {
            writer.write(analyzer);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
