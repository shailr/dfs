
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Client {

	private String clientname;
	private InetAddress clientip;
	private List<Socket> socket;
	private List<FileDetails> filetable;
	Path clientfilesystem;
	
	public Client() {
		clientname = "Client";
		try {
			clientip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = new ArrayList<Socket>();
		filetable = new ArrayList<FileDetails>();
		clientfilesystem = getLocalFileSystem();
        System.out.println("Starting Client at "+clientip);
        System.out.println("File system root at "+clientfilesystem);
	}
	public Client(String name) {
		clientname = name;
		try {
			clientip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = new ArrayList<Socket>();
		filetable = new ArrayList<FileDetails>();
		clientfilesystem = getLocalFileSystem();
        System.out.println("Starting Client at "+clientip);
        System.out.println("File system root at "+clientfilesystem);
	}

	public String getName(){
		return clientname;
	}
	public InetAddress getInetAddress(){
		return clientip;
	}
	private void setName(String name){
		clientname = name;
	}
	private void setInetAddress(InetAddress address){
		clientip = address;
	}
	private void addServer(InetAddress address, int port){
		boolean exist = false;
		try {
			for(Socket sock: socket){
				if(sock.getInetAddress().equals(address) && sock.getPort()==port){
					exist = true;
					break;
				}
			}
			if(exist){
				System.out.println("Server already added ");
			}
			else{
				socket.add(new Socket(address, port));
				System.out.println("Server added "+address+":"+port);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	Socket getSocketByAddress(InetAddress address){    
	    for (Socket sock : socket) {
	        if (sock.getInetAddress().equals(address)) {
	            return sock;
	        }
	    }
	    return null; 
	}
	
	private Path getLocalFileSystem(){
		Path clientfilesystem = Paths.get(System.getProperty("user.home")+"\\DFS\\"+clientname+"\\");
		if(Files.notExists(clientfilesystem)){
			try {
				Files.createDirectory(clientfilesystem);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return clientfilesystem;
	}
	private void close(){
		for(FileDetails file: filetable){
			System.out.println("Closing file: "+file.filename);
		    close(file);
		    System.out.println("Closed file...");
		}
		for(Socket sock: socket){
			try {
			    System.out.println("Closing connection with host "+sock.getInetAddress()+":"+sock.getPort());
			    sock.close();
				socket.remove(sock);
			    System.out.println("Closed connection with host...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void closeConnection(){		
		String ip = "127.0.0.1";
		int port = 0;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter server address:");
			ip = b.readLine();
			System.out.println("Enter server port:");
			port = Integer.parseInt(b.readLine());
			
			for(Socket sock: socket){
				if(sock.getInetAddress().equals(InetAddress.getByName(ip)) && sock.getPort()==port){
					close(sock);
					break;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void closeFile(){		
		String name = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter filename:");
		try {
			name = b.readLine();
			for(FileDetails file: filetable){
				if(file.filename.equals(name)){
					close(file);
					break;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void close(FileDetails file){
		for(Socket sock: socket){
			if(sock.getInetAddress().equals(file.server) ){
				push(sock, file.location);
				break;
			}
		}
		System.out.println("File Closed: "+file.filename);
		filetable.remove(file);
	}
	private void close(Socket sock){
		try {
			for(FileDetails file: filetable){
				if(sock.getInetAddress().equals(file.server)){
					close(file);
				}
			}
			System.out.println("Socket Closed- "+sock.getInetAddress()+":"+sock.getPort());
			sock.close();
			socket.remove(sock);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void connect(){
		String ip = "127.0.0.1";
		int port = 0;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter server address:");
		try {
			ip = b.readLine();
			System.out.println("Enter server port:");
			port = Integer.parseInt(b.readLine());
			addServer(InetAddress.getByName(ip), port);
			System.out.println("Connected to server "+ip+":"+port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void read(){
		String loc = null;
		FileDetails fileinfo = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter File Name with location:");
			loc = b.readLine();
			for(Socket sock: socket){
				fileinfo = pull(sock, loc);
				if(fileinfo!=null){
			    	filetable.add(fileinfo);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readServer(){
		String loc = null;
		String serveraddress = null;
		FileDetails fileinfo = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter File Name with location:");
			loc = b.readLine();
			System.out.println("Enter Server Name:");
			serveraddress = b.readLine();
			for(Socket sock: socket){
				if(sock.getInetAddress().equals(InetAddress.getByName(serveraddress))){
					fileinfo = pull(sock, loc);
					if(fileinfo!=null){
				    	filetable.add(fileinfo);
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void update(){
		String loc = null;
		FileDetails f = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter File Name with location:");
			loc = b.readLine();
			for(FileDetails file: filetable){
				if(file.location.equals(loc)){
					f = file;
					break;
				}
			}
			if(f != null){
				for(Socket sock: socket){
					if(sock.getInetAddress().equals(f.server)){
						f = pull(sock, loc);
						if(f!=null){
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	private FileDetails pull(Socket sock, String loc){
		int bytesread = 0;
	    boolean s = false;
	    FileDetails fileinfo = null;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    PrintStream outstream = null;
	    InputStream inbytestream = null;
	    ObjectInputStream inobjstream = null;
	    
	    try {
	    	//Request file details..
	    	outstream = new PrintStream(new BufferedOutputStream(sock.getOutputStream()), true);
	    	System.out.println("Requesting file details " + loc);
	    	outstream.println("read "+loc+" options");
	    	
	    	//Receive file details object..
	    	inobjstream = new ObjectInputStream(sock.getInputStream());
	    	fileinfo = (FileDetails) inobjstream.readObject();
	    	if(fileinfo != null){
		    	System.out.println("File details object received " + loc +" ("+(int)fileinfo.size+"bytes to be read)");
		    	System.out.println("Starting file reception " + loc);
		    	fileinfo.server = sock.getInetAddress();
		    	fileinfo.port = sock.getPort();
	    		//Receive file
		    	byte [] bytearray  = new byte [(int)fileinfo.size];
		    	
		    	inbytestream = sock.getInputStream();
		    	fos = new FileOutputStream(clientfilesystem + loc);
		    	bos = new BufferedOutputStream(fos);
		    	bytesread = inbytestream.read(bytearray,0,bytearray.length);
		    	//System.out.println("Bytes read " + bytesread);
		    	bos.write(bytearray, 0, bytesread);
		    	//System.out.println("Bytes written to file " + bytesread);
		    	bos.flush();
		    	fileinfo.fileAccessed();
		    	s = true;
		    	System.out.println("File " + loc + " downloaded (" + bytesread + " bytes read)");
	    	}
	    	else{
	    		//File not found..
	    		System.out.println("Server "+sock.getInetAddress()+":"+sock.getPort()+" - File not found");
	    	}
	    	return fileinfo;
	    } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return fileinfo;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return fileinfo;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return fileinfo;
		}
	    finally {
			try {
				if (fos != null) fos.close();
		    	if (bos != null) bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	}

	private void write(){
		String loc = null;
		String serveraddress = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Enter File Name with location:");
			loc = b.readLine();
			System.out.println("Enter Server Name:");
			serveraddress = b.readLine();
			for(Socket sock: socket){
				if(sock.getInetAddress().equals(InetAddress.getByName(serveraddress)) ){
					push(sock, loc);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean push(Socket sock, String loc){
		boolean s = false;
		File file = null;
		FileDetails filedetails = new FileDetails(clientfilesystem + loc);
	    ObjectOutputStream outobjstream = null;
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    PrintStream outstream = null;
	    OutputStream os = null;
	    try {
	    	System.out.println("Sending file write request " + loc);
	    	//Send file write request..
	    	outstream = new PrintStream(new BufferedOutputStream(sock.getOutputStream()), true);
	    	outstream.println("write "+loc+" options");
	    	System.out.println("File write request sent " + loc);
	    	
			file = new File(filedetails.location);
			if(file.exists()){
		    	System.out.println("Sending file details " + loc);
		    	//Send file details...
				filedetails.location = loc;
		    	System.out.println(filedetails.location);
				outobjstream = new ObjectOutputStream(sock.getOutputStream());
				outobjstream.writeObject(filedetails);
				outobjstream.flush();
		    	System.out.println("File details sent " + loc);

		    	System.out.println("Buffering file " + loc);
				//Buffer file into byte array...
				byte [] bytearray  = new byte[(int)file.length()];
				fis = new FileInputStream(file);
		        bis = new BufferedInputStream(fis);
		        bis.read(bytearray,0,bytearray.length);
		    	System.out.println("Sending file " + loc);
		        //Send file...
		        os = sock.getOutputStream();
		        System.out.println("Sending " + filedetails.filename + "(" + bytearray.length + " bytes)");
		        os.write(bytearray,0,bytearray.length);
		        os.flush();
		        s = true;
		        System.out.println("File Sent.");
			}
			else{
		        System.out.println("Requested file doesn't exist.");
		    	//Send null...
				outobjstream = new ObjectOutputStream(sock.getOutputStream());
				outobjstream.writeObject(null);
			}
	        return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return s;
	    } finally {
			try {
				outobjstream.reset();
				if (fis != null) fis.close();
				if (bis != null) bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static void main(String args[]){
		boolean next = true;
		String in = null;
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		Client client = new Client();
		
		if(args.length==1){
			client = new Client(args[0]);
		}
		
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
				client.write();
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
