package recover;

import java.util.ArrayList;

import snapshot.SnapshotService;

import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.mo.VirtualMachine;

public class RecoveryThread extends Thread 
{
	VirtualMachine vm=null;
	private static ArrayList<String> penddingRecovery=new ArrayList<String>();
	
	private static ArrayList<String> penddingVHostRecovery=new ArrayList<String>();

	public RecoveryThread(VirtualMachine vm) {

		this.vm=vm;
	}

	public void run()
	{
		

		String hostIp=VMService.getHostIP(vm);

		if(HeartBeatsService.isAlive(hostIp)) //Host is alive
		{
			
			
			System.out.println("Host of VM "+vm.getName()+" is alive.");
			System.out.println("Trying to recover VM: "+vm.getName()+" to most recent snapshot."+ vm.getSnapshot());
			if(vm.getSnapshot()==null)
			{
				System.out.println("There is no snap-shot for VM: "+vm.getName());
				
				return;
			}
			penddingRecovery.add(vm.getName());
			SnapshotService.revertToMostRecentSnapshot(vm);

				synchronized (vm) {
					vm.notify();		
					System.out.println("Reverted VM: "+vm.getName()+" to most recent snapshot.");
				}
				
				if(VMService.restartVMAndmakeReady(vm))
				{
				
					if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
					{
						System.out.println("VM "+vm.getName()+" is pinging now.");
						System.out.println("VM "+vm.getName()+" recovered successfully!");
						penddingRecovery.remove(vm.getName());
					}
				}

		}
		else  //Host is not alive
		{
			if(isVHostPenddingforRecovery(hostIp)){  //host is already being recovered
				System.out.println("Host:"+ hostIp +" is not alive! It is being recovered.");
				return;
			}
			System.out.println("Host:"+ hostIp +" is not alive! Host need to be recovered!");

			ArrayList<VirtualMachine> Vhost= VMService.getAllVMs(ServiceInstanceSingleton.getServiceClassInstance());
		 	
			for(final VirtualMachine v:Vhost)
		 	{
				System.out.println("*************************************");
				if(v.getName().contains(hostIp.substring(hostIp.length() - 7)))
				{
					System.out.println("****************"+hostIp+"*********************");

					penddingVHostRecovery.add(hostIp);
					SnapshotService.revertToMostRecentSnapshot(v);
					System.out.println("Successfully reverted Host: "+vm.getName()+" to most recent snapshot.");

					synchronized (v) {
						v.notify();
						System.out.println("Host "+v.getName()+" recovered successfully!");
					}
					if(VHostService.restartVMAndmakeReady(v,hostIp))
					{
					
						if(HeartBeatsService.isAlive(hostIp))
						{
							System.out.println("Host "+v.getName()+" is pinging now.");
							System.out.println("Host "+v.getName()+" recovered successfully!");
							penddingVHostRecovery.remove(hostIp);
						}
					}
					if(VMService.restartVMAndmakeReady(vm))
					{
					
						if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
						{
							System.out.println("VM "+vm.getName()+" is pinging now.");
							System.out.println("VM "+vm.getName()+" recovered successfully!");
							penddingRecovery.remove(vm.getName());
						}
					}
					break;

				}
		 	} 	
			
		}
	}

	boolean isVMPenddingforRecovery(VirtualMachine vm)
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
	
	boolean isVHostPenddingforRecovery(String vHostName)
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

}
