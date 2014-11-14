package com.groupfio.message.pojo;

public interface ActionMessageConstants {
	
	//public static String N = "";
	//Action ids
	public static String LIC_FILE_ACTION_MSG = "ChecksumAndFileSize";
	public static String IS_ENABLED_ACTION_MSG = "IsEnabled";
	public static String TERM_ACTION_MSG = "Terminate";
	
	
	//end point locations
	public static String LIC_FILE_ACTION_DESTINATION = "/app/licfileverify";
	public static String IS_ENABLED_ACTION_DESTINATION = "/app/isenabled";

}
