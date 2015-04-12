package com.java.shaman;


//import java.util.ArrayList;
import java.util.Timer;

import alarm.AlarmManagerService;

import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import recover.HostService;
import recover.RecoveryTimerTask;
import recover.VMService;
import snapshot.SnapshotService;
import snapshot.SnapshotsTimerTask;



public class RecoveryManager {
	public static void main(String... args) {

		//new AlarmManagerService().setPowerOffAlarmForAllVms(ServiceInstanceSingleton.getServiceInstance());
//
//
//		//Timer task for snapshots
		Timer snapshotTimee = new Timer(); 
		SnapshotsTimerTask snapshotTask = new SnapshotsTimerTask(); 
		snapshotTimee.schedule(snapshotTask, 0, 30*60*1000); 

//		//	Timer task for VM recovery
//		Timer recoveryTimer = new Timer(); 
//		RecoveryTimerTask recoveryTast = new RecoveryTimerTask(); 
//		recoveryTimer.schedule(recoveryTast, 0, 5*1000); 



	}

}
