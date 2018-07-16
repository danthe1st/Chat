<%@ page import="chat.utils.Constants"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="de">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Login</title>
		
		<script type="text/javascript">

        
        function byteUmkehr(s){
        	return s.match(/[a-fA-F0-9]{2}/g).reverse().join('');
        }
  </script>
		
	</head>
	<body>
	
		<c:if test="${requestScope.errmsg!=null}">
			<c:out value="${requestScope.errmsg}" escapeXml="true"/>
		</c:if>
		<h2>Login</h2>
		<form action="./login" method="post">
			<input type="hidden" name="<%=Constants.PW_FIELD %>" id="LoginHiddenPass"/>
			<label for="usernameIn" title="Benutzername:">Benutzername:</label>
			<input name="<%=Constants.UNAME_FIELD %>" id="usernameIn"/><br>
			<label for="passwdInLog" title="Passwort:">Passwort:</label>
			<input name="<%=Constants.PW_FIELD %>" type="password" id="passwdInLog">
			<input type="submit" name="loginUsr" value="Send"
			onclick="document.getElementById('LoginHiddenPass').value=byteUmkehr(document.getElementById('passwdInLog').value);"
			/>
		</form>
		
		
		<h2>Registrieren</h2>
		<form action="./register" method="post">
			<input type="hidden" name="<%=Constants.PW_FIELD %>" id="RegHiddenPass"/>
			<input type="hidden" name="<%=Constants.PW_CONFIRM_FIELD %>" id="RegHiddenPassConf"/>
			<label for="usernameInReg" title="Benutzername:">Benutzername:</label>
			<input name="<%=Constants.UNAME_FIELD %>" id="usernameInReg"/><br>
			<label for="passwdInReg" title="Passwort:">Passwort:</label>
			<input name="<%=Constants.PW_FIELD %>" type="password" id="passwdInReg">
			<br>
			<label for="passwd2" title="Passwort wiederholen:">Passwort wiederholen:</label>
			<input name="<%=Constants.PW_CONFIRM_FIELD %>" type="password" id="passwd2">
			<input type="submit" name="registerUsr" value="Send"
			onclick="document.getElementById('RegHiddenPass').value=byteUmkehr(document.getElementById('passwdInReg').value);
				document.getElementById('RegHiddenPassConf').value=byteUmkehr(document.getElementById('passwd2').value);"
			/>
		</form>
	</body>
</html>