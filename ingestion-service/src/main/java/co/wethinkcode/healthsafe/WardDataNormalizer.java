package co.wethinkcode.healthsafe;

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

        String wardId = normalizeWardId(dirtyWardId);
        String wing = normalizeWing(dirtyWing);
        String department = normalizeDepartment(dirtyDepartment);
        Integer bedsAvailable = normalizeBedsAvailable(dirtyBeds);

        String notes = "";

        if (dirtyBeds.trim().startsWith("-")) {
            notes = "bedsAvailable was negative ('" + dirtyBeds.trim() + "') — flagged for follow-up";
        } else if (bedsAvailable == null) {
            notes = "bedsAvailable was non-numeric ('" + dirtyBeds + "') — flagged for follow-up";
        }

        return new WardRecord(wardId, wing, department, bedsAvailable, notes);
    }
}
