import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import database.Database_con;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "employeeBean")
@SessionScoped
public class EmployeeBean implements Serializable {

    private List<Employee> employees = new ArrayList<>();
    private Employee selectedEmployee = new Employee();
    private Integer EmpId;
    
    boolean modFlag=false;
    boolean saveNewEmp = false;
    
    @PostConstruct
    public void init() {
    	//this.selectedEmployee.setName("Hello");
    	
        System.out.println("EmployeeBean PostConstruct1");
        
    	Database_con db = new Database_con();
    	String query = "SELECT * FROM employee";
    	
    	try {            
            ResultSet rs = db.executeQuery(query);
            employees.clear();
            while (rs.next()) {
                int id = rs.getInt("e_id");
                String name = rs.getString("e_name");
                String email = rs.getString("e_email");
                String department = rs.getString("e_dept");
                String designation = rs.getString("e_desg");
                
                employees.add(new Employee(id,name,email, department, designation));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeResources();
        }
    	
    	
    	System.out.println("In init() before calling : goToModifyEmployeeFetch Remote1");
    	this.selectedEmployee = new Employee();
    	System.out.println("init() flag:"+modFlag);
    	if(modFlag) {
    		goToModifyEmployeeFetch(this.selectedEmployee);
    	}
    	System.out.println("In init() after calling : goToModifyEmployeeFetch 2local");
    }

    public EmployeeBean() {
    	System.out.println("Constructor Invoked");
    	
    	this.selectedEmployee = new Employee();
    	//this.selectedEmployee.setName("Hello");
    	
        /*employees.add(new Employee(100, "John Doe", "john.doe@example.com", "IT", "Developer"));
        employees.add(new Employee(200, "Jane Smith", "jane.smith@example.com", "HR", "Manager"));*/
    }


    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

    public String saveEmployee() {
    	
    	saveNewEmp =true;
    	String validationError = nameValidation();
    	if (validationError != null) {
    		FacesContext.getCurrentInstance().addMessage(null, 
    	            new FacesMessage(FacesMessage.SEVERITY_ERROR, validationError, null));
    	    return null;
    	}
    	
    	Database_con db = new Database_con();
    	Connection con = null;
    	
    	String query = "INSERT INTO employee (e_id, e_name, e_email, e_dept, e_desg) VALUES (?, ?, ?, ?, ?)";
    	try {
            con = db.connectDb();

            if (con != null) {
                PreparedStatement pstmt = con.prepareStatement(query);
                
                System.out.println("ID: "+selectedEmployee.getId());
                pstmt.setString(1, String.valueOf(selectedEmployee.getId()));
                pstmt.setString(2, selectedEmployee.getName());
                pstmt.setString(3, selectedEmployee.getEmail());
                pstmt.setString(4, selectedEmployee.getDepartment());
                pstmt.setString(5, selectedEmployee.getDesignation());

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("A new employee was inserted successfully!");
                    //db.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                db.rollback();
            }
        } finally {
            db.closeResources();
        }
    	
    	init();//this.selectedEmployee = new Employee();
    	return "home?faces-redirect=true";
    }

    public String deleteEmployee(Employee emp) {
    	
    	
    	int empid = emp.getId();
    	Database_con db = new Database_con();
    	Connection con = null;
    	String query = "DELETE FROM employee WHERE e_id=?";
    	try {
            con = db.connectDb();
            if (con != null) {
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, emp.getId());
                
                int rowsDeleted = pstmt.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Employee Record deleted for ID: "+empid);
                    //db.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                db.rollback();
            }
        } finally {
            db.closeResources();
        }
    	
    	init();//this.selectedEmployee = new Employee();
        return "home?faces-redirect=true";
    }

    public String goToEmployeeDetails(Employee emp) {
    	
        System.out.println("Emp ID: "+emp.getId());
        
        Database_con db = new Database_con();
        String query = "SELECT * FROM employee WHERE e_id = ?";
        
        try {
            PreparedStatement pstmt = db.connectDb().prepareStatement(query);
            pstmt.setInt(1, emp.getId());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                selectedEmployee = new Employee(
                    rs.getInt("e_id"),
                    rs.getString("e_name"),
                    rs.getString("e_email"),
                    rs.getString("e_dept"),
                    rs.getString("e_desg")
                );
                System.out.println("Employee loaded: " + selectedEmployee.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeResources();
        }
        
        try {
            return "employeeDetails?faces-redirect=true" +
                   "&employeeId=" + emp.getId() +
                   "&empName=" + URLEncoder.encode(emp.getName(), "UTF-8") +
                   "&empEmail=" + URLEncoder.encode(emp.getEmail(), "UTF-8") +
                   "&empDep=" + URLEncoder.encode(emp.getDepartment(), "UTF-8") +
                   "&empDesg=" + URLEncoder.encode(emp.getDesignation(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "error.xhtml";
        }
    }
    
    public String goToModifyEmployee(Employee emp) {
    	
    	System.out.println("goToModifyEmployee called");
        modFlag = true;
        EmpId = emp.getId();
        System.out.println("goToModifyEmployee flag:"+modFlag);
        System.out.println("goToModifyEmployee EmpId:"+EmpId);
        init();
        
        try {
            return "employeeModify?faces-redirect=true" +
                   "&employeeId=" + emp.getId() +
                   "&empName=" + URLEncoder.encode(emp.getName(), "UTF-8") +
                   "&empEmail=" + URLEncoder.encode(emp.getEmail(), "UTF-8") +
                   "&empDep=" + URLEncoder.encode(emp.getDepartment(), "UTF-8") +
                   "&empDesg=" + URLEncoder.encode(emp.getDesignation(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "error.xhtml";
        }
    }
    
    public String goToModifyEmployeeFetch(Employee emp) {
    	
        //this.selectedEmployee.setName("Hello");
    	System.out.println("goToModifyEmployeeFetch called");
        System.out.println("Emp ID: "+EmpId);
        
        Database_con db = new Database_con();
        String query = "SELECT * FROM employee WHERE e_id = ?";
        
        try {
            PreparedStatement pstmt = db.connectDb().prepareStatement(query);
            pstmt.setInt(1, EmpId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	this.selectedEmployee.setId(rs.getInt("e_id"));
            	this.selectedEmployee.setName(rs.getString("e_name"));
            	this.selectedEmployee.setEmail(rs.getString("e_email"));
            	this.selectedEmployee.setDepartment(rs.getString("e_dept"));
            	this.selectedEmployee.setDesignation(rs.getString("e_desg"));
                System.out.println("Employee loaded: " + this.selectedEmployee.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeResources();
        }
        
        return "employeeModify?faces-redirect=true";
    }
    
    public String updateEmployee() {
        String name = selectedEmployee.getName();
        String email = selectedEmployee.getEmail();
        String department = selectedEmployee.getDepartment();
        String designation = selectedEmployee.getDesignation();

        
        System.out.println("Updating Employee:");
        System.out.println("ID: " + selectedEmployee.getId()); 
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Department: " + department);
        System.out.println("Designation: " + designation);
        
        saveNewEmp = false;
        String validationError = nameValidation();
    	if (validationError != null) {
    		FacesContext.getCurrentInstance().addMessage(null, 
    	            new FacesMessage(FacesMessage.SEVERITY_ERROR, validationError, null));
    	    return null;
    	}
        
        Database_con db = new Database_con();
    	Connection con = null;
    	
    	String query = "UPDATE employee set e_name = ?, e_email = ?, e_dept =?, e_desg=? WHERE e_id = ?";
    	try {
            con = db.connectDb();

            if (con != null) {
                PreparedStatement pstmt = con.prepareStatement(query);
                
                System.out.println("ID: "+selectedEmployee.getId());
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, department);
                pstmt.setString(4, designation);
                pstmt.setString(5, String.valueOf(selectedEmployee.getId()));

                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Employee record updated successfully!");
                    //db.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                db.rollback();
            }
        } finally {
            db.closeResources();
        }
    	
    	init();//this.selectedEmployee = new Employee();
    	return "home?faces-redirect=true";
    }
    
    public String createEmployee() {
    	this.selectedEmployee = new Employee();
    	return "employeeForm?faces-redirect=true";
    }
    
    public String toHome() {
    	init();//this.selectedEmployee = new Employee();
    	return "home?faces-redirect=true";
    }
    
    public String nameValidation() {
    	int eid=0;
    	Database_con db = new Database_con();
    	String query="";
    	
    		query = "SELECT e.e_id FROM `employee` e WHERE trim(e.e_name) = trim(?)";
    	
    	try {
    		PreparedStatement pstmt = db.connectDb().prepareStatement(query);
    		pstmt.setString(1, selectedEmployee.getName());

            ResultSet rs = pstmt.executeQuery();

            	if (rs.next()) {
            		eid = rs.getInt("e_id");
            		if(saveNewEmp) {
            			System.out.println("Name already Exists");
                    	return "Name already Exists";
            		}
            		else if(eid != selectedEmployee.getId()) {
            			System.out.println("Name already Exists");
                    	return "Name already Exists";
            		}
            	}
 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeResources();
        }
    	return null;
    }

}
