/**
 * Protect label/issue input from deleting while Refreshing
 * 
 * Problems: last pressed key(in input) will be deleted
 * 	You are kicked out of the input field while Refreshing
 */
var refreshTime=20;
var autoRefreshDelay=10*1000;
function initFullTimer(){
	if (refreshTime == 0) {
        //StopTheClock();
        // Here's where you put something useful that's
        // supposed to happen after the allotted time.
        // For example, you could display a message:
        //window.location.href = window.location.href;
        if (doRefresh) {
        	window.location.href = window.location.href;
		}
        else {
        	refreshAllowed=true;
		}
    }
    else {
        secs = secs - 1;
        timerRunning = true;
        timerID = self.setTimeout("StartTheTimer()", autoRefreshDelay);
    }
}
/*
 * set Label to saved label
 */
function customReset(){
    if(typeof(sessionStorage.getItem('last_entry'))!="undefined"){
        var element = document.getElementById("msgName");
        element.value = sessionStorage.getItem('last_entry');
    }
//    for (var i = 0; i < sessionStorage.length; i++){
//       var item=sessionStorage.getItem(localStorage.key(i));
//        if(typeof(item)!="undefined"&&item!=null){
//            var element = document.getElementById(item.toString());
//            element.value = sessionStorage.getItem(item.toString());
//        }
//    }
}
//function customReset(name, id){
//    if(sessionStorage.getItem(name)){
//        var element = document.getElementById(id);
//        element.value = sessionStorage.getItem(name);
//    }
//}
/*
 * Speichere Inhalt von Input
 */
function setStorage(element){
	if(element.value==undefined||element.value==null){return;}
    sessionStorage.setItem('last_entry',element.value);
}
//function setStorage(element, name){
//	if(element.value==undefined||element.value==null){return;}
//    sessionStorage.setItem(name,element.value);
//}

/*
 * Führe customReset bei laden des Fensters aus
 */
//window.onload = function() {
//	customReset();
//}


//auto-reload
var secs;
var timerID = null;
var timerRunning = false;
var delay = 1000;
var refreshAllowed=true;
function refreshAllowed(){
	return refreshAllowed;
}
/*
 * Startet den Timer(neu)
 */
function InitializeTimer(seconds) {
    // Set the length of the timer, in seconds
    secs = seconds;
    StopTheClock();
    StartTheTimer();
}
/*
 * Stoppt den Timer
 */
function StopTheClock() {
    if (timerRunning)
        clearTimeout(timerID);
    timerRunning = false;
}
/*
 * Startet den Timer
 */
var doRefresh=false;
function StartTheTimer() {
    if (secs == 0) {
        StopTheClock();
        // Here's where you put something useful that's
        // supposed to happen after the allotted time.
        // For example, you could display a message:
        //window.location.href = window.location.href;
        refreshAllowed=true;
        if (doRefresh) {
        	window.location.href = window.location.href;
		}
    }
    else {
    	refreshAllowed=false;
        self.status = 'Remaining: ' + secs;
        //document.getElementById("lbltime").innerText = secs + " ";
        secs = secs - 1;
        timerRunning = true;
        timerID = self.setTimeout("StartTheTimer()", delay);
    }
}






function getRootUri() {
    return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" +
            (document.location.port == "" ? "8080" : document.location.port);
}

function init() {
    output = document.getElementById("output");
}

/*function send_message() {

    websocket = new WebSocket(wsUri);
    websocket.onopen = function(evt) {
        onOpen(evt)
    };
    websocket.onmessage = function(evt) {
        onMessage(evt)
    };
    websocket.onerror = function(evt) {
        onError(evt)
    };

}*/

/*function onOpen(evt) {
    writeToScreen("Connected to Endpoint!");
   // doSend(textID.value);

}*/

function onMessage(evt) {
	window.location.href = window.location.href;
}

/*function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function doSend(message) {
    writeToScreen("Message Sent: " + message);
    websocket.send(message);
}

function writeToScreen(message) {
    alert(message);
}

function onClose(evt) {
    writeToScreen("Disconnected!");
    doSend(textID.value);

}*/

//window.addEventListener("load", init, false);




