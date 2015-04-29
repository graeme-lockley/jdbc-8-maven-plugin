package za.co.no9.jdbcdry.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static <T> List<T> fromIterable(Iterable<T> fieldsMetaData) {
        List<T> result = new ArrayList<>();
        for(T fieldMetaData: fieldsMetaData) {
            result.add(fieldMetaData);
        }
        return result;
    }
}
