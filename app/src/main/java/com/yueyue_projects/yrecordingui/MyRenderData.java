package com.yueyue_projects.yrecordingui;

import com.yueyue_projects.library.RenderData;

public class MyRenderData extends RenderData {

    int volumeHigh;

    public MyRenderData(int volume) {
        super(volume);
    }

    public int getVolumeHigh() {
        return volumeHigh;
    }

    public void setVolumeHigh(int volumeHigh) {
        this.volumeHigh = volumeHigh;
    }
}
