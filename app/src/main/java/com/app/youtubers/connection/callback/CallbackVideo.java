package com.app.youtubers.connection.callback;

import com.app.youtubers.connection.responses.ResponseVideos;

public interface CallbackVideo {

    void onComplete(ResponseVideos data);

    void onFailed();
}
