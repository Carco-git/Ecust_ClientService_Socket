package marco;
/**
 * @author Macro
 * @date 2018
 * @project ClientService
 * ���� �����ϵ�����ְҵʵ��
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
		System.out.println("�ѽ�������");
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
				JOptionPane.showMessageDialog(null, "���ܿͻ���ʱ�����쳣", "����",JOptionPane.ERROR_MESSAGE);
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
			//  �����߳�
			this.socket=socket;
			/********************************** ��ʼ�����������	**********************************************************/
			try {
				is=socket.getInputStream();
				br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				name=br.readLine();
			} catch (IOException e) {
				System.out.println("��ʼ����������������쳣");
				e.printStackTrace();
				}
			/********************************** ���·�����UI	**********************************************************/
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
						ServiceUI.append("�û���"+name+"������");
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
						 ServiceUI.append(name+"�������ļ�\n");
					}
					else if(message.equals("no")) {
						ServiceUI.append(name+"�������ļ�\n");
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
			// ɾ�������ͻ��˷����߳�
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
					System.out.println("�������˴����ļ�ʧ��");
				}
			}else{
				System.out.println("��·���Ѵ�����ͬ�ļ������и���");
			}
			try {
				fos = new FileOutputStream(file);
				long file_size = Long.parseLong(filesize);
				/**sizeΪÿ�ν������ݰ��ĳ���*/
				int size = 0;
				/**count������¼�ѽ��յ��ļ��ĳ���*/
				long count = 0;
				
				while(count < file_size){
					size = is.read(buffer);
					fos.write(buffer, 0, size);
					fos.flush();
					count += size;
					ServiceUI.progressBar_download.setValue((int) (100*count/file_size));
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("������д�ļ�ʧ��");
			} catch (IOException e) {
				System.out.println("���������ͻ��˶Ͽ�����");
			}finally{
				/**
				 * ���򿪵��ļ��ر�
				 * ������Ҫ��Ҳ�����ڴ˹ر�socket����
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
			JOptionPane.showMessageDialog(null,"��Ϣ����Ϊ�գ�", "����",
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
/*************************************�����ļ���**********************************************************/

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
					// �ļ����ͳ���  
					os=socket.getOutputStream();
					pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
					pw.println(file.getName());  
					pw.flush();  
					pw.println(file.length());  
					pw.flush();  

					// ��ʼ�����ļ�  
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
				listen.flag=false;// ֹͣ�������߳�
			if (server != null) {
				server.close();// �رշ�����������
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
/*************************************�����ļ���**********************************************************/
	
	
	


