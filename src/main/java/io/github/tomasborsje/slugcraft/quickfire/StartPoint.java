package io.github.tomasborsje.slugcraft.quickfire;

public class StartPoint {
    public final int x;
    public final int z;
    public final String name;
    public final int spreadRadius;
    public final float roundTimeMultiplier;
    public StartPoint(int x, int z, String name, int spreadRadius) {
        this.x = x;
        this.z = z;
        this.name = name;
        this.spreadRadius = spreadRadius;
        this.roundTimeMultiplier = 1.0f;
    }
    public StartPoint(int x, int z, String name, int spreadRadius, float roundTimeMultiplier) {
        this.x = x;
        this.z = z;
        this.name = name;
        this.spreadRadius = spreadRadius;
        this.roundTimeMultiplier = roundTimeMultiplier;
    }
}
