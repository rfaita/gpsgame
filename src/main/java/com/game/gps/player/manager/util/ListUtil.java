package com.game.gps.player.manager.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtil {

    public static <T> List<T> concat(List<T> list, List<T> list2) {
        return Stream.concat(list.stream(), list2.stream())
                .collect(Collectors.toUnmodifiableList());
    }

    public static <T> List<T> concat(List<T> list, T element) {
        return concat(list, List.of(element));
    }

}
