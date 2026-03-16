/**
 * Represents a single cheese record from the CSV dataset.
 */
public class Cheese {

    private String milkTreatmentTypeEn; // e.g., "Pasteurized", "Raw Milk"
    private int organic;                 // 0 = not organic, 1 = organic
    private double moisturePercent;      // moisture percentage
    private String milkTypeEn;           // e.g., "Cow", "Goat", "Ewe", "Buffalo"

    public Cheese(String milkTreatmentTypeEn, int organic, double moisturePercent, String milkTypeEn) {
        this.milkTreatmentTypeEn = milkTreatmentTypeEn.trim();
        this.organic = organic;
        this.moisturePercent = moisturePercent;
        this.milkTypeEn = milkTypeEn.trim();
    }

    public String getMilkTreatmentTypeEn() { return milkTreatmentTypeEn; }
    public int getOrganic()                { return organic; }
    public double getMoisturePercent()     { return moisturePercent; }
    public String getMilkTypeEn()          { return milkTypeEn; }

    @Override
    public String toString() {
        return "Cheese{milkTreatment='" + milkTreatmentTypeEn + "', organic=" + organic
                + ", moisture=" + moisturePercent + ", milkType='" + milkTypeEn + "'}";
    }
}
