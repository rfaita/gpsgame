package com.game.util;

import com.game.dto.Position;

public class GeoUtil {

    private static final Integer EARTH_RADIUS = 6378100;
    private static final Double METERS_IN_ONE_RADIUS = 111300d;

    public static Double distance(Position ini, Position end) {
        Double a = 0.5 - Math.cos((end.getLat() - ini.getLat()) * Math.PI / 180) / 2
                + Math.cos(ini.getLon() * Math.PI / 180)
                * Math.cos(end.getLon() * Math.PI / 180)
                * (1 - Math.cos((end.getLon() - ini.getLon()) * Math.PI / 180)) / 2;
        return EARTH_RADIUS * 2 * Math.asin(Math.sqrt(a));
    }

    public static Position randomGeo(Position center, Integer radiusInMeters) {
        Double y0 = center.getLat();
        Double x0 = center.getLon();
        Double rd = radiusInMeters / METERS_IN_ONE_RADIUS;

        Double u = Math.random();
        Double v = Math.random();

        Double w = rd * Math.sqrt(u);
        Double t = 2 * Math.PI * v;
        Double x = w * Math.cos(t);
        Double y = w * Math.sin(t);

        //Adjust the x-coordinate for the shrinking of the east-west distances
        //????
        //Double xp = x / Math.cos(y0);

        return Position.builder()
                .lat(y + y0)
                .lon(x + x0)
                .build();
    }

}