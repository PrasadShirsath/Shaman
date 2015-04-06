package recover;

import java.util.ArrayList;

import snapshot.SnapshotService;

import com.vmware.vim25.mo.VirtualMachine;

public class RecoveryThread extends Thread 
{
	VirtualMachine vm=null;
	private static ArrayList<String> penddingRecovery=new ArrayList<String>();

	public RecoveryThread(VirtualMachine vm) {

		this.vm=vm;
	}

	public void run()
	{
		penddingRecovery.add(vm.getName());

		String hostIp=VMService.getHostIP(vm);

		if(HeartBeatsService.isAlive(hostIp)) //Host is alive
		{
			System.out.println("Host of VM "+vm.getName()+" is alive.");
			System.out.println("Trying to recover VM: "+vm.getName()+" to most recent snapshot."+ vm.getSnapshot());
			synchronized (this) {


				SnapshotService.revertToMostRecentSnapshot(vm);

				System.out.println("Successfully reverted VM: "+vm.getName()+" to most recent snapshot.");

				synchronized (vm) {
					vm.notify();
					System.out.println("Notify that it's done");
					penddingRecovery.remove(vm.getName());
				}
				if(VMService.restartVMAndmakeReady(vm))
				{
					if(HeartBeatsService.isAlive(vm.getGuest().getIpAddress()))
					{
						System.out.println("VM "+vm.getName()+" is pinging now.");
						System.out.println("VM "+vm.getName()+" recovered successfully!");
						

						for(String a:penddingRecovery)
						{
							System.out.println("Pendding====== "+a);
						}
					}
				}
			}

		}
		else  //Host is not alive
		{

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

}
