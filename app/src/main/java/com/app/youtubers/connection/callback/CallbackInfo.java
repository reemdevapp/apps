package com.app.youtubers.connection.callback;

import com.app.youtubers.connection.responses.ResponseInfo;

public interface CallbackInfo {

    void onComplete(ResponseInfo data);

    void onFailed();
}
