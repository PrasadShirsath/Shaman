package snapshot;


import java.util.ArrayList;
import java.util.TimerTask;

import recover.VMService;

import com.java.shaman.Config;
import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;


public class SnapshotsTimerTask extends TimerTask 
{

	@Override
	public void run() {
		
		ArrayList<VirtualMachine> vms= VMService.getAllVMs(ServiceInstanceSingleton.getServiceInstance());
	 	
		for(final VirtualMachine v:vms)
	 	{
			Thread t1 = new Thread(new Runnable() {
	 	     public void run() {
	 	    	SnapshotService.removeAllSnapshot(v);
				SnapshotService.takeSnapshot(v);	
	 	     }
	 	});  
	 	t1.start();
			
	 	} 	
		
		ArrayList<VirtualMachine> Vhost= VMService.getAllVMs(ServiceInstanceSingleton.getServiceClassInstance());
	 	
		for(final VirtualMachine v:Vhost)
		{
			if(Config.getMyHosts().contains(v.getName()))
			{
				Thread t1 = new Thread(new Runnable() {
			 	     public void run() {
			 	    	SnapshotService.removeAllSnapshot(v);
						SnapshotService.takeSnapshot(v);	
			 	     }
			 	});  
			 	t1.start();
			}
					
		}     
		
	}
	
	

}
