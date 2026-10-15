package co.wethinkcode.healthsafe;
import java.util.ArrayList;
import java.util.List;

public class WardDataNormalizer {

//    public String normalizeWardId = normalizeWard;

    public static String normalizeWardId(String dirtyWardId) {
        return dirtyWardId.toUpperCase().trim();
    }

    public static String normalizeWing(String dirtyWing) {

        if (dirtyWing.trim().isEmpty()) {
            return null;
        }

        String[] words = dirtyWing.trim().split("\\s+");

        String firstWord = words[0].substring(0, 1).toUpperCase()
                + words[0].substring(1);

        String secWord = words[1].substring(0, 1).toUpperCase()
                + words[1].substring(1);

        return firstWord + " " + secWord;
    }

    public static String normalizeDepartment(String dirtyDep) {
        String[] words = dirtyDep.split("\\s+");

        String firstWord = words[0].substring(0, 1).toUpperCase() + words[0].substring(1).toLowerCase();

        if (firstWord.equals("Pediatrics")) {
            return "Paediatrics";
        }
        if (firstWord.equals("Icu")) {
            return "ICU";
        }

        return firstWord;
    }

    public static Integer normalizeBedsAvailable(String dirtyBeds) {
        try {
            Integer beds = Integer.parseInt(dirtyBeds.trim());

            if (beds < 0) {
                return null;
            }

            return beds;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static WardRecord normalizeWard(
            String dirtyWardId,
            String dirtyWing,
            String dirtyDepartment,
            String dirtyBeds) {

        // Step 1: Normalize each field
        String wardId = normalizeWardId(dirtyWardId);
        String wing = normalizeWing(dirtyWing);
        String department = normalizeDepartment(dirtyDepartment);
        Integer bedsAvailable = normalizeBedsAvailable(dirtyBeds);

        // Step 2: Create a list to store validation warnings
        List<String> warnings = new ArrayList<>();

        // Step 3: Check whether the wing is missing
        if (wing == null) {
            warnings.add("wing was missing — flagged for follow-up");
        }

        // Step 4: Check whether bedsAvailable is invalid
        if (dirtyBeds.trim().startsWith("-")) {
            warnings.add(
                    "bedsAvailable was negative ('" + dirtyBeds.trim()
                            + "') — flagged for follow-up"
            );
        } else if (bedsAvailable == null) {
            warnings.add(
                    "bedsAvailable was non-numeric ('" + dirtyBeds
                            + "') — flagged for follow-up"
            );
        }

        // Step 5: Combine all warnings into one String
        String notes = String.join("; ", warnings);

        // Step 6: Create and return the cleaned WardRecord
        return new WardRecord(
                wardId,
                wing,
                department,
                bedsAvailable,
                notes
        );
    }
}
