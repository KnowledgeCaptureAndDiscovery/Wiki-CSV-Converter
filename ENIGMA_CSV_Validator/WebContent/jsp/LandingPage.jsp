<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*,Report.ReportEntry" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>ENIGMA_CSV_Validator</title>
		<link rel="stylesheet" type="text/css" href="../css/LandingPage.css"/>
		<script>
			window.onload = function() { 
				var loader = document.getElementById("loader");
				loader.style.display = "none";
				
				// Get the modal
				var modal = document.getElementById("report");
				
				// Get the button that opens the modal
				var btn = document.getElementById("popup_button");
				
				// Get the <span> element that closes the modal
				var span = document.getElementsByClassName("close")[0];
				
				// When the user clicks the button, open the modal 
				btn.onclick = function() {
				    modal.style.display = "block";
				}
				
				// When the user clicks on <span> (x), close the modal
				span.onclick = function() {
				    modal.style.display = "none";
				}
				
				// When the user clicks anywhere outside of the modal, close it
				window.onclick = function(event) {
				    if (event.target == modal) {
				        modal.style.display = "none";
				    }
				}
				
				var acc = document.getElementsByClassName("accordion");
				var i;
				
				for (i = 0; i < acc.length; i++) {
				  acc[i].addEventListener("click", function() {
				    this.classList.toggle("active");
				    var panel = this.nextElementSibling;
				    
				    if (panel.style.maxHeight) {
				      panel.style.maxHeight = null;
				    }
				    else {
				      panel.style.maxHeight = "none";
				    } 
				  });
				}
			};
			
			function validateForm() {
			    var extension = document.getElementById("upload_button").value.split('.').pop();
			    
			    if (extension != "csv") {
			        alert("ERROR: Incorrect file format, please use csv.");
			        return false;
			    }
			    else {
			    	var loader = document.getElementById("loader");
					loader.style.display = "block";
			    	return true;
			    }
			}	
		</script>
	</head>
	<body>
		<div id="banner">
			<img src="../assets/enigma_logo.png" style="width:8%; height:8%;">	
		</div>
		<div id="header_line"></div>
		<h1 id="header">ENIGMA CSV Validator</h1>		
		<div id="upload_form">
			<form method="post" action="../ValidatorServlet" enctype="multipart/form-data" onsubmit="return validateForm()">
		 		<div id="select_header">Select a csv file to upload:</div> 
		 		<input id="upload_button" type="file" name="img"> 
		 		<input id="submit_button" type="submit">
		 		<div id="loader"></div>
			</form>
		</div>
		<div id="report_container">
			<%
			ArrayList<ReportEntry> validationReport = (ArrayList<ReportEntry>) session.getAttribute("report");

				if (validationReport != null) {
					request.getSession().removeAttribute("report");
	      	%>
	      			<button id="popup_button">View Report</button>
	      			<div id="report" class="report">
		 				<div class="report_content">
		    				<span class="close">&times;</span>
		    				<h1 style="text-align: center">Validation Report</h1>
		    				<%
		    					for(ReportEntry reportEntry : validationReport) {
		    				%>
		    						<hr>    						
		    						<button style="font-weight: bold; font-size: 18px" class="accordion"><%=reportEntry.getName() %></button>
		    						<div class="panel">	
		    							<div style="font-style: italic; font-size: 14px">NOTE: <%=reportEntry.getHeader() %></div>
		    							<br />
		    				<% 
		    							if(reportEntry.getWarningsHeader() != null) {
		    				%>
			    							<button class="accordion"><%=reportEntry.getWarningsHeader() %></button>
			    							<div class="panel">    							
			    								<p style="font-size: 15px"><%=reportEntry.getWarnings() %></p>
			    							</div>
			    							<br />
			    			<% 
			    					    }

									HashMap<String, ArrayList<String>> propValues = reportEntry.getPropValMap();
				    				for(HashMap.Entry<String, ArrayList<String>> entry : propValues.entrySet()) {
				    				    String prop = entry.getKey();
				    				    ArrayList<String> values = entry.getValue();
							%>
										<button class="accordion">Property <strong><%=prop%></strong> will be added with value(s):</button>
										<div class="panel">
							<%
											
											for(String value : values) {
							%>
												<p style="font-weight: bold; font-size: 15px">&emsp; - <%=value %><p>
							<%				
											}
							%>											
										</div>
				    		<%			
				    				}
	    								if(reportEntry.getNotesHeader() != null) {
	    					%>
		    								<br />
		    								<button class="accordion" style="font-size: 13px"><%=reportEntry.getNotesHeader() %></button>
		    								<div class="panel">    							
		    									<p style="font-size: 13px"><%=reportEntry.getNotes() %></p>
		    								</div>
		    				<% 
		    					    	}
				    		%>
		    						</div>	
		    				<% 	    				
		    					}
		    				%>
						</div>
					</div>
	      	<%
	      		}
	      	%>
		</div>
	</body>
</html>