package com.barebrains.gyanith20.Models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Post {
    public String postId;
    public String username;
    public String gyanithId;
    public long time;
    public String caption;
    public List<String> imgIds;
    public long likes;
    public long shares;

    public Post(String postId,String username,String gyanithId, long time, String caption, List<String> imgIds)
    {
        this.postId = postId;
        this.username = username;
        this.gyanithId = gyanithId;
        this.time = time;
        this.caption = caption;
        this.imgIds = imgIds;
        likes = 0;
        shares = 0;
    }

    public Post(){}
}
