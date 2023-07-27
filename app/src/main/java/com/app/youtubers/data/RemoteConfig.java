package com.app.youtubers.data;

import com.app.youtubers.advertise.AdConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class RemoteConfig {

    public static long app_version = 0;
    public static boolean force_update = false;
    public static String channel_id = "UCCw71tkKLvdxUosX8_1btgw";
    public static String playlist_id = "UUCw71tkKLvdxUosX8_1btgw";

    // Set data from remote config
    public static void setFromRemoteConfig(FirebaseRemoteConfig remote){
        String ad_network = remote.getString("ad_network");
        if(!remote.getString("app_version").isEmpty()) RemoteConfig.app_version = Long.parseLong(remote.getString("app_version"));
        if(!remote.getString("force_update").isEmpty()) RemoteConfig.force_update = Boolean.parseBoolean(remote.getString("force_update"));
        if(!remote.getString("channel_id").isEmpty()) RemoteConfig.channel_id = remote.getString("channel_id");
        if(!remote.getString("playlist_id").isEmpty()) RemoteConfig.playlist_id = remote.getString("playlist_id");
    }

}
