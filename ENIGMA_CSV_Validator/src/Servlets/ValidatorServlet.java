package Servlets;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import Validator.*;

/**
 * Servlet implementation class ValidatorServlet
 */
@WebServlet("/ValidatorServlet")
@MultipartConfig
public class ValidatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidatorServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// gets savePath
        String savePath = getServletContext().getRealPath("/") + "docUploads";

        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
        
        // get and save uploaded file
        String filePath = "";
        for (Part part : request.getParts()) {
            String fileName = getFileName(part);
            fileName = new File(fileName).getName();
            filePath = savePath + File.separator + fileName;
            part.write(filePath);
        }
        
        // validate uploaded file
        try {
        	Validator validator = new Validator();
			
			HttpSession session = request.getSession(); // get current session
			
			// saves validation report locally
		    session.setAttribute("report", validator.getValidationReport(
		    		filePath, 
		    		getServletContext().getRealPath("/")
		    	)
		    ); 
		    
		    response.sendRedirect("jsp/LandingPage.jsp"); //redirect to login menu
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			deleteFile(filePath);
		}
    }
	
    /**
     * Extracts file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
    
    private void deleteFile(String filepath) {
    	 try
         {
             Files.deleteIfExists(Paths.get(filepath));
         }
         catch(NoSuchFileException e)
         {
             System.out.println("No such file/directory exists");
         }
         catch(DirectoryNotEmptyException e)
         {
             System.out.println("Directory is not empty.");
         }
         catch(IOException e)
         {
             System.out.println("Invalid permissions.");
         }          
         System.out.println("Deletion successful.");
    }

}
