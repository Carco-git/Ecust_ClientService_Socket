package marco;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.Color;

public class ClientUI {
	private int port;
	private String host;
	private JFrame frame;
	private JTextField textField_input= new JTextField();
	private JTextField textField_port;
	private JTextField textField_ip;
	private JFileChooser filechooser;  
	private JButton button;
	private JButton button_1 ;
	private JButton btnSent_1;
	private JButton btnBreak_1;
	private	static JButton btnQueding;
	private static JTextArea textArea_client;
	private JTextField txtMarco;
	private Client client;
	protected boolean isConnected = false;
	private JTextField textField_file;
	public static JProgressBar progressBar;
	public static DefaultListModel<String> dlm = new DefaultListModel<String>();
	private static JList<String> list_file = new JList<String>();
	private JScrollPane scrollPane_1;
	public static JProgressBar progressBar_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				try {
					ClientUI window = new ClientUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u5BA2\u6237\u7AEF");
		frame.setBounds(900, 100, 573, 653);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel_main = new JPanel();
		frame.getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(null);
		
		JPanel panel_input = new JPanel();
		panel_input.setBorder(new TitledBorder(null, "\u8F93\u5165\u6846", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_input.setBounds(0, 482, 559, 102);
		panel_main.add(panel_input);
		panel_input.setLayout(null);
		
		textField_input.setBounds(10, 16, 424, 47);
		panel_input.add(textField_input);
		textField_input.setColumns(10);
		onClick onclick = new onClick();
		btnSent_1 = new JButton("\u53D1\u9001(Enter)");
		
		btnSent_1.setBounds(440, 16, 109, 23);
		panel_input.add(btnSent_1);
		
		btnBreak_1 = new JButton("中断");
		btnBreak_1.setBounds(440, 40, 109, 23);
		panel_input.add(btnBreak_1);
		
		JLabel label_2 = new JLabel("文件路径:");
		label_2.setBounds(10, 70, 54, 15);
		panel_input.add(label_2);
		
		textField_file = new JTextField();
		textField_file.setColumns(10);
		textField_file.setBounds(74, 70, 324, 14);
		panel_input.add(textField_file);
		
		button = new JButton("...");

		filechooser = new JFileChooser();
		button.setBounds(408, 70, 26, 25);
		panel_input.add(button);
		
		button_1 = new JButton("文件发送");
		
		button_1.setBounds(440, 70, 109, 25);
		panel_input.add(button_1);
		
		progressBar_1 = new JProgressBar();
		progressBar_1.setForeground(new Color(02, 203, 02));
		progressBar_1.setBounds(74, 84, 323, 11);
		panel_input.add(progressBar_1);
		
		JPanel panel_chat = new JPanel();
		panel_chat.setBorder(new TitledBorder(null, "\u804A\u5929\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_chat.setBounds(0, 0, 447, 482);
		panel_main.add(panel_chat);
		panel_chat.setLayout(new GridLayout(0, 1, 0, 0));
		
		textArea_client = new JTextArea();
		textArea_client.setEditable(false);
		textArea_client.setLineWrap(true);
		scrollPane_1 = new JScrollPane();
		panel_chat.add(scrollPane_1);
		scrollPane_1.setViewportView(textArea_client);

		
		JPanel panel_file = new JPanel();
		panel_file.setBorder(new TitledBorder(null, "\u6587\u4EF6", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_file.setBounds(457, 0, 92, 482);
		panel_main.add(panel_file);
		panel_file.setLayout(new BorderLayout(0, 0));
		
		
		panel_file.add(list_file);
		list_file.setBorder(null);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(2,203,2));
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("宋体", Font.PLAIN, 18));
		panel_file.add(progressBar, BorderLayout.NORTH);
		list_file.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent evt) {
	            @SuppressWarnings("unchecked")
				JList<String> list = (JList<String>)evt.getSource();
	            if (evt.getClickCount() == 2) {   
	            	String file = "D://download/"+list.getSelectedValue();
	            	try {
						Desktop.getDesktop().open(new File(file));
					} catch (IOException e) {}
	            } 
	        }
	    });
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);
		
		JLabel lblip = new JLabel("\u8BF7\u8F93\u5165IP");
		panel_1.add(lblip);
		
		textField_ip = new JTextField();
		textField_ip.setText("127.0.0.1");
		panel_1.add(textField_ip);
		textField_ip.setColumns(10);
		
		JLabel label_1 = new JLabel("\u7528\u6237\u540D");
		panel_1.add(label_1);
		
		txtMarco = new JTextField();
		txtMarco.setText("Marco");
		panel_1.add(txtMarco);
		txtMarco.setColumns(8);
		JLabel label = new JLabel("端口:");
		panel_1.add(label);
		textField_port = new JTextField();
		textField_port.setText("6666");
		textField_port.setColumns(5);
		textField_port.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {	
			}
			@Override
			public void keyReleased(KeyEvent arg0) {	
			}
	        @Override        
	        public void keyTyped(KeyEvent e) {  
	           int temp = e.getKeyChar();         
	            if(temp == 10){}//按回车时  
	            if(temp==46)
	            {
	              e.consume();
	            }
	            else{   //没有按小数点时  
	              if(temp != 8){  //没有按backspace时  
	               //下面检查是不是在0~9之间；  
	                if(temp > 57){  
	                   e.consume();    //如果不是则消除key事件,也就是按了键盘以后没有反应;  
	                }else if(temp < 48){  
	                  e.consume();  
	                }  
	              }    
	             }  
	         
	       } 
		});
		panel_1.add(textField_port);
		btnQueding = new JButton("确定");
		panel_1.add(btnQueding);
		

		button.addActionListener(onclick);
		button_1.addActionListener(onclick);
		btnBreak_1.addActionListener(onclick);
		btnQueding.addActionListener(onclick);
		textField_input.addActionListener(onclick);
		btnSent_1.addActionListener(onclick);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					if(isConnected) {
						client.close();// 关闭连接
					}
				
				}catch(Exception e1) {		
				}finally {
					System.exit(0);// 退出程序
				}	
			}
		});

	}
/******************按键监听开始*****************************************/
	private class onClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			/******************选择文件***********************/
			if(e.getSource()==button) {
				filechooser.showOpenDialog(null);  
				File f = filechooser.getSelectedFile();
				textField_file.setText(f.getPath());
			}
			/******************发送文件***********************/
			if(e.getSource()==button_1) {
				if(isConnected) {
					if(!textField_file.getText().equals("")) {
						client.sendFile(textField_file.getText());
						append("已发送文件");
					}
					else {
						JOptionPane.showMessageDialog(null, "请选择文件", "错误",JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "未连接", "错误",JOptionPane.ERROR_MESSAGE);
				}
			}
			/******************发送文本***********************/
			if(e.getSource()==btnSent_1) {
				if(isConnected) {
					client.send(textField_input.getText().trim());
					append("我:"+"\n"+textField_input.getText().trim());
					textField_input.setText(null);
				}else {
					JOptionPane.showMessageDialog(null, "未连接", "错误",JOptionPane.ERROR_MESSAGE);
				}
			}
			/******************中断连接***********************/
			if(e.getSource()==btnBreak_1) {
				if(isConnected) {
					client.close();	
					if(!client.isStart()) {
						btnQueding.setEnabled(true);
						isConnected = false;
					}
				isConnected=false;
				}
				else {
					JOptionPane.showMessageDialog(null, "未连接", "错误",JOptionPane.ERROR_MESSAGE);
				}
			}
			/******************开始连接***********************/
			if(e.getSource()==btnQueding) {
				port= Integer.parseInt(textField_port.getText()); //获取端口号
				host=textField_ip.getText();//本地IP
				String name = txtMarco.getText();
				try {
					client = new Client(host,port,name);
					isConnected = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(client.isStart()) {
					btnQueding.setEnabled(false);
					frame.setTitle("客户端："+name);
				}
			}
			/******************发送文本***********************/
			if(e.getSource()==textField_input) {
				if(isConnected) {
					client.send(textField_input.getText().trim());
					append("我:"+"\n"+textField_input.getText().trim());
					textField_input.setText(null);
				}
				else {
					JOptionPane.showMessageDialog(null, "未连接", "错误",JOptionPane.ERROR_MESSAGE);		
				}
			}

		}

	}

/******************按键监听结束***********************/
	public static void append(String message) {		//文本追加
		textArea_client.append(message);
		textArea_client.append("\n");
	}

	public static void updataList() {
		list_file.setModel(dlm);
	}
	public static void resumequeding() {
		btnQueding.setEnabled(true);
	}
}
