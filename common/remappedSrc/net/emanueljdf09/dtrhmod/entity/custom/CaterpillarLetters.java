package net.emanueljdf09.dtrhmod.entity.custom;

import java.util.*;

public class CaterpillarLetters {

    public enum Letter { O, R, U }

    private static final Map<Letter, List<double[]>> SHAPES = new EnumMap<>(Letter.class);

    static {
        List<double[]> o = new ArrayList<>();
        arc(o, 0.0, 2.0, 1.5, 1.9, 0.0, Math.PI * 2.0, 28);
        SHAPES.put(Letter.O, Collections.unmodifiableList(o));


        List<double[]> u = new ArrayList<>();
        line(u, -1.4, 3.8, -1.4, 1.6);
        arc(u, 0.0, 1.6, 1.4, 1.4, Math.PI, Math.PI * 2.0, 12);
        line(u, 1.4, 1.6, 1.4, 3.8);
        SHAPES.put(Letter.U, Collections.unmodifiableList(u));

        List<double[]> r = new ArrayList<>();
        line(r, -1.2, 0.1, -1.2, 3.9);
        line(r, -1.2, 3.9, 0.3, 3.9);
        arc(r, 0.3, 2.9, 1.0, 1.0, Math.PI / 2.0, -Math.PI / 2.0, 12);
        line(r, 0.3, 1.9, -1.2, 1.9);
        line(r, -0.1, 1.9, 1.4, 0.1);
        SHAPES.put(Letter.R, Collections.unmodifiableList(r));
    }

    private CaterpillarLetters() { }

    public static List<double[]> points(Letter letter) {
        return SHAPES.get(letter);
    }

    private static void line(List<double[]> out, double u0, double v0, double u1, double v1) {
        double len = Math.hypot(u1 - u0, v1 - v0);
        int steps = Math.max(2, (int) Math.ceil(len / 0.4));
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            out.add(new double[]{u0 + (u1 - u0) * t, v0 + (v1 - v0) * t});
        }
    }

    private static void arc(List<double[]> out, double cu, double cv, double ru, double rv, double a0, double a1, int steps) {
        for (int i = 0; i <= steps; i++) {
            double a = a0 + (a1 - a0) * i / steps;
            out.add(new double[]{cu + ru * Math.cos(a), cv + rv * Math.sin(a)});
        }
    }
}
