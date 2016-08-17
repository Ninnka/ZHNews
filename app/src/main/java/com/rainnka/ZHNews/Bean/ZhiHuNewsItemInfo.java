package com.rainnka.ZHNews.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/5/16 16:16
 * Project name is ZHKUNews
 */
public class ZhiHuNewsItemInfo implements Serializable {

	public int item_layout;

	public int date_cus;

	public String body;

	public String image_source;

	public String title;

	public String image;

	public List<String> images = new ArrayList<>();

	public String share_url;

	public Object js;

	public String ga_prefix;

	public int type = 0;

	public int id;

	public List<String> css;

	public String thumbnail;

	public String url_hot;

}
