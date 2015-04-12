package recover;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;


public class HostService {

	public static ArrayList<HostSystem> getAllVHosts(ServiceInstance serviveInstance)
	 {
		
		 Folder rootFolder = serviveInstance.getRootFolder();		    
	        InventoryNavigator navigator = new InventoryNavigator(rootFolder);
		    ManagedEntity[] hostSystems = null;
			try {
				hostSystems = navigator.searchManagedEntities("HostSystem");
			} catch (InvalidProperty e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    ArrayList<HostSystem> hosts = new ArrayList<HostSystem>();
		    
		    for(ManagedEntity managedEntity: hostSystems)
		    {
		    	hosts.add((HostSystem)managedEntity);
		    	
		    }
		    return hosts;
	 }
	
	public static VirtualMachine getResponsibleHost(String hostIp)
	{
		VirtualMachine vHost=null;
		ArrayList<VirtualMachine> Vhost= VMService.getAllVMs(ServiceInstanceSingleton.getServiceClassInstance());
	 	
		for(final VirtualMachine v:Vhost)
	 	{
			if(v.getName().contains(hostIp.substring(hostIp.length() - 7)))
			{
				vHost= v;
				break;
			}
	 	}
		return vHost;
		
	}
	
	public static String getAnotherHost(String hostIp)
	{
		ArrayList<HostSystem> hosts=getAllVHosts(ServiceInstanceSingleton.getServiceInstance());
		
		for(HostSystem host:hosts)
		{
			if(!host.getName().equals(hostIp))
			{
				return host.getName();
			}
		}
	
		return null;
		
	}

}
