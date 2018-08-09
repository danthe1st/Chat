<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="chat.utils.Constants"%>
<%@page import="chat.ChatData"%>
<%@page import="chat.ChatMsg"%>
<%@page import="chat.utils.ChatProperties"%>
<%
	final String contextRoot = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="de">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Live Chat</title>
		<link type="text/css" href="<%=contextRoot%>/design.css" rel="stylesheet">
		<script src="Skripts.js" type="text/javascript"></script>
		<script type="text/javascript">
			/*var func=window.onload;
			window.onload=function(){
				func();
				InitializeTimer(${ChatProperties.getRefreshTime()});)
					}*/
					
					
					var wsUri = getRootUri() + "/Chat/liveChat";
					websocket = new WebSocket(wsUri);
					/*websocket.onopen = function(evt) {
					    onOpen(evt)
					};*/
					
					websocket.onmessage = function(evt) {
						elem=document.getElementById('ChatOutput')
						elem.innerText=evt.data+'\n'+elem.innerText;
						//add msg
					};
					/*websocket.onerror = function(evt) {
					    onError(evt)
					};
					websocket.onclose = function(evt) {
					    onClose(evt)
					};*/
					function sendMsg(msg){
						websocket.send('[${sessionScope.user }] '+msg)
					}
		</script>
	</head>
	<body>
	
		<h2>Chats:</h2>
		
		
	
	<form action="./Chat" method="post" id="clickChatForm">
		<input type="hidden" name="name" id="clickChat"/>
			
		<c:forEach var="chat" items="${applicationScope.CHAT_DATA.listChats() }">
			
			<span>
				
				<input type="submit" name="name" value="${chat}"  onclick="document.getElementById('clickChat').value = '${chat}';
				document.getElementById('clickChatForm').action='./Chat?chat=${chat}';">
			</span>
		</c:forEach>
		</form>
		
		
		<h2>Chat erstellen:</h2>
		<form action="./createChat" method="post">
			<label for="chatname" title="Name des Chats:">Name des Chats:</label>
			<input name="name" id="chatname" autocomplete="off"
			onkeyup="InitializeTimer(
			  ${ChatProperties.getRefreshTime()}
			  )"
			/>
			<input type="submit" name="createChat" value="Erstellen"
			
			/>
			
		</form>	
		<h2>Live-Chat</h2>
		<div id="ChatDiv"><input name="msg" id="ChatMsg" />
		<input type="submit" value="write msg" id="writeMsg" onclick="sendMsg(document.getElementById('ChatMsg').value);document.getElementById('ChatMsg').value=''"/>
		<br>
		<output id=ChatOutput></output></div>
		
	
		
		
		<aside>
		<div><b>Username: </b><i>${sessionScope.user }</i>
		<form action="./logout"><input type="submit" name="logout" value="Logout"/></form>
		<c:set var="chatData" value="<%=this.getServletContext().getAttribute(Constants.attributeChatData)%>"/>
		<c:if test="${chatData.isAdmin(sessionScope.user)}">
			<form action="./Manager"><input type="submit" name="Manager" value="get Manager Mode"/></form>
		</c:if>
		</div>
		<b>eingeloggte Benutzer:</b><br>
		<form action="./kick">
			<input type="hidden" name="${Constants.UNAME_KICK_FIELD}" value="null" id=kickF>
			<table>
			<c:forEach var="username" items="${applicationScope.CHAT_DATA.getUsersLoggedIn()}">
			<tr>
				<c:set var="chatData" value="<%=this.getServletContext().getAttribute(Constants.attributeChatData)%>"></c:set>
				<td><c:out value="${username }" escapeXml="true"/> </td>
					<td><c:if test="${chatData.isAdmin(sessionScope.user)&&!username.equals(sessionScope.user) }">
						<input type="submit" name="_" value="kick" id="kick_${ username}" onclick="document.getElementById('kickF').value = '${ username}';">
					</c:if></td>
				
				</tr>
			</c:forEach>
			</table>
		</form>
		
		<c:if test="${chatData.isAdmin(sessionScope.user)}">
			<b>alle Benutzer</b><br>
			
				
				<table>
				<c:forEach var="username" items="${applicationScope.CHAT_DATA.getAllUsernames()}"><tr>
					<td><c:set var="chatData" value="<%=this.getServletContext().getAttribute(Constants.attributeChatData)%>"></c:set>
					<span><c:out value="${username}" escapeXml="true"/></span></td>
						<td><form action="./delUser">
							<input type="hidden" name="<%=Constants.UNAME_DEL_FIELD%>" value="null" id="delUF_${username}">
							<input type="submit" name="_" value="löschen" id="kick_${ username}" onclick="document.getElementById('delUF_${ username}').value = '${ username}';">
						</form></td>
						<td><form action="./ban">
							<input type="hidden" name="<%=Constants.BAN_FIELD%>" value="null" id="banUF_${username}">
							<input type="submit" name="_" value="ban" id="ban_${ username}" onclick="document.getElementById('banUF_${ username}').value = '${ username}';">
						</form></td>
					</tr>
				</c:forEach>
				</table>
			<c:if test="${!applicationScope.CHAT_DATA.getBans().isEmpty()}">
				<b>banned IPs</b><br>
			
				<form id="form-1" action="./ban">
					<input type="hidden" name="<%=Constants.BAN_FIELD%>" value="null" id=banIPF>
					<table>
						<c:forEach var="ban" items="${applicationScope.CHAT_DATA.getBans()}">
							<tr><td><c:out value="${ban}" escapeXml="true"/></td>
							<td><input type="submit" name="_" value="unban" id="unban_${ban}" onclick="document.getElementById('banIPF').value = '${ban}';"></td></tr>
						</c:forEach>						
					</table>
				</form>
			</c:if>
		</c:if>
		</aside>
		
	</body>
</html>