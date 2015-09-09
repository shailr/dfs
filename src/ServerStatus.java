
import java.net.*;
import java.util.Date;

public class ServerStatus {

	public String clientid;
	public Socket clientsocket;
	public FileDetails file;
	public boolean cache;
	public Date connectiontime;
	public Date updatetime;
	
	public ServerStatus(){
		clientid = "0";
		clientsocket = new Socket();
		file = new FileDetails();
		cache = true;
		connectiontime = new Date();
		updatetime = new Date();
	}
	public ServerStatus(String id, Socket s, FileDetails f, boolean c, Date ctime, Date utime){
		clientid = id;
		clientsocket = s;
		file = f;
		cache = c;
		connectiontime = ctime;
		updatetime = utime;
	}
	public ServerStatus(String id, Socket s, FileDetails f){
		clientid = id;
		clientsocket = s;
		file = f;
		cache = true;
		connectiontime = new Date();
		updatetime = new Date();
	}

	public void updateTime(){
		updatetime = new Date();
	}
	public void cacheInvalidate(){
		cache = false;
	}
	public void cacheValidate(){
		cache = false;
	}
}
