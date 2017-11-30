package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import parser.*;
import SQL.*;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/Submit")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	
    	String username = request.getParameter("username");
    	String password = request.getParameter("password");
    	String ipaddress = request.getParameter("ipaddress");
    	String ssl = request.getParameter("ssl");
    	String sslBoolean = "";
    	if(ssl == null) {
    		sslBoolean = "false";
    	}
    	else {
    		sslBoolean = "true";
    	}
    	
    	String connectionQuery = "jdbc:mysql://" + ipaddress + "/rdupadhy_201_site?user=" + username + "&password=" + password + "&useSSL=" + sslBoolean;
    	//process only if its multipart content
    	
    	//Catching the input file's contents as a stream
    	Part filePart = request.getPart("file");
    	String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
    	
    	Education education = new Education();
    	
    	if(fileName.equals("") == false) {
    		InputStream fileContent = filePart.getInputStream();
        	System.out.println(fileName);
        	Gson gson = new Gson();
        	
        	//Creating a buffered reader for that input stream
        	JsonReader jsonReader = new JsonReader(new InputStreamReader(fileContent));
        	
        	//Contents parsed using gson and stored in Education class which encapsulates the School class
        	education = gson.fromJson(jsonReader, Education.class);
        
        	//Inserting into MySQL database
        	for(int i = 0; i < education.getSchools().length; i++) {
	    		InsertSQL.insertSchool(education.getSchools()[i], connectionQuery);
	    	} 
        	request.setAttribute("connectionQuery", connectionQuery);
    	}
    	else {
    		education = RetrieveSQL.retrieveEducation(connectionQuery);
    	}
    	
    	//Design
    	String design = request.getParameter("design");
    	request.setAttribute("design", design);
    	
    	//Storing the Education object in the HttpServletResponse object
    	request.setAttribute("education", education);
    	
    	//Forwarding the request to a jsp file
    	RequestDispatcher dispatch = request.getRequestDispatcher("/home.jsp");
		dispatch.forward(request, response);
    	return;
   	
	}

}
