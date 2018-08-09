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
		<title>Chatrooms</title>
		<link type="text/css" href="<%=contextRoot%>/design.css" rel="stylesheet">
		<script src="Skripts.js" type="text/javascript"></script>
		<script type="text/javascript">
			/*var func=window.onload;
			window.onload=function(){
				func();
				InitializeTimer(${ChatProperties.getRefreshTime()});)
					}*/
					
					var wsUri = getRootUri() + "/Chat/reload";
					websocket = new WebSocket(wsUri);
					<c:if test="${requestScope.chat!=null}">
					websocket.onopen = function(evt) {
					    //onOpen(evt)
					    websocket.send("${requestScope.chat}");
					};
						
					</c:if>
					/**/
					websocket.onmessage = function(evt) {
						if (!refreshAllowed) {
							doRefresh=true;
							return;
						}
					    onMessage(evt)
					};
					/*websocket.onerror = function(evt) {
					    onError(evt)
					};
					websocket.onclose = function(evt) {
					    onClose(evt)
					};*/

					
		
					
		</script>
	</head>
	<body>
	
	<h2>Chats:</h2>
		
		
	<form action="./LiveChat">
		<input type="submit" value="Live Chat" id="LiveChatButton"/>
	</form>
	<form action="./Chat" method="post" id="clickChatForm">
		<input type="hidden" name="name" id="clickChat"/>
			
		<c:forEach var="chat" items="${applicationScope.CHAT_DATA.listChats() }">
			<c:if test="${requestScope.chat!=chat}">
				<span>
					
					<input type="submit" name="name" value="${chat}"  onclick="document.getElementById('clickChat').value = '${chat}';
					document.getElementById('clickChatForm').action='./Chat?chat=${chat}';">
				</span>
			</c:if>
			
			
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
	
	
		<c:if test="${requestScope.chat!=null}">
			
		
		
			<div id="ChatDiv" > <div>
			<h2><c:out value="${requestScope.chat}" escapeXml="true"/> </h2>
			<c:if test="${sessionScope.manager}">
				<form action="./DelChat">
					<input type="hidden" name="${Constants.CHAT_ID }" id="delChatHidden"/>
					<input type="submit" name="${Constants.CHAT_ID }" value="löschen" id="button-1"
					onclick="document.getElementById('delChatHidden').value = '${requestScope.chat}';"/>
				</form>
				<hr>
			</c:if>
			<form action="./writeMsg" method="post">
				<label for="msgName" title="Name der Nachricht:">Name der Nachricht:</label>
				<input type="hidden" name="name" value="${requestScope.chat}" />
				
				<input name="msg" id="msgName" autocomplete="off"
				   onkeyup="InitializeTimer(
				  ${ChatProperties.getRefreshTime()}
				  )">
				<input type="submit" name="createChat" value="Senden"/>
				
			</form>
			<c:if test="${ChatProperties.isFilesAllowed()}">
				<form action="./Upload" method="post"  enctype="multipart/form-data">
					<input type="hidden" name="name" value="${requestScope.chat}" id="uploadChatnameHidden"/>
	    			<label for="fileInput" title="Datei">Datei:</label>
	    			<input type="file" name="file" id="fileInput" />
	    			<input type="submit" onclick="document.getElementById('uploadChatnameHidden').value = '${requestScope.chat}';"/>
				</form>
			</c:if>
			<hr>
			<form action="./DelMsg">
				<input type="hidden" name="${Constants.MSG_ID }" value="Submit" id="delMsgHidden"/>
				<input type="hidden" name="${Constants.CHAT_ID }" value="Submit" id="delMsgChatHidden"/>
				<table id="table-1">
					<tbody>
						<c:set var="chat1"  value="${applicationScope.CHAT_DATA.getChat(chat)}"/>
								<c:forEach var="msg" items="${chat1.getMsgs()}">
								<tr>
									
									<td>
									
									<c:if test="${msg.getLink()!=null}">
										<c:set var="ending" value="${msg.getLink().split('.')}" />
										
										
										<c:set var="link" value="${msg.getLink()}"/>
										
										<a href="${link}" target="_blank">
									</c:if>
									<c:out value="${msg.getMsg()}" escapeXml="true"/>
									<c:if test="${msg.getLink()!=null}">
											<c:if test="${ChatMsg.isImage(msg.getLink()) }">
											<br><img alt="image[${msg.getLink()}]" src="${msg.getLink()}" class="inChatImg">
										</c:if>
										<c:set var="link" value="${msg.getLink()}"/>
										</a>
									</c:if>
									
									</td>
										<c:if test="${sessionScope.manager}"><td>
											<input type="submit" value="Nachricht löschen" id="button-2" onclick="
												document.getElementById('delMsgHidden').value = '${msg.getId()}';
											document.getElementById('delMsgChatHidden').value='${chat}';
												"/>
										</td></c:if>
								</tr>
								</c:forEach>
					</tbody>
				</table>
			</form>	</div></div>
			
		</c:if>
		
		
		
		
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