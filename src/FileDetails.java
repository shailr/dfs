
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileDetails implements Serializable  {

	private static final long serialVersionUID = 5144138859135530531L;
	public String filename;
	public String type;
	public String location;
	public InetAddress server;
	public int port;
	public double size;
	public Date created;
	public Date modified;
	public Date accessed;
	public boolean dirty;
	public boolean valid;
	public FileDetails() {
		filename = "New File";
		type = ".txt";
		location = "";
		try {
			server = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port = 0;
		size = 0;
		created = new Date();
		modified = new Date();
		accessed = new Date();
		dirty = false;
		valid = true;
	}
	public FileDetails(String fn, String ty, String ln, InetAddress serv, int p, int s, Date c, Date m, Date a, boolean d, boolean v) {
		filename = fn;
		type = ty;
		location = ln;
		server = serv;
		port =  p;
		size = s;
		created = c;
		modified = m;
		accessed = a;
		dirty = d;
		valid = v;
	}
	public FileDetails(String fn, String ty, String ln, InetAddress serv, int p, int s, boolean d, boolean v) {
		filename = fn;
		type = ty;
		location = ln;
		Path path = Paths.get(ln);
		BasicFileAttributes attr = null;
		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server = serv;
		port = p;
		size = s;
		 
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			created = sdf.parse(sdf.format(attr.creationTime()));
			modified = sdf.parse(sdf.format(attr.lastModifiedTime()));
			accessed = sdf.parse(sdf.format(attr.lastAccessTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dirty = d;
		valid = v;
	}
	public FileDetails(String ln) {
		try {
			Path path = Paths.get(ln);
			type = "";
			File f = new File(ln);
			if(f.exists()){
				filename = f.getName();
				size = f.length();
				int i = filename.lastIndexOf('.');
				if (i > 0) {
					type = filename.substring(i+1);
				}
			}
			location = ln;
			BasicFileAttributes attr = null;
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			server = InetAddress.getByName("127.0.0.1");
			port = 0;
			created = new Date(attr.creationTime().toMillis());
			modified = new Date(attr.lastModifiedTime().toMillis());
			accessed = new Date(attr.lastAccessTime().toMillis());
			dirty = false;
			valid = true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fileAccessed(){
		accessed = new Date();
	}
	public void fileModified(){
		modified = new Date();
	}
}
