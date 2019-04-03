package marco;
/**
 * @author Macro
 * @date 2018
 * @project ClientService
 * 华理 计算机系大二上职业实践
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


public class Service {

	private ServerSocket server = null;
	private ArrayList<ControlServer> clients=new ArrayList<ControlServer>();
	private OutputStream os;
	private PrintWriter pw;
	private int count =0;
	private String FilePath;
	private Listen listen;
	private Random ra =new Random();
	public Service(int port) throws IOException {
		super();
		server=new ServerSocket(port);
		System.out.println("已建立监听");
		listen = new Listen();
		listen.start();

	}

	
	class Listen extends Thread{
		public boolean flag =true;
		Socket socket;
		@Override
		public void run() {
			while(flag)
		    {
			
			try {
				socket = server.accept();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "接受客户端时发生异常", "错误",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}

			ControlServer mServer=new ControlServer(socket);
			mServer.start();

			}
		}
	}
	
	class ControlServer extends Thread{
		private String message = null;
		private BufferedReader br;
		private InputStream is;
		private FileOutputStream fos = null;
		private Socket socket;
		private int id = ra.nextInt(1000000);
		private String name;
		private boolean flag = true;
		
		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		public ControlServer(Socket socket) {
			//  控制线程
			this.socket=socket;
			/********************************** 初始化输入输出流	**********************************************************/
			try {
				is=socket.getInputStream();
				br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				name=br.readLine();
			} catch (IOException e) {
				System.out.println("初始化输入输出流发生异常");
				e.printStackTrace();
				}
			/********************************** 更新服务器UI	**********************************************************/
			ServiceUI.dlm.addElement(name);
			ServiceUI.clm.addElement(name);
			ServiceUI.updataList();
//			clients.add(new User(name, socket));
			clients.add(this);
			count=clients.size();
			ServiceUI.setLabel_3(count);
		}
		
		@Override
		public void run() {
			while(flag) {
				
				try {
					message = br.readLine();
					if (message.equals("CLOSEME")) {
						ServiceUI.append("用户："+name+"已下线");
//						close();
						for (int i = clients.size() - 1; i >= 0; i--) {
							if (clients.get(i).id == id) {
								clients.remove(i);
								ServiceUI.dlm.removeElementAt(i);
								ServiceUI.clm.removeElementAt(i+1);
								ServiceUI.updataList();
							}
						}
						ServiceUI.setLabel_3(clients.size());
						flag=false;
						socket.close();
					}else if(message.equals("FILE")) {
						getFile();
					}
					else if(message.equals("yes")) {
						 startSendFile(this.socket);
						 ServiceUI.append(name+"接受了文件\n");
					}
					else if(message.equals("no")) {
						ServiceUI.append(name+"拒收了文件\n");
						 continue;
					}
					else {
						post(socket,message);
						StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
						String source = stringTokenizer.nextToken();
						String content = stringTokenizer.nextToken();
						message= source+":\n"+content;
						ServiceUI.append(message);
						
					}
				} catch (IOException e) {e.printStackTrace();}
			}
			
		}
		public void close() {
			// 删除此条客户端服务线程
			for (int i = clients.size() - 1; i >= 0; i--) {
				if (clients.get(i).id == id) {
					clients.remove(i);
					ServiceUI.dlm.removeElementAt(i);
					ServiceUI.clm.removeElementAt(i+1);
					ServiceUI.updataList();
				}
			}

		}
		public void getFile() {
			
			try {
				message = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
			String filename = stringTokenizer.nextToken();
			String filesize = stringTokenizer.nextToken();
			ServiceUI.flm.addElement(filename);
			ServiceUI.updatafileList();
			File file = new File(filename);
			byte[] buffer = new byte[4096*5];
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.out.println("服务器端创建文件失败");
				}
			}else{
				System.out.println("本路径已存在相同文件，进行覆盖");
			}
			try {
				fos = new FileOutputStream(file);
				long file_size = Long.parseLong(filesize);
				/**size为每次接收数据包的长度*/
				int size = 0;
				/**count用来记录已接收到文件的长度*/
				long count = 0;
				
				while(count < file_size){
					size = is.read(buffer);
					fos.write(buffer, 0, size);
					fos.flush();
					count += size;
					ServiceUI.progressBar_download.setValue((int) (100*count/file_size));
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("服务器写文件失败");
			} catch (IOException e) {
				System.out.println("服务器：客户端断开连接");
			}finally{
				/**
				 * 将打开的文件关闭
				 * 如有需要，也可以在此关闭socket连接
				 * */
				try {
					if(fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			
		}

		

	}
	
	
	
	


	public void post(int selectedIndex, String message) throws IOException {
		String sendMessage;
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(null,"消息不能为空！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(selectedIndex==0) {
			Socket msocket;
			for (int i = clients.size() - 1; i >= 0; i--) {
				msocket=clients.get(i).socket;
				os=msocket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				sendMessage="Service:  ToAll"+ "@" + message;
				pw.println(sendMessage);
				pw.flush();
			}
		}
		else {
			selectedIndex--;
			Socket socket = clients.get(selectedIndex).socket;
			os=socket.getOutputStream();
			pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
			sendMessage="Service:"+ "@" + message;
			pw.println(sendMessage);
			pw.flush();
		}	
	}
	public void post(Socket socket, String message) throws IOException {
		Socket msocket;
		for (int i = clients.size() - 1; i >= 0; i--) {
			if(clients.get(i).socket!=socket) {
				msocket = clients.get(i).socket;
				os=msocket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				pw.println(message);
				pw.flush();
			}
		}
	}



	public void closeItem(int selectedIndex) throws IOException {
		Socket msocket;
		if(selectedIndex==0) {
			for (int i = clients.size() - 1; i >= 0; i--) {
				msocket=clients.get(i).socket;
				os=msocket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				pw.println("CLOSEYOU");
				pw.flush();
			}
		}
		else {
			selectedIndex--;
			msocket = clients.get(selectedIndex).socket;
			os=msocket.getOutputStream();
			pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
			pw.println("CLOSEYOU");
			pw.flush();
		}
		
	}
/*************************************发送文件块**********************************************************/

	public void sendfile(String filePath, int SelectedIndex) throws IOException {
		FilePath=filePath;
		OutputStream os;
		PrintWriter pw;
		Socket msocket;	
		if(SelectedIndex==0) {
			for (int i = clients.size() - 1; i >= 0; i--) {
				msocket=clients.get(i).socket;
				os=msocket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				pw.println("FILESTART");
				pw.flush();

			}
		}
		else{
			SelectedIndex--;
			msocket=clients.get(SelectedIndex).socket;
			os=msocket.getOutputStream();
			pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);

			pw.println("FILESTART");
			pw.flush();

		}		
		
	}

	public void startSendFile(Socket msocket) {
		OutputStream os;
		PrintWriter pw;
			try {
				os=msocket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				System.out.println("OK");
				pw.println("OK");
				pw.flush();
				sendFile(FilePath, msocket);
			} catch (IOException e) {
				e.printStackTrace();
			}

	}
		public void sendFile(String filePath,Socket socket) {
			FileInputStream fis = null;
			File file =  new File(filePath);
			 OutputStream os;
			 PrintWriter pw;
			try {  
				if(file.exists()) { 
					fis = new FileInputStream(file); 
					System.out.println(file.getName());
					// 文件名和长度  
					os=socket.getOutputStream();
					pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
					pw.println(file.getName());  
					pw.flush();  
					pw.println(file.length());  
					pw.flush();  

					// 开始传输文件  
					byte[] bytes = new byte[4096*5];  
					int length = 0;  
					long progress = 0;  
					while((length = fis.read(bytes, 0, bytes.length)) != -1) {  
                    os.write(bytes, 0, length);  
                    os.flush();  
                    progress += length;  

                    ServiceUI.progressBar_upload.setValue((int) (100*progress/length));
					}  
					}  
				} catch (Exception e) {  
					e.printStackTrace(); 
				}finally {  
		            if(fis != null)
						try {
							
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}  
		              }
		}
		public void closeServer() throws IOException {
			
			for (int i = clients.size() - 1; i >= 0; i--) {
				os=clients.get(i).socket.getOutputStream();
				pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
				pw.println("CLOSEYOU");
				pw.flush();
				clients.get(i).setFlag(false);
				clients.get(i).socket.shutdownInput();
				clients.get(i).socket.shutdownOutput();
				clients.get(i).socket.close();
				clients.remove(i);
			}
			if (listen != null)
				listen.stop();
				listen.flag=false;// 停止服务器线程
			if (server != null) {
				server.close();// 关闭服务器端连接
			}
			ServiceUI.dlm.removeAllElements();
			ServiceUI.clm.removeAllElements();
			ServiceUI.flm.removeAllElements();
			ServiceUI.clm.addElement("ALL");
			ServiceUI.updataList();
			ServiceUI.updatafileList();
			ServiceUI.setLabel_3(0);
		}	

}
/*************************************接受文件块**********************************************************/
	
	
	


