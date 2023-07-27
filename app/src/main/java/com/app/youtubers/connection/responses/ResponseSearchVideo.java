package com.app.youtubers.connection.responses;

import com.app.youtubers.model.SearchItemModel;

import java.io.Serializable;
import java.util.List;

public class ResponseSearchVideo implements Serializable {
    public String nextPageToken;
    public List<SearchItemModel> items;
}
