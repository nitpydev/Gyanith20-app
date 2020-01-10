package com.barebrains.gyanith20.models;

import android.widget.ListView;

import java.util.List;

public class Post {
    public String postId;
    public String username;
    public String gyanithId;
    public long time;
    public String caption;
    public List<String> imgIds;
    public long likes;

    public Post(String postId,String username,String gyanithId, long time, String caption, List<String> imgIds)
    {
        this.postId = postId;
        this.username = username;
        this.gyanithId = gyanithId;
        this.time = time;
        this.caption = caption;
        this.imgIds = imgIds;
        likes = 0;
    }

    public Post(){}
}
