
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileServer {
	private String servername;
	private InetAddress serverip;
	private ServerSocket server;
	private List<ServerStatus> servertable;
	private List<FileDetails> filetable;
	Path serverfilesystem;

	public FileServer() {
		try {
			server = new ServerSocket(0);
			servername = "FileServer"+server.getLocalPort();
			serverip = InetAddress.getLocalHost();
			servertable = new ArrayList<ServerStatus>();
			filetable = new ArrayList<FileDetails>();
			serverfilesystem = getLocalFileSystem();
	        System.out.println("Starting Server at "+serverip+":"+server.getLocalPort());
	        System.out.println("File system root at "+serverfilesystem);
		}
		catch (Exception e) {
		    System.out.println(e);
		    try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public FileServer(String name) {
		try {
			server = new ServerSocket(0);
			servername = name;
			serverip = InetAddress.getLocalHost();
			servertable = new ArrayList<ServerStatus>();
			filetable = new ArrayList<FileDetails>();
			serverfilesystem = getLocalFileSystem();
	        System.out.println("Starting Server at "+serverip+":"+server.getLocalPort());
		}
		catch (Exception e) {
		    System.out.println(e);
		    try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public FileServer(int port) {
		try {
			server = new ServerSocket(port);
			servername = "FileServer"+server.getLocalPort();
			serverip = InetAddress.getLocalHost();
			servertable = new ArrayList<ServerStatus>();
			filetable = new ArrayList<FileDetails>();
			serverfilesystem = getLocalFileSystem();
	        System.out.println("Starting Server at "+serverip+":"+server.getLocalPort());
		}
		catch (Exception e) {
		    System.out.println(e);
		    try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public FileServer(String name, int port) {
		try {
			server = new ServerSocket(port);
			servername = name;
			serverip = InetAddress.getLocalHost();
			servertable = new ArrayList<ServerStatus>();
			filetable = new ArrayList<FileDetails>();
			serverfilesystem = getLocalFileSystem();
	        System.out.println("Starting Server at "+serverip+":"+server.getLocalPort());
		}
		catch (Exception e) {
		    System.out.println(e);
		    try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public Socket accept() throws Exception{ 
        System.out.println("Accepting connections to server "+serverip+":"+server.getLocalPort());
		return server.accept();
	}
	public void setServerIP(InetAddress ip){
		serverip = ip;
	}
	public InetAddress getServerIP(){
		return serverip;
	}
	private void updateClientStatus(String id, Socket sock, FileDetails fi){
		for(ServerStatus clientstat: servertable){
			if(clientstat.clientid.equals(id) && clientstat.clientsocket.equals(sock) && clientstat.file.filename.equals(fi.filename)){
				clientstat.cache = true;
				clientstat.updatetime = new Date();
				break;
			}
			else{
				servertable.add(new ServerStatus(id, sock, fi, false, new Date(), new Date()));
			}
		}
	}
	public void close(){
		servertable.clear();
		filetable.clear();
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Path getLocalFileSystem(){
		Path serverfilesystem = Paths.get(System.getProperty("user.home")+"\\DFS\\"+servername+"\\");
		if(Files.notExists(serverfilesystem)){
			try {
				Files.createDirectory(serverfilesystem);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return serverfilesystem;
	}
	
	/*class ServerUtils extends Thread{
		private Thread th = null;
		
		public ServerUtils(){
			th = new Thread(this);
			th.start();
		}
		
		public void run(){
			boolean next = true;
			String in = null;
			BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
			
			while(next){
				System.out.println("Options:");
				System.out.println("1: Connect File Server.");
				System.out.println("2: Pull a File");
				System.out.println("3: Push a File");
				System.out.println("4: Update local copy of a File");
				System.out.println("5: Close a File");
				System.out.println("6: Close connection to a Server.");
				System.out.println("0: Close Client.");
				System.out.println("\n");
				try {
					in = b.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				switch(Integer.parseInt(in)){
				case 0:
					next = false;
					break;
				case 1:
					client.connect();
					break;
				case 2:
					client.read();
					break;
				case 3:
					client.push();
					break;
				case 4:
					client.update();
					break;
				case 5:
					client.closeFile();
					break;
				case 6:
					client.closeConnection();
					break;
				}
			}
			try {
				System.out.println("Closing client file system...");
				Files.delete(client.clientfilesystem);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.close();
			System.out.println("Client Closed...");
		}
	}
	*/
	
	
	class ReadFile implements Runnable {
		Socket socket;
		FileDetails filedetails;
		String restline;
		Thread th;
		
		public ReadFile(Socket sock, FileDetails fl, String rest){
			socket = sock;
			filedetails = fl;
			restline = rest;
			th = new Thread(this);
			System.out.println("Starting read thread for " + filedetails.filename);
			th.start();
		}
		public ReadFile(Socket sock, String fl, String rest){
			socket = sock;
			filedetails = new FileDetails(serverfilesystem + fl);
			restline = rest;
			th = new Thread(this);
			System.out.println("Starting read thread for " + filedetails.filename + "--" + filedetails.location);
			th.start();
		}
		public void run(){
			System.out.println("Read thread started for " + filedetails.filename + "--" + filedetails.location);
			File file = null;
		    ObjectOutputStream outobjstream = null;
		    FileInputStream fis = null;
		    BufferedInputStream bis = null;
		    OutputStream os = null;
		    try {
				file = new File(filedetails.location);
				if(file.exists()){
			    	//Send file details...
					System.out.println("\tSending file details for " + filedetails.filename + "--" + filedetails.location);
					outobjstream = new ObjectOutputStream(socket.getOutputStream());
					outobjstream.writeObject(filedetails);
					outobjstream.flush();
					System.out.println("\tFile details sent " + filedetails.filename + "--" + filedetails.location);
					System.out.println("\tBuffering file " + filedetails.filename + "--" + filedetails.location);
					//Buffer file into byte array...
					byte [] bytearray  = new byte[(int)file.length()];
					fis = new FileInputStream(file);
			        bis = new BufferedInputStream(fis);
			        bis.read(bytearray,0,bytearray.length);
			        System.out.println("\tFile buffering complete " + filedetails.filename + "--" + filedetails.location);
					//Send file...
			        os = socket.getOutputStream();
			        System.out.println("\tSending " + filedetails.filename + "(" + bytearray.length + " bytes)");
			        os.write(bytearray,0,bytearray.length);
			        os.flush();
			        //manageRead(filedetails);
			        System.out.println("\tFile Sent.");
				}
				else{
			        System.out.println("\tRequested file doesn't exist.");
			    	//Send null...
					outobjstream = new ObjectOutputStream(socket.getOutputStream());
					outobjstream.writeObject(null);
					outobjstream.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    finally {
				try {
					outobjstream.reset();
					if (bis != null) bis.close();
					if (fis != null) fis.close();
			        System.out.println("Exiting read thread...");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		private void manageRead(FileDetails filedetails){
			boolean entryexist = false;
			for(ServerStatus clientstatus: servertable){
				if(!entryexist && clientstatus.clientid.equals(socket.getInetAddress().toString()+socket.getPort()) && clientstatus.file.filename.equals(filedetails.filename)){
					clientstatus.updateTime();
					entryexist = true;
				}
			}
			if(!entryexist){
				servertable.add(new ServerStatus(socket.getInetAddress().toString()+socket.getPort(), socket, filedetails));
			}
			entryexist = false;
			for(FileDetails file: filetable){
				if(file.location.equals(filedetails.location)){
					file.fileAccessed();
					entryexist = true;
					break;
				}
			}
			if(!entryexist){
				filetable.add(filedetails);
			}
		}
	}
	class WriteFile implements Runnable {
		Socket socket;
		FileDetails filedetails;
		String restline;
		Thread th;
		
		public WriteFile(Socket sock, String rest){
			socket = sock;
			filedetails = null;
			restline = rest;
			th = new Thread(this);
			System.out.println("Starting write thread");
			th.start();
		}
		public void run(){
			System.out.println("Write thread started... ");
			int bytesread = 0;
		    boolean s = false;
		    FileOutputStream fos = null;
		    BufferedOutputStream bos = null;
		    InputStream inbytestream = null;
		    ObjectInputStream inobjstream = null;
		    
		    try {
		    	//Receive file details object..
				System.out.println("\tReceiving File Object... ");
		    	inobjstream = new ObjectInputStream(socket.getInputStream());
		    	filedetails = (FileDetails) inobjstream.readObject();
		    	if(filedetails != null){
					System.out.println("\tFile Object Received for " + filedetails.location +" ("+filedetails.size+"bytes to be read)");
					System.out.println("\tStarting file reception...");
		    		//Receive file
			    	byte [] bytearray  = new byte [(int)filedetails.size];
			    	
			    	inbytestream = socket.getInputStream();
			    	fos = new FileOutputStream(serverfilesystem + filedetails.location);
			    	bos = new BufferedOutputStream(fos);
			    	bytesread = inbytestream.read(bytearray,0,bytearray.length);
			    	bos.write(bytearray, 0, bytesread);
			    	bos.flush();
			    	filetable.add(filedetails);
			    	s = true;
			    	System.out.println("\tFile " + filedetails.location + " reception finished(" + bytesread + " bytes read)");
		    	}
		    	else{
		    		//File not found..
		    		System.out.println("\tClient: File not found...");
		    	}
		    } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    finally {
				try {
					if (fos != null) fos.close();
			    	if (bos != null) bos.close();
		    		System.out.println("Exiting write thread...");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	
		private void manageWrite(FileDetails filedetails){
			boolean entryexist = false;
			for(ServerStatus clientstatus: servertable){
				if(!entryexist && clientstatus.clientid.equals(socket.getInetAddress().toString()+socket.getPort()) && clientstatus.file.filename.equals(filedetails.filename)){
					clientstatus.updateTime();
					entryexist = true;
				}
				if(clientstatus.file.location.equals(filedetails.location)){
					clientstatus.cacheInvalidate();
				}
			}
			if(!entryexist){
				servertable.add(new ServerStatus(socket.getInetAddress().toString()+socket.getPort(), socket, filedetails));
			}
			entryexist = false;
			for(FileDetails file: filetable){
				if(file.location.equals(filedetails.location)){
					file.fileModified();
					entryexist = true;
					break;
				}
			}
			if(!entryexist){
				filetable.add(filedetails);
			}
		}
	}
		
	public static void main(String args[]){
		FileServer fs = new FileServer();
		Socket sock = null;
		BufferedReader in = null;
		String line = null;
		if(args.length==1){
			fs = new FileServer(args[0]);
		}
		else if(args.length==2){
			fs = new FileServer(args[0], Integer.parseInt(args[1]));
		}
		try {
		while(true){
	        System.out.println("Waiting... ");
			sock = fs.accept();
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			line = in.readLine();
			
	        System.out.println(line);
			String arr[] = line.split(" ", 3);
			String command = arr[0];
			String location = arr[1];
			String restline = arr[2];
			
			if(command.equals("read")){
				System.out.println("Inside read command...");
				ReadFile r = fs.new ReadFile(sock, location, restline);
			}
			else if(command.equals("write")){
				System.out.println("Inside write command...");
				WriteFile w = fs.new WriteFile(sock, restline);
			}
			else if(command.equals("update")){
				//fs.update(sock, file, restline);
			}
			else{
				//invalid command
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			fs.close();
		}

	}
}
