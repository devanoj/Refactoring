package re;

/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	private static EmployeeDetails frame = new EmployeeDetails();
	EmployeeDetailsData data = new EmployeeDetailsData(0, new RandomFile(), new FileNameExtensionFilter("dat files (*.dat)", "dat"), false, false, new Font("SansSerif", Font.BOLD, 16),
			new String[] { "", "M", "F" }, new String[] { "", "Administration", "Production", "Transport", "Management" }, new String[] { "", "Yes", "No" });

	// initialize menu bar
	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		open(fileMenu);
		save_as(fileMenu);
		createUpdateDeleteData(recordMenu);
		navigation(navigateMenu);
		closingApp(closeMenu);

		return menuBar;
	}// end menuBar

	private void save_as(JMenu fileMenu) {
		fileMenu.add(data.save = new JMenuItem("Save")).addActionListener(this);
		data.save.setMnemonic(KeyEvent.VK_S);
		data.save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(data.saveAs = new JMenuItem("Save As")).addActionListener(this);
		data.saveAs.setMnemonic(KeyEvent.VK_F2);
		data.saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
	}

	private void open(JMenu fileMenu) {
		fileMenu.add(data.open = new JMenuItem("Open")).addActionListener(this);
		data.open.setMnemonic(KeyEvent.VK_O);
		data.open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	}

	private void navigation(JMenu navigateMenu) {
		navigateMenu.add(data.firstItem = new JMenuItem("First"));
		data.firstItem.addActionListener(this);
		navigateMenu.add(data.prevItem = new JMenuItem("Previous"));
		data.prevItem.addActionListener(this);
		navigateMenu.add(data.nextItem = new JMenuItem("Next"));
		data.nextItem.addActionListener(this);
		navigateMenu.add(data.lastItem = new JMenuItem("Last"));
		data.lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(data.searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(data.searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(data.listAll = new JMenuItem("List all Records")).addActionListener(this);
	}

	private void createUpdateDeleteData(JMenu recordMenu) {
		recordMenu.add(data.create = new JMenuItem("Create new Record")).addActionListener(this);
		data.create.setMnemonic(KeyEvent.VK_N);
		data.create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(data.modify = new JMenuItem("Modify Record")).addActionListener(this);
		data.modify.setMnemonic(KeyEvent.VK_E);
		data.modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(data.delete = new JMenuItem("Delete Record")).addActionListener(this);
	}

	private void closingApp(JMenu closeMenu) {
		closeMenu.add(data.closeApp = new JMenuItem("Close")).addActionListener(this);
		data.closeApp.setMnemonic(KeyEvent.VK_F4);
		data.closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));
	}

	// initialize search panel
	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
		searchPanel.add(data.searchByIdField = new JTextField(20), "width 200:200:200, growx, pushx");
		data.searchByIdField.addActionListener(this);
		data.searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(data.searchId = new JButton("Go"),
				"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		data.searchId.addActionListener(this);
		data.searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
		searchPanel.add(data.searchBySurnameField = new JTextField(20), "width 200:200:200, growx, pushx");
		data.searchBySurnameField.addActionListener(this);
		data.searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				data.searchSurname = new JButton("Go"),"width 35:35:35, height 20:20:20, growx, pushx, wrap");
		data.searchSurname.addActionListener(this);
		data.searchSurname.setToolTipText("Search Employee By Surname");

		return searchPanel;
	}// end searchPanel

	// initialize navigation panel
	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(data.first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		data.first.setPreferredSize(new Dimension(17, 17));
		data.first.addActionListener(this);
		data.first.setToolTipText("Display first Record");

		navigPanel.add(data.previous = new JButton(new ImageIcon(new ImageIcon("prev.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		data.previous.setPreferredSize(new Dimension(17, 17));
		data.previous.addActionListener(this);
		data.previous.setToolTipText("Display next Record");

		navigPanel.add(data.next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		data.next.setPreferredSize(new Dimension(17, 17));
		data.next.addActionListener(this);
		data.next.setToolTipText("Display previous Record");

		navigPanel.add(data.last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		data.last.setPreferredSize(new Dimension(17, 17));
		data.last.addActionListener(this);
		data.last.setToolTipText("Display last Record");

		return navigPanel;
	}// end naviPanel

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(data.add = new JButton("Add Record"), "growx, pushx");
		data.add.addActionListener(this);
		data.add.setToolTipText("Add new Employee Record");
		buttonPanel.add(data.edit = new JButton("Edit Record"), "growx, pushx");
		data.edit.addActionListener(this);
		data.edit.setToolTipText("Edit current Employee");
		buttonPanel.add(data.deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		data.deleteButton.addActionListener(this);
		data.deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(data.displayAll = new JButton("List all Records"), "growx, pushx");
		data.displayAll.addActionListener(this);
		data.displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}

	// initialize main/details panel
	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		inputEmpDetails(empDetails);  // Add Employee Details 

		buttonPanel.add(data.saveChange = new JButton("Save"));
		data.saveChange.addActionListener(this);
		data.saveChange.setVisible(false);
		data.saveChange.setToolTipText("Save changes");
		buttonPanel.add(data.cancelChange = new JButton("Cancel"));
		data.cancelChange.addActionListener(this);
		data.cancelChange.setVisible(false);
		data.cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

		// loop through panel components and add listeners and format
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(data.font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == data.ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			} // end if
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color.WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					// set foregroung to combo boxes
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}// end paint
				});
			} // end else if
		} // end for
		return empDetails;
	}// end detailsPanel

	private void inputEmpDetails(JPanel empDetails) {
		empDetails.add(new JLabel("ID:"), "growx, pushx");
		empDetails.add(data.idField = new JTextField(20), "growx, pushx, wrap");
		data.idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), "growx, pushx");
		empDetails.add(data.ppsField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Surname:"), "growx, pushx");
		empDetails.add(data.surnameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("First Name:"), "growx, pushx");
		empDetails.add(data.firstNameField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Gender:"), "growx, pushx");
		empDetails.add(data.genderCombo = new JComboBox<String>(data.gender), "growx, pushx, wrap");

		empDetails.add(new JLabel("Department:"), "growx, pushx");
		empDetails.add(data.departmentCombo = new JComboBox<String>(data.department), "growx, pushx, wrap");

		empDetails.add(new JLabel("Salary:"), "growx, pushx");
		empDetails.add(data.salaryField = new JTextField(20), "growx, pushx, wrap");

		empDetails.add(new JLabel("Full Time:"), "growx, pushx");
		empDetails.add(data.fullTimeCombo = new JComboBox<String>(data.fullTime), "growx, pushx, wrap");
	}
/*
 * Fixxxxx this
 */
	// display current Employee details
	public void displayRecords(Employee thisEmployee) {
		data.searchByIdField.setText("");
		data.searchBySurnameField.setText("");

		// if Employee is null or ID is 0 do nothing else display Employee details
		if (thisEmployee == null || thisEmployee.getEmployeeId() == 0) {
		    return;
		}

		int genderIndex = Arrays.binarySearch(data.gender, thisEmployee.getGender() + "", String::compareToIgnoreCase);
		int depIndex = Arrays.binarySearch(data.department, thisEmployee.getDepartment(), String::compareToIgnoreCase);
		
		if (genderIndex >= 0) {
		    data.genderCombo.setSelectedIndex(genderIndex);
		}
		if (depIndex >= 0) {
		    data.departmentCombo.setSelectedIndex(depIndex);
		}
		
		data.idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
		data.ppsField.setText(thisEmployee.getPps().trim());
		data.surnameField.setText(thisEmployee.getSurname().trim());
		data.firstNameField.setText(thisEmployee.getFirstName());
		data.salaryField.setText(format.format(thisEmployee.getSalary()));

		if (thisEmployee.getFullTime()) {
		    data.fullTimeCombo.setSelectedIndex(1);
		} else {
		    data.fullTimeCombo.setSelectedIndex(2);
		}

		data.change = false;
}// end display records

	// display Employee summary dialog
	private void displayEmployeeSummaryDialog() {
		// display Employee summary dialog if these is someone to display
		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}// end displaySummaryDialog

	// display search by ID dialog
	private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}// end displaySearchByIdDialog

	// display search by surname dialog
	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}// end displaySearchBySurnameDialog

	// find byte start in file for first active record
	private void firstRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			data.application.openReadFile(data.file.getAbsolutePath());
			// get byte start in file for first record
			data.currentByteStart = data.application.getFirst();
			// assign current Employee to first record in file
			data.currentEmployee = data.application.readRecords(data.currentByteStart);
			data.application.closeReadFile();// close file for reading
			// if first record is inactive look for next record
			if (data.currentEmployee.getEmployeeId() == 0)
				nextRecord();// look for next record
		} // end if
	}// end firstRecord

	// find byte start in file for previous active record
	private void previousRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			data.application.openReadFile(data.file.getAbsolutePath());
			// get byte start in file for previous record
			data.currentByteStart = data.application.getPrevious(data.currentByteStart);
			// assign current Employee to previous record in file
			data.currentEmployee = data.application.readRecords(data.currentByteStart);
			// loop to previous record until Employee is active - ID is not 0
			while (data.currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for previous record
				data.currentByteStart = data.application.getPrevious(data.currentByteStart);
				// assign current Employee to previous record in file
				data.currentEmployee = data.application.readRecords(data.currentByteStart);
			} // end while
			data.application.closeReadFile();// close file for reading
		}
	}// end previousRecord

	// find byte start in file for next active record
	private void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			data.application.openReadFile(data.file.getAbsolutePath());
			// get byte start in file for next record
			data.currentByteStart = data.application.getNext(data.currentByteStart);
			// assign current Employee to record in file
			data.currentEmployee = data.application.readRecords(data.currentByteStart);
			// loop to previous next until Employee is active - ID is not 0
			while (data.currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for next record
				data.currentByteStart = data.application.getNext(data.currentByteStart);
				// assign current Employee to next record in file
				data.currentEmployee = data.application.readRecords(data.currentByteStart);
			} // end while
			data.application.closeReadFile();// close file for reading
		} // end if
	}// end nextRecord

	// find byte start in file for last active record
	private void lastRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			data.application.openReadFile(data.file.getAbsolutePath());
			// get byte start in file for last record
			data.currentByteStart = data.application.getLast();
			// assign current Employee to first record in file
			data.currentEmployee = data.application.readRecords(data.currentByteStart);
			data.application.closeReadFile();// close file for reading
			// if last record is inactive look for previous record
			if (data.currentEmployee.getEmployeeId() == 0)
				previousRecord();// look for previous record
		} // end if
	}// end lastRecord

	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;

		try {// try to read correct correct from input
				// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) {
				firstRecord();// look for first record
				int firstId = data.currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop
				// through records
				if (data.searchByIdField.getText().trim().equals(data.idField.getText().trim()))
					found = true;
				else if (data.searchByIdField.getText().trim().equals(Integer.toString(data.currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(data.currentEmployee);
				} // end else if
				else {
					found = loopForID(found, firstId);
				} // end else
					// if Employee not found display message
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} // end if
		} // end try
		catch (NumberFormatException e) {
			data.searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		} // end catch
		data.searchByIdField.setBackground(Color.WHITE);
		data.searchByIdField.setText("");
	}// end searchEmployeeByID

	private boolean loopForID(boolean found, int firstId) {
		nextRecord();// look for next record
		// loop until Employee found or until all Employees have
		// been checked
		while (firstId != data.currentEmployee.getEmployeeId()) {
			// if found break from loop and display Employee details
			// else look for next record
			if (Integer.parseInt(data.searchByIdField.getText().trim()) == data.currentEmployee.getEmployeeId()) {
				found = true;
				displayRecords(data.currentEmployee);
				break;
			} else
				nextRecord();// look for next record
		} // end while
		return found;
	}

	// search Employee by surname
	public void searchEmployeeBySurname() {
		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) {
			firstRecord();// look for first record
			String firstSurname = data.currentEmployee.getSurname().trim();
			// if ID to search is already displayed do nothing else loop through
			// records
			if (data.searchBySurnameField.getText().trim().equalsIgnoreCase(data.surnameField.getText().trim()))
				found = true;
			else if (data.searchBySurnameField.getText().trim().equalsIgnoreCase(data.currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(data.currentEmployee);
			} // end else if
			else {
				nextRecord();// look for next record
				// loop until Employee found or until all Employees have been
				// checked
				while (!firstSurname.trim().equalsIgnoreCase(data.currentEmployee.getSurname().trim())) {
					// if found break from loop and display Employee details
					// else look for next record
					if (data.searchBySurnameField.getText().trim().equalsIgnoreCase(data.currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(data.currentEmployee);
						break;
					} // end if
					else
						nextRecord();// look for next record
				} // end while
			} // end else
				// if Employee not found display message
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		} // end if
		data.searchBySurnameField.setText("");
	}// end searchEmployeeBySurname

	// get next free ID from Employees in the file
	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else look
		// for last active record
		if (data.file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();// look for last active record
			// add 1 to last active records ID to get next ID
			nextFreeId = data.currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}// end getNextFreeId

	// get values from text fields and create Employee object
	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) data.fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(data.idField.getText()), data.ppsField.getText().toUpperCase(),
				data.surnameField.getText().toUpperCase(), data.firstNameField.getText().toUpperCase(),
				data.genderCombo.getSelectedItem().toString().charAt(0), data.departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(data.salaryField.getText()), fullTime);

		return theEmployee;
	}// end getChangedDetails

	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		// open file for writing
		data.application.openWriteFile(data.file.getAbsolutePath());
		// write into a file
		data.currentByteStart = data.application.addRecords(newEmployee);
		data.application.closeWriteFile();// close file for writing
	}// end addRecord

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
									// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				data.application.openWriteFile(data.file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				data.application.deleteRecords(data.currentByteStart);
				data.application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (isSomeoneToDisplay()) {
					nextRecord();// look for next record
					displayRecords(data.currentEmployee);
				} // end if
			} // end if
		} // end if
	}// end deleteDecord

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = data.currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = data.currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(data.currentEmployee.getEmployeeId()));
			empDetails.addElement(data.currentEmployee.getPps());
			empDetails.addElement(data.currentEmployee.getSurname());
			empDetails.addElement(data.currentEmployee.getFirstName());
			empDetails.addElement(new Character(data.currentEmployee.getGender()));
			empDetails.addElement(data.currentEmployee.getDepartment());
			empDetails.addElement(new Double(data.currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(data.currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != data.currentEmployee.getEmployeeId());// end do - while
		data.currentByteStart = byteStart;

		return allEmployee;
	}// end getAllEmployees

	// activate field for editing
	private void editDetails() {
		// activate field for editing if there is records to display
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			data.salaryField.setText(fieldFormat.format(data.currentEmployee.getSalary()));
			data.change = false;
			setEnabled(true);// enable text fields for editing
		} // end if
	}// end editDetails

	// ignore changes and set text field unenabled
	private void cancelChange() {
		setEnabled(false);
		displayRecords(data.currentEmployee);
	}// end cancelChange

	// check if any of records in file is active - ID is not 0
	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		// open file for reading
		data.application.openReadFile(data.file.getAbsolutePath());
		// check if any of records in file is active - ID is not 0
		someoneToDisplay = data.application.isSomeoneToDisplay();
		data.application.closeReadFile();// close file for reading
		// if no records found clear all text fields and display message
		if (!someoneToDisplay) {
			data.currentEmployee = null;
			data.idField.setText("");
			data.ppsField.setText("");
			data.surnameField.setText("");
			data.firstNameField.setText("");
			data.salaryField.setText("");
			data.genderCombo.setSelectedIndex(0);
			data.departmentCombo.setSelectedIndex(0);
			data.fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}// end isSomeoneToDisplay

	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		// check for correct PPS format based on assignment description
		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
					&& Character.isDigit(pps.charAt(2))	&& Character.isDigit(pps.charAt(3)) 
					&& Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5)) 
					&& Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				// open file for reading
				data.application.openReadFile(data.file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = data.application.isPpsExist(pps, currentByte);
				data.application.closeReadFile();// close file for reading
			} // end if
			else
				ppsExist = true;
		} // end if
		else
			ppsExist = true;

		return ppsExist;
	}// end correctPPS

	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}// end checkFileName

	// check if any changes text field where made
	private boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (data.change) {
			saveChanges();// save changes
			anyChanges = true;
		} // end if
			// if no changes made, set text fields as unenabled and display
			// current Employee
		else {
			setEnabled(false);
			displayRecords(data.currentEmployee);
		} // end else

		return anyChanges;
	}// end checkForChanges

	// check for input in text fields
	private boolean checkInput() {
		boolean valid = true;
		// if any of inputs are in wrong format, colour text field and display
		// message
		if (data.ppsField.isEditable() && data.ppsField.getText().trim().isEmpty()) {
			data.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (data.ppsField.isEditable() && correctPps(data.ppsField.getText().trim(), data.currentByteStart)) {
			data.ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (data.surnameField.isEditable() && data.surnameField.getText().trim().isEmpty()) {
			data.surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (data.firstNameField.isEditable() && data.firstNameField.getText().trim().isEmpty()) {
			data.firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (data.genderCombo.getSelectedIndex() == 0 && data.genderCombo.isEnabled()) {
			data.genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (data.departmentCombo.getSelectedIndex() == 0 && data.departmentCombo.isEnabled()) {
			data.departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		try {// try to get values from text field
			Double.parseDouble(data.salaryField.getText());
			// check if salary is greater than 0
			if (Double.parseDouble(data.salaryField.getText()) < 0) {
				data.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end try
		catch (NumberFormatException num) {
			if (data.salaryField.isEditable()) {
				data.salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end catch
		if (data.fullTimeCombo.getSelectedIndex() == 0 && data.fullTimeCombo.isEnabled()) {
			data.fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
			// display message if any input or format is wrong
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		// set text field to white colour if text fields are editable
		if (data.ppsField.isEditable())
			setToWhite();

		return valid;
	}

	// set text field background colour to white
	private void setToWhite() {
		data.ppsField.setBackground(UIManager.getColor("TextField.background"));
		data.surnameField.setBackground(UIManager.getColor("TextField.background"));
		data.firstNameField.setBackground(UIManager.getColor("TextField.background"));
		data.salaryField.setBackground(UIManager.getColor("TextField.background"));
		data.genderCombo.setBackground(UIManager.getColor("TextField.background"));
		data.departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		data.fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}// end setToWhite

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		data.ppsField.setEditable(booleanValue);
		data.surnameField.setEditable(booleanValue);
		data.firstNameField.setEditable(booleanValue);
		data.genderCombo.setEnabled(booleanValue);
		data.departmentCombo.setEnabled(booleanValue);
		data.salaryField.setEditable(booleanValue);
		data.fullTimeCombo.setEnabled(booleanValue);
		data.saveChange.setVisible(booleanValue);
		data.cancelChange.setVisible(booleanValue);
		data.searchByIdField.setEnabled(search);
		data.searchBySurnameField.setEnabled(search);
		data.searchId.setEnabled(search);
		data.searchSurname.setEnabled(search);
	}// end setEnabled

	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(data.datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (data.file.length() != 0 || data.change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (data.file.getName().equals(data.generatedFileName))
				data.file.delete();// delete file
			data.file = newFile;// assign opened file to file
			// open file for reading
			data.application.openReadFile(data.file.getAbsolutePath());
			firstRecord();// look for first record
			displayRecords(data.currentEmployee);
			data.application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (data.file.getName().equals(data.generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (data.change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!data.idField.getText().equals("")) {
						// open file for writing
						data.application.openWriteFile(data.file.getAbsolutePath());
						// get changes for current Employee
						data.currentEmployee = getChangedDetails();
						// write changes to file for corresponding Employee
						// record
						data.application.changeRecords(data.currentEmployee, data.currentByteStart);
						data.application.closeWriteFile();// close file for writing
					} // end if
				} // end if
			} // end if

			displayRecords(data.currentEmployee);
			setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			data.application.openWriteFile(data.file.getAbsolutePath());
			// get changes for current Employee
			data.currentEmployee = getChangedDetails();
			// write changes to file for corresponding Employee record
			data.application.changeRecords(data.currentEmployee, data.currentByteStart);
			data.application.closeWriteFile();// close file for writing
			data.changesMade = false;// state that all changes has bee saved
		} // end if
		displayRecords(data.currentEmployee);
		setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(data.datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				// create new file
				data.application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				data.application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(data.file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (data.file.getName().equals(data.generatedFileName))
					data.file.delete();// delete file
				data.file = newFile;// assign new file to file
			} // end try
			catch (IOException e) {
			} // end catch
		} // end if
		data.changesMade = false;
	}// end saveFileAs

	// allow to save changes to file when exiting the application
	private void exitApp() {
		// if file is not empty allow to save changes
		if (data.file.length() != 0) {
			if (data.changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// if user chooses to save file, save file
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();// save file
					// delete generated file if user saved details to other file
					if (data.file.getName().equals(data.generatedFileName))
						data.file.delete();// delete file
					System.exit(0);// exit application
				} // end if
					// else exit application
				else if (returnVal == JOptionPane.NO_OPTION) {
					// delete generated file if user chooses not to save file
					if (data.file.getName().equals(data.generatedFileName))
						data.file.delete();// delete file
					System.exit(0);// exit application
				} // end else if
			} // end if
			else {
				// delete generated file if user chooses not to save file
				if (data.file.getName().equals(data.generatedFileName))
					data.file.delete();// delete file
				System.exit(0);// exit application
			} // end else
				// else exit application
		} else {
			// delete generated file if user chooses not to save file
			if (data.file.getName().equals(data.generatedFileName))
				data.file.delete();// delete file
			System.exit(0);// exit application
		} // end else
	}// end exitApp

	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	private void createRandomFile() {
		data.generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		data.file = new File(data.generatedFileName);
		// create file
		data.application.createFile(data.file.getName());
	}// end createRandomFile

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == data.closeApp) {
			if (checkInput() && !checkForChanges())
				exitApp();
		} else if (e.getSource() == data.open) {
			if (checkInput() && !checkForChanges())
				openFile();
		} else if (e.getSource() == data.save) {
			if (checkInput() && !checkForChanges())
				saveFile();
			data.change = false;
		} else if (e.getSource() == data.saveAs) {
			if (checkInput() && !checkForChanges())
				saveFileAs();
			data.change = false;
		} else if (e.getSource() == data.searchById) {
			if (checkInput() && !checkForChanges())
				displaySearchByIdDialog();
		} else if (e.getSource() == data.searchBySurname) {
			if (checkInput() && !checkForChanges())
				displaySearchBySurnameDialog();
		} else if (e.getSource() == data.searchId || e.getSource() == data.searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == data.searchSurname || e.getSource() == data.searchBySurnameField)
			searchEmployeeBySurname();
		else if (e.getSource() == data.saveChange) {
			if (checkInput() && !checkForChanges())
				;
		} else if (e.getSource() == data.cancelChange)
			cancelChange();
		else if (e.getSource() == data.firstItem || e.getSource() == data.first) {
			if (checkInput() && !checkForChanges()) {
				firstRecord();
				displayRecords(data.currentEmployee);
			}
		} else if (e.getSource() == data.prevItem || e.getSource() == data.previous) {
			if (checkInput() && !checkForChanges()) {
				previousRecord();
				displayRecords(data.currentEmployee);
			}
		} else if (e.getSource() == data.nextItem || e.getSource() == data.next) {
			if (checkInput() && !checkForChanges()) {
				nextRecord();
				displayRecords(data.currentEmployee);
			}
		} else if (e.getSource() == data.lastItem || e.getSource() == data.last) {
			if (checkInput() && !checkForChanges()) {
				lastRecord();
				displayRecords(data.currentEmployee);
			}
		} else if (e.getSource() == data.listAll || e.getSource() == data.displayAll) {
			if (checkInput() && !checkForChanges())
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
		} else if (e.getSource() == data.create || e.getSource() == data.add) {
			if (checkInput() && !checkForChanges())
				new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == data.modify || e.getSource() == data.edit) {
			if (checkInput() && !checkForChanges())
				editDetails();
		} else if (e.getSource() == data.delete || e.getSource() == data.deleteButton) {
			if (checkInput() && !checkForChanges())
				deleteRecord();
		} else if (e.getSource() == data.searchBySurname) {
			if (checkInput() && !checkForChanges())
				new SearchBySurnameDialog(EmployeeDetails.this);
		}
	}// end actionPerformed

	// content pane for main dialog
	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());// add menu bar to frame
		// add search panel to frame
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		// add navigation panel to frame
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		// add button panel to frame
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
		// add details panel to frame
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}// end createContentPane

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}// end createAndShowGUI

	// main method
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}// end main

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		data.change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		data.change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		data.change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		data.change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}// end class EmployeeDetails
