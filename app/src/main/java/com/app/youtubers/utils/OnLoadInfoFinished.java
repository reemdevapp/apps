package com.app.youtubers.utils;

import com.app.youtubers.room.table.EntityInfo;

public interface OnLoadInfoFinished {

    void onComplete(EntityInfo data);

    void onFailed();

}
