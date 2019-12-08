package com.barebrains.gyanith20.Models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Post {
    public String postId;
    public String username;
    public long time;
    public String caption;
    public List<String> imgIds;
    public long likes;
    public long shares;
    public List<String> likedUsers;

    public Post(String postId,String username, long time, String caption, List<String> imgIds)
    {
        this.postId = postId;
        this.username = username;
        this.time = time;
        this.caption = caption;
        this.imgIds = imgIds;
        likes = 0;
        shares = 0;
        likedUsers = new LinkedList<>();
    }

    public Post(){}
}
