package co.wethinkcode.healthsafe;

public class WardDataNormalizer {

//    public String normalizeWardId = normalizeWard;

    public static String normalizeWardId(String dirtyWardId){
        return dirtyWardId.toUpperCase().trim();
    }

    public static String normalizeWing(String dirtyWing){

        String[] words = dirtyWing.trim().split("\\s+");

        String firstWord = words[0].substring(0, 1).toUpperCase()
                + words[0].substring(1);

        String secWord = words[1].substring(0,1).toUpperCase()
                + words[1].substring(1);

        return firstWord + " " + secWord;
    }

}