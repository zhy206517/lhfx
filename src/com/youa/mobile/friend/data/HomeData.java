package com.youa.mobile.friend.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.youa.mobile.input.data.PublishData;

public class HomeData implements Serializable{
	public User originUser;
	public User transPondUser;
	public User PublicUser;
	public List<PublishData> draftList;
}
