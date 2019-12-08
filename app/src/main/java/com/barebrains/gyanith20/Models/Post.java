package com.barebrains.gyanith20.Models;

import java.util.List;

public class Post {
    public String postId;
    public String username;
    public long time;
    public String caption;
    public List<String> imgIds;
    public long likes = 0;
    public long shares = 0;

    public Post(String postId,String username, long time, String caption, List<String> imgIds)
    {
        this.postId = postId;
        this.username = username;
        this.time = time;
        this.caption = caption;
        this.imgIds = imgIds;
    }
}
