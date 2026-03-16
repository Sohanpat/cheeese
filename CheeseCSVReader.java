import java.io.*;
import java.util.*;

/**
 * Reads a CSV file and parses each row into a Cheese object.
 *
 * Uses a proper quoted-CSV parser so that commas inside quoted fields
 * (e.g. "Sharp, lactic") do NOT break column alignment.
 *
 * Expected CSV columns (order does not matter — columns are found by header name):
 *   MilkTreatmentTypeEn, Organic, MoisturePercent, MilkTypeEn
 */
public class CheeseCSVReader {

    /**
     * Reads the CSV at the given path and returns a list of Cheese objects.
     *
     * @param filePath path to the CSV file
     * @return list of parsed Cheese objects (skips rows with missing/invalid data)
     * @throws IOException if the file cannot be read
     */
    public List<Cheese> read(String filePath) throws IOException {
        List<Cheese> cheeses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty: " + filePath);
            }

            // Map column names to their indices using the quoted-aware parser
            List<String> headers = parseRow(headerLine);
            Map<String, Integer> columnIndex = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                columnIndex.put(headers.get(i).trim(), i);
            }

            // Verify required columns exist
            String[] required = {"MilkTreatmentTypeEn", "Organic", "MoisturePercent", "MilkTypeEn"};
            for (String col : required) {
                if (!columnIndex.containsKey(col)) {
                    throw new IOException("Required column not found in CSV: " + col);
                }
            }

            int milkTreatmentIdx = columnIndex.get("MilkTreatmentTypeEn");
            int organicIdx       = columnIndex.get("Organic");
            int moistureIdx      = columnIndex.get("MoisturePercent");
            int milkTypeIdx      = columnIndex.get("MilkTypeEn");

            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                List<String> fields = parseRow(line);

                try {
                    String milkTreatment = getField(fields, milkTreatmentIdx);
                    String organicStr    = getField(fields, organicIdx).trim();
                    String moistureStr   = getField(fields, moistureIdx).trim();
                    String milkType      = getField(fields, milkTypeIdx);

                    // Skip rows where key fields are blank
                    if (milkTreatment.isEmpty() && milkType.isEmpty()) continue;

                    int organic = organicStr.isEmpty() ? 0 : Integer.parseInt(organicStr);
                    double moisture = moistureStr.isEmpty() ? 0.0 : Double.parseDouble(moistureStr);

                    cheeses.add(new Cheese(milkTreatment, organic, moisture, milkType));

                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Warning: skipping malformed row at line " + lineNumber + " — " + e.getMessage());
                }
            }
        }

        return cheeses;
    }

    /**
     * Parses a single CSV line respecting double-quoted fields.
     * A quoted field may contain commas and escaped quotes ("").
     * Returns a list of unquoted field values.
     */
    static List<String> parseRow(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    // Peek ahead: "" is an escaped quote inside a quoted field
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++; // skip the second quote
                    } else {
                        inQuotes = false; // closing quote
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString()); // last field
        return fields;
    }

    /** Safely retrieves a field by index, returning empty string if out of bounds. */
    private String getField(List<String> fields, int index) {
        return (index < fields.size()) ? fields.get(index).trim() : "";
    }
}
