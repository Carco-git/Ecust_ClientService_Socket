package marco;

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
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class Client {
	private Socket socket;
	private OutputStream os;
	private PrintWriter pw;
	private BufferedReader br;
	private InputStream is;
	private GetMessage getMessage;
	private String name;
	private boolean isStart = false;
	
	public boolean isStart() {
		return isStart;
	}

	public Client(String host,int port,String name) throws UnknownHostException, IOException {
		try{
			this.name=name;
			socket = new Socket(host,port);
			ClientUI.append("���ӳɹ�....");
			isStart=true;	
			os=socket.getOutputStream();
			pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"),true);
			pw.println(name);
	        pw.flush();
	        is = socket.getInputStream();
	        br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
	        getMessage = new GetMessage();
	        getMessage.start();
		}catch(ConnectException e) {
			JOptionPane.showMessageDialog(null, "�������ܾ�����", "����",JOptionPane.ERROR_MESSAGE);
		}
				
	}
	
	// ִ����Ϣ����
		public void send(String message) {
			if(message.equals("CLOSEME")) {
				pw.println(message);
				pw.flush();
				return;
			}
			if (!isStart) {
				JOptionPane.showMessageDialog(null, "δ���ӷ�����", "����",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (message == null || message.equals("")) {
				JOptionPane.showMessageDialog(null,"��Ϣ����Ϊ�գ�", "����",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String sendMessage=name+ "@" + message;
			pw.println(sendMessage);
			pw.flush();

		}

		class GetMessage extends Thread{
			private String message = null;
			@Override
			public void run() {
				while(true) {
					try {
						message = br.readLine();
						if(message.equals("FILESTART")){
							int res=JOptionPane.showConfirmDialog(null, "�����������ļ�", "�Ƿ����", JOptionPane.YES_NO_OPTION);
			                if(res==JOptionPane.YES_OPTION){ 
			                	pw.println("yes");
			        			pw.flush();
			        			message = br.readLine();
			        			System.out.println(message);
			        			if(message.equals("OK")) {
			        				getFile();
			        			}
			        		
			                }else{
			                	pw.println("no");
			        			pw.flush();
			                    continue;
			                }

			                
						}
						else if (message.equals("CLOSESUCCESS")) {

						}
						else if (message.equals("CLOSEYOU")) {
							ClientUI.append("�������Ͽ���������");
							close();
							ClientUI.resumequeding();//�رպ� ��Ϊ�ɼ�
						}
							else {
								System.out.println(message);
								StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
								String source = stringTokenizer.nextToken();
								String content = stringTokenizer.nextToken();
								message= source+":\n"+content;
								ClientUI.append(message);
							}
				} catch (IOException e) {}
				}
			}
			@SuppressWarnings("deprecation")
			private void closeConnection() {
				if(!isStart) {
					JOptionPane.showMessageDialog(null,"δ����", "����",JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					this.stop();// ֹͣ������Ϣ�߳�
					// �ͷ���Դ
					if (is != null) {
						is.close();
					}
					if (os != null) {
						os.close();
					}
					if (br != null) {
						br.close();
					}
					if (pw != null) {
						pw.close();
					}
					socket.close();
					isStart = false;
					return;
				} catch (IOException e) {
					e.printStackTrace();
					isStart = true;
					return;
				}
				
			}
		}

		public void close() {
			send("CLOSEME");
			ClientUI.append("��������");
			ClientUI.resumequeding();//�رպ� ��Ϊ�ɼ�
			getMessage.closeConnection();
			isStart = false;
			
		}
/*************************************�����ļ���**********************************************************/

	        public void getFile() {  
	            try {              
	            	FileOutputStream fos=null;
	                String fileName = br.readLine();	
	                String filesize = br.readLine();	
	                ClientUI.dlm.addElement(fileName);
	                ClientUI.updataList();
	                File directory = new File("D://download");  
	                if(!directory.exists()) {  
	                    directory.mkdir();  
	                }  
	                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName); 
	                fos = new FileOutputStream(file);  
	                /************ ��ʼ�����ļ�  ****************/
	                byte[] bytes = new byte[4096*5];  
	                int length = 0;  
	                long count = 0;
	                long file_size = Long.parseLong(filesize);
	                while(count < file_size) {  
	                	length = is.read(bytes, 0, bytes.length);
	                    fos.write(bytes, 0, length);  
	                    fos.flush(); 
	                    count += length;
	                    ClientUI.progressBar.setValue((int) (100*count/file_size));
	                }  
	                ClientUI.progressBar.setValue(100);
	                ClientUI.append("���յ��ļ���"+fileName+"   ������D:/download");
	                if(fos != null)fos.close(); 

	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	                        
	              
	        }  

/*************************************�����ļ���**********************************************************/
	        public void sendFile(String text) {
	        	File sendfile = new File(text);
	        	long progress = 0;
	        	/**�����ļ��������������򿪡���ȡ����Ҫ���͵��ļ�*/
	        	FileInputStream fis = null;
	        	/**����byte��������Ϊ���ݰ��Ĵ洢���ݰ�*/
	        	byte[] buffer = new byte[4096*5];
	        	/**���Ҫ���͵��ļ��Ƿ����*/
	        	if(!sendfile.exists()){
	        		System.out.println("�ͻ��ˣ�Ҫ���͵��ļ�������");
	        		return;
	        	}
	        	try {
					fis = new FileInputStream(sendfile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			
				try {
					pw.println("FILE");
					pw.flush();
					pw.println(sendfile.getName() + "@" + fis.available());
					pw.flush();
				} catch (IOException e) {
					System.out.println("�����������ж�");
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					int size = 0;
					while((size = fis.read(buffer)) != -1){
						os.write(buffer, 0, size);
						os.flush();
						progress += size;  
	                    ClientUI.progressBar_1.setValue((int) (100*progress/fis.available()));
					}
					ClientUI.progressBar_1.setValue(100);
					
				} catch (FileNotFoundException e) {
					System.out.println("�ͻ��˶�ȡ�ļ�����");
				} catch (IOException e) {
					System.out.println("�ͻ�������ļ�����");
				}finally{
						try {
							if(fis != null)
								fis.close();
						} catch (IOException e) {
							System.out.println("�ͻ����ļ��رճ���");
						}
				}  
	}
}
