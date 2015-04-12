package recover;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.java.shaman.ServiceInstanceSingleton;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateDiskMoveOptions;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class VMService {


	void listVMsWithStatus(String url,String username,String password) throws RemoteException, MalformedURLException
	{
		ServiceInstance si = new ServiceInstance(
				new URL(url), username, password, true);
		Folder rootFolder = si.getRootFolder();

		System.out.println("\n============ Virtual Machines============");
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"VirtualMachine", "name" }, }, true);
		for(int i=0; i<vms.length; i++)
		{
			VirtualMachine vm = (VirtualMachine) vms[i];
			VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();

			System.out.println(i+") " + vm.getName()+"\t"+vmri.getPowerState());

			VirtualMachineConfigInfo congif= vm.getConfig();

			System.out.println(""+congif.getGuestFullName()+" "+vm.getDatastores()[0].getName());


		} 
	}
	public static void powerOnVM(VirtualMachine vm) throws RemoteException, MalformedURLException
	{
		if(vm==null)
		{
			System.out.println("Cannot find the VM " + "" 
					+ "\nExisting...");
			return;
		}
		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
		{
			Task task = vm.powerOnVM_Task(null);
			task.waitForMe();
			System.out.println("vm:" + vm.getName() + " powered off.");
		}
	}
	public static void powerOffVM(VirtualMachine vm) throws RemoteException, MalformedURLException
	{
		if(vm==null)
		{
			System.out.println("Cannot find the VM " + "" 
					+ "\nExisting...");
			return;
		}
		VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
		if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
		{
			Task task = vm.powerOffVM_Task();
			task.waitForMe();
			System.out.println("vm:" + vm.getName() + " powered off.");
		}
	}

	public void clone(String url,String username,String password,String vmname,String cloneName) throws RemoteException, MalformedURLException
	{

		ServiceInstance si = new ServiceInstance(
				new URL(url), username, password, true);

		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(
				rootFolder).searchManagedEntity(
						"VirtualMachine", vmname);

		if(vm==null)
		{
			System.out.println("No VM " + vmname + " found");
			si.getServerConnection().logout();
			return;
		}

		VirtualMachineCloneSpec cloneSpec = 
				new VirtualMachineCloneSpec();
		cloneSpec.setLocation(new VirtualMachineRelocateSpec());
		cloneSpec.setPowerOn(false);
		cloneSpec.setTemplate(false);

		Task task = vm.cloneVM_Task((Folder) vm.getParent(), 
				cloneName, cloneSpec);
		System.out.println("Launching the VM clone task. " +
				"Please wait ...");

		String status = task.waitForMe();
		if(status==Task.SUCCESS)
		{
			System.out.println("VM got cloned successfully.");
		}
		else
		{
			System.out.println("Failure -: VM cannot be cloned");
		}
	}

	public void printInventory(String ip,String username,String password) throws Exception
	{

		ServiceInstance si = new ServiceInstance(new URL("https://"+ip+"/sdk"), username, password, true);
		Folder rootFolder = si.getRootFolder();

		System.out.println("============ Data Centers ============");
		ManagedEntity[] dcs = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"Datacenter", "name" }, }, true);
		for(int i=0; i<dcs.length; i++)
		{
			System.out.println("Datacenter["+i+"]=" + dcs[i].getName());
		}

		System.out.println("\n============ Virtual Machines ============");
		ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"VirtualMachine", "name" }, }, true);
		for(int i=0; i<vms.length; i++)
		{
			System.out.println("vm["+i+"]=" + vms[i].getName());
		}

		System.out.println("\n============ Hosts ============");
		ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(
				new String[][] { {"HostSystem", "name" }, }, true);
		for(int i=0; i<hosts.length; i++)
		{
			System.out.println("host["+i+"]=" + hosts[i].getName());
		}

		si.getServerConnection().logout();
	}

	public static ArrayList<VirtualMachine> getAllVMs(ServiceInstance serviveInstance)
	{

		Folder rootFolder = serviveInstance.getRootFolder();		    
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
			if(!((VirtualMachine)managedEntity).getName().contains("template"))
			vms.add((VirtualMachine)managedEntity);
		}
		return vms;
	}
	public static boolean restartVMAndmakeReady(VirtualMachine vm) {

		int count=0;
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
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
			if(count>100)
				break;
		} while (!HeartBeatsService.isAlive(vm.getGuest().getIpAddress()));
		
		return count<100;

	
	
	}
	public static String getHostIP(VirtualMachine vm) {
		String host_ip = null;
		try {
			ArrayList<HostSystem> hostList=HostService.getAllVHosts(ServiceInstanceSingleton.getServiceInstance());

			for(HostSystem host:hostList)
			{
				VirtualMachine[] vm_pool = host.getVms();
				for (int j = 0; j < vm_pool.length; j++) {
					if (vm_pool[j].getName().equals(vm.getName())) {
						host_ip = host.getName();
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return host_ip;
	}

	public static boolean migrateVM(VirtualMachine vm, String newHost) throws Exception {

		Folder RootFolder = ServiceInstanceSingleton.getServiceInstance().getRootFolder();
		HostSystem hs= (HostSystem) new InventoryNavigator(RootFolder).searchManagedEntity("HostSystem", newHost);
		ComputeResource cr = (ComputeResource) hs.getParent();
		Task task = vm.migrateVM_Task(cr.getResourcePool(), hs, VirtualMachineMovePriority.highPriority, VirtualMachinePowerState.poweredOff);
		System.out.println("Try to migrate " + vm.getName() + " to " + hs.getName());
		if (task.waitForTask() == task.SUCCESS) 
		{
			System.out.println("Migration of virtual machine: " + vm.getName() + " successfully!");
			return true;
		} 
		else 
		{
			System.out.println("Migration of vm failed!");
			return false;
		}
	}

//	public static void doClone(String vmname, String clonename) throws Exception
//	{
//		
//		 Folder rootFolder = ServiceInstanceSingleton.getServiceInstance().getRootFolder();
//	        String cloneName=vmname+"_clone";
//	    //  String AlternateHostName="130.65.133.21";
//
//	        VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
//	    //  ResourcePool rp = (ResourcePool) new InventoryNavigator(rootFolder).searchManagedEntity("ResourcePool", "Resources");
//	        HostSystem targetHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem",alternateHost); 
//
//		
//		ManagedObjectReference targethostMOR =VHostService.getAllVHost(ServiceInstanceSingleton.getServiceClassInstance()).get(0).getMOR();
//	    ServiceInstance si = ServiceInstanceSingleton.getServiceInstance();
//	 
//	    VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
//	 
//	    if(vm==null)
//	    {
//	        System.out.println("VM " + vmname + " not found");
//	        si.getServerConnection().logout();
//	        throw new Exception("Source Virtual Machine Not Found");
//	    }
//	 
//	    VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
//	    VirtualMachineRelocateSpec relSpec = new VirtualMachineRelocateSpec();
//	    relSpec.diskMoveType = "createNewChildDiskBacking";
//	    relSpec.setHost(targethostMOR);
//	    cloneSpec.setLocation(relSpec);
//	    cloneSpec.setPowerOn(false);
//	    cloneSpec.setTemplate(false);
//	    cloneSpec.snapshot = vm.getCurrentSnapShot().getMOR();
//	 
//	    System.out.println("Cloning " + vmname + " into " + clonename);
//	    Task task = vm.cloneVM_Task((Folder) vm.getParent(), clonename, cloneSpec);
//	 
//	    String status = task.waitForMe();
//	    if(status==Task.SUCCESS)
//	    {
//	        System.out.println("VM cloned successfully.");
//	    }
//	    else
//	    {
//	        throw new Exception("Error while cloning VM");
//	    }
//	}

}
