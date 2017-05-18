package com.myproject.huutam.test.item;

/**
 * Created by Quyen Hua on 5/8/2017.
 */

public class MissionItem {
    private int level;
    private boolean lock;
    private int imageStar;
    private int background;
    private int image;

    public MissionItem(int level, boolean lock, int imageStar, int background, int image) {
        this.level = level;
        this.lock = lock;
        this.imageStar = imageStar;
        this.background = background;
        this.image = image;
    }

    public MissionItem(int level, boolean lock, int imageStar, int background) {
        this.level = level;
        this.lock = lock;
        this.imageStar = imageStar;
        this.background = background;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public int getImageStar() {
        return imageStar;
    }

    public void setImageStar(int imageStar) {
        this.imageStar = imageStar;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
