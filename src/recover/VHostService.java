package recover;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.ComputeResourceConfigSpec;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostConnectSpec;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.Permission;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Datacenter;
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
	public static String addNewVhost(){

		String newHostIp="130.65.132.211";
		HostConnectSpec hcSpec = new HostConnectSpec();
		hcSpec.setHostName("130.65.132.211");
		hcSpec.setUserName("root");
		hcSpec.setPassword("12!@qwQW");
		hcSpec.setSslThumbprint("40:6E:0D:47:5B:FC:8C:90:3D:55:13:DF:B6:DB:2D:3F:C3:1F:B9:1D");
		ComputeResourceConfigSpec compResSpec = new ComputeResourceConfigSpec();
		Task task  = null;
		try {

			Permission permission = new Permission();
			permission.setPropagate(true);


			permission.setEntity(ServiceInstanceSingleton.getServiceInstance().getMOR());
			Folder rootFolder = ServiceInstanceSingleton.getServiceInstance().getRootFolder();

			ManagedEntity[] dcs = new InventoryNavigator(rootFolder).searchManagedEntities(
					new String[][] { {"Datacenter", "name" }, }, true);

			task = ((Datacenter)dcs[0]).getHostFolder().addStandaloneHost_Task(hcSpec, compResSpec, true);
			try {
				if(task.waitForMe() == Task.SUCCESS){
					System.out.println("Host Created Succesfully");
					return newHostIp;
				}
			} catch (Exception e) {
				System.out.println("Error in creating a new vHost2 : " + e);
			}
		} catch (Exception e) {
			System.out.println("Error in creating a new vHost : " + e);
		}

		return "Host Creation Failed!";
	}
	
	
}
