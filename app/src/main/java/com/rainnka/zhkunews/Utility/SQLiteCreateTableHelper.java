package com.rainnka.zhkunews.Utility;

/**
 * Created by rainnka on 2016/7/14 14:25
 * Project name is ZHKUNews
 */
public class SQLiteCreateTableHelper {

	public final static String CREATE_PRAISE_TABLE = "create table if not exists my_praise (Id " +
			"integer primary key, ItemId text, ItemImage text, ItemTitle text)";

	public final static String CREATE_STAR_TABLE = "create table if not exists my_star (Id " +
			"integer primary key, ItemId text, ItemImage text, ItemTitle text)";

}
