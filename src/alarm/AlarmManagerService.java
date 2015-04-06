package alarm;
import java.net.URL;
import java.rmi.RemoteException;

import recover.VMService;

import com.vmware.vim25.Action;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.MethodAction;
import com.vmware.vim25.MethodActionArgument;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ManagedObject;
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
