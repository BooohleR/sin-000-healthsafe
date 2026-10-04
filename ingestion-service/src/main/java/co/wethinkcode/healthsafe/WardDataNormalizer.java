package co.wethinkcode.healthsafe;

public class WardDataNormalizer {

//    public String normalizeWardId = normalizeWard;

    public static String normalizeWardId(String dirtyWardId){
        return dirtyWardId.toUpperCase();
    }

}