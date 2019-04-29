package com.kyle_jason.imagefinal;

public enum ModelObject {

    TEST(R.string.test, R.layout.view_image1),
    RED(R.string.red, R.layout.view_red);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
