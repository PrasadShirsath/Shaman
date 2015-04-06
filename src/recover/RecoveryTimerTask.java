package recover;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;


public class RecoveryTimerTask extends TimerTask 
{

	@Override
	public void run() {
		
		ArrayList<VirtualMachine> vms= VMService.getAllVMs(ServiceInstanceSingleton.getServiceInstance());
	 	for(VirtualMachine vm:vms)
	 	{
	 		System.out.println(vm.getName()+" is alive "+HeartBeatsService.isAlive(vm.getGuest().getIpAddress()));
	 		if(!HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
	 		{
	 			if (vm.getOverallStatus() == ManagedEntityStatus.yellow) {
	 				System.out.println(vm.getName() + " was turned off manually"+vm.getOverallStatus());
	 				// poweredOffVM.add(vm.getName());
	 			}
	 			else
	 			{
	 					System.out.println(vm.getName() + " need to be recovered.."+vm.getOverallStatus());
	 				
	 					RecoveryThread recover = new RecoveryThread(vm);
	 					if(recover.isVMPenddingforRecovery(vm))
	 					{
	 						System.out.println("VM "+vm.getName()+" is already being recovered...");
	 					}
	 					else{
	 						recover.start();
	 					}
		 			
					}
	 				
	 		}
	 	} 
	 	
	 	ArrayList<HostSystem> hostList=HostService.getAllVHosts(ServiceInstanceSingleton.getServiceInstance());
		
		for(HostSystem host:hostList)
		{
			System.out.println(""+HeartBeatsService.isAlive(host.getName()));
		}
				
		
	}

}
