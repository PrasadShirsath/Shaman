package snapshot;


import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vmware.vim25.FileFault;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class SnapshotService {
	
	public static void takeSnapshot(VirtualMachine vm)
	{
		        String vmName = vm.getName().intern();
		        System.out.println("Creating Snapshot of VM: "+vmName+"...");
		        String description = String.format("Snapshot of VM with name %s at %s created.", vmName, new Date());
		        boolean memorySnapshotsSupported = vm.getCapability().memorySnapshotsSupported;
		        String snapshotName=getSnapShotName(vm);
		        Task snapshotTask = null;
				try {
					snapshotTask = vm.createSnapshot_Task(snapshotName, description, memorySnapshotsSupported, true);

					try {
						if (snapshotTask.waitForTask() == Task.SUCCESS) {
							// totalSnapshot++;
				
							System.out.println("Snapshot: "
									+snapshotName+" taken successfully!");
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block successfully 
						e.printStackTrace();
					}
				
				} catch (InvalidName e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (VmConfigFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SnapshotFault e) {
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
				} catch (RuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
		        
		    
	}
		    public static String getSnapShotName(VirtualMachine virtualMachine){
		    	Timestamp timestamp =new Timestamp(new java.util.Date().getTime());
		    	
		        return new StringBuilder().append("snap").append(virtualMachine.getName()).append(timestamp).toString().trim();
		    }
		    
		    public static boolean revertToMostRecentSnapshot(VirtualMachine vm) {
				System.out.println("Reverting to " +vm.getSnapshot()+"...");
				
				Task task;
				try {
					task = vm.revertToCurrentSnapshot_Task(null);
					
					if (task.waitForTask() == Task.SUCCESS) {
						System.out.println("Virtual machine "+vm.getName()+" successfully reverted to current snapshot");
						return true;
					}
				} catch (VmConfigFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SnapshotFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TaskInProgress e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidState e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InsufficientResourcesFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotFound e) {
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
				return false;
				
			}

			public static void removeAllSnapshot(VirtualMachine vm) {
				Task task;
				try {
					System.out.println("Removing all snapshot on " + vm.getName()+"...");
					task = vm.removeAllSnapshots_Task();
					if (task.waitForTask() == Task.SUCCESS) {
						System.out.println("Removed all snapshot on " + vm.getName());
					}
				} catch (SnapshotFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TaskInProgress e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidState e) {
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

}
