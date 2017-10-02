<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>ENIGMA_CSV_Validator</title>
	</head>
	<body>
		<div class="file_upload">
			<h1>Welcome to the ENIGMA csv file validator!</h1>
			<form name="cinemateVisual" method="post" action="ValidatorServlet" enctype="multipart/form-data">
		 		Select a csv file to upload: <input type="file" name="img"> <input type="submit">
			</form>
		</div>
		<div class="uploaded_document">
			<%
	      		String validationReport = (String) session.getAttribute("report");

				if (validationReport != null) {
					request.getSession().removeAttribute("report");
	      	%>
	      			<a href=<%="file:///" + validationReport%> >View Report</a>
	      	<%
	      		}
	      	%>
		</div>
	</body>
</html>