package recover;
import java.io.IOException;

import com.java.shaman.Config;
import com.vmware.vim25.mo.VirtualMachine;


public class HeartBeatsService 
{
	public static boolean isAlive(String ip) 
	{
		
		Process p = null;
		int failureCount=0;
		while(failureCount<3)
		{
			try {
				p = Runtime.getRuntime().exec("ping -c 1 " + ip);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (p.exitValue() != 0)  //IP not responding 
			{
				failureCount++;
			}
			else    //IP is alive
			{				
				return true;
			}
		}
		return false;
	}

}
