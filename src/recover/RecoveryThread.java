package recover;

import java.awt.color.CMMException;
import java.util.ArrayList;

import snapshot.SnapshotService;

import com.java.shaman.Config;
import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.mo.VirtualMachine;

public class RecoveryThread //extends Thread 
{
	VirtualMachine vm=null;
	
	public RecoveryThread(VirtualMachine vm) {

		this.vm=vm;
	}

	public void run()
	{
		System.out.println("<<<<<<<<<<<<<<<<<Recovery Started>>>>>>>>>>>>>>>>>>>");

		String hostIp=VMService.getHostIP(vm);
		
		if(Config.isVHostPenddingforRecovery(hostIp)){  //host is already being recovered
			System.out.println("Host:"+ hostIp +" of this VM is being recovered.");
			return;
		}
		if(HeartBeatsService.isAlive(hostIp)) //Host is alive
		{		
			System.out.println("Host of VM "+vm.getName()+" is alive.");
			System.out.println("Trying to recover VM: "+vm.getName()+" to most recent snapshot."+ vm.getSnapshot());
			if(vm.getSnapshot()==null)
			{
				System.out.println("There is no snap-shot for VM: "+vm.getName());

				return;
			}
			Config.addToPenddingVM(vm.getName());
			SnapshotService.revertToMostRecentSnapshot(vm);

			synchronized (vm) {
				vm.notify();		
				System.out.println("Reverted VM: "+vm.getName()+" to most recent snapshot.");
			}

//			if(VMService.restartVMAndmakeReady(vm))
//			{
//
//				if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
//				{
//					System.out.println("VM "+vm.getName()+" recovered successfully!");
//					Config.removeFromPenddingVM(vm.getName());
//				}
//			}

		}
		else  //Host is not alive
		{


			
			System.out.println("Host:"+ hostIp +" is not alive! Host need to be recovered!");


			VirtualMachine v= HostService.getResponsibleHost(hostIp);

//			if (v.getOverallStatus() == ManagedEntityStatus.yellow) {
//				System.out.println(v.getName() + " was turned off manually status:"+vm.getOverallStatus());
//				// poweredOffVM.add(vm.getName());
//			}
//			else
//			{
				Config.addToPenddingVHost(hostIp);
				if(SnapshotService.revertToMostRecentSnapshot(v)) //revert success true 
				{

					synchronized (v) {
						v.notify();
						System.out.println("Host "+v.getName()+" recovered successfully! Trying to ping the host...");
					}
					if(VHostService.restartVMAndmakeReady(v,hostIp))
					{

						if(HeartBeatsService.isAlive(hostIp))
						{
							System.out.println("Host "+v.getName()+" is pinging now!");
							Config.removeFromPenddingVHost(hostIp);
						}
					}
//					if(VMService.restartVMAndmakeReady(vm))
//					{
//
//						if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
//						{
//							System.out.println("VM "+vm.getName()+" is pinging now!");
//							Config.removeFromPenddingVM(vm.getName());
//						}
//					}

				}else{   //revert success false
					Config.removeFromPenddingVHost(hostIp);
					String newHost = HostService.getAnotherHost(hostIp);
					if(newHost!=null) // if another hhost is present then migrate 
					{
						System.out.println("VM "+vm.getName()+" is migrating to new host: "+newHost);
						try {
							VMService.powerOffVM(vm);
							VMService.migrateVM(vm,newHost);
							VMService.powerOnVM(vm);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("VM "+vm.getName()+" is migrated to new host: "+hostIp);

					}
					else{   //if another host is not present then add new host.
						
						System.out.println("New host being added...");
						 newHost = VHostService.addNewVhost();
							System.out.println("VM "+vm.getName()+" is migrating to new host: "+newHost);

						 try {
								VMService.powerOffVM(vm);
								VMService.migrateVM(vm,newHost);
								VMService.powerOnVM(vm);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("VM "+vm.getName()+" is migrated to new host: "+newHost);	
					}

				}
			}
		
		System.out.println("Trying to ping VM "+vm.getName());
		if(VMService.restartVMAndmakeReady(vm))
			{

				if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
				{
					System.out.println("VM "+vm.getName()+" is pinging now!");
					System.out.println("VM "+vm.getName()+" recovered successfully!");
					Config.removeFromPenddingVM(vm.getName());
				}
			}
		else{
			System.out.println("VM is taking too long to give response!");
		}
		

		System.out.println("<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>");

	}

	
}
