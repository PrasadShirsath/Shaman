package com.java.shaman;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.ServiceInstance;


public class ServiceInstanceSingleton 
{
	private static ServiceInstance serviceInstance = null;

	public static final ServiceInstance getServiceInstance() {
		if(serviceInstance!=null)
		{
			return serviceInstance;
		}
		try {
			serviceInstance= new ServiceInstance(
			        new URL(Config.getTeam9_Vc()), Config.getUsername(), Config.getPassword(), true);
			return serviceInstance;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}