package com.java.shaman;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vmware.vim25.mo.VirtualMachine;

public class Config 
{
	private final static String username="administrator";
	private final static String password="12!@qwQW";
	private final static String class_Vc="https://130.65.132.19/sdk";
	private final static String class_Vc_Username="student@vsphere.local";
	private final static String host_user ="root";
	private final static String team9_Vc="https://130.65.132.109/sdk";
	 static String[] MyHosts = {"T09-vHost_132.212","T09-vHost_132.213","T09-vHost01_132.211"};
	private static List<String> MyHostsList = Arrays.asList(MyHosts);
	
	private static ArrayList<String> penddingRecovery=new ArrayList<String>();
	private static ArrayList<String> penddingVHostRecovery=new ArrayList<String>();

	


	public static final String getTeam9_Vc() {
		return team9_Vc;
	}
	
	public static final String getUsername() {
		return username;
	}
	
	public static final String getPassword() {
		return password;
	}
	
	public static final String getClass_Vc() {
		return class_Vc;
	}

	public static final String getHost_user() {
		return host_user;
	}
	public static final String getClassVcUsername() {
		return class_Vc_Username;
	}
	public static final List<String> getMyHosts() {
		return MyHostsList;
	}
	
	public static boolean isVMPenddingforRecovery(VirtualMachine vm)
	{
		if(penddingRecovery.contains(vm.getName()))
		{
			for(String a:penddingRecovery)
			{
				System.out.println("Pendding====== "+a);
			}
			return true;
		}
		return false;
	}

	public static boolean isVHostPenddingforRecovery(String vHostName)
	{
		if(penddingVHostRecovery.contains(vHostName))
		{
			for(String a:penddingVHostRecovery)
			{
				System.out.println("Pendding====== "+a);
			}
			return true;
		}
		return false;
	}
	public static void addToPenddingVM(String vmname)
	{
		if(!penddingRecovery.contains(vmname))
			penddingRecovery.add(vmname);

	}
	public static void addToPenddingVHost(String hostIp)
	{
		if(!penddingVHostRecovery.contains(hostIp))
			penddingVHostRecovery.add(hostIp);
	}
	public static void removeFromPenddingVM(String vmname)
	{
		if(penddingRecovery.contains(vmname))
			penddingRecovery.remove(vmname);

	}
	public static void removeFromPenddingVHost(String hostIp)
	{
		if(penddingVHostRecovery.contains(hostIp))
			penddingVHostRecovery.remove(hostIp);
	}
	
}
