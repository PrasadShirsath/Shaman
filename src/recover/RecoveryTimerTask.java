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
	 		if(!HeartBeatsService.isAlive(vm.getGuest().getIpAddress())) //VM is not alive
	 		{
	 			if (vm.getOverallStatus() == ManagedEntityStatus.yellow) {
	 				System.out.println(vm.getName() + " was turned off manually status:"+vm.getOverallStatus());
	 				// poweredOffVM.add(vm.getName());
	 			}
	 			else
	 			{
	 					System.out.println(vm.getName()+" is not alive.");	
	 					RecoveryThread recover = new RecoveryThread(vm);
	 					if(recover.isVMPenddingforRecovery(vm))
	 					{
	 						System.out.println("VM "+vm.getName()+" is already being recovered...");
	 					}
	 					else{
		 					System.out.println(vm.getName() + " need to be recovered.."+vm.getOverallStatus());
	 						recover.start();
	 					}
		 			
					}
	 				
	 		}
	 		else{    //VM is alive so do nothing..
	 			System.out.println(vm.getName()+" is alive.");
	 		}
	 	} 	
		
	}

}
