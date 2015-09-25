package com.demo.mike.model;

import com.demo.models.ModelBase;

/**
 * Created by Rohit on 06/09/15.
 */
public class Category extends ModelBase {

    private String name;
    private int imageId;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
