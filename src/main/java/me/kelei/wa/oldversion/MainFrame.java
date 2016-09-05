package me.kelei.wa.oldversion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sun.swing.table.DefaultTableCellHeaderRenderer;

public class MainFrame  extends JFrame{
	private static final long serialVersionUID = -5330471824577985279L;
	private JPanel mainPanel;
	private JPanel holidayPanel;
	private JPanel topPanel;
	private JPanel dataPanel;
	private JPanel statePanel;
	private JTextField usrname_t;
	private JPasswordField usrpwd_t;
	private JTextField fromdate_t;
	private JTextField todate_t;
	private DateChooser fromDate_b;
	private DateChooser toDate_b;
	private JButton press_b;
	private JTable data_t;
	private JLabel name_l; 
	private JLabel late_l; 
	private JLabel early_l;
	private JLabel forget_l;
	private JLabel absenteeism_l;
	private JButton btnSaveConfig;
	private JTabbedPane tab = new JTabbedPane();
	
	private String[][] dataArry = new String[20][5];
	private String[] tabNames = new String[]{"����ʱ��","����״̬","��֤��ʽ", "�豸", "��״̬"};
	private String[] holidayTabNames = new String[]{"����","����","��ע"};
	private String[][] leftArry = new String[30][3];
	private String[][] rightArry = new String[30][3];
	
	private Map<String, Object> globalMap = new HashMap<String, Object>();
	private String configPath;
	private String currentYear;
	
	public MainFrame(){
		
		initConfig();
		initTopPanel();
		initDataPanel();
		initStatePanel();
		loadConfig();
		
		initQueryTab();
		initHolidayTab();
		
		this.add(tab);
		this.setTitle("���ѿ��ڼ�¼��ѯ");
		this.setSize(540,700);
		this.setIconImage(new ImageIcon(getClass().getResource("images/kl.png")).getImage());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//��ȡ��Ļ�ߴ�
		this.setLocation((int)(screenSize.getWidth()-this.getSize().getWidth())/2, 
						(int)(screenSize.getHeight()-this.getSize().getHeight())/2);//��ʾ��Ļ����
		this.setResizable(false);   //���ɸı��С
		this.setVisible(true);      //�ɼ�
	}
	
	private void initQueryTab(){
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainPanel.add(topPanel);
		mainPanel.add(dataPanel);
		mainPanel.add(statePanel);
		
		tab.addTab("����ͳ��", mainPanel);
	}
	
	private void initHolidayTab() {
		holidayPanel = new JPanel();
		
		holidayInfoPanel();
		
		tab.addTab("�ڼ��չ���", holidayPanel);
	}
	
	private void initTopPanel(){
		
		topPanel = new JPanel();
		topPanel.setBorder(new TitledBorder("��ѯ��Ϣ"));
		
		JPanel usr_p = new JPanel();
		JPanel serch_p = new JPanel();
		
		JLabel usrname_l = new JLabel("�� �� ��");
		usrname_t = new JTextField(20);
		JLabel usrpwd_l = new JLabel("��  ��");
		usrpwd_t = new JPasswordField(20);
		
		JLabel searchdate_l = new JLabel("��ѯ����");
		JLabel split_l = new JLabel("  ��  ");
		fromdate_t = new JTextField(15);
		todate_t = new JTextField(15);
		
		fromDate_b = new DateChooser(fromdate_t);
		toDate_b = new DateChooser(todate_t);
		
		btnSaveConfig = new JButton("��������");
		press_b = new JButton("��    ѯ");
		
		usr_p.setLayout(new FlowLayout(FlowLayout.LEFT));
		usr_p.add(usrname_l);
		usr_p.add(usrname_t);
		usr_p.add(usrpwd_l);
		usr_p.add(usrpwd_t);
		usr_p.add(btnSaveConfig);
		
		serch_p.setLayout(new FlowLayout(FlowLayout.LEFT));
		serch_p.add(searchdate_l);
		serch_p.add(fromdate_t);
		serch_p.add(fromDate_b);
		serch_p.add(split_l);
		serch_p.add(todate_t);
		serch_p.add(toDate_b);
		serch_p.add(press_b);
		
		topPanel.setLayout(new GridLayout(2,1,0,0));
		topPanel.add(usr_p);
		topPanel.add(serch_p);
		
		press_b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String userName = usrname_t.getText();
				String userPwd = new String(usrpwd_t.getPassword());
				String fromDate = fromdate_t.getText();
				String toDate = todate_t.getText();
				try {
					if(validateInfo()){
						globalMap = new WorkAttendance().httpClientLogin(userName, userPwd, fromDate, toDate, leftArry, rightArry);
						dataArry = (String[][]) globalMap.get("dataArry");
						DefaultTableModel dataModel = (DefaultTableModel) data_t.getModel();
						dataModel.setDataVector(dataArry, tabNames);
						initTable();
						printRow();
						setWorkState();
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(getContentPane(), e1.getMessage());
				}
			}
			
		});
		
		btnSaveConfig.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Properties prop = new Properties();
				try {
					prop.load(new FileInputStream(configPath));
					prop.setProperty("user_name", usrname_t.getText());
					prop.setProperty("user_pwd", String.valueOf(usrpwd_t.getPassword()));
					prop.setProperty("from_date", fromdate_t.getText());
					prop.setProperty("to_date", todate_t.getText());
					prop.store(new FileOutputStream(configPath), "WorkAttendanceConfig");
					JOptionPane.showMessageDialog(getContentPane(), "����ɹ���");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(getContentPane(), "���������ļ�ʧ�ܣ�");
				}
			}
		});
	}
	
	private void initConfig(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		currentYear = sdf.format(new Date());
		configPath = System.getProperty("user.home")+"/workAttendance/config.properties";
		File file = new File(configPath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��������
	 */
	private void loadConfig(){
		Properties prop = new Properties();
		String leftDates2014 = "2014-05-01@\u661F\u671F\u56DB@\u52B3\u52A8\u8282||2014-05-02@\u661F\u671F\u4E94@\u52B3\u52A8\u8282||2014-06-02@\u661F\u671F\u4E00@\u7AEF\u5348\u8282||2014-09-08@\u661F\u671F\u4E00@\u4E2D\u79CB\u8282||2014-10-01@\u661F\u671F\u4E09@\u56FD\u5E86\u8282||2014-10-02@\u661F\u671F\u56DB@\u56FD\u5E86\u8282||2014-10-03@\u661F\u671F\u4E94@\u56FD\u5E86\u8282||2014-10-04@\u661F\u671F\u516D@\u56FD\u5E86\u8282||2014-10-05@\u661F\u671F\u65E5@\u56FD\u5E86\u8282||2014-10-06@\u661F\u671F\u4E00@\u56FD\u5E86\u8282||2014-10-07@\u661F\u671F\u4E8C@\u56FD\u5E86\u8282||";
		String rightDates2014 = "2014-05-04@\u661F\u671F\u65E5@\u52B3\u52A8\u8282||2014-09-28@\u661F\u671F\u65E5@\u56FD\u5E86\u8282||2014-10-11@\u661F\u671F\u516D@\u56FD\u5E86\u8282||";
		try {
			prop.load(new FileInputStream(configPath));
			usrname_t.setText(prop.getProperty("user_name"));
			usrpwd_t.setText(prop.getProperty("user_pwd"));
			fromdate_t.setText(prop.getProperty("from_date"));
			todate_t.setText(prop.getProperty("to_date"));
			
			String leftDates = prop.getProperty("left_date");
			String rightDates = prop.getProperty("right_date");
			
			if("2014".equals(currentYear)){
				leftDates = leftDates2014;
				rightDates = rightDates2014;
			}
			
			if(!isEmpty(leftDates)){
				String[] tmpArr = leftDates.split("\\|\\|");
				for(int i = 0; i < tmpArr.length; i++){
					String[] tmpInfo = tmpArr[i].split("@");
					for(int j = 0; j < tmpInfo.length; j++){
						leftArry[i][j] = tmpInfo[j];
					}
				}
			}
			
			if(!isEmpty(rightDates)){
				String[] tmpArr = rightDates.split("\\|\\|");
				for(int i = 0; i < tmpArr.length; i++){
					String[] tmpInfo = tmpArr[i].split("@");
					for(int j = 0; j < tmpInfo.length; j++){
						rightArry[i][j] = tmpInfo[j];
					}
				}
			}
			
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(getContentPane(), "���������ļ�ʧ�ܣ�"+e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	private void initDataPanel(){
		dataPanel = new JPanel();
		dataPanel.setBorder(new TitledBorder("�����б�"));
		data_t = new JTable();
		DefaultTableModel dataModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
		};
		dataModel.setDataVector(dataArry, tabNames);
		data_t.setModel(dataModel);
		data_t.setRowHeight(20);
		initTable();
		JScrollPane panel = new JScrollPane(data_t);
		panel.setPreferredSize(new Dimension(500,400));
		dataPanel.add(panel);
			
	}
	
	private void initTable(){
		TableColumn column1 = data_t.getColumnModel().getColumn(0);
		column1.setMinWidth(150);
		TableColumn column2 = data_t.getColumnModel().getColumn(1);
		column2.setMinWidth(60);
		TableColumn column3 = data_t.getColumnModel().getColumn(2);
		column3.setMinWidth(60);
		TableColumn column4 = data_t.getColumnModel().getColumn(3);
		column4.setMinWidth(150);
		TableColumn column5 = data_t.getColumnModel().getColumn(4);
		column5.setMinWidth(50);
		DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
		thr.setHorizontalAlignment(JLabel.CENTER);
		data_t.getTableHeader().setDefaultRenderer(thr);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// ����table���ݾ���
		tcr.setHorizontalAlignment(SwingConstants.CENTER);// �����Ͼ�����һ��
		data_t.setDefaultRenderer(Object.class, tcr);
	}
	
	private void printRow() {
		TableColumnModel tcm = data_t.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn tc = tcm.getColumn(i);
			tc.setCellRenderer(new DefaultTableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					String v = (String) data_t.getValueAt(row, 4);
					if (!(v.equals("����") || v.equals("��") || v.equals("δ�°�"))) {
						setForeground(Color.red);
					} else {
						setForeground(Color.black);
					}
					return super.getTableCellRendererComponent(table, value,
							isSelected, hasFocus, row, column);
				}

			});
		}
	}
	
	private void holidayInfoPanel(){
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		leftPanel.setBorder(new TitledBorder("�����շż�"));
		rightPanel.setBorder(new TitledBorder("�������ϰ�"));
		
		JLabel year_l = new JLabel(currentYear + "�귨��������Ϣ¼��");
		year_l.setFont(new Font("΢���ź�", Font.BOLD, 16));
		topPanel.add(year_l);
		
		final JTable leftTable = new JTable(){

			@Override
			public void setValueAt(Object aValue, int row, int column) {
				if(column == 0){
					String dateStr = (String) aValue;
					if(!isEmpty(dateStr)){
						try {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat weekDF = new SimpleDateFormat("EEEE");
							Date date = sdf.parse(dateStr);
							String week = weekDF.format(date);
							setValueAt(week, row, 1);
						} catch (ParseException e) {
							JOptionPane.showMessageDialog(getContentPane(), "���ڸ�ʽ�������������룡");
							aValue = "";
						}
					}else{
						setValueAt("", row, 1);
					}
				}
				super.setValueAt(aValue, row, column);
			}
			
		};
		DefaultTableModel leftModel = (DefaultTableModel) leftTable.getModel();
		leftModel.setDataVector(leftArry, holidayTabNames);
		leftTable.setModel(leftModel);
		leftTable.setRowHeight(20);
		DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
		thr.setHorizontalAlignment(JLabel.CENTER);
		leftTable.getTableHeader().setDefaultRenderer(thr);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		leftTable.setDefaultRenderer(Object.class, tcr);
		leftTable.getTableHeader().setReorderingAllowed(false);
		leftTable.getTableHeader().setResizingAllowed(false);;
		leftTable.setToolTipText("˫����Ԫ��¼����Ϣ");
		  
		final JTable rightTable = new JTable(){

			@Override
			public void setValueAt(Object aValue, int row, int column) {
				if(column == 0){
					String dateStr = (String) aValue;
					if(!isEmpty(dateStr)){
						try {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat weekDF = new SimpleDateFormat("EEEE");
							Date date = sdf.parse(dateStr);
							String week = weekDF.format(date);
							setValueAt(week, row, 1);
						} catch (ParseException e) {
							JOptionPane.showMessageDialog(getContentPane(), "���ڸ�ʽ�������������룡");
							aValue = "";
						}
					}else{
						setValueAt("", row, 1);
					}
				}
				super.setValueAt(aValue, row, column);
			}
			
		};
		DefaultTableModel rightModel = (DefaultTableModel) rightTable.getModel();
		rightModel.setDataVector(rightArry, holidayTabNames);
		rightTable.setModel(rightModel);
		rightTable.setRowHeight(20);
		DefaultTableCellHeaderRenderer rightthr = new DefaultTableCellHeaderRenderer();
		rightthr.setHorizontalAlignment(JLabel.CENTER);
		rightTable.getTableHeader().setDefaultRenderer(rightthr);
		DefaultTableCellRenderer righttcr = new DefaultTableCellRenderer();
		righttcr.setHorizontalAlignment(SwingConstants.CENTER);
		rightTable.setDefaultRenderer(Object.class, righttcr);
		rightTable.getTableHeader().setReorderingAllowed(false);
		rightTable.getTableHeader().setResizingAllowed(false);;
		rightTable.setToolTipText("˫����Ԫ��¼����Ϣ");
		
		JScrollPane leftSp = new JScrollPane(leftTable);
		leftSp.setPreferredSize(new Dimension(230,500));
		
		JScrollPane rightSp = new JScrollPane(rightTable);
		rightSp.setPreferredSize(new Dimension(230,500));
		
		leftPanel.add(leftSp);
		rightPanel.add(rightSp);
		
		JButton save_holiday_b = new JButton("������Ϣ");
		bottomPanel.add(save_holiday_b);
		
		JPanel centerPanel = new JPanel();
		centerPanel.add(leftPanel);
		centerPanel.add(rightPanel);
		
		holidayPanel.setLayout(new BorderLayout());
		holidayPanel.add(topPanel, BorderLayout.NORTH);
		holidayPanel.add(centerPanel, BorderLayout.CENTER);
		holidayPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		save_holiday_b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				StringBuilder leftDates = new StringBuilder(); 
				for(int i = 0; i < leftTable.getRowCount(); i++){
					String value = (String) leftTable.getValueAt(i, 0);
					String week = (String) leftTable.getValueAt(i, 1);
					String remark = (String) leftTable.getValueAt(i, 2);
					if(!isEmpty(value)){
						leftDates.append(value)
									.append("@")
									.append(week == null ? "" : week)
									.append("@")
									.append(remark == null ? "" : remark)
									.append("||");
					}
				}
				StringBuilder rightDates = new StringBuilder(); 
				for(int i = 0; i < rightTable.getRowCount(); i++){
					String value = (String) rightTable.getValueAt(i, 0);
					String week = (String) rightTable.getValueAt(i, 1);
					String remark = (String) rightTable.getValueAt(i, 2);
					if(!isEmpty(value)){
						rightDates.append(value)
									.append("@")
									.append(week == null ? "" : week)
									.append("@")
									.append(remark == null ? "" : remark)
									.append("||");
					}
				}
				Properties prop = new Properties();
				try {
					prop.load(new FileInputStream(configPath));
					prop.setProperty("left_date", leftDates.toString());
					prop.setProperty("right_date", rightDates.toString());
					prop.store(new FileOutputStream(configPath), "WorkAttendanceConfig");
					JOptionPane.showMessageDialog(getContentPane(), "����ɹ���");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(getContentPane(), "����ʧ�ܣ�");
				}
			}
		});
	}
	
	private void initStatePanel(){
		statePanel = new JPanel();
		statePanel.setBorder(new TitledBorder("����״̬ͳ��"));
		
		name_l = new JLabel("������δ֪");
		late_l = new JLabel("�ٵ������� 0 �Σ�");
		early_l = new JLabel("���˴�����0 �Σ�");
		forget_l = new JLabel("���򿨴����� 0 �Σ�");
		absenteeism_l = new JLabel("����������0 �Σ�");
		
		JPanel basePanel = new JPanel();
		JPanel stationPanel = new JPanel();
		basePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		basePanel.add(name_l);
		stationPanel.add(late_l);
		stationPanel.add(early_l);
		stationPanel.add(forget_l);
		stationPanel.add(absenteeism_l);
		
		statePanel.setLayout(new BorderLayout());
		statePanel.add(basePanel, BorderLayout.NORTH);
		statePanel.add(stationPanel, BorderLayout.CENTER);
	}
	
	private void setWorkState(){
		String name = (String) globalMap.get("userName");
		int lateCount = (Integer) (globalMap.get("lateCount"));
		int earlyCount = (Integer) (globalMap.get("earlyCount"));
		int forgetCount = (Integer) (globalMap.get("forgetCount"));
		int absenteeismCount = (Integer) (globalMap.get("absenteeismCount"));
		
		name_l.setText("������ " + (name == null ? "δ֪" : name) + "");
		late_l.setText("�ٵ������� " + lateCount + " �Σ�");
		early_l.setText("���˴����� " + earlyCount + " �Σ�");
		forget_l.setText("���򿨴����� " + forgetCount + " �Σ�");
		absenteeism_l.setText("���������� " + absenteeismCount + " �Σ�");
		
	}
	
	private boolean validateInfo(){
		
		String userName = usrname_t.getText();
		String userPwd = new String(usrpwd_t.getPassword());
		String fromDate = fromdate_t.getText();
		String toDate = todate_t.getText();
		if(isEmpty(userName)){
			JOptionPane.showMessageDialog(getContentPane(), "�û�������Ϊ�գ����������룡");
			usrname_t.requestFocus();
			return false;
		}
		if(isEmpty(userPwd)){
			JOptionPane.showMessageDialog(getContentPane(), "���벻��Ϊ�գ����������룡");
			usrpwd_t.requestFocus();
			return false;
		}
		if(isEmpty(fromDate)){
			JOptionPane.showMessageDialog(getContentPane(), "��ʼʱ�䲻��Ϊ�գ����������룡");
			fromdate_t.requestFocus();
			return false;
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.parse(fromDate);
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(getContentPane(), "���ڸ�ʽ�������������룡");
				fromdate_t.requestFocus();
				return false;
			}
		}
		if(isEmpty(toDate)){
			JOptionPane.showMessageDialog(getContentPane(), "����ʱ�䲻��Ϊ�գ����������룡");
			todate_t.requestFocus();
			return false;
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.parse(toDate);
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(getContentPane(), "���ڸ�ʽ�������������룡");
				todate_t.requestFocus();
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
	
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			new MainFrame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
