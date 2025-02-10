package org.strassburger.cookieclickerz.util.achievements;

import org.strassburger.cookieclickerz.util.NumFormatter;

import java.math.BigInteger;

public class Achievement {
    private final AchievementType type;
    private int progress = 0;

    public Achievement(AchievementType type) {
        this.type = type;
    }

    public Achievement(AchievementType type, int progress) throws IllegalArgumentException {
        if (type == null) throw new IllegalArgumentException("Achievement type cannot be null");
        this.type = type;
        this.progress = progress;
    }

    public AchievementType getType() {
        return type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        if (type.getCategory() == AchievementCategory.COOKIES) return progress == 1;
        return progress >= type.getIntGoal();
    }

    public int getGoal() {
        return type.getIntGoal();
    }

    public BigInteger getBigIntegerGoal() {
        return type.getBigIntegerGoal();
    }

    public void printState() {
        System.out.println("---\nAchievement " + getType() + "\nprogress: " + getProgress() + " / " + NumFormatter.formatBigInt(getType().getBigIntegerGoal()) + "\ncompleted: " + isCompleted() + "\n---");
    }
}
