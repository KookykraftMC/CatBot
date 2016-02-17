package com.kookykraftmc.CatBot;

import java.util.List;
import java.util.Vector;

public class BadPerson
{
	public static List<String> nameList = new Vector<String>();
	public String username;
	public int warnTimes;
	public BadPerson(String name)
	{
		username = name;
		warnTimes = 1;
		nameList.add(name);
	}
}
