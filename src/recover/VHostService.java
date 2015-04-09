package recover;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.vmware.vim25.FileFault;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class VHostService 
{
	
	public static ArrayList<VirtualMachine> getAllVHost(ServiceInstance serviveInstanceofClass)
	{
		Folder rootFolder = serviveInstanceofClass.getRootFolder();		    
		InventoryNavigator navigator = new InventoryNavigator(rootFolder);
		ManagedEntity[] virtualMachines = null;
		try {
			virtualMachines = navigator.searchManagedEntities("VirtualMachine");
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
		ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();

		for(ManagedEntity managedEntity: virtualMachines)
		{
			vms.add((VirtualMachine)managedEntity);
		}
		return vms;
	}


	public static boolean restartVMAndmakeReady(VirtualMachine vm,String hostIp) {
		
		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
		{
			Task task;
			try {
				task = vm.powerOnVM_Task(null);
				task.waitForTask();
			} catch (VmConfigFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TaskInProgress e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidState e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsufficientResourcesFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		do {
		} while (!HeartBeatsService.isAlive(hostIp));

		return true;
		
	}
	
}
