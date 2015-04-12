package alarm;

import java.rmi.RemoteException;

import recover.VMService;

import com.vmware.vim25.Action;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class AlarmManagerService
{

	public void setPowerOffAlarmForAllVms(ServiceInstance serviceInstance) {
		AlarmManager am = serviceInstance.getAlarmManager();
		StateAlarmExpression alarm_exp = createStateAlarmExpression();
		
		for(VirtualMachine vm:VMService.getAllVMs(serviceInstance))
		{
			Alarm[] alarms;
			try {
				alarms = am.getAlarm(vm);
				if (alarms != null) {
					for (int k = 0; k < alarms.length; k++) {
						Alarm a = alarms[k];
						System.out.println("Removed alarm "
								+ a.getAlarmInfo().getName() + " on "
								+ vm.getName());
						a.removeAlarm();
					}
				}
				String alarm_name = vm.getName() + "_poweroffalarm";
				AlarmSpec spec = createSpec(alarm_name, alarm_exp);
				am.createAlarm(vm, spec);
				System.out.println("Alarm " + alarm_name + " created for "
						+ vm.getName());
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void createAlarmForAllHost(ServiceInstance si) 
	{
		
		AlarmManager am = si.getAlarmManager();
		StateAlarmExpression alarm_exp = createStateAlarmExpression();
		Folder root_folder = si.getRootFolder();
		ManagedEntity[] host_pool;
		try {
			host_pool = new InventoryNavigator(root_folder)
					.searchManagedEntities("VirtualMachine");
			for (int i = 0; i < host_pool.length; i++) {
				VirtualMachine vhost = (VirtualMachine) host_pool[i];
				if (isMyVHost(vhost)) {
					Alarm[] alarms = am.getAlarm(vhost);
					if (alarms != null) {
						for (int k = 0; k < alarms.length; k++) {
							Alarm a = alarms[k];
							System.out.println("Removed alarm "
									+ a.getAlarmInfo().getName() + " on "
									+ vhost.getName());
							a.removeAlarm();
						}
					}
					String alarm_name = vhost.getName() + "_poweroffalarm";
					AlarmSpec spec = createSpec(alarm_name, alarm_exp);
					am.createAlarm(vhost, spec);
					System.out.println("Alarm " + alarm_name + " created for "
							+ vhost.getName());
				}
			}
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
		
	}
	public boolean isMyVHost(VirtualMachine vm) {
		if (vm.getName().contains("T09-vHost")) {
			return true;
		}
		return false;
	}


	public AlarmSpec createSpec(String name, StateAlarmExpression exp) {
		AlarmSpec spec = new AlarmSpec();
		// spec.setAction(action);
		spec.setName(name);
		spec.setDescription("Alert when VM is power off by user");
		spec.setEnabled(true);
		spec.setExpression(exp);
		return spec;
	}



	// configure alarm to set off when vm is power off manually
	public StateAlarmExpression createStateAlarmExpression() {
		StateAlarmExpression exp = new StateAlarmExpression();
		exp.setOperator(StateAlarmOperator.isEqual);
		exp.setYellow("poweredOff");
		exp.setStatePath("runtime.powerState");
		exp.setType("VirtualMachine");
		return exp;
	}

	public AlarmTriggeringAction createAlarmTrigger(Action action)
			throws Exception {
		AlarmTriggeringAction trigger = new AlarmTriggeringAction();
		trigger.setYellow2green(true);
		return trigger;
	}
}
