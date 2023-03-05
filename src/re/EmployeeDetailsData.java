package re;

import java.awt.Font;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EmployeeDetailsData {
	public long currentByteStart;
	public RandomFile application;
	public FileNameExtensionFilter datfilter;
	public File file;
	public boolean change;
	public boolean changesMade;
	public JMenuItem open;
	public JMenuItem save;
	public JMenuItem saveAs;
	public JMenuItem create;
	public JMenuItem modify;
	public JMenuItem delete;
	public JMenuItem firstItem;
	public JMenuItem lastItem;
	public JMenuItem nextItem;
	public JMenuItem prevItem;
	public JMenuItem searchById;
	public JMenuItem searchBySurname;
	public JMenuItem listAll;
	public JMenuItem closeApp;
	public JButton first;
	public JButton previous;
	public JButton next;
	public JButton last;
	public JButton add;
	public JButton edit;
	public JButton deleteButton;
	public JButton displayAll;
	public JButton searchId;
	public JButton searchSurname;
	public JButton saveChange;
	public JButton cancelChange;
	public JComboBox<String> genderCombo;
	public JComboBox<String> departmentCombo;
	public JComboBox<String> fullTimeCombo;
	public JTextField idField;
	public JTextField ppsField;
	public JTextField surnameField;
	public JTextField firstNameField;
	public JTextField salaryField;
	public Font font1;
	public String generatedFileName;
	public Employee currentEmployee;
	public JTextField searchByIdField;
	public JTextField searchBySurnameField;
	public String[] gender;
	public String[] department;
	public String[] fullTime;

	public EmployeeDetailsData(long currentByteStart, RandomFile application, FileNameExtensionFilter datfilter,
			boolean change, boolean changesMade, Font font1, String[] gender, String[] department, String[] fullTime) {
		this.currentByteStart = currentByteStart;
		this.application = application;
		this.datfilter = datfilter;
		this.change = change;
		this.changesMade = changesMade;
		this.font1 = font1;
		this.gender = gender;
		this.department = department;
		this.fullTime = fullTime;
	}
}