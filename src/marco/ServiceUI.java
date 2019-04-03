package marco;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import java.awt.GridLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.Color;

public class ServiceUI {
	private JFrame frame;
	private JTextField port_Field;
	private JTextField textField_input;
	private JTextField textField_file;
	private JButton btn_listen;
	private JButton btn_stop ;
	private JButton btn_send;
	private JButton button_select;
	private JButton button_filesend;
	private JButton button_break;
	private JFileChooser filechooser;  
	private Service service;
	private onClick onclick ;
	private JScrollPane scrollPane;
	public boolean isStart=false;
	private static JTextArea textArea ;	
	private static JComboBox<String> comboBox;
	private static JLabel label_3 = new JLabel("0");
	private static JList<String> list_1 ;
	public static JProgressBar progressBar_upload;
	public static JProgressBar progressBar_download;
	public static JList<String> list_client = new JList<String>();
	public static DefaultListModel<String> dlm = new DefaultListModel<String>();
	public static DefaultListModel<String> flm = new DefaultListModel<String>();
	public static DefaultComboBoxModel<String> clm = new DefaultComboBoxModel<String>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServiceUI window = new ServiceUI();
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
	public ServiceUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("服务器");
		frame.setResizable(false);
		frame.setBounds(100, 100, 745, 672);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel_top = new JPanel();
		frame.getContentPane().add(panel_top, BorderLayout.NORTH);
		
		JLabel label = new JLabel("监听端口:");
		panel_top.add(label);
		
		port_Field = new JTextField();
		port_Field.setText("6666");
		panel_top.add(port_Field);
		port_Field.setColumns(10);
		port_Field.addKeyListener(new KeyListener() {
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
		onclick = new onClick();
		btn_listen = new JButton("\u76D1\u542C");
		btn_listen.addActionListener(onclick);
		panel_top.add(btn_listen);
		
		JLabel label_2 = new JLabel("当前人数：");
		panel_top.add(label_2);
		
		
		panel_top.add(label_3);
		
		btn_stop = new JButton("断开");
		btn_stop.setEnabled(false);
		btn_stop.addActionListener(onclick);
		panel_top.add(btn_stop);
		
		JPanel panel_main = new JPanel();
		panel_main.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frame.getContentPane().add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "\u8F93\u5165\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(0, 472, 729, 134);
		panel_main.add(panel);
		panel.setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(10, 22, 85, 23);

		clm.addElement("ALL");
		panel.add(comboBox);
		
		textField_input = new JTextField();
		textField_input.setToolTipText("\u8BF7\u8F93\u5165\u53D1\u9001\u5185\u5BB9");
		textField_input.setBounds(105, 22, 504, 65);
		panel.add(textField_input);
		textField_input.setColumns(10);
		
		btn_send = new JButton("发送 Enter");
		///设置回车发送
		textField_input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					service.post(comboBox.getSelectedIndex(),textField_input.getText().trim());
				} catch (IOException e) {
					e.printStackTrace();
				}
				append("服务器："+"\n"+textField_input.getText().trim());
				textField_input.setText(null);
			}
		});
		btn_send.addActionListener(onclick);
		btn_send.setBounds(619, 22, 100, 32);
		panel.add(btn_send);
		
		button_break = new JButton("\u4E2D\u65AD");
		button_break.addActionListener(onclick);
		button_break.setBounds(619, 55, 100, 32);
		panel.add(button_break);
		
		JLabel label_1 = new JLabel("文件路径:");
		label_1.setBounds(41, 90, 54, 18);
		panel.add(label_1);
		
		button_filesend = new JButton("文件发送");
		button_filesend.setBounds(619, 91, 100, 32);
		panel.add(button_filesend);
		
		button_select = new JButton("...");
		button_select.addActionListener(onclick);
		filechooser = new JFileChooser();
		button_select.setBounds(583, 90, 26, 32);
		panel.add(button_select);
		
		textField_file = new JTextField();
		textField_file.setBounds(105, 90, 474, 18);
		panel.add(textField_file);
		textField_file.setColumns(10);
		
		progressBar_upload = new JProgressBar();
		progressBar_upload.setBounds(105, 113, 473, 11);
		panel.add(progressBar_upload);
		button_filesend.addActionListener(onclick);
		
		JPanel panel_user = new JPanel();
		panel_user.setBorder(new TitledBorder(null, "\u5728\u7EBF\u7528\u6237", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_user.setBounds(10, 10, 126, 464);
		panel_main.add(panel_user);
		panel_user.setLayout(new GridLayout(0, 1, 0, 0));
		panel_user.add(list_client);
		
		JPanel panel_chat = new JPanel();
		panel_chat.setBorder(new TitledBorder(null, "\u804A\u5929\u533A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_chat.setBounds(137, 10, 482, 464);
		panel_main.add(panel_chat);
		panel_chat.setLayout(new GridLayout(0, 1, 0, 0));
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);

		
		scrollPane = new JScrollPane();
		panel_chat.add(scrollPane);
		scrollPane.setViewportView(textArea);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "\u6587\u4EF6", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(624, 10, 97, 464);
		panel_main.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		list_1 = new JList<String>();
		panel_1.add(list_1);
		list_1.setBorder(null);
		
		progressBar_download = new JProgressBar();
		progressBar_download.setForeground(new Color(02, 203, 02));
		progressBar_download.setStringPainted(true);
		progressBar_download.setFont(new Font("宋体", Font.PLAIN, 18));
		panel_1.add(progressBar_download, BorderLayout.NORTH);

		list_client.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent evt) {
	            @SuppressWarnings("unchecked")
				JList<String> list = (JList<String>)evt.getSource();
	            if (evt.getClickCount() == 2) {   
	            	int index = list.locationToIndex(evt.getPoint());
	            	comboBox.setSelectedIndex(index+1);
	            	} 
	        }
	    });
		list_1.addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent evt) {
	            @SuppressWarnings("unchecked")
				JList<String> list = (JList<String>)evt.getSource();
	            if (evt.getClickCount() == 2) {   
	            	String file = list.getSelectedValue();
	            	try {
						Desktop.getDesktop().open(new File(file));
					} catch (IOException e) {}
	            } 
	        }
	    });
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isStart) {
					try {
						service.closeItem(0);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);// 退出程序
			}
		});
		
	}
	
	
	
	public class onClick implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btn_listen) {
				int port = Integer.parseInt(port_Field.getText());		
				try {
					service = new Service(port);
					isStart=true;
					btn_stop.setEnabled(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				btn_listen.setEnabled(false);
				port_Field.setEnabled(false);	
			}
			if(e.getSource()==btn_stop) {
				if (!isStart) {
					JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					service.closeServer();
					btn_listen.setEnabled(true);
					port_Field.setEnabled(true);
					btn_stop.setEnabled(false);
					textArea.append("服务器成功停止!\r\n");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			if(e.getSource()==btn_send) {
				try {
					if(isStart) {
						service.post(comboBox.getSelectedIndex(),textField_input.getText().trim());
						append("服务器："+"\n"+textField_input.getText().trim());
						textField_input.setText(null);
					}
					else {
						JOptionPane.showMessageDialog(frame, "没有连接！", "错误",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(e.getSource()==button_break) {
				if(isStart) {
				try {
					service.closeItem(comboBox.getSelectedIndex());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				}else {
					JOptionPane.showMessageDialog(frame, "没有连接", "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			if(e.getSource()==button_select) {
				filechooser.showOpenDialog(null);  
				File f = filechooser.getSelectedFile();
				textField_file.setText(f.getPath());
			}
			if(e.getSource()==button_filesend) {
				try {
					if(!textField_file.getText().equals("")) {
						service.sendfile(textField_file.getText(),comboBox.getSelectedIndex());
					}
					else {
						JOptionPane.showMessageDialog(null, "请选择文件", "错误",JOptionPane.ERROR_MESSAGE);
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "文件传送发生异常", "错误",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
   
		}
	}


	public static void updataList() {			//更新list界面
		list_client.setModel(dlm);
		comboBox.setModel(clm);
	}
	public static void append(String message) {		//文本追加
		textArea.append(message);
		textArea.append("\n");
	}
	public static void setLabel_3(int size) {		//更新在线数
		label_3.setText(size+"");
	}

	public static void updatafileList() {
		list_1.setModel(flm);
		
	}


	
}
