package com.game.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtil {

    public static <T> List<T> concat(final List<T> list, final List<T> list2) {
        return Stream.concat(list.stream(), list2.stream())
                .collect(Collectors.toUnmodifiableList());
    }

    public static <T> List<T> concat(final List<T> list, final T element) {
        return concat(list == null ? List.of() : list, List.of(element));
    }

}
