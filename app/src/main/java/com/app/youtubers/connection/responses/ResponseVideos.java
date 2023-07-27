package com.app.youtubers.connection.responses;

import com.app.youtubers.model.Video;

import java.io.Serializable;
import java.util.List;

public class ResponseVideos implements Serializable {
    public String nextPageToken;
    public List<Video> items;
}
