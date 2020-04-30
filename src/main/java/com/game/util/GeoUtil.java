package com.game.util;

public class GeoUtil {


    public static Double calculateDistance(Double latIni, Double lonIni, Double latEnd, Double lonEnd) {
//        double theta = lonIni - lonEnd;
//        double dist = Math.sin(deg2rad(latIni)) * Math.sin(deg2rad(latEnd))
//                + Math.cos(deg2rad(lonIni)) * Math.cos(deg2rad(lonEnd)) * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515 * 1.609344;
//        } else if (unit == 'N') {
//            dist = dist * 0.8684;
//        }
//        return (dist);
        return 0d;
    }

    public static Double deg2rad(Double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static Double rad2deg(Double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
