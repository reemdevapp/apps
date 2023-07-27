package com.app.youtubers.connection.callback;

import com.app.youtubers.connection.responses.ResponseSearchVideo;

public interface CallbackSearchVideo {

    void onComplete(ResponseSearchVideo data);

    void onFailed();

}
